## AIO 

NIO 2.0引入了新的异步通道的概念，并提供了异步文件通道和异步套接字通道的实现。

异步的套接字通道时真正的异步非阻塞I/O，对应于UNIX网络编程中的事件驱动I/O（AIO）。他不需要过多的Selector对注册的通道进行轮询即可实现异步读写，从而简化了NIO的编程模型。

我们分析一下我们在这个例子中的流程：

### Application

`Application` 是所有程序的入口，它首先启动了一个服务端程序，随后启动了一个客户端程序：并且一直从标准输入接收数据，发送给服务端并等待服务端的返回数据；

**在以下的描述中,所有的 AsynchronousServerSocketChannel 都简称为 `ssc`,所有的 AsynchronousSocketChannel 都简称为 `sc`**

1. `ssc` : 发起一个异步的accept()方法,注册回调函数 AcceptHandler
2. `sc`  : 发起一个异步的 connect():
    1. `ssc` : 触发(1)注册的回调函数;
	2. `sc`  : 注册回调函数 AsyncClientHandler;
3. 在 (2.1) 触发的回调函数中:
	1. `ssc` : 再次发出(1)中的异步请求
	2. `ssc` : 发出一个异步的 read() 请求,注册回调函数为 ServerReadHadler
4. `sc`  : 在 (2.2) 的回调函数中输出连接成功日志;
5. `sc`  : 从 System.in 读取数据,并发起一个异步的 write() 方法,回调函数为 ClientWriteHandler
	1. `ssc` : (5) 会触发 (3.2) 中的回调函数,
	2. `sc`  : 同时也会触发 (5) 中的回调函数
6. `ssc` : 在(5.1)触发的回调函数中,通过输入值计算出输出值,发起一个异步的 write() 方法,并注册一个匿名的回调函数
7. `sc`  : 在(5.2)触发的回调函数中,通过 buffer.hasRemaining()来保证所有的请求被输出完,并注册回调函数 ClientReadHandler
8. `ssc` : 在(6)中的write()方法执行,触发(7)中的回调函数输出结果,同时继续接收 `sc` 的写请求.

>注意一个非常重要的点:
>所有的回调函数都只能够触发一次,譬如 accept() 方法,必须在回调函数中再次注册才能够继续的监听某个端口的连接请求;
>对于 read() 方法,在触发一次之后,如果不重新注册read()方法,那么后续的请求将不会被接收;


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

#### 阻塞的connect()

