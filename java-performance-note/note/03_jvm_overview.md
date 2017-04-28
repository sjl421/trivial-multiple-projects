[1]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/HotSpot%20VM%20high%20level%20architecture.png
[2]:https://docs.oracle.com/javase/specs/jvms/se7/html/
[3]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/java%20memory%20model.png
[4]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/class%20loader.gif



JVM Overview
============

There are three major components of the HotSpot VM: `VM Runtime`, `JIT compiler`,
and a `memory manager`. This chapter begins with a high level architecture view of
the HotSpot VM followed by an overview of each of the three major components. In
addition, information on ergonomic decisions the HotSpot VM makes automatically
is included at the end of the chapter

## HotSpot VM High Level Architecture

The HotSpot VM possesses an architecture that supports a strong foundation of fea-
tures and capabilities. Its architecture supports the ability to realize high performance
and massive scalability.For example, the HotSpot VM JIT compilers generate dynamic
optimizations; in other words, it makes optimization decisions while the Java applica-
tion is running and generates high performing native machine instructions targeted
for the underlying system architecture.In addition, through its maturing evolution and
continuous engineering of its runtime environment and multithreaded garbage collector,
the HotSpot VM yields high scalability on even the largest computer systems available.

![HotSpot VM high level architecture][1]

As shown in Figure 3-1, the JIT compiler, client or server, is pluggable as is the
choice of garbage collector: `Serial GC`, `Throughput`, `Concurrent`, or `G1`.

**The HotSpot VM Runtime provides services and
common APIs to the HotSpot JIT compilers and HotSpot garbage collector**.
In addition the HotSpot VM Runtime provides basic functionality to the VM such as a
launcher, thread management, Java Native Interface, and so on.

## HotSpot VM Runtime

Not all the details presented in this section are necessary to realize a high 
performance Java application. However, it can be beneficial to have a basic 
understanding of the HotSpot VM Runtime since there may be cases where tuning 
a property of service provided by the VM Runtime may yield significant improvement 
in Java application performance.

The HotSpot VM Runtime encompasses many responsibilities, including `parsing
of command line arguments`, `VM life cycle`, `class loading`, `byte code interpreter`, `excep-
tion handling`, `synchronization`, `thread management`, `Java Native Interface`, `VM fatal
error handling`, and `C++ (non-Java) heap management`. In the following subsections,
each of these areas of the VM Runtime is described in more detail.


### Command Line Options

The HotSpot VM Runtime parses the many command line options and configures the
HotSpot VM based on those options. A number of command line options and environ-
ment variables can affect the performance characteristics of the HotSpot VM. Some
of these options are consumed by the HotSpot VM launcher such as the choice of JIT
compiler and choice of garbage collector; some are processed by the launcher and
passed to the launched HotSpot VM where they are consumed such as Java heap sizes.

There are three main categories of command line options: `standard options`,
`nonstandard options`, and `developer options`.

Standard command line options are
expected to be accepted by all Java Virtual Machine implementations as required by
the [Java Virtual Machine Specification][2]. Standard command line options are sta-
ble between releases. However, it is possible for standard command line options to be
deprecated in subsequent releases after the release in which it was first introduced.

**Nonstandard command line options begin with a `-X` prefix**. Nonstandard command
line options are not guaranteed to be supported in all JVM implementations, nor are
they required to be supported in all JVM implementations.

Developer command line options in the HotSpot VM begin with a `-XX` prefix.
Developer command line options often have specific system requirements for
correct operation and may require privileged access to system configuration param-
eters.

Command line options control the values of internal variables in the HotSpot
VM, all of which have a type and a default value.

For developer command line options (-XX options)
with boolean flags, a + or - before the name of the options indicates a true or false
value, respectively, to enable or disable a given HotSpot VM feature or option. For
example, `-XX:+AggressiveOpts` sets a HotSpot internal boolean variable to true to
enable additional performance optimizations. In contrast, `-XX:-AggressiveOpts`
sets the same internal variable to false to disable additional performance optimiza-
tions.Developer command line options (the -XX options) that take an additional
argument, those that are nonboolean, tend to be of the form, `-XX:OptionName=<N>`
where <N> is some numeric value. Almost all developer command line options that
take an additional argument, accept an integer value along with a suffix of `k`, `m`, or
`g`, which are used as `kilo-`, `mega-`, or `giga-` multipliers for the integer value specified.

### VM Life Cycle

>The HotSpot VM Runtime is responsible for launching the HotSpot VM and the shutdown of the HotSpot VM

**The component that starts the HotSpot VM is called the launcher**. There are
several HotSpot VM launchers. The most commonly used launcher is the java
command on Unix/Linux and on Windows the `java` and `javaw` commands.

