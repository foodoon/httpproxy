package guda.httpproxy.io;

/*
 * $RCSfile: OutputStreamPool.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-27  $
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PoolOutputStream extends OutputStream {
    public List outputStreamList;

    public PoolOutputStream() {
        outputStreamList = new ArrayList();
    }

    public PoolOutputStream(OutputStream outputStream) {
        outputStreamList = new ArrayList();

        outputStreamList.add(outputStream);
    }

    public PoolOutputStream(OutputStream[] outputStreamPool) {
        outputStreamList = new ArrayList();

        for (int i = 0; i < outputStreamPool.length; i++) {
            if (outputStreamPool[i] != null) {
                outputStreamList.add(outputStreamPool[i]);
            }
        }
    }

    public void add(OutputStream outputStream) {
        outputStreamList.add(outputStream);
    }

    public void remove(OutputStream outputStream) {
        outputStreamList.remove(outputStream);
    }

    public int size() {
        return outputStreamList.size();
    }

    public void close() throws IOException {
        for (Iterator iterator = outputStreamList.iterator(); iterator.hasNext(); ) {
            OutputStream outputStream = (OutputStream) (iterator.next());

            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public void flush() throws IOException {
        for (Iterator iterator = outputStreamList.iterator(); iterator.hasNext(); ) {
            OutputStream outputStream = (OutputStream) (iterator.next());

            try {
                outputStream.flush();
            } catch (IOException e) {
            }
        }
    }

    public void write(byte[] bytes, int offset, int len) throws IOException {
        for (Iterator iterator = outputStreamList.iterator(); iterator.hasNext(); ) {
            OutputStream outputStream = (OutputStream) (iterator.next());

            try {
                outputStream.write(bytes, offset, len);
            } catch (IOException e) {
            }
        }
    }

    public void write(byte[] bytes) throws IOException {
        for (Iterator iterator = outputStreamList.iterator(); iterator.hasNext(); ) {
            OutputStream outputStream = (OutputStream) (iterator.next());

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
            }
        }
    }

    public void write(int b) throws IOException {
        for (Iterator iterator = outputStreamList.iterator(); iterator.hasNext(); ) {
            OutputStream outputStream = (OutputStream) (iterator.next());

            try {
                outputStream.write(b);
            } catch (IOException e) {
            }
        }
    }
}
