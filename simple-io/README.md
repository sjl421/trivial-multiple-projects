## Java NIO 和 AIO

### 简介

NIO提供了与传统BIO模型中的Socket和ServerSocket相对应的SocketChannel和ServerSocketChannel两种不同的套接字通道实现。

新增的着两种通道都支持阻塞和非阻塞两种模式。

阻塞模式使用就像传统中的支持一样，比较简单，但是性能和可靠性都不好；非阻塞模式正好与之相反。

对于低负载、低并发的应用程序，可以使用同步阻塞I/O来提升开发速率和更好的维护性；对于高负载、高并发的（网络）应用，应使用NIO的非阻塞模式来开发。

### 缓冲区Buffer

Buffer是一个对象，包含一些要写入或者读出的数据。

在NIO库中，所有数据都是用缓冲区处理的。在读取数据时，它是直接读到缓冲区中的；在写入数据时，也是写入到缓冲区中。任何时候访问NIO中的数据，都是通过缓冲区进行操作。

缓冲区实际上是一个数组，并提供了对`数据结构`化访问以及维护读写位置等信息。

具体的缓存区有这些：
    - ByteBuffer
    - CharBuffer
	- ShortBuffer
	- IntBuffer
	- LongBuffer
	- FloatBuffer
	- DoubleBuffer
他们集成自共同的抽象类 `Buffer`.

### 通道Channel

我们对数据的读取和写入要通过Channel，它就像水管一样，是一个通道。通道不同于流的地方就是通道是双向的，可以用于读、写和同时读写操作。

底层的`操作系统`的通道一般都是全双工的，所以全双工的Channel比流能更好的映射底层操作系统的API。

Channel 主要分为两大类：

	1. SelectableChannel：用户网络读写
	2. FileChannel：用于文件操作

后面代码会涉及的`ServerSocketChannel`和`SocketChannel`都是`SelectableChannel`的子类。

#### ServerSocketChannel

A selectable channel for stream-oriented listening sockets.

A server-socket channel is created by invoking the open method of this class. It is not possible to create a channel for an arbitrary, pre-existing ServerSocket. A newly-created server-socket channel is open but not yet bound. An attempt to invoke the accept method of an unbound server-socket channel will cause a NotYetBoundException to be thrown. A server-socket channel can be bound by invoking one of the bind methods defined by this class.
Socket options are configured using the setOption method. Server-socket channels support the following options:
<br/>
| Option Name | Description |
|-------------|-------------|
| SO_RCVBUF | The size of the socket receive buffer |
| SO_REUSEADDR | Re-use address |
<br/>
Additional (implementation specific) options may also be supported.
Server-socket channels are safe for use by multiple concurrent threads.

#### SocketChannel

A selectable channel for stream-oriented connecting sockets.
A socket channel is created by invoking one of the open methods of this class. It is not possible to create a channel for an arbitrary, pre-existing socket. A newly-created socket channel is open but not yet connected. An attempt to invoke an I/O operation upon an unconnected channel will cause a NotYetConnectedException to be thrown. A socket channel can be connected by invoking its connect method; **once connected, a socket channel remains connected until it is closed**. Whether or not a socket channel is connected may be determined by invoking its isConnected method.



### 多路复用器 Selector

Selector是Java  NIO 编程的基础。

Selector提供选择已经就绪的任务的能力：Selector会不断轮询注册在其上的Channel，如果某个Channel上面发生读或者写事件，这个Channel就处于就绪状态，会被Selector轮询出来，然后通过SelectionKey可以获取就绪Channel的集合，进行后续的I/O操作。

一个Selector可以同时轮询多个Channel，因为JDK使用了epoll()代替传统的select实现，所以没有最大连接句柄1024/2048的限制。所以，只需要一个线程负责Selector的轮询，就可以接入成千上万的客户端。

### NIO服务端

可以看到，创建NIO服务端的主要步骤如下：
1. 打开 `ServerSocketChannel`，监听客户端连接
2. 绑定监听端口，设置连接为非阻塞模式
3. 创建Reactor线程，创建多路复用器并启动线程
4. 将 `ServerSocketChannel` 注册到 Reactor 线程中的 `Selector`，监听 accept 事件
5. `Selector` 轮询准备就绪的 key
6. Selector 监听到新的客户端的接入，处理新的接入请求，完成TCP三次握手，建立物理链路
7. 设置客户端链接为非阻塞模式
8. 将新接入的客户端连接注册到Reactor线程的Selector上，监听读操作，读取客户端发送的网络消息
9. 异步读取客户端消息到缓冲区
10. 对Buffer进行解码编码，处理半包消息，将解码成功的消息封装成Task
11. 将应答消息编码为Buffer，调用SocketChannel的write将消息异步发送给客户端









