```java
/**
 * Connects this channel.
 *
 * This method initiates an operation to connect this channel.This method behaves in exactly
 * the same manner as the {@link #connect(SocketAddress, Object, CompletionHandler)} method
 * except that instead of specifying a completion handler, this method returns a {@code Future}
 * representing the pending result. The {@code Future}'s {@link Future#get() get} method 
 * returns {@code null} on successful completion.
 * 
 * @param remote
 * 			The remote address to which this channel is to be connected
 *
 * @return A {@code Future} object representing the pending result
 *
 */
public abstract Future<Void> connect(SocketAddress remote);
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

#### AsynchronousSocketChannel.connect()

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

### AsynchronousChannelGroup

```java
/**
 * A grouping of asynchronous channels for the purpose of resource sharing.
 *
 * An asynchronous channel group encapsulates the mechanics required to handle the completition
 * of I/O initiated by {@link AsynchronousChannel channels} that are bound to the group.
 * A group has an associated thread pool to which tasks are submitted to handle I/O events and 
 * dispatch to {@link CompletionHandler completion-handlers} that consume the result of 
 * asynchronous operations performed on channels in the group.In addition to handling I/O
 * events, the pooled threads may also execute other task required to support the execution
 * of asynchronous I/O operations.
 *
 * An asynchronous channel group is created by invoking the 
 * {@link #withFixedThreadPool withFixedThreadPool} or 
 * {@link #withCachedThreadPool withCachedThreadPool} methods defined here.
 * Channels are bound to a group by specifying the group when constructing the channel.The
 * associated thread pool is owned by the group;termination of the group results in the shutdown
 * of the associated thread pool.
 *
 * In addition to groups created explicitly, the Java VM maintains a system-wide default group
 * that is constructed automatically.Asynchronous channels that do not specify a group a
 * construction time are bound to the default group.The default group has an associated thread 
 * pool that creates new threads as needed.The default group may be configured by means of
 * system properties defined in the table below.Where the 
 * {@link java.util.concurrent.ThreadFactory ThreadFactory} for the default group is not 
 * configured then the pooled threads of the default group are
 * {@link Thread#isDaemon daemon} threads.
 * 
 * <table border summary="System properties">
 *   <tr>
 *     <th>System property</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td> {@code java.nio.channels.DefaultThreadPool.threadFactory} </td>
 *     <td> The value of this property is taken to be the fully-qualified name
 *     of a concrete {@link java.util.concurrent.ThreadFactory ThreadFactory}
 *     class. The class is loaded using the system class loader and instantiated.
 *     The factory's {@link java.util.concurrent.ThreadFactory#newThread
 *     newThread} method is invoked to create each thread for the default
 *     group's thread pool. If the process to load and instantiate the value
 *     of the property fails then an unspecified error is thrown during the
 *     construction of the default group. </td>
 *   </tr>
 *   <tr>
 *     <td> {@code java.nio.channels.DefaultThreadPool.initialSize} </td>
 *     <td> The value of the {@code initialSize} parameter for the default
 *     group (see {@link #withCachedThreadPool withCachedThreadPool}).
 *     The value of the property is taken to be the {@code String}
 *     representation of an {@code Integer} that is the initial size parameter.
 *     If the value cannot be parsed as an {@code Integer} it causes an
 *     unspecified error to be thrown during the construction of the default
 *     group. </td>
 *   </tr>
 * </table>
 *
 * The completion handler for an I/O operation initiated on a channel bound to a group is
 * guaranteeed to be invoked by one of the pooled threads in the group.
 * This ensures that the completion handler is run by a thread with the expected identity.
 *
 * Where an I/O operation completes immediately, and the initiating thread is one of the 
 * pooled threads in the group then the completion handler may be invoked directly by the
 * initiating thread.To avoid stack overflow, an implementation may impose a limit as to 
 * the number of activations on the thread stack.Some I/O operations may prohibit invoking
 * the completion handler directly by the initiating thread. {@see accept}
 *
 * ************ Shutdown and Termination ************
 *
 * The {@link #shutdown() shutdown} method is used to initiate an orderly shutdown of a group.
 * An orderly shutdown makrs the group as shutdown; further attempts to construct a channel
 * that binds to the group will throw {@link ShutdownChannelGroupException}. Wheter or not
 * a group is shutdown can be tested using the {@link #isShutdown() isShutdown} method.
 * Once shutdown, the group **terminate** when all asynchronous channels that are bound to
 * the group are closed, all actively executing completion handlers have run to completion,
 * and resources used by the group are releaesd.No attempt is made to stop or interrupt 
 * threads that are executing completion handlers.The {@link #isTerminated() isTerminated}
 * method is used to test if the group has terminated, and the 
 * {@link #awaitTermination awaitTermination} method can be used to block until the group
 * has terminated.
 *
 * The {@link #shutdownNow() shutdownNow} method can be used to initiate a **forceful** 
 * shutdown of the group.
 *
 */
public abstract class AsynchronousChannelGroup {
}
```


---

## The asynchronous channel APIs

An asynchronous channel represents a connection that supports nonblocking operations, such as connecting, reading, and writing, and provides mechanisms for controlling the operations after they've been initiated. The More New I/O APIs for the Java Platform (NIO.2) in Java 7 enhance the New I/O APIs (NIO) introduced in Java 1.4 by adding four asynchronous channels to the java.nio.channels package:
	- AsynchronousSocketChannel
	- AsynchronousServerSocketChannel
	- AsynchronousFileChannel
	- AsynchronousDatagramChannel

These classes are similar in style to the NIO channel APIs. **They share the same method and argument structures**, and most operations available to the NIO channel classes are also available in the new asynchronous versions. The main difference is that `the new channels enable some operations to be executed asynchronously`.

The asynchronous channel APIs provide two mechanisms for monitoring and controlling the initiated asynchronous operations. The first is by returning a `java.util.concurrent.Future` object, which models a pending operation and can be used to query its state and obtain the result. The second is by passing to the operation an object of a new class, `java.nio.channels.CompletionHandler`, which defines handler methods that are executed after the operation has completed. Each asynchronous channel class defines duplicate API methods for each operation so that either mechanism can be used.

### Asynchronous socket channels and futures

To start, we'll look at the `AsynchronousServerSocketChannel` and `AsynchronousSocketChannel` classes. Our first example demonstrates how a simple client/server can be implemented using these new classes. First we'll set up the server.

#### Server setup

```java
AsynchronouServerSocket server = AsynchronousServerSocketChannel.open();
server.bind(port);

