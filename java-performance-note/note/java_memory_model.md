[1]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/java%20memory%20model.png
[2]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/runtime%20data%20areas.png
[3]:http://stackoverflow.com/questions/24427056/what-is-an-operand-stack


JVM中的Stack和Frame
===================

JVM执行Java程序时需要装载各种数据，比如类型信息（Class）、类型实例（Instance）、常量数据（Constant）、本地变量等。不同的数据存放在不同的内存区中，这些数据内存区称作“运行时数据区（Runtime Data Area）”。运行时数据区有这样几个重要区：

- JVM Stack（简称Stack或者虚拟机栈、线程栈、栈等）
- Frame（又称StackFrame/栈帧、方法栈等）
- Heap（堆/GC堆，即垃圾收集的对象所在区）。

## 概览

### 线程的内存模型

- 单个线程共享的区 : PC Register/JVM Stack/Native Method Stack;
- 所有线程共享的区 : Heap/Method Area/Runtime Constant Pool;
	
![Runtime Data Areas][2]


### 应用的内存模型

![jvm memory model][1]

- Program Counter就是所谓的程序计数器,对应于 Pc Register;
- Stack 对应 JVM Stack, Native Stack 对应 Native Method Stack;
- Stack 包含 Frame.
- Frame 包含了Return value(返回值),Local Variables(本地变量),[Operand Stack][3],Current Class Constant Pool Reference;

## Stack

- Stack 在每个线程被创建时创建,用来存放一组栈帧(Frame);
- Stack 的大小可以是固定的,也可以是动态扩展的.

## Frame

每次方法调用均会创建一个对应的Frame,方法执行完毕或者异常终止后Frame被销毁.

## JVM

- JVM
	- Heap(堆,所以线程共享)
		- 老年代
		- 新生代
			- Eden
			- From
			- To
		- 堆JVM调优参数
			- -Xms 堆的最小值,堆内存越大就越不容易发生Full GC,越小垃圾回收越平凡,增加垃圾回收总时间,减少吞吐量;
			- -Xmx 堆的最大值
			- -Xmn 设置新生代的大小
			- -XX:NewSize 设置新生代的大小
			- -XX:NewRatio 设置老年代和新生代的比例
			- -XX:SurviorRatio 新生代中的eden与survivor区的比例
			- -XX:TargetSurvivorRatio 设置survivor区的可使用率,达到此值被送往老年代.
	- Non_Heap(所有线程共享,也称为持久代)
		- -XX:PermSize 初始持久区大小
		- -XX:MaxPermSize 最大持久区大小
	- Stack
		- -Xss 设置线程栈的大小,线程栈的内存越大,能创建的线程数就越小
	- GC
		- 垃圾回收算法
			- <del>引用计数法(无法处理循环引用的问题,不适用于JVM的垃圾回收)</del>
			- 标记清除算法,容易产生内存碎片,对大对象的内存分配,由于内存碎片的存在,分配效率降低;
			- 复制算法,内存折半,内存利用率地;
			- 标记-压缩算法,效率低,可能有卡顿;
			- 增量算法,让垃圾回收线程和用户线程能够交替的执行,其他的算法都会 stop-the-world;
			- 分代,新生代使用复制算法,老年代采用标记-压缩算法.
		- 垃圾收集器
			- Serial : 串行收集器,可能会产生较长时间的停顿,只使用一个线程去回收;
			- ParNew : Serial 的多线程版本;
			- Parallel Scavenge : 类似于ParNew,但是是并行的,它更关注系统的吞吐量;
			- Parallel Old : Parallel Scavenge 的老年代版本,使用多线程和 "标记-压缩"算法;
			- CMS : Concurrent Mark Sweep以一种获取最短停顿时间作为目标的收集器;
			- G1 : Garbage-First 一款面向服务器的垃圾收集器,主要针对配备多颗处理器及大容量内存的机器,以极高高绿满足GC停顿时间要求的同事,还具备高吞吐量性能特征;
