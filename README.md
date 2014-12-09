java 工程，带有界面，代理并记录Http请求，方便app调试http请求，暂时不支持https
1)maven下载完jar包（如果是eclipse,在工程根目录下执行:mvn eclipse:eclipse，如果是Idea直接导入即可）
2)启动test下面的JettyServerStart.java 默认代理端口是7272,控制台端口是7777
3)访问http://localhost:7777/help.htm，可以看到控制台首页
4)设置手机wifi的代理：代理主机为部署的服务器的ip，默认端口为7272