It is also possible to launch an embedded JVM through the JNI interface, `JNI_CreateJavaVM`.

The launcher executes a sequence of operations to start the HotSpot VM. These
steps are summarized here:

>1. Parse command line options.
>>Some of the command line options are consumed immediately by the launcher such as `-client` or `–server`, which determines the JIT compiler to load. Other command line options are passed to the launched HotSpot VM.
>2. Establish the `Java heap sizes` and the `JIT compiler type` (client or server) if these options are not explicitly specified on the command line.
>>If Java heap sizes and JIT compiler are not explicitly specified as a command line option, these are ergonomically established by the launcher. Ergonomic defaults vary depending on the underlying system configuration and operating system. Ergonomic choices made by the HotSpot VM are described in more detail in the “HotSpot VM Adaptive Tuning” section later in this chapter.
>3. Establish environment variables such as `LD_LIBRARY_PATH` and `CLASSPATH`.
>4. If the Java `Main-Class` is not specified on the command line, the launcher fetches the `Main-Class` name from the `JAR’s manifest`.
>5. Create the HotSpot VM using the standard Java Native Interface method `JNI_CreateJavaVM` in a newly created nonprimordial thread.
>>In contrast to a nonprimordial thread, a primordial thread is the first thread allocated by an operating system kernel when a new process is launched. Hence, when a HotSpot VM is launched, the primordial thread is the first thread allocated by the operating system kernel running in the newly created HotSpot VM process. Creating the HotSpot VM in a nonprimordial thread provides the ability to customize the HotSpot VM such as changing the stack size on Windows. More details of what happens in the HotSpot VM’s implementation of `JNI_CreateJavaVM` are provided in the “JNI_CreateJavaVM Details” sidebar.
>6. Once the HotSpot VM is created and initialized, the Java Main-Class is loaded and the launcher gets the Java main method’s attributes from the Java `Main-Class`.
>7. The Java main method is invoked in the HotSpot VM using the Java Native Interface method `CallStaticVoidMethod` passing it the marshaled arguments from the command line.

Once a Java program, or Java main method completes its execution, the HotSpot
VM must check and clear any pending exceptions that may have occurred during
the program’s or method’s execution.Additionally, both the method’s exit status
and program’s exit status must be passed back to their caller’s.

The Java main method is detached from the HotSpot VM using the Java Native Interface method
`DetachCurrentThread`. When the HotSpot VM calls `DetachCurrentThread`, it
decrements the thread count so the Java Native Interface knows when to safely
shut down the HotSpot VM and to ensure a thread is not performing operations in
the HotSpot VM along with there being no active Java frames on its stack.

Specific details of the operations performed by the HotSpot VM’s Java Native Interface
method implementation of `DestroyJavaVM` is described in the “DestroyJavaVM
Details” sidebar.

### JNI_CreteJavaVM Details

>1. Ensure no two threads call this method at the same time and only one HotSpot VM instance is created in the process.
>>Because the HotSpot VM creates static data structures that cannot be reinitialized, only one HotSpot VM can be created in a process space once a certain point in initialization is reached. To the engineers who develop the HotSpot VM this stage of launching a HotSpot VM is referred to as the “point of no return.”
>2. Check to make sure the Java Native Interface version is supported, and the output stream is initialized for garbage collection logging.
>3. The OS modules are initialized such as the `random number generator`, `the current process id`, `high-resolution timer`, `memory page sizes`, and `guard pages`. Guard pages are no-access memory pages used to bound memory region accesses. For example, often operating systems put a guard page at the top of each thread stack to ensure references off the end of the stack region are trapped.
>4. The command line arguments and properties passed in to the `JNI_CreateJavaVM` method are parsed and stored for later use.
>5. The standard Java system properties are initialized, such as **java.version**, **java.vendor**, **os.name**, and so on.
>6. The modules for **supporting synchronization**, **stack**, **memory**, and **safepoint pages** are initialized
>7. Libraries such as `libzip`, `libhpi`, `libjava`, and `libthread` are loaded.
>8. Signal handlers are initialized and set.
>9. The thread library is initialized.
>10. The output stream logger is initialized.
>11. Agent libraries (`hprof`, `jdi`), if any are being used, are initialized and started.
>12. The thread states and the thread local storage, which holds thread specific data required for the operation of threads, are initialized.
>13. A portion of the HotSpot VM global data is initialized such as the event log, OS synchronization primitives, `perfMemory` (performance statistics memory), and `chunkPool` (memory allocator).
>14. At this point, the HotSpot VM can create threads. The Java version of the main thread is created and attached to the current operating system thread. However, this thread is not yet added to the known list of threads.
>15. Java level synchronization is initialized and enabled.
>16. `bootclassloader`, `code cache`, `interpreter`, `JIT compiler`, `Java Native Interface`, `system dictionary`, and `universe` are initialized.
>17. The Java main thread is now added to the known list of threads. The universe, a set of required global data structures, is sanity checked. The HotSpot VMThread, which performs all the HotSpot VM’s critical functions, is created. At this point the appropriate JVMTI events are posted to notify the current state of the HotSpot VM.
>18. The following Java classes `java.lang.String, java.lang.System, java.lang.Thread, java.lang.ThreadGroup, java.lang.reflect.Method, java.lang.ref.Finalizer, java.lang.Class, and the rest of the Java System classes are loaded and initialized`. At this point, the HotSpot VM is initialized and operational, but not quite fully functional.
>19. The HotSpot VM’s signal handler thread is started, the JIT compiler is initialized, and the HotSpot’s compile broker thread is started. Other HotSpot VM helper threads such as watcher threads and stat sampler are started. At this time the HotSpot VM is fully functional
>20. Finally, the `JNIEnv` is populated and returned to the caller and the HotSpot VM is ready to service new JNI requests.

