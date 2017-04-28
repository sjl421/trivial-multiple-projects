[1]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/gnome%20system%20monitor%20on%20linux.png
[2]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/no%20swapping.png
[3]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/do%20swapping.png
[4]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/nicstat.png




Operating System Performance Monitoring
=======================================

>The first step in isolating a performance issue is to monitor the application’s behavior. Monitoring offers clues as to the type or general category of performance issue.

This chapter begins by presenting definitions for performance monitoring, performance profiling, and performance tuning.

## Definitions

Three distinct activities are involved when engaging in performance improvement activities: `performance monitoring`, `performance profiling`, and `performance tuning`.

- Performance monitoring is an act of nonintrusively collecting or observing performance data from an operating or running application. Monitoring is usually a preventative or proactive type of action and is usually performed in a production environment, qualification environment, or development environment. Monitoring is also usually the first step in a reactive situation where an application stakeholder has reported a performance issue but has not provided sufficient information or clues as to a potential root cause. In this situation, performance profiling likely follows performance monitoring.
- Performance profiling in contrast to performance monitoring is an act of collecting performance data from an operating or running application that may be intrusive on application responsiveness or throughput. Performance profiling tends to be a reactive type of activity, or an activity in response to a stakeholder reporting a performance issue, and usually has a more narrow focus than performance monitoring. Profiling is rarely done in production environments. It is typically done in qualification, testing, or development environments and is often an act that follows a monitoring activity that indicates some kind of performance issue.
- Performance tuning, in contrast to performance monitoring and performance profiling, is an act of changing tune-ables, source code, or configuration attribute(s) for the purposes of improving application responsivenessor throughput. Performance tuning often follows performance monitoring or performance profiling activities.

## CPU Utilization

For an application to reach its highest performance or scalability it needs to not only
take full advantage of the CPU cycles available to it but also to utilize them in a
manner that is not wasteful. Being able to make efficient use of CPU cycles can be
challenging for multithreaded applications running on multiprocessor and multicore
systems.

**Additionally, it is important to note that an application that can saturate
CPU resources does not necessarily imply it has reached its maximum performance
or scalability.**

CPU utilization on most operating systems is reported in both `user CPU utilization` and `kernel or system (sys) CPU utilization`.

- `User CPU utilization` is the percent of time the application spends in application code
- `kernel or system CPU utilization` is the percent of time the application spends executing operating system kernel code on behalf of the application

High kernel or system CPU utilization can be an indication of shared resource contention
or a large number of interactions between I/O devices. **The ideal situation for maxi-
mum application performance and scalability is to have 0% kernel or system CPU
utilization** since CPU cycles spent executing in operating system kernel code are
CPU cycles that could be utilized by application code. Hence, one of the objectives to
achieving maximum application performance and scalability is to reduce kernel or
system CPU utilization as much as possible.

### Monitoring CPU Utilization on Linux

On Linux, CPU utilization can be monitored graphically with the GNOME System
Monitor tool, which is launched with the `gnome-system-monitor` command.

The GNOME System Monitor shown in Figure 2-4 is running on a system with two
virtual processors.The number of virtual processors matches the number returned
by the Java API `Runtime.availableProcessors()`.

![GNOME System Monitor on Linux][1]

Another popular graphical tool to monitor CPU utilization on Linux is `xosview`.

### Monitoring CPU Utilization on linux and Solaris with Command Line Tools

Linux and Solaris also provide command line tools to monitor CPU utilization. These
command line tools are useful when you want to keep a running textual history of
CPU utilization or keep a log of CPU utilization. Linux and Solaris have `vmstat`,
which shows combined CPU utilization across all virtual processors.

>vmstat - Report virtual memory statics
>>`vmstat` reports information about processes, memory, paging, block IO, traps, disks and cpu activity.
>>The first report produced gives averages since the last reboot.Additional reports give information on a sampling period of length _delay_. The process and memory reports are instantaneous in either case.

```
procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
 1  0      0 851844 219364 2948472   0    0    38    42  281  726  3  2 96  0  0
```

