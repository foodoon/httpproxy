package guda.httpproxy.http.server.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import guda.httpproxy.util.EncodingUtil;
import guda.httpproxy.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChunkedPump {
    private final static Logger logger = LoggerFactory.getLogger(ChunkedPump.class);

    public static void pumpChunkedStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        int chunkSize = 0;

        do {
            chunkSize = getChunkSizeFromInputStream(inputStream);

            outputStream.write((Integer.toString(chunkSize, 16) + "\r\n").getBytes());

            if (chunkSize == 0) {
                break;
            }

            IO.copy(inputStream, outputStream, chunkSize);

            outputStream.write("\r\n".getBytes());

            readCRLF(inputStream);
        }
        while (true);

        outputStream.write("\r\n".getBytes());
        outputStream.flush();
    }


    public static void readCRLF(InputStream in) throws IOException {
        int cr = in.read();
        int lf = in.read();
        
        /* 13, 10 */
        if ((cr != 13) || (lf != 10)) {
            throw new IOException("CRLF expected at end of chunk: [" + cr + "/" + lf + "]");
        }
    }


    public static int getChunkSizeFromInputStream(final InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // States: 0=normal, 1=/r was scanned, 2=inside quoted string, -1=end

        int state = 0;
        while (state != -1) {
            int b = in.read();

            if (b == -1) {
                throw new IOException("chunked stream ended unexpectedly");
            }

            switch (state) {
                case 0:
                    switch (b) {
                        case 13:
                            state = 1;
                            break;
                        case 34:
                            state = 2;
                            /* fall through */
                        default:
                            baos.write(b);
                    }
                    break;
                case 1:
                    if (b == 10) {
                        state = -1;
                    } else {
                        // this was not CRLF
                        throw new IOException("Protocol violation: Unexpected" + " single newline character in chunk size");
                    }
                    break;

                case 2:
                    switch (b) {
                        case 47:
                            b = in.read();
                            baos.write(b);
                            break;
                        case 34:
                            state = 0;
                            /* fall through */
                        default:
                            baos.write(b);
                    }
                    break;
                default:
                    throw new RuntimeException("assertion failed");
            }
        }

        //parse data
        String dataString = EncodingUtil.getAsciiString(baos.toByteArray());

        int separator = dataString.indexOf(';');

        dataString = (separator > 0) ? dataString.substring(0, separator).trim() : dataString.trim();

        int result;

        try {
            result = Integer.parseInt(dataString.trim(), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Bad chunk size: " + dataString);
        }

        return result;
    }


    protected static void copy2(InputStream inputStream, OutputStream outputStream, int size) throws IOException {
        int b = 0;
        for (int i = 0; i < size && (b = inputStream.read()) > -1; i++) {
            outputStream.write(b);
        }
    }

    public static void main(String[] args) {
        byte[] bytes = new String("01234567890123456789012345678901-->").getBytes();

        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bytes);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            IO.copy(bis, bos, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[" + bos.toString() + "]");
    }
}
