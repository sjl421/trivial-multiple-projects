## AIO 

NIO 2.0引入了新的异步通道的概念，并提供了异步文件通道和异步套接字通道的实现。

异步的套接字通道时真正的异步非阻塞I/O，对应于UNIX网络编程中的事件驱动I/O（AIO）。他不需要过多的Selector对注册的通道进行轮询即可实现异步读写，从而简化了NIO的编程模型。

我们分析一下我们在这个例子中的流程：

### Apllication

`Application` 是所有程序的入口，它首先启动了一个服务端程序，随后启动了一个客户端程序：并且一直从标准输入接收数据，发送给服务端并等待服务端的返回数据；

**在以下的描述中,所有的 AsynchronouServerSocketChannel 都简称为 `ssc`,所有的 AsynchronousSocketChannel 都简称为 `sc`**

1. AioServer.start()
	1. 创建AioServer，创建 `ssc` 并绑定监听的端口 `PORT`;
	2. server 调用 accept(this, new AcceptHandler()) 方法,并设置回调函数为 AcceptHandler(),当触发一个尝试连接到 `PORT` 的事件时,回调函数会被执行.同时,通过 `this` 参数,将自身作为作为参数传递给了回调函数;
	3. 当一个客户端尝试连接到服务器时,刚才注册的回调函数会被触发,在这个回调函数中:
		1. 再次为 `ssc` 注册了 `accept`事件;
		2. 为 `sc` (在accept方法的回调函数的completed方法中的第一个参数)注册了一个读事件,并为该事件增加了回调函数(ServerReadHandler);
	4. 当刚才注册的通道可读时,会触发ServerReadHandler的回调,在这个回调中:
		1. 读取并处理客户端输入;
		2. 为 `sc` 注册回调函数(在本例子中这个函数是一个匿名函数);

2. AioClient.start()
	1. 创建 `sc` 并通过connect请求连接到 `ssc`,同时注册了 connect 的回调函数;
	2. 步骤1会触发 `ssc` 的回调函数(即1.2中设置的回调函数);
	3. 连接成功之后,会触发 2.1 中注册的回调函数(在例子中就是简单的输出了一句话);
3. AioClient.sendMsg()
	1. 向 `ssc` 发送写请求,这将触发 1.3.2 中注册的回调函数,同时注册 `sc` 的写回调函数;
		1. 在`ssc`的回调函数中读取接收到的消息计算结果,向 `sc` 写入消息;
		2. 在 `sc` 写完之后,触发 `sc`上的写回调函数,

### AsynchronousServerSocketChannel

```java
/**
 * An asynchronous channel for stream-oriented listening sockets.
 * 
 * An asynchronous server-socket channel is created by invoking the `open()` method of this class.
 * 
 * A newly-created asynchronous server-socket channel is open but not yet bound.
 * It can be bound to a local address and configured to listen for connections by invoking 
 * the `bind(SocketAddress, int)` method.
 *
 * Once bound, the `accept(Object, CompletionHandler)` method is used to initiate the accepting 
 * of connections to the channels's socket.
 *
 * An attempt to invoke the `accept` method on an unbound channel will cause 
 * a **NotYetBoundException** to be thrown.
 * 
 * Channels of this type are safe for use by multiple concurrent threads though at mast 
 * one accept operation can be outstanding at any time.

 * If a thread initiates an accept operation before a previous accept operation 
 * has completed then an **AcceptPendingException** will be thrown.
 * 
 * Socket options are configured using the `setOption` method.Channels of this type 
 * support the following options:
 *
 *
 * | Option Name | Desc |
 * |-------------|------|
 * | SO_RCVBUF | The size of the socket receive buffer |
 * | SO_REUSEADDR | Re-use address |
*/
public abstract class AsynchronousServerSocketChannel
	implements AsynchronousChannel, NetworkChannel {

}
```

#### AsynchronousServerSocketChannel.accept