- linux process status
	- R(TASK_RUNNING)
	- S(TASK_INTERRUPTIBLE)
	- D(TASK_UNINTERRUPTIBLE)
	- T(TASK_STOPPED or TASK_TRACED)
	- Z(TASK_DEAD - EXIT_ZOMBIE)

- Procs
	- r: The number of runnable processes (running or waiting for run time).
	- b: The number of processes in uninterruptible sleep(blocking status).
- Memory
	- swpd: the amount of virtual memory used.
	- free: the amount of idle memory.
	- buff: the amount of memory used as buffers.
	- cache: the amount of memory used as cache.
	- inact: the amount of inactive memory. (-a option)
	- active: the amount of active memory. (-a option)
- Swap
	- si: Amount of memory swapped in from disk (/s).
	- so: Amount of memory swapped to disk (/s).
- IO
	- bi: Blocks received from a block device (block/s).
	- bo: Blocks sent to a block device (block/s).
- System
	- in: The number of interrupts per second, including the clock.
	- cs: The number of context switches per second.
- CPU : These are percentages of total CPU time
	- us: Time spent running non-kernel code.(user time, including nice time)
	- sy: Time spent running kernel code.(sytem time)
	- id: Time spent idle, this include IO-wait time.
	- wa: Time spent waiting for IO, includ in idle.
	- st: Time stolen from a virtual machine.

Linux `top` reports not only CPU utilization but also process statistics and memory
utilization. Its display, shown in the following example, has two major parts. The
upper section of the display reports overall system statistics, while the lower section
reports process level statistics that, by default, are ordered in highest to lowest CPU
utilization.

## CPU Scheduler Run Queue

In addition to CPU utilization, monitoring the CPU scheduler’s run queue is impor-
tant to tell if the system is being saturated with work. **The run queue is where light-
weight processes are held that are ready to run but are waiting for a CPU where it
can execute.**

The number of virtual processors is the number of hardware threads on the system. 
It is also the value returned by the Java API, `Runtime.availableProcessors()`.

A general guideline to follow is observing run queue depths over an extended
period of time greater than 1 times the number of virtual processors is something to
be concerned about but may not require urgent action. Run queue depths at 3 to 4
times, or greater, than the number of virtual processors over an extended time period
should be considered an observation that requires immediate attention or action.

There are generally two alternative resolutions to observing high run queue depth.
One is to acquire additional CPUs and spread the load across those additional CPUs,
or reduce the amount of load put on the processors available. This approach essen-
tially reduces the number of active threads per virtual processor and as a result
fewer lightweight processes build up in the run queue.

The other alternative is to profile the applications being run on the system and
improve the CPU utilization of those applications. In other words, explore alterna-
tive approaches that will result in fewer CPU cycles necessary to run the applica-
tion such as reducing garbage collection frequency or alternative algorithms that
result in fewer CPU instructions to execute the same work. Performance experts
often refer to this latter alternative as reducing code path length and better CPU
instruction selection.

A Java programmer can realize better performance through
choosing more efficient algorithms and data structures. The JVM, through a mod-
ern JIT compiler, can improve an application’s performance by generating code that
includes sophisticated optimizations. Since there is little a Java application pro-
grammer can do to manipulate a JVM’s JIT compiler, the focus for Java developers
should be on more efficient alternative algorithms and data structures. Where to
focus with alternative algorithms and data structures is identified through profil-
ing activities.

### Monitoring Linux CPU Scheduler Run Queue

On Linux a system’s run queue depth can be monitored using the `vmstat` command.
The first column in vmstat reports the run queue depth. The number reported is the
actual number of lightweight processes in the run queue. 

## Memory Utilization

In addition to CPU utilization there are attributes of a system’s memory that should
be monitored, such as **paging or swapping activity, locking, and voluntary and invol-
untary context switching along with thread migration activity**.

A JVM’s garbage collector performs poorly on systems that are swapping because
a large portion of memory is traversed by the garbage collector to reclaim space from
objects that are unreachable.If part of the Java heap has been swapped out it must
be paged into memory so its contents can be scanned for live objects by the garbage
collector.The time it takes to page in any portion of the Java heap into memory can
dramatically increase the duration of a garbage collection. If the garbage collection
is a “stop the world” type of operation, one that stops all application threads from
executing, a system that is swapping during a garbage collection is likely to experi-
ence lengthy JVM induced pause times.

