package guda.httpproxy.http;

/*
 * $RCSfile: HttpResponseFactory.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import guda.httpproxy.util.ArrayUtil;
import guda.httpproxy.util.IO;


public class HttpResponseFactory {

    public static HttpResponse read(InputStream in, OutputStream out) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        byte[] bytes = null;

        byte[] LF = new byte[]{0x0A};
        byte[] CRLF = new byte[]{0x0D, 0x0A};

        if ((bytes = IO.readLine(in)) != null) {
            if (!ArrayUtil.equals(bytes, CRLF) && !ArrayUtil.equals(bytes, LF)) {
                String header = new String(bytes).trim();

                java.util.StringTokenizer st = new java.util.StringTokenizer(header, " ");

                if (st.hasMoreTokens()) {
                    httpResponse.setHttpProtocol(st.nextToken());
                }

                if (st.hasMoreTokens()) {
                    String value = st.nextToken();

                    int statusCode = 200;

                    try {
                        statusCode = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                    }

                    httpResponse.setStatusCode(statusCode);
                }
                if (st.hasMoreTokens()) {
                    httpResponse.setStatusString(st.nextToken());
                }
            }
        }

        while ((bytes = IO.readLine(in)) != null) {
            if (ArrayUtil.equals(bytes, CRLF) || ArrayUtil.equals(bytes, LF)) {
                break;
            }
            String header = new String(bytes);
            int k = header.indexOf(":");
            if (k > -1) {
                String name = header.substring(0, k).trim();
                String value = header.substring(k + 1).trim();

                httpResponse.setHeader(name, value);
            }
        }

        httpResponse.setInputStream(in);
        httpResponse.setOutputStream(out);

        return httpResponse;
    }
}