```java
/** 
 * 
 * Accepts a connection.
 *
 * This method initiates an asynchronous operation to accept a connection made to this channel's socket.
 * The {@code handler} parameter is a completion handler that is invoked when a connection
 * is accepted (or the operation fails). The result passed to the completion handler is 
 * the {@link AynchronousSocketChannel} to the new connection.
 *
 * When a new connection is accepted then the resulting {@code AsynchronousSocketChannel} will
 * be bound to the same {@link AsynchronousChannelGroup} as this channel. If the group is
 * {@link AsynchronousChannelGroup#isShutdown shutdown} and a connection is accepted, then 
 * the connection is closed, and the operation completes with an {@code IOException} and cause
 * {@link ShutdownChannelGroupException}.
 *
 * To allow for concurrent handling of new connections, the completion handler is not invoked
 * directly by the initiating thread when a new connection is accepted immediately.
 *
 * If a security manager has been installed then it verifies that the address and port number
 * of the connection's remote endpoint are permitted by the security manager's 
 * {@link SecurityManager#checkAccept checkAccept} method.The permission check is performed 
 * with privileges that are restricted by the calling context of this method.If the 
 * permission check fails then the connection is closed and the operation completes with 
 * a {@link SecurityException}
 *
 * @param <A> 
 * 			The type of attachment
 * @param attachment
 * 			The object to attach to the I/O operation; can be {@code null}
 * @param handler
 * 			The handler for consuming the result
 */
public abstract <A> void accept(A attachment, 
								CompletionHandler<AsynchronousSocketChannel, ? super A> handler);
```

#### AsynchronousServerSocketChannel.read

```java
/**
 * Reads a sequence of bytes from channel into the given buffer.
 *
 * This method initiates an asynchronous read operation to read a sequence
 * of bytes from this channel into the given buffer.
 * The {@code handler} parameters is a completion handler that is invoked when 
 * the read operation completes (or fails).The result passed to the completion
 * handler is the number of bytes read or {@code -1} if no butes could be read
 * because the channel has reached end-of-stream.
 *
 * If a timeout is specified and the timeout elapses before the operation
 * completes then the operation completes with the exception 
 * {@link InterruptedByTimeoutException}. Where a timeout occurs, and the 
 * implementtation cannot guarantee that bytes have not been read, or will
 * not be read from the channel into the given buffer, then further attempts
 * to read from the channel will cause an unspecific runtime exception to be thrown.
 *
 * Otherwise this method works in the same manner as the 
 * {@link AsynchronousByteChannel#read(ByteBuffer, Object, CompletionHandler)}
 * 
 * @param <A>
 * 			The type of the attachment
 * @param dst
 * 			The buffer into which bytes are to be transferred
 * @param timeout
 * 			The maximum time for the I/O operation to complete
 * @param unit
 * 			The time unit of the {@code timeout} argument
 * @param attachment
 * 			The object to attach to the I/O operation; can be {@code null}
 * @param handler
 * 			The handler for consuming the result
 */
public abstract <A> void read(ByteBuffer dst,
								long timeout,
								TimeUnit unit,
								A attachment,
								CompletionHanlder<Integer, ? super A> handler);
```

#### AsynchronousServerSocketChannel.write

```java
/**
 * Writes a sequence of bytes to this channel from the given buffer
 *
 * This method initiates an asynchronous write operation to write a sequence of bytes
 * to this channel from the given buffer.The {@code handler} parameter is a completion
 * handler that is invoked when the write operation completes (or fails).The result
 * passed to the completion handler is the number of bytes written.
 *
 * If the timeout is specified and the timeout elapses before the operation completes then
 * it completes with the exception {@link InterruptedByTimeoutException}. Where a timeout
 * occurs, and the implementation cannot guaranteee that bytes have not been written,
 * or will not be written to the channel from the given buffer,then further attempts to 
 * write to the channel will cause an unspecific runtime exception to be thrown.
 * 
 * @param <A>
 * 			The type of the attachment
 * @param dst
 * 			The buffer into which bytes are to be transferred
 * @param timeout
 * 			The maximum time for the I/O operation to complete
 * @param unit
 * 			The time unit of the {@code timeout} argument
 * @param attachment
 * 			The object to attach to the I/O operation; can be {@code null}
 * @param handler
 * 			The handler for consuming the result
 */
public abstract <A> void write(ByteBuffer src,
								long timeout,
								TimeUnit unit,
								A attachment,
								CompletionHandler<Integer, ? super A> handler);
```


### AsynchronousSocketChannel

