package guda.httpproxy.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

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
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, charset));

        int len = 1024;
        char[] chars = new char[len];

        java.io.StringWriter out = new java.io.StringWriter();

        while ((len = br.read(chars)) > -1) {
            out.write(chars, 0, len);
        }

        return out.toString();
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
}
