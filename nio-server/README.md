## 使用NIO来构建的一个事件驱动的服务器

1. Server端负责初始化ServerSocketChannel并负责监听客户端的HTTP请求，为客户端的请求返回响应；
2. Server端为非阻塞式，Client端为阻塞式；