Future<AsynchronousSocketChannel> acceptFuture = server.accept();
```

This is the first difference from NIO. The accept call always returns immediately, and — unlike **ServerSocketChannel.accept()**, which returns a SocketChannel — it returns a **Future<AsynchronousSocketChannel>** object that can be used to retrieve an AsynchronousSocketChannel at a later time. The generic type of the Future object is the result of the actual operation. For example, a read or write returns a Future<Integer> because the operation returns the number of bytes read or written.

Using the **Future** object, the current thread can block to wait for the result:

```java
AsynchronousSocketChannel worker = future.get();
```

Here it blocks with a timeout of 10 seconds:

```java
AsynchronousSocketChannel worker = future.get(10, TimeUnit.SECONDS);
```

Or it can poll the current state of the operation, and also cancel the operation:

```java
if (!future.isDone()) {
    future.cancel(true);
}
```

The **cancel()** method takes a boolean flag to indicate whether the thread performing the accept can be interrupted. This is a useful enhancement; in previous Java releases, blocking I/O operations like this could only be aborted by closing the socket.

### Client setup

Next, we can set up the client by opening and connecting a **AsynchronousSocketChannel** to the server:

```java
AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
client.connect(server.getLocalAddress()).get();
```

Once the client is connected to the server, reads and writes can be performed via the channels using byte buffers, as shown in Listing 1:

Listing 1. Using byte buffers for reads and writes

```java
// send a message to the server
ByteBuffer message = ByteBuffer.wrap("ping".getBytes());
client.write(message).get();
 
 // read a message from the client
 worker.read(readBuffer).get(10, TimeUnit.SECONDS);
 System.out.println("Message: " + new String(readBuffer.array()));
```

Scattering reads and writes, which take an array of byte buffers, are also supported asynchronously.

>The APIs of the new asynchronous channels completely abstract away from the underlying sockets: there's no way to obtain the socket directly, whereas previously you could call **socket()** on, for example, a **SocketChannel**. Two new methods — getOption and setOption — have been introduced for querying and setting socket options in the asynchronous network channels. For example, the receive buffer size can be retrieved by channel.getOption(StandardSocketOption.SO_RCVBUF) instead of channel.socket().getReceiveBufferSize();.

### Completion handlers

The alternative mechanism to using **Future** objects is to register a callback to the asynchronous operation. The **CompletionHandler** interface has two methods:

```void
void completed(V result, A attachment)
void failed(Throwable e, A attachment)
```

The attachment parameter of both methods is an object that is passed in to the asynchronous operation. It can be used to track which operation finished if the same completion-handler object is used for multiple operations.

#### Open commands

Let's look at an example using the **AsynchronousFileChannel** class. We can create a new channel by passing in a **java.nio.file.Path** object to the static open() method:

```java
AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("myfile"));
```

Path is a new class in Java 7 that we look at in more detail in Part 2. We use the Paths.get(String) utility method to create a Path from a String representing the filename.

By default, the file is opened for reading. The open() method can take additional options to specify how the file is opened. For example, this call opens a file for reading and writing, creates it if necessary, and tries to delete it when the channel is closed or when the JVM terminates:

```java
fileChannel = AsynchronousFileChannel.open(Paths.get("afile"),
    StandardOpenOption.READ, StandardOpenOption.WRITE,
    StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE);