### DestroyJavaVM Details

The `DestroyJavaVM` method can be called from the HotSpot launcher to shut down
the HotSpot VM when errors occur during the HotSpot VM launch sequence. The
DestroyJavaVM method can also be called by the HotSpot VM during execution, after
the HotSpot VM has been launched, when a very serious error occurs.

>1. Wait until there is only one nondaemon thread executing noting that the HotSpot VM is still functional.
>2. Call the Java method `java.lang.Shutdown.shutdown()`, which invokes the Java level shutdown hooks and runs Java object finalizers if finalization-on-exit is true.
>3. Prepare for HotSpot VM exit by running HotSpot VM level shutdown hooks (those that were registered through `JVM_OnExit()`), stop the following HotSpot VM threads: profiler, stat sampler, watcher, and garbage collector threads. Post status events to JVMTI, disable JVMTI, and stop the Signal thread.
>4. Call the HotSpot method `JavaThread::exit()` to release Java Native Interface handle blocks, remove guard pages, and remove the current thread from known threads list. From this point on the HotSpot VM cannot execute any Java code.
>5. Stop the HotSpot VM thread. This causes the HotSpot VM to bring the remaining HotSpot VM threads to a safepoint and stop the JIT compiler threads.
>6. Disable tracing at the Java Native Interface, HotSpot VM, and JVMTI barriers.
>7. Set HotSpot “`vm exited`” flag for threads that may be running in native code.
>8. Delete the current thread.
>9. Delete or remove any input/output streams and release `PerfMemory` (performance statistics memory) resources.
>10. Finally return to the caller.

### VM Class Loading

The HotSpot VM and Java SE
`class loading` libraries share the responsibility for class loading. The HotSpot VM
is responsible for **resolving constant pool symbols**, that require loading, linking,
and then initializing Java classes and Java interfaces. **The term class loading
is used to describe the overall process of mapping a class or interface name to a
class object**, and the more specific terms `loading`, `linking`, and `initializing` for the
phases of class loading as defined by the Java Virtual Machine Specification

The most common reason for class loading is during bytecode resolution, when a 
constant pool symbol in a Java classfile requires resolution.Java APIs such as 
`Class.forName()`, `ClassLoader.loadClass()`, `reflection APIs`, and `JNI_FindClass`
can initiate class loading. The HotSpot VM itself can also initiate class loading.

The HotSpot VM loads core classes such as `java.lang.Object` and `java.lang.
Thread` along with many others at HotSpot VM startup time. **Loading a class
requires loading all Java superclasses and all Java superinterfaces**. And classfile
verification, which is part of the linking phase, can require loading additional
classes. The loading phase is a cooperative effort between the HotSpot VM and
specific class loaders such as java.lang.ClassLoader.

#### Class Loading Phases

For a given Java class or Java interface, **the load class phase takes its name, finds
the binary in Java classfile format, defines the Java class, and creates a java.lang.
Class object** to represent that given Java class or Java interface.

The load class
phase can throw a `NoClassDefFound` error if a binary representation of a Java
class or Java interface cannot be found. In addition, the load class phase does format
checking on the syntax of the classfile, which can throw a `ClassFormatError` or
`UnsupportedClassVersionError`.

Before completing the load of a Java class, the
HotSpot VM must load all its superclasses and superinterfaces. If the class hierarchy
has a problem such that a Java class is its own superclass or superinterface (recur-
sively), then the HotSpot VM throws a `ClassCircularityError`. The HotSpot VM
also throws an `IncompatibleClassChangeError` if the direct superinterface is not
an interface, or the direct superclass is an interface.

