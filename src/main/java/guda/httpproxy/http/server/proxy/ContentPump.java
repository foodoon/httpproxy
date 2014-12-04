package guda.httpproxy.http.server.proxy;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import guda.httpproxy.util.IO;


public class ContentPump {

    public static void pumpContentStream(InputStream inputStream, OutputStream outputStream, int size) throws IOException {
        IO.copy(inputStream, outputStream, size);

    }
}