```

>**New commands for**
>The format of the open commands for asynchronous channels has been backported to the FileChannel class. Under NIO, a FileChannel is obtained by calling getChannel() on a FileInputStream, FileOutputStream, or RandomAccessFile. With NIO.2, a FileChannel can be created directly using an open() method, as in the examples shown here.

An alternative **open()** method provides finer control over the channel, allowing file attributes to be set.

#### Implementing a completion handler

Next, we want to write to the file and then, once the write has completed, execute something. We first construct a **CompletionHandler** that encapsulates the "something" as shown in Listing 2:

```java
CompletionHandler<Integer, Object> handler =
    new CompletionHandler<Integer, Object>() {
    @Override
    public void completed(Integer result, Object attachment) {
        System.out.println(attachment + " completed with " + result + " bytes written");
    }
    @Override
    public void failed(Throwable e, Object attachment) {
        System.err.println(attachment + " failed with:");
        e.printStackTrace();
    }
};
```

Now we can perform the write:

```java
fileChannel.write(ByteBuffer.wrap(bytes), 0, "Write operation 1", handler);
```

The write() method takes:
	- A ByteBuffer containing the contents to write
	- An absolute position in the file
	- An attachment object that is passed on to the completion handler methods
	- A completion handler

Operations must give an absolute position in the file to read to or write from. It doesn't make sense for the file to have an internal position marker and for reads/writes to occur from there, because the operations can be initiated before previous ones are completed and the order they occur in is not guaranteed. For the same reason, there are no methods in the AsynchronousFileChannel API that set or query the position, as there are in FileChannel.

In addition to the read and write methods, an asynchronous lock method is also supported, so that a file can be locked for exclusive access without having to block in the current thread (or poll using tryLock) if another thread currently holds the lock.

### Asynchronous channel groups

>Each asynchronous channel constructed belongs to a `channel group` that shares a pool of Java threads, which are used for handling the completion of initiated asynchronous I/O operations. 

This might sound like a bit of a cheat, because you could implement most of the asynchronous functionality yourself in Java threads to get the same behaviour, and you'd hope that NIO.2 could be implemented purely using the operating system's asynchronous I/O capabilities for better performance. However, in some cases, it's necessary to use Java threads: for instance, the completion-handler methods are guaranteed to be executed on threads from the pool.

By default, channels constructed with the **open()** methods belong to a global channel group that can be configured using the following system variables:

```java
AsynchronousServerSocketChannel.open(channelGroup);
AsynchronousSocketChannel.open(channelGroup);
```

Three utility methods in **java.nio.channels.AsynchronousChannelGroup** provide a way to create new channel groups:

```java
withCachedThreadPool()
withFixedThreadPool()
withThreadPool()
```

These methods take either the definition of the thread pool, given as a **java.util.concurrent.ExecutorService**, or a java.util.concurrent.ThreadFactory. For example, the following call creates a new channel group that has a fixed pool of 10 threads, each of which is constructed with the default thread factory from the Executors class:

```java
AsynchronousChannelGroup tenThreadGroup =
AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
```

Defining your own channel group allows finer control over the threads used to service the operations and also provides mechanisms for shutting down the threads and awaiting termination. Listing 3 shows an example:

```java
// first initiate a call that won't be satisfied
channel.accept(null, completionHandler);
// once the operation has been set off, the channel group can
// be used to control the shutdown
if (!tenThreadGroup.isShutdown()) {
    // once the group is shut down no more channels can be created with it
    tenThreadGroup.shutdown();
}
if (!tenThreadGroup.isTerminated()) {
    // forcibly shutdown, the channel will be closed and the accept will abort
    tenThreadGroup.shutdownNow();
}
// the group should be able to terminate now, wait for a maximum of 10 seconds
tenThreadGroup.awaitTermination(10, TimeUnit.SECONDS);
```

The **AsynchronousFileChannel** differs from the other channels in that, in order to use a custom thread pool, the **open()** method takes an **ExecutorService** instead of an **AsynchronousChannelGroup**.

### Asynchronous datagram channels and multicasting

The final new channel is the **AsynchronousDatagramChannel**. It's similar to the **AsynchronousSocketChannel** but worth mentioning separately because the NIO.2 API adds support for multicasting to the channel level, whereas in NIO it is only supported at the level of the **MulticastDatagramSocket**. The functionality is also available in **java.nio.channels.DatagramChannel** from Java 7.

An **AsynchronousDatagramChannel** to use as a server can be constructed as follows:

```java
AsynchronousDatagramChannel server = AsynchronousDatagramChannel.open().bind(null);
```

Next, we set up a client to receive datagrams broadcast to a multicast address. First, we must choose an address in the multicast range (from 224.0.0.0 to and including 239.255.255.255), and also a port that all clients can bind to:

```java
// specify an arbitrary port and address in the range
int port = 5239;
InetAddress group = InetAddress.getByName("226.18.84.25");
```

We also require a reference to which network interface to use:

```java
// find a NetworkInterface that supports multicasting
NetworkInterface networkInterface = NetworkInterface.getByName("eth0");
```

Now, we open the datagram channel and set up the options for multicasting, as shown in Listing 4:

```java
// the channel should be opened with the appropriate protocol family,
// use the defined channel group or pass in null to use the default channel group
AsynchronousDatagramChannel client =
    AsynchronousDatagramChannel.open(StandardProtocolFamily.INET,  tenThreadGroup);
// enable binding multiple sockets to the same address
client.setOption(StandardSocketOption.SO_REUSEADDR, true);
// bind to the port
client.bind(new InetSocketAddress(port));
// set the interface for sending datagrams
client.setOption(StandardSocketOption.IP_MULTICAST_IF, networkInterface);
```

The client can join the multicast group in the following way:

```java
MembershipKey key = client.join(group, networkInterface);
```

The **java.util.channels.MembershipKey** is a new class that provides control over the group membership. Using the key you can drop the membership, block and unblock datagrams from certain addresses, and return information about the group and channel.

The server can then send a datagram to the address and port for the client to receive, as shown in Listing 5:

```java
// send message
ByteBuffer message = ByteBuffer.wrap("Hello to all listeners".getBytes());
server.send(message, new InetSocketAddress(group, port));
 
// receive message
final ByteBuffer buffer = ByteBuffer.allocate(100);
client.receive(buffer, null, new CompletionHandler<SocketAddress, Object>() {
    @Override
    public void completed(SocketAddress address, Object attachment) {
        System.out.println("Message from " + address + ": " +
            new String(buffer.array()));
    }
 
    @Override
    public void failed(Throwable e, Object attachment) {
        System.err.println("Error receiving datagram");
        e.printStackTrace();
    }
});
```

Multiple clients can also be created on the same port and joined to the multicast group to receive the datagrams sent from the server.
