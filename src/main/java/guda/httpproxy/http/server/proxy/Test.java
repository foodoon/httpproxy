package guda.httpproxy.http.server.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import guda.httpproxy.util.IO;

public class Test {

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Socket socket = new Socket("localhost", 8081);
		InputStream socketInputStream = socket.getInputStream();
		OutputStream socketOutputStream = socket.getOutputStream();
		String header = "GET /BJMMIS/images/rebotton.jpg HTTP/1.1/r/nAccept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/QVOD, application/QVOD, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*/r/nAccept-Language: zh-cn/r/nUser-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C)/r/nAccept-Encoding: gzip, deflate/r/nHost: localhost:8081/r/nConnection: Keep-Alive/r/nCookie: JSESSIONID=FCE7115E35475D52B380821BD22756A3/r/n/r/n";
		header =header.replaceAll("/r/n", "\r\n");
		System.out.println(header);
		System.out.println("test8081");
		socketOutputStream.write(header.getBytes());
		Test.read(socketInputStream);
	}

	public static void read(InputStream in) throws IOException {
		byte[] bytes = null;
		while ((bytes = IO.readLine(in)) != null) {
			String headers = new String(bytes);
			System.out.print(headers);

//			if ("\r\n".equals(headers)) {
//				break;
//			}
		}

	}
}
