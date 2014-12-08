package guda.httpproxy.util;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class IO {
    private IO() {
    }


    public static void close(InputStream ins) {
        if (ins != null) {
            try {
                ins.close();
            } catch (IOException e) {
            }
        }
    }


    public static void close(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }


    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    public static void copy(String src, String tgt) throws IOException {
        copy(new File(src), new File(tgt));
    }

    public static void copy(File src, File tgt) throws IOException {
        FileInputStream fin = null;
        FileOutputStream fos = null;

        try {
            fin = new FileInputStream(src);
            fos = new FileOutputStream(tgt);

            copy(fin, fos);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void copy(File file, OutputStream out) throws IOException {
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);

            copy(fin, out);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }
        }
    }


    public static void copy(InputStream ins, String filePath) throws IOException {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filePath);

            copy(ins, fos);
        } catch (IOException e) {
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        if (in instanceof BufferedInputStream) {
            bis = (BufferedInputStream) in;
        } else {
            bis = new BufferedInputStream(in);
        }

        if (out instanceof BufferedOutputStream) {
            bos = (BufferedOutputStream) out;
        } else {
            bos = new BufferedOutputStream(out);
        }

        copy(bis, bos);
    }

    public static void copy(BufferedInputStream bis, BufferedOutputStream bos) throws IOException {
        int len = 0;

        byte[] buf = new byte[512];
        try {
            while ((len = bis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bos.flush();
    }


    public static void copy(InputStream in, OutputStream out, int size) throws IOException {
        if (size > 0) {
            int bufSize = Math.min(size, 4096);
            int readBytes = 0;
            int count = 0;

            byte[] bytes = new byte[bufSize];

            while (size > count && (readBytes = in.read(bytes, 0, bufSize)) > 0) {
                out.write(bytes, 0, readBytes);

                count += readBytes;

                bufSize = Math.min(size - count, 4096);
            }

            out.flush();
        }
    }


    public static void copy(BufferedInputStream bis, BufferedOutputStream bos, int size) throws IOException {
        int bufSize = Math.min(size, 4096);
        int readBytes = 0;
        int count = 0;

        byte[] bytes = new byte[bufSize];

        while (size > count && (readBytes = bis.read(bytes, 0, bufSize)) > 0) {
            bos.write(bytes, 0, readBytes);

            count += readBytes;

            bufSize = Math.min(size - count, 4096);
        }

        bos.flush();
    }


    public static String readAsText(InputStream inputStream,String charset) throws IOException {
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, charset));

            int len = 1024;
            char[] chars = new char[len];

            java.io.StringWriter out = new java.io.StringWriter();

            while ((len = br.read(chars)) > -1) {
                out.write(chars, 0, len);
            }

            return out.toString();
        }catch(Exception e){

        }
        return null;
    }

    public static byte[] readLine(InputStream stream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);

        int b = -1;

        while ((b = stream.read()) != -1) {
            if (b == 10) {
                bos.write(b);
                break;
            }

            bos.write(b);
        }

        if (bos.size() < 1) {
            return null;
        }

        return bos.toByteArray();
    }

    public static String readStringLine(InputStream stream,String charset) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);

        int b = -1;

        while ((b = stream.read()) != -1) {
            if (b == 10) {
                bos.write(b);
                break;
            }

            bos.write(b);
        }

        if (bos.size() < 1) {
            return null;
        }

        return new String(bos.toByteArray(),charset);
    }

    public static String uncompress(byte[] buf, int offset, int length,String charset) {
        if(buf == null || length < 1){
            return null;
        }
        InputStream is = null;
        GZIPInputStream gzin = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = new ByteArrayInputStream(buf, offset, length);
            gzin = new GZIPInputStream(is);
            isr = new InputStreamReader(gzin, charset);
            br = new BufferedReader(isr);
            char[] buffer = new char[4096];
            int readlen = -1;
            while ((readlen = br.read(buffer, 0, 4096)) != -1) {
                sb.append(buffer, 0, readlen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e1) {
                // ignore
            }
            try {
                isr.close();
            } catch (Exception e1) {
                // ignore
            }
            try {
                gzin.close();
            } catch (Exception e1) {
                // ignore
            }
            try {
                is.close();
            } catch (Exception e1) {
                // ignore
            }
        }
        return sb.toString();
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static byte[] readChunked(byte[] bytes) throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int mark = 0;
        int pos = 0;
        int orginLength = bytes.length;
        while((pos+2) < orginLength){

            if((int)bytes[pos+1] == 13 && (int)bytes[pos+2] == 10){

                byte[] length2Byte = new byte[pos - mark + 1];
                System.arraycopy(bytes, mark, length2Byte, 0, pos - mark + 1);
                int length2Int = get16Int(new String(length2Byte));
                if(length2Int == -1){
                    continue;
                }
                if(length2Int == 0) {
                    return baos.toByteArray();
                }

                pos += 3 ; mark = pos;
                baos.write(bytes, mark, length2Int);

                pos += (length2Int - 1)+3;
                mark = pos;
            }else{
                pos++;
            }
        }
        return baos.toByteArray();
    }

    private static int get16Int(String str){
        if(str == null){
            return -1;
        }
        try {
            return Integer.parseInt(str, 16);
        }catch(Exception e){

        }
        return -1;
    }
}