If you observe lengthy garbage collections, it is a possibility that the system is
swapping. To prove whether the lengthy garbage collection pauses are caused by
swapping, you must monitor the system for swapping activity.

### Monitoring Memory Utilization on Linux

On Linux, monitoring for swapping activity can be done using `vmstat` and observing
the free column. There are other ways to monitor for swap activity on Linux such
as using the `top` command or observing the contents of the file `/proc/meminfo`.
The columns in Linux vmstat to monitor are the “`si`” and “`so`” columns, 
which represent the amount of memory paged-in and the amount of memory paged-out.
In addition, the “`free`” column reports the amount of available free memory.

The following is an example of a system that is experiencing no swapping activity; since
there is no paging activity as shown in the “si” and “so” columns and the amount of
free memory is not very low.

![no swapping][2]

However, the following vmstat output from a Linux system illustrates a system that
is experiencing swapping.

![do swapping][3]

Notice the pattern in this example. Where free memory initially decreases, there
is little paging activity shown in either the “si” column or “so” column.
But as free memory reaches values in the 155,000 – 175,000 range, page-out activity picks
up as shown in the “so” column. Once the page-out activity begins to plateau, the
page-in activity begins and increases rather quickly as shown in the “si” column.

## Monitoring Lock Contention

Many Java applications that do not scale suffer from lock contention. Identifying
that lock contention in Java applications can be difficult and the tools to identify
lock contention are limited.

In addition, optimizations have been made in modern JVMs to improve the per-
formance of applications that experience lock contention.

### Monitoring Lock Contention on Linux

It is possible to monitor lock contention by observing thread context switches in
Linux with the `pidstat` command from the sysstat package.

```bash
sudo apt-get install sysstat
```

>pidstat - Report statistics for Linux tasks.
>>The  pidstat command is used for monitoring individual tasks currently being managed by the Linux kernel.  It writes to standard output activities for every task selected with  option -p  or  for  every  task  managed  by  the Linux kernel if option -p ALL has been used. Not selecting any tasks is equivalent to specifying -p ALL but only active  tasks  (tasks  with non-zero statistics values) will appear in the report.

- The use of pidstat -w reports voluntary context switches in a “cswch/s” column.
	- cswch/s Total Number of voluntary context switches the task made per second.
	- nvcswch/s Total Number of non voluntary context switches the task made per second.

```bash
pidstat -w -p 2148
```

```
Linux 4.8.0-46-generic (dhy) 	2017年04月15日 	_x86_64_	(4 CPU)

15时31分33秒   UID       PID   cswch/s nvcswch/s  Command
15时31分33秒  1000      2148    145.08      7.78  chrome
```

As a result, the number of voluntary context switches
times 80,000 divided by the number of clock cycles per second of the CPU provides
the percentage of CPU clock cycles spent in voluntary context switches. The follow-
ing is an example from pidstat -w monitoring a Java application having a process
id of 9391 reporting results every 5 seconds.

```
$pidstat -w -I -p 9391 5
Linux 4.8.0-46-generic (dhy) 	2017年04月15日 	_x86_64_	(4 CPU)

15时31分33秒   UID       PID   cswch/s nvcswch/s  Command
15时31分33秒  1000      9391    3645      322      java
15时31分33秒  1000      9391    3512      292      java
15时31分33秒  1000      9391    3499      310      java
```

To estimate the percentage of clock cycles wasted on context switching, there are
about 3500 context switches per second occurring on the system being monitored
with pidstat -w, a 3.0GHz dual core Intel CPU. Hence, 3500 divided by 2, the num-
ber of virtual processors = 1750. 1750 * 80,000 = 140,000,000. The number of clock
cycles in 1 second on a 3.0GHz processor is 3,000,000,000. Thus, the percentage of
clock cycles wasted on context switches is 140,000,000/3,000,000,000 = 4.7%. Again
applying the general guideline of 3% to 5% of clock cycles spent in voluntary context
switches implies a Java application that may be suffering from lock contention.

