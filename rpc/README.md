# 实现一个简单的RPC框架

参考了黄勇大神的这个项目, [黄勇-分布式RPC框架](http://git.oschina.net/huangyong/rpc)

我做了一些修改,修复了一部分无法因为Spring API变动引起的无法编译的问题;

## RPC框架的需求

![dubbo架构](http://hangyudu.oss-cn-shanghai.aliyuncs.com/sundries/dubbo.jpg)

对于一个基本的RPC框架来讲,其实我们需要的只有三个部分:
	- server:对应dubbo中的provider;
	- client:对应dubbo中的consumer;
	- registry:对应dubbo中的registry;

### Registry端

Registry 需要提供的两个基本功能是:
	- 服务注册功能,服务端使用该功能向其注册服务;
	- 服务发现功能,客户端使用该功能向其发现服务;

而为了更加方便的扩展,我们应当将这个功能声明为一个接口,放在单独的maven项目 `rpc-registry` 下,这样就可以根据需求进行不同的定制了,黄勇大大提供的实现是通过 zookeeper (在 `rpc-registry-zookeeper`下)实现的服务注册中心;

### Server端

Server端应该提供的基本功能为:
	- 发布服务(使用 Registry 端提供的接口 `register()`,暴露服务的地址);
	- 处理RPC请求,并向客户端发送返回值;

### Client端

Client端应该提供的基本功能为:
	- 发现服务(使用 Registry 端提供的接口 `discovery()`,查找服务地址);
	- 向服务器发送RPC请求,并处理服务端的返回值;

### Common端

Server端和Client端会有通信,而通信就必须通过Common端提供的RpcRequest和RpcResponse来实现;

### 几个实例

rpc-sample-api,rpc-sample-client,rpc-sample-server 这三个就是一个简单实例;

- `rpc-sample-api`:这个包声明的接口规范了服务端需要实现的接口和客户端需要调用的接口;
- `rpc-sample-server`:这个包实现 `rpc-sample-api` 声明的接口,并将服务注册到 `registry`中;
- `rpc-sample-client`:这个包每次调用 `rpc-sample-api` 中的方法时,将自动的在 `RpcProxy` 声明的地址中,根据方法的名字去进行RPC调用;