```java
/**
 * An asynchronous channel for stream-oriented connecting sockets.
 *
 * Asynchronous socket channels are create in one of two ways. A newly-created
 * {@code AsynchronousSocketChannel} is created by invoking one of the {@link #open open}
 * methods defined by this class. A newly-created channel is open but not yet bounded.
 * A connected {@code AsynchronouSocketChannel} is created when a connection is made to
 * the socket socket of an {@link AsynchronouServerSocketChannel}.
 * It is not possible to create an asynchronous socket channel for an arbitrary, pre-existing
 * {@link java.net.Socket socket}.
 *
 * A newly-created channel is connected by invoking its {@link #connect connect} method;
 * once connected, a channel remains connected until it is closed.Whether or not a socket
 * channel is connected may be determined by invoking its 
 * {@link #getRemoteAddress getRemoteAddress} method.An attempt to invoke an I/O
 * operation upon an unconnected channel will cause a {@link NotYetConnectedException}
 * to be thrown.
 *
 * Channels of this type are safe for use by multiple concurrent threads.
 * They support concurrent reading and writing, though at most one read operation
 * and one write operation can be outstanding at any time.
 *
 * If a thread initiates a read operation before a previous read operation
 * has completed then a {@link ReadPendingException} will be thrown.Similarly,
 * an attempt to initiate a write operation before a previous write has completed
 * will throw a {@link WritePendingException}
 *
 * Socket options are configured using {@link setOption(SocketOption, Object)}
 * method.Asynchronous socket channels support the following options:
 *
 * | Option Name | Desc |
 * |-------------|------|
 * | SO_SNDBUF | The size of the socket send buffer |
 * | SO_RCVBUF | The size of the socket receive buffer |
 * | SO_KEEPALIVE | Keep connection alive |
 * | SO_REUSEADDR | Re-use address |
 * | TCP_NODELAY | disable the nagle algorithm |
 *
 * Additional (implementation specific) options may also be supported.
 *
 * Timeouts
 *
 * The {@link #read} and {@link write} methods defined by this class allow
 * a timeout to be specified when initiating a read or write operation.
 * If the timeout elapses before an operation completes then the operation
 * completes with the exception {@link InterruptedByTimeoutException}.
 *
 * A timeout may leave the channel, or the underlying connection, in an inconsistent
 * state.Where the implementation cannot guarantee that bytes have not been read from 
 * the channel then it puts the channel into an implementataion specific error state.
 * A subsequent attempt to initiate a {@code read} operations causes an unspecified runtime
 * exception to be thrown.Similarly if a {@code write} operation times out and the 
 * implementation cannot guarantee bytes have not been written to the channel then 
 * further attempts to {@code write} to the channel cause an unspecified runtime exception
 * to be throws.When a timeout elapses then the state of the {@link ByteBuffer}, or the 
 * sequence of buffers, for the I/O operation is not defined.Buffers should be 
 * discard or at least care must be taken to ensure that the buffers are not accessed
 * while the channel remains open.All methods that accept timeout parameters treat values
 * less than or equal to zero to mean that the I/O operation does not timeout.
 */
public abstract class AsynchronousSocketChannel
	implements AsynchronousByteChannel, NetworkChannel {

}
```

#### connect()

```java
/**
 * Connects this channel.
 *
 * This method initiates an operation to connect this channel.The {@code handler}
 * parameter is a completion handler that is invoked when the connection is 
 * successfully established or connection cannot be established.If the connection
 * cannot be established then the channel is closed.
 *
 * This method performs exactly the same security checks as the 
 * {@link java.net.Socket} class. That is, if a security manager has permits connection
 * to the address and port number of the given remote endpoint.
 *
 * @param <A>
 * 			The type of the attachement
 * @param remtoe
 * 			The remote address to which this channel is to be connected
 * @param attachment
 * 			The object to attach to the I/O operation; can be {@code null}
 * @param hanler
 * 			The handler for consuming the result
 */
public abstract <A> void connect(SocketAddress remote, 
								 A attachment,
								 CompletionHandler<Void, ? super A> handler);
```


### CompletionHandler<V, A>

```java
/**
 * @param <V> The result type of the I/O operation
 * @param <A> The type of the object attached to the I/O operation
 *
 */
public interface CompletionHandler<V, A> {
	/**
	 * Invoked when a operations has compeleted
	 *
	 * @param result 
	 * 		The result of the I/O operation
	 *
	 * @param attachment 
	 * 		The object attached to the I/O operation when it was initiated
	 */
	void completed(V result, A attachment);

	/**
	 * Invoked when an operation fails.
	 * 
	 * @param exc
	 * 		The exception to indicate why the I/O operation failed
	 *
	 * @param attachment
	 * 		The object attached to the I/O operation when it was initiated
	 */
	void failed(Throwable exc, A attachment);
}
```

A handler for consuming the result of an asynchronous I/O operation.

The asynchronous channels defined in this package allow a **completition** handler to be specified to consume the result of an asynchronous operation.

The `completed` method is invoked when a I/O operation completes successfully.The `failed` method is invoked if the I/O operations fails.The implementations of these methods should complete in a timely manner so as to avoid keeping the invoking thread from dispatching to other completion handlers.