### Isolating Hot Locks

Tracing down the location in Java source code of contended locks has historically
been a challenge. A common practice to find contended locks in a Java application
has been to periodically take thread dumps and look for threads that tend to be
blocked on the same lock across several thread dumps. An example of this procedure
is presented in Chapter 4, “JVM Performance Monitoring.”

### Monitoring Involuntary Context Switches

High involuntary context switches are an indication there are more threads ready
to run than there are virtual processors available to run them.
As a result it is common to observe a high run queue depth in `vmstat`, high CPU 
utilization, and a highnumber of migrations in conjunction
with a large number of involuntary context switches.

### Monitoring Thread Migrations

Migration of ready-to-run threads between processors can also be a source of observed
performance degradation. Most operating systems’ CPU schedulers attempt to keep
a ready-to-run thread on the same virtual processor it last executed. If that same
virtual processor is busy, the scheduler may migrate that ready-to-run thread to
some other available virtual processor. Migration of threads can impact an applica-
tion’s performance since data, or state information, used by a ready-to-run thread
may not be readily available in a virtual processor’s cache.

## Network I/O Utilization

Distributed Java applications may find performance and scalability limited to either
network bandwidth or network I/O performance. For instance, if a system’s network
interface hardware is sent more traffic than it can handle, messages can be queued
in operating system buffers, which may cause application delays. Additionally, other
things may be occurring on the network that cause delays as well.

### Monitoring Network I/O Utilization on Linux

a tool called `nicstat` from the freeware K9Toolkit reports network uti-
lization and saturation by network interface. The K9Toolkit is also included in the
Solaris Performance Tools CD 3.0 package mentioned earlier in the “Monitoring CPU
Utilization on Solaris” section of this chapter. The K9Toolkit can also be downloaded
from http://www.brendangregg.com/k9toolkit.html.

```bash
nicstat [-hnsz] [-i interface [,...]] | [interval [count]]
```

where -h displays a help message, -n shows nonlocal interfaces only, -s shows a
summary output, -z skips reporting of zero values, -i interface is the network inter-
face device name, interval is the frequency at which output is to be reported in sec-
onds, and count is the number of samples to report.

```bash
nicstat -i yukonx0 1
```

![nicstat output][4]

- The column headings are
	- Int is the network interface device name.
	- rKb/s is the number of kilobytes read per second.
	- wKb/s is the number of kilobytes written per second.
	- rPk/s is the number of packets read per second.
	- wPk/s is the number of packets written per second.
	- rAvs is average bytes read per read.
	- wAvs is the average bytes written per write.
	- %Util is the network interface utilization.
	- Sat is the saturation value.

As you can see a wealth of meaningful data is presented with nicstat to help
you identify whether your distributed Java application is saturating the network.
You can see there is activity occurring at the yukonx0 network interface as shown
in the number of bytes read and written yet the network utilization never reaches
much above 4% utilization. As a result, you can conclude the applications running
on this system are not experiencing a performance issue as a result of a saturated
network.

### Application Performance Improvement Considerations

An application executing a large number of reads and writes to a network with small
amounts of data in each individual read or write call consumes large amounts of
system or kernel CPU and may also report a high number of system calls. A strat-
egy to reduce system or kernel CPU in such an application is to reduce the number
network read or write system calls. 

Additionally, the use of nonblocking Java NIO
instead of blocking java.net.Socket may also improve an application’s perfor-
mance by reducing the number of threads required to process incoming requests or
send outbound replies.

A strategy to follow when reading from a nonblocking socket is to design and
implement your application to read as much data as there is available per read call.

## Disk I/O Utilization

If an application performs disk operations, disk I/O should be monitored for possible
performance issues. Some applications make heavy use of disk as a major part of its
core functionality such as databases, and almost all applications utilize an application
log to write important information about the state or behavior of the application as
events occur.Disk I/O utilization is the most useful monitoring statistic for under-
standing application disk usage since it is a measure of active disk I/O time. Disk
I/O utilization along with system or kernel CPU utilization can be monitored using
`iostat` on Linux and Solaris.