The link phase first does verification, which checks the classfile semantics,
checks the constant pool symbols, and does type checking. These checks can throw a
`VerifyError`. Linking then does what is called preparation, which **creates and
initializes static fields to standard defaults and allocates method tables**. It is
worth noting at this point of execution no Java code has yet been run. The link
class phase then optionally does resolution of symbolic references. 

Next, **class initialization runs the class static initializers, and initializers for 
static fields**. This is the first Java code that runs for this class. It is important to 
note that **class initialization requires superclass initialization, although not 
superinterface initialization**.

The `Java Virtual Machine` Specification specifies that class initialization occurs on
the first active use of a class. However, the `Java Language Specification` allows flex-
ibility in when the symbolic resolution step of linking occurs as long as the seman-
tics of the language are held; the JVM finishes each step of loading, linking, and
initializing before performing the next step; and throws errors when Java programs
would expect them to be thrown. As a performance optimization, the HotSpot VM
generally waits until class initialization to load and link a class. This means if class
A references class B, loading class A will not necessarily cause loading of class B
(unless class B is required for verification). Execution of the first instruction that
references class B causes the class initialization of B, which requires loading and
linking of class B.

#### Class Loader Delegation

When a class loader is asked to find and load a class, it can ask another class loader
to do the loading. This is called `class loader delegation`. The first class loader is an
`initiating class loader`, and the class loading that ultimately defines the class is
called the `defining class loader`. In the case of bytecode resolution, the initiating
class loader is the class loader for the class whose constant pool symbol is being
resolved.

Class loaders are defined hierarchically and each class loader has a delegation
parent. The delegation defines a search order for binary class representations. **The
Java SE class loader hierarchy searches the bootstrap class loader, the extension
class loader, and the system class loader in that order**. The system class loader is
the default application class loader, which loads the main Java method and loads
classes from the classpath. The application class loader can be a class loader
from the Java SE class loader libraries, or it can be provided by an applica-
tion developer. The Java SE class loader libraries implement the extension class
loader, which loads classes from the lib/ext directory of the JRE (Java Runtime
Environment).

![class loader][4]

#### Bootstrap Class Loader

The HotSpot VM implements the bootstrap class loader. The bootstrap class loader
loads classes from the HotSpot VM’s `BOOTCLASSPATH`, including for example
rt.jar, which contains the Java SE class libraries.For faster startup, the Client
HotSpot VM can also process preloaded classes via a feature called `class data shar-
ing`, which is enabled by default. It can be explicitly enabled with the -Xshare:on
HotSpot VM command line switch. Likewise, it can be explicitly disabled with
-Xshare:off. 

#### Type Safety

A Java class or Java interface name is defined as a fully qualified name, which
includes the package name. A Java class type is **uniquely determined by that
fully qualified name and the class loader**. In other words, a class loader defines a
namespace. This means the same fully qualified class name loaded by two distinctly
defined class loaders results in two distinct class types. Given the existence of custom
class loaders, the HotSpot VM is responsible for ensuring that non-well-behaved class
loaders cannot violate type safety.

#### Class Metadata in HotSpot

Class loading in the HotSpot VM creates an internal representation of a class in
either an `instanceKlass` or an `arrayKlass` in the HotSpot VM’s permanent generation
space.The instanceKlass
refers to a Java mirror, which is the instance of `java.lang.Class` mirroring this
class. The HotSpot VM internally accesses the instanceKlass using an internal
data structure called a `klassOop`. An “Oop” is an ordinary object pointer. Hence, a
klassOop is an internal HotSpot abstraction for a reference, an ordinary object
pointer, to a Klass representing or mirroring a Java class.

#### Internal Class Loading Data

The HotSpot VM maintains three hash tables to track class loading. 

The `SystemDictionary` contains loaded classes, which maps a class name/class loader pair to a `klassOop`.
The `SystemDictionary` contains both **class name/initiating loader**
pairs and **class name/defining loader pairs**.Entries are currently only removed at a
`safepoint`.

The `PlaceholderTable` contains classes that
are currently being loaded. It is used for ClassCircularityError checking and
for parallel class loading for class loaders that support multithreaded class loading.

The `LoaderConstraintTable` tracks constraints for type safety checking. 
These hash tables are all guarded by a lock; in the HotSpot VM it is called the 
`SystemDictionary_lock`. In general, the load class phase in the HotSpot VM is serialized
using the Class loader object lock.

### Byte Code Verification

The Java language is a type-safe language, and standard Java compilers (javac)
produce valid classfiles and type-safe code; but a Java Virtual Machine cannot guar-
antee that the code was produced by a trustworthy javac compiler. It must reestab-
lish type-safety through a process at link time called bytecode verification.