>iostat - Report CPU statistics and input/output statistics for devices and partitions
>> The `iostat` command is used for monitoring system input/output devices loading by observing the time the devices are active in relation to their average transfer rates.

```
Linux 4.8.0-46-generic (dhy) 	2017年04月15日 	_x86_64_	(4 CPU)

avg-cpu:  %user   %nice %system %iowait  %steal   %idle
           2.38    0.04    0.97    0.36    0.00   96.26

Device:         rrqm/s   wrqm/s     r/s     w/s    rkB/s    wkB/s avgrq-sz avgqu-sz   await r_await w_await  svctm  %util
sda               2.57     4.82   50.91    2.74  1264.80    42.15    48.72     0.05    0.96    0.73    5.05   0.23   1.25
sdb               0.00     0.00    0.40    0.00    14.97     0.00    74.28     0.00    4.14    4.14    0.00   1.92   0.08
```

- avg-cpu
	- %user   : Show the percentage of CPU utilization that occurred while executing at the user level (application).
	- %nice   : Show the percentage of CPU utilization that occurred while executing at the user level with nice priority.
	- %system : Show the percentage of CPU utilization that occurred while executing at the system level (kernel).
	- %iowait : Show the percentage of time that the CPU or CPUs were idle during which the system had an outstanding disk I/O request.
	- %steal  : Show the percentage of time spent in involuntary wait by the virtual CPU or CPUs while the hypervisor was servicing another virtual processor.
	- %idle   : Show the percentage of time that the CPU or CPUs were idle and the system did not have an outstanding disk I/O request.

- Device : this columns gives the device(or partition) name as listed in the /dev directory
- rrqm/s : The number of read requests merged per second that were queued to the device.
- wrqm/s : The number of write requests merged per second that were queued to the device.
- r/s    : The number (after merges) of read requests completed per second for the device.
- w/s    : The number (after merges) of write requests completed per second for the device.
- rsec/s : (rkb/s, rMB/s) The number of sectors (kilobytes, megabytes) read from the device per second.
- wsec/s : (wkB/s, wMB/s) The number of sectors (kilobytes, megabytes) written to the device per second.
- avgrq-sz : The average size (in sectors) of the requests that were issued to the device.
- avgqu-sz : The average queue length of the requests that were issued to the device.
- await    : The average time (in milliseconds) for I/O requests issued to the device to be served. This includes the time spent by the requests in queue and the time spent servicing them.
- r_await  : The average time (in milliseconds) for read requests issued to the device to be served. This includes the time spent by the requests in queue and the time spent servicing them.
- w_await  : The average time (in milliseconds) for write requests issued to the device to be served. This includes the time spent by the requests in queue and the time spent servicing them.
- %utl     : Percentage of elapsed time during which I/O requests were issued to the device (bandwidth utilization for the device). Device saturation occurs when this value is close to  100% for devices serving requests serially.  But for devices serving requests in parallel, such as RAID arrays and modern SSDs, this number does not reflect their performance limits.

if high disk I/O utilization is observed with an application, it may
be worthwhile to further analyze the performance of your system’s disk I/O subsys-
tem by looking more closely at its expected workload, disk service times, seek times,
and the time spent servicing I/O events. If improved disk utilization is required,
several strategies may help. At the hardware and operating system level any of the
following may improve disk I/O utilization:

- A faster storage device
- Spreading file systems across multiple disks
- Tuning the operating system to cache larger amounts of file system data structures

At the application level any strategy to minimize disk activity will help such as
reducing the number of read and write operations using buffered input and output
streams or integrating a caching data structure into the application to reduce or
eliminate disk interaction. The use of buffered streams reduces the number of sys-
tem calls to the operating system and consequently reduces system or kernel CPU
utilization. It may not improve disk I/O performance, but it will make more CPU
cycles available for other parts of the application or other applications running on the
system. Buffered data structures are available in the JDK that can easily be utilized,
such as `java.io.BufferedOutputStream` and `java.io.BufferedInputStream`.
