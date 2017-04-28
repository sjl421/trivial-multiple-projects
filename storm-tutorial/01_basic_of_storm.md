# Basic of Storm

## [Concepts](http://storm.apache.org/releases/1.1.0/Concepts.html)

This page lists the main concepts of Storm and links to resources where you can find more information. The concepts discussed are:

1. Topologies
2. Streams
3. Spouts
4. Bolts
5. Stream groupings
6. Reliability
7. Tasks
8. Workers

### Topologies

The logic for a realtime application is packaged into a Storm topology. A Storm topology is analogous to a MapReduce job. **One key difference is that a MapReduce job eventually finishes, whereas a topology runs forever** (or until you kill it, of course). A topology is a graph of spouts and bolts that are connected with stream groupings. These concepts are described below.

- Resources:
	- [TopologyBuilder](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/TopologyBuilder.html): use this class to constructor topologies in Java.
	- [Running topologies on a production cluster](http://storm.apache.org/releases/1.1.0/Running-topologies-on-a-production-cluster.html)
	- [Local mode](http://storm.apache.org/releases/1.1.0/Local-mode.html):Read this to learn how to develop and test topologies in local mode.

#### TopologyBuilder

TopologyBuilder exposes the Java API for specifying a topology for Storm to execute. Topologies are Thrift structures in the end, but since the Thrift API is so verbose, TopologyBuilder greatly eases the process of creating topologies. The template for creating and submitting a topology looks something like:

```java
public class TopologyBuilderExample {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("1", new TestWordSpout(true), 5);
        builder.setSpout("2", new TestWordSpout(true), 3);
        builder.setBolt("3", new TestWordCounter(), 3)
                .fieldsGrouping("1", new Fields("word"))
                .fieldsGrouping("2", new Fields("word"));
        builder.setBolt("4", new TestGlobalCount())
                .globalGrouping("1");

        Map<String, Integer> conf = new HashMap<>();
        conf.put(Config.TOPOLOGY_WORKERS, 4);
		// Running the exact same topology in local mode(in process), and configuring it to log
		// all tuples emitted, looks like the following.
		// conf.put(Config.TOPOLOGY_DEBUG, true);

        StormSubmitter.submitTopology("myTopology", conf, builder.createTopology());
    }
}
```

Running the exact same topology in local mode (in process), and configuring it to log all tuples emitted, looks like the following. Note that it lets the topology run for 10 seconds before shutting down the local cluster.


```java
public class LocalClusterExample {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("1", new TestWordSpout(true), 5);
        builder.setSpout("2", new TestWordSpout(true), 3);
        builder.setBolt("3", new TestWordCounter(), 3)
                .fieldsGrouping("1", new Fields("word"))
                .fieldsGrouping("2", new Fields("word"));
        builder.setBolt("4", new TestGlobalCount())
                .globalGrouping("1");

        Map<String, Object> conf = new HashMap<>();
        conf.put(Config.TOPOLOGY_WORKERS, 4);
        conf.put(Config.TOPOLOGY_DEBUG, true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("myTopology", conf, builder.createTopology());
        Utils.sleep(10000);
        cluster.shutdown();
    }
}
```

The pattern for TopologyBuilder is to map component ids to components using the setSpout and setBolt methods. Those methods return objects that are then used to declare the inputs for that component.

```java
/**
 * Define a new spout in this topology with the specified parallelism.If the spout declares
 * itself as non-distributed, the parallelism_hint will be ignored and only one task
 * will be allocated to this component.
 *
 * @param id
 * 			the id of this component.This id is referenced by other components that want
 * 			to consume this spout's outputs
 * @param parallelism_hint
 * 			the number of tasks that should be assigned to execute this spout.Each task
 * 			will run on a thread in a process somewhere around the cluster
 * @param spout
 * 			the spout
 */
public SpoutDeclarer setSpout(String id, IRichSpout spout, Number parallelism_hint) {
}
```

```java
/**
 * Define a new bolt in this topology.This defines a windowed bolt, intended for windowing
 * operations.The {@link IWindowedBolt#execute(TupleWindow)} method is triggered for each
 * window interval with the list of current events in the window.
 *
 * @param id
 * 			the is of this component.This is is referenced by other components that want to
 * 			consume this bolts's outputs.
 * @param bolt
 * 			the windowed bolt
 */
public BoltDeclarer setBolt(String id, IWindowedBolt bolt, Number parallelism_hint) {
	
}
```

```java
/**
 * The stream is partitioned by the fields specified in the grouping.
 *
 */
public <T extends InputDeclarer> fieldsGrouping(String componentId, Fields fields);
```

```java
/**
 * The entire stream goes to a single one of the bolt's tasks.
 * Specifically, it goes to the task with the lowest id.
 */
public <T extends InputDeclarer> globalGrouping(String componentId);
```

#### Local Mode

Local mode simulates a Storm cluster in process and is useful for developing and testing topologies. Running topologies in local mode is similar to running topologies [on a cluster](http://storm.apache.org/releases/1.1.0/Running-topologies-on-a-production-cluster.html).

```java
LocalCluster cluster = new LocalCluster();
```

You can then submit topologies using the `submitTopology` method on the `LocalCluster` object. Just like the corresponding method on `StormSubmitter`, `submitTopology` takes a name, a topology configuration, and the topology object. You can then kill a topology using the killTopology method which takes the topology name as an argument.

To shutdown a local cluster, simple call:

```java
cluster.shutdown();
```

[Common configurations for local mode](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html)

#### Running topologies on a Production Cluster

1. Define the topology (Use [TopologyBuilder](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/TopologyBuilder.html) if defining using Java)
2. Use [StormSubmitter](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/StormSubmitter.html) to submit the topology to the cluster. `StormSubmitter` takes as input the name of the topology, a configuration for the topology, and the topology itself. For example:


```java
Config conf = new Config();
conf.setNumWorkers(20);
conf.setMaxSpoutPending(5000);
StormSubmitter.submitTopology("myTopology", conf, topology);
```

3. Create a jar containing your code and all the dependencies of your code (except for Storm -- the Storm jars will be added to the classpath on the worker nodes).

If you're using Maven, the [Maven Assembly Plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) can do the packaging for you. Just add this to your pom.xml:

```xml
  <plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
      <descriptorRefs>  
        <descriptorRef>jar-with-dependencies</descriptorRef>
      </descriptorRefs>
      <archive>
        <manifest>
          <mainClass>com.path.to.main.Class</mainClass>
        </manifest>
      </archive>
    </configuration>
  </plugin>
```

Then run mvn assembly:assembly to get an appropriately packaged jar. **Make sure you [exclude](http://maven.apache.org/plugins/maven-assembly-plugin/examples/single/including-and-excluding-artifacts.html) the Storm jars since the cluster already has Storm on the classpath**.

4. Submit the topology to the cluster using the `storm client`, specifying the path to your jar, the classname to run, and any arguments it will use:

```bash
storm jar allMyCode.jar org.me.MyTopology arg1 arg2
```

```bash
SyntaxL [storm jar topology-jar-path main-class ...]
```

`storm jar` will submit the jar to the cluster and configure the `StormSubmitter` class to talk to the right cluster. In this example, after uploading the jar `storm jar` calls the main function on `org.me.MyTopology` with the arguments "arg1", "arg2", and "arg3".

You can find out how to configure your storm client to talk to a Storm cluster on [Setting up development environment](http://storm.apache.org/releases/1.1.0/Setting-up-development-environment.html).


[Common configurations](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html)

#### Setting Up a Development Environment

- This page outlines what you need to do to get a Storm development environment set up. In summary, the steps are:
	- Download a [Storm release](http://storm.apache.org/downloads.html) , unpack it, and put the unpacked bin/ directory on your PATH
	- To be able to start and stop topologies on a remote cluster, put the cluster information in `~/.storm/storm.yaml`

##### What is a development environment?

Storm has two modes of operation: local mode and remote mode. In local mode, you can develop and test topologies completely in process on your local machine. In remote mode, you submit topologies for execution on a cluster of machines.

A Storm development environment has everything installed so that you can develop and test Storm topologies in local mode, package topologies for execution on a remote cluster, and submit/kill topologies on a remote cluster.

Let's quickly go over the relationship between your machine and a remote cluster. A Storm cluster is managed by a master node called "Nimbus". Your machine communicates with Nimbus to submit code (packaged as a jar) and topologies for execution on the cluster, and Nimbus will take care of distributing that code around the cluster and assigning workers to run your topology. Your machine uses a command line client called storm to communicate with Nimbus. The storm client is only used for remote mode; it is not used for developing and testing topologies in local mode.

##### Installing a Storm release locally

If you want to be able to submit topologies to a remote cluster from your machine, you should install a Storm release locally. Installing a Storm release will give you the storm client that you can use to interact with remote clusters. To install Storm locally, download a release [from here](http://storm.apache.org/downloads.html) and unzip it somewhere on your computer. Then add the unpacked `bin/` directory onto your `PATH` and make sure the `bin/storm` script is executable.

Installing a Storm release locally is only for interacting with remote clusters. For developing and testing topologies in local mode, it is recommended that you use Maven to include Storm as a dev dependency for your project. You can read more about using Maven for this purpose on [Maven](http://storm.apache.org/releases/1.1.0/Maven.html).

##### Starting and stopping topologies on a remote cluster

The previous step installed the `storm` client on your machine which is used to communicate with remote Storm clusters. Now all you have to do is tell the client which Storm cluster to talk to. To do this, all you have to do is put the host address of the master in the `~/.storm/storm.yaml` file. It should look something like this:

```yaml
nimbus.seeds: ["123.45.678.890"]
```

### Streams

The stream is the core abstraction in Storm. A stream is an unbounded sequence of tuples that is processed and created in parallel in a distributed fashion. Streams are defined with a schema that names the fields in the stream's tuples. By default, tuples can contain integers, longs, shorts, bytes, strings, doubles, floats, booleans, and byte arrays. You can also define your own serializers so that custom types can be used natively within tuples.

Every stream is given an id when declared. Since single-stream spouts and bolts are so common, [OutputFieldsDeclarer](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/OutputFieldsDeclarer.html) has convenience methods for declaring a single stream without specifying an id. In this case, the stream is given the default id of "default".


- Resources:
	- [Tuple](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/tuple/Tuple.html): streams are composed of tuples
	- [OutputFieldsDeclarer](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/OutputFieldsDeclarer.html): used to declare streams and their schemas
	- [Serialization](http://storm.apache.org/releases/1.1.0/Serialization.html): Information about Storm's dynamic typing of tuples and declaring custom serializations


#### Tuple

```java
/**
 * The tuple is the main data structure in Strom.A tuple is a named list of values, where
 * each value can be any type.Tuples are dynamically typed -- the types of the fields
 * do not need to be declared.Tuples have helper methods like getInteger and getString to 
 * get field values without to cast the result.
 *
 * Strom needs to know how to serialize all the values in a tuple. By default, Storm knows how
 * to serialize the primitive types, strings, and byte arrays.If you want to use another type,
 * you'll need to implement and register a serializer for that type.
 */
public interface Tuple extends ITuple {
}
```

#### OutputFieldsDeclarer

```java
public interface OutputFieldsDeclarer {
	/**
	 * Uses default stream id
	 */
	public void declare(Fields fields);
	public void declare(boolean direct, Fields fields);

	public void declareStream(String streamId, Fields fields);
	public void declareStream(String streamId, boolean direct, Fields fields);
}
```

#### Serialization

This page is about how the serialization system in Storm works for versions 0.6.0 and onwards. 

Tuples can be comprised of objects of any types. Since Storm is a distributed system, it needs to know how to serialize and deserialize objects when they're passed between tasks.

Storm uses [Kryo](https://github.com/EsotericSoftware/kryo) for serialization. Kryo is a flexible and fast serialization library that produces small serializations.

By default, Storm can serialize **primitive types, strings, byte arrays, ArrayList, HashMap, HashSet, and the Clojure collection types**. If you want to use another type in your tuples, you'll need to register a custom serializer.

#### Dynamic typing

There are no type declarations for fields in a Tuple. You put objects in fields and Storm figures out the serialization dynamically. Before we get to the interface for serialization, let's spend a moment understanding why Storm's tuples are dynamically typed.

Adding static typing to tuple fields would add large amount of complexity to Storm's API. Hadoop, for example, statically types its keys and values but requires a huge amount of annotations on the part of the user. Hadoop's API is a burden to use and the "type safety" isn't worth it. Dynamic typing is simply easier to use.

Further than that, it's not possible to statically type Storm's tuples in any reasonable way. Suppose a Bolt subscribes to multiple streams.The tuples from all those streams may have different types across the fields. When a Bolt receives a `Tuple` in `execute`, that tuple could have come from any stream and so could have any combination of types.There might be some reflection magic you can do to declare a different method for every tuple stream a bolt subscribes to, but Storm opts for the simpler, straightforward approach of dynamic typing.

Finally, another reason for using dynamic typing is so Storm can be used in a straightforward manner from dynamically typed languages like Clojure and JRuby.

##### Custom serialization

As mentioned, Storm uses Kryo for serialization. To implement custom serializers, you need to register new serializers with Kryo. It's highly recommended that you read over [Kryo's home page](https://github.com/EsotericSoftware/kryo) to understand how it handles custom serialization.

- Adding custom serializers is done through the "topology.kryo.register" property in your topology config. It takes a list of registrations, where each registration can take one of two forms:
	- The name of a class to register. In this case, Storm will use Kryo's `FieldsSerializer` to serialize the class. This may or may not be optimal for the class -- see the Kryo docs for more details.
	- A map from the name of a class to register to an implementation of `com.esotericsoftware.kryo.Serializer`.

```
topology.kryo.register:
	- com.mycompany.CustomType1
	- com.mycompany.CustomType2: com.mycompany.serializer.CustomType2Serializer
	- com.mycompany.CustomType3
```

`com.mycompany.CustomType1` and `com.mycompany.CustomType3` will use the `FieldSerializer`,whereas `com.mycompany.CustomType2` will use `com.mycompany.serializer.CustomType2Serializer` for serialization.

There's an advanced config called `Config.TOPOLOGY_SKIP_MISSING_KRYO_REGISTRATIONS`. If you set this to true, Storm will ignore any serializations that are registered but do not have their code available on the classpath. Otherwise, Storm will throw errors when it can't find a serialization. This is useful if you run many topologies on a cluster that each have different serializations, but you want to declare all the serializations across all topologies in the storm.yaml files.

```java
public class SerializationExample {
    public static void main(String[] args) {
        Config config = new Config();
        config.registerSerialization(Integer.class);
        config.registerSerialization(String.class, CustomStringSerializer.class);
    }

    private static class CustomStringSerializer extends Serializer<String> {

        @Override
        public String read(Kryo kryo, Input input, Class type) {
            return "Hello world";
        }

        @Override
        public void write(Kryo kryo, Output output, String object) {
            // do nothing
        }
    }
}
```

##### Java serialization

If Storm encounters a type for which it doesn't have a serialization registered, it will use Java serialization if possible. If the object can't be serialized with Java serialization, then Storm will throw an error.

Beware that Java serialization is extremely expensive, both in terms of CPU cost as well as the size of the serialized object. It is highly recommended that you register custom serializers when you put the topology in production. The Java serialization behavior is there so that it's easy to prototype new topologies.

You can turn off the behavior to fall back on Java serialization by setting the `Config.TOPOLOGY_FALL_BACK_ON_JAVA_SERIALIZATION` config to false.

##### Component-specific serialization registrations

if one component defines a serialization that serialization will need to be available to other bolts -- otherwise they won't be able to receive messages from that component!

When a topology is submitted, a single set of serializations is chosen to be used by all components in the topology for sending messages. This is done by merging the component-specific serializer registrations with the regular set of serialization registrations. If two components define serializers for the same class, one of the serializers is chosen arbitrarily.

To force a serializer for a particular class if there's a conflict between two component-specific registrations, just define the serializer you want to use in the topology-specific configuration. The topology-specific configuration has precedence over component-specific configurations for serialization registrations.

### Spouts

**A spout is a source of streams in a topology**. Generally spouts will read tuples from an external source and emit them into the topology.Spouts can either be `reliable` or `unreliable`. A reliable spout is capable of replaying a tuple if it failed to be processed by Storm, whereas an unreliable spout forgets about the tuple as soon as it is emitted.

Spouts can emit more than one stream. To do so, declare multiple streams using the `declareStream` method of OutputFieldsDeclarer and specify the stream to `emit` to when using the emit method on SpoutOutputCollector.

```java
/**
 * This output collector exposes the API for emmitting tuples from an {@link IRichSpout}
 * The main differece between this output collector and {@link OutputCollector}
 * for {@link IRichBolt} is that spouts can tag messages with ids so that they can be
 * acked or failed later on.This is the Spout portion of Storm's API to guarantee that
 * each message is fully processed at least once.
 */
public class SpoutOutputCollector implements ISpoutOutputCollector {

}
```

The main method on spouts is nextTuple. nextTuple either emits a new tuple into the topology or simply returns if there are no new tuples to emit. It is `imperative` that nextTuple does not block for any spout implementation, because **Storm calls all the spout methods on the same thread**.

The other main methods on spouts are ack and fail. These are called when Storm detects that a tuple emitted from the spout either successfully completed through the topology or failed to be completed. ack and fail are only called for reliable spouts. See the [Javadoc](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/spout/ISpout.html) for more information.

```java
/**
 * ISpout is the core interface for implementing spouts.A Spout is responsible for feeding 
 * messages into the topology for processing.For every tuple emitted by a spout, Strom will
 * track the (potentially very large) DAG of tuples generated based on a tuple emmitted by the 
 * spout.When Storm detects that every tuple in that DAG been successfully processed, it will
 * send an ack message to the spout.
 * If a tuple fails to be fully processed within the configured timeout for the topology,
 * Storm will send a fail message to the Spout for the message.
 *
 * When a Spout emits a tuple, it can tag the tuple with a message id,The message id
 * can be any type. When Storm acks or fails a message, it will pass back to the spout
 * the same message id to identify which tuple it's referring to.If the spout leaves out
 * the message id, or sets it to null, then Storm will not track the message and the spout
 * will not receive any ack or fail callbacks for the message.
 *
 * Storm executes ack, fail, and nextTuple all on the same thread.This means that an implementor
 * of an ISpout does not to worry about concurrency issues between those methods. However,
 * it also means that an implementor must ensure that nextTuple is non-blocking: otherwise
 * the method could block acks and fails that are pending to be processed.
 */
public class ISpout extends Serializable {
}
```

- Resources:
	- [IRichSpout](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/IRichSpout.html): this is the interface that spouts must implement.
	- [Guaranteeing](http://storm.apache.org/releases/1.1.0/Guaranteeing-message-processing.html) message processing


```java
/**
 * Common methods for all possible components in a topology.This interface is used when defining
 * topologies using the Java API.
 */
public interface IComponent extends Serializable {
	/**
	 * Declare the output schema for all the streams of this topology.
	 *
	 * @param declarer
	 * 				this is used to declare output stream ids, output fields,
	 * 				and whether or not each output stream is a direct stream
	 */
	void declareOutputFields(OutputFieldsDeclarer declarer);
}
```

### Bolts

All processing in topologies is done in bolts. Bolts can do anything from filtering, functions, aggregations, joins, talking to databases, and more.

Bolts can do simple stream transformations. Doing complex stream transformations often requires multiple steps and thus multiple bolts. For example, transforming a stream of tweets into a stream of trending images requires at least two steps: a bolt to do a rolling count of retweets for each image, and one or more bolts to stream out the top X images (you can do this particular stream transformation in a more scalable way with three bolts than with two).

Bolts can emit more than one stream. To do so, declare multiple streams using the `declareStream` method of [OutputFieldsDeclarer](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/OutputFieldsDeclarer.html) and specify the stream to emit to when using the `emit` method on [OutputCollector](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/task/OutputCollector.html).

When you declare a bolt's input streams, you always subscribe to specific streams of another component. If you want to subscribe to all the streams of another component, you have to subscribe to each one individually. [InputDeclarer](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/InputDeclarer.html) has syntactic sugar for subscribing to streams declared on the default stream id. Saying `declarer.shuffleGrouping("1")` subscribes to the default stream on component "1" and is equivalent to `declarer.shuffleGrouping("1", DEFAULT_STREAM_ID)`.

The main method in bolts is the `execute` method which takes in as input a new tuple. Bolts emit new tuples using the `OutputCollector` object. Bolts must call the `ack` method on the `OutputCollector` for every tuple they process so that Storm knows when tuples are completed (and can eventually determine that its safe to ack the original spout tuples). For the common case of processing an input tuple, emitting 0 or more tuples based on that tuple, and then acking the input tuple, Storm provides an [IBasicBolt](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/IBasicBolt.html) interface.

Its perfectly fine to launch new threads in bolts that do processing asynchronously. `OutputCollector` is thread-safe and can be called at any time.

- Resources:
	- [IRichBolt](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/IRichBolt.html): this is general interface for bolts.
	- [IBasicBolt](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/IBasicBolt.html):  this is a convenience interface for defining bolts that do filtering or simple functions.
	- [OutputCollector](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/task/OutputCollector.html): bolts emit tuples to their output streams using an instance of this class
	- [Guaranteeing message processing](http://storm.apache.org/releases/1.1.0/Guaranteeing-message-processing.html)

### Stream groupings

Part of defining a topology is specifying for each bolt which streams it should receive as input. A stream grouping defines how that stream should be partitioned among the bolt's tasks.

There are eight built-in stream groupings in Storm, and you can implement a custom stream grouping by implementing the [CustomStreamGrouping](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/grouping/CustomStreamGrouping.html) interface:

```java
public interface CustomStreamGrouping extends Serializable {
	/**
	 * Tells the stream grouping at runtime the tasks in the target bolt.
	 * This information should be used in chooseTasks to determine the target tasks.
	 *
	 * It also tells the grouping the metadata on the stream this grouping will be used on.
	 */
	void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks);
	/**
	 * This function implements a custom stream grouping. It takes in as input 
	 * the number of tasks in the target bolt in prepare and returns the 
	 * tasks to send the tuple to;
	 */
	List<Integer> chooseTasks(int taskId, List<Object> values);
}
```

- **Shuffle grouping**: Tuples are randomly distributed across the bolt's tasks in a way such that each bolt is guaranteed to get an equal number of tuples.
- **Fields grouping**: The stream is partitioned by the fields specified in the grouping. For example, if the stream is grouped by the "user-id" field, tuples with the same "user-id" will always go to the same task, but tuples with different "user-id"'s may go to different tasks.
- **Partial Key grouping**: The stream is partitioned by the fields specified in the grouping, like the Fields grouping, but are load balanced between two downstream bolts, which provides better utilization of resources when the incoming data is skewed. [This paper](https://melmeric.files.wordpress.com/2014/11/the-power-of-both-choices-practical-load-balancing-for-distributed-stream-processing-engines.pdf) provides a good explanation of how it works and the advantages it provides.
- **All grouping**:The stream is replicated across all the bolt's `tasks`. Use this grouping with care.
- **Global grouping**: The entire stream goes to a single one of the bolt's tasks. Specifically, it goes to the task with the lowest id.
- **None grouping**: This grouping specifies that you don't care how the stream is grouped. Currently, none groupings are equivalent to shuffle groupings. Eventually though, Storm will push down bolts with none groupings to execute in the same thread as the bolt or spout they subscribe from (when possible).
- **Direct grouping**: This is a special kind of grouping. A stream grouped this way means that the `producer` of the tuple decides which task of the consumer will receive this tuple.Direct groupings can only be declared on streams that have been declared as direct streams.Tuples emitted to a direct stream must be emitted using one of the emitDirect() methods. A bolt can get the task ids of its consumers by either using the provided `TopologyContext` or by keeping track of the output of the emit method in OutputCollector (which returns the task ids that the tuple was sent to).
- **Local or shuffle grouping**: If the target bolt has one or more tasks in the same worker process, tuples will be shuffled to just those in-process tasks. Otherwise, this acts like a normal shuffle grouping.

```java
/**
 * A `TopologyContext` is given to bolts and spouts in their `prepare()` and `open()` methods,
 * respectively.This object provides information about the component's place within the topology
 * such as task id, inputs and outputs, etc.
 *
 * The `TopologyContext` is also used to declare `ISubscribedState` objects to synchronized
 * state with StatSpouts this object is subscribed to.
 */
public class TopologyContext extends WorkerTopologyContext implements IMetricsContext {
}
```

- Resources:
	- [InputDeclarer](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/InputDeclarer.html): this object is returned whenever `setBolt` is called on `TopologyBuilder` and is used for declaring a bolt's input streams and how those streams should be grouped

### Reliability

Storm guarantees that every spout tuple will be fully processed by the topology. It does this by tracking the tree of tuples triggered by every spout tuple and determining when that tree of tuples has been successfully completed. Every topology has a "message timeout" associated with it. If Storm fails to detect that a spout tuple has been completed within that timeout, then it fails the tuple and replays it later.

To take advantage of Storm's reliability capabilities, you must tell Storm when new edges in a tuple tree are being created and tell Storm whenever you've finished processing an individual tuple. These are done using the OutputCollector object that bolts use to emit tuples. Anchoring is done in the `emit` method, and you declare that you're finished with a tuple using the `ack` method.

### Tasks

The execution of each and every spout and bolt by Storm is called as “Tasks”. In simple words, a task is either the execution of a spout or a bolt. At a given time, each spout and bolt can have multiple instances running in multiple separate threads.

Each spout or bolt executes as many tasks across the cluster.You set the parallelism for each `spout` or `bolt` in the setSpout and setBolt methods of TopologyBuilder.

### Workers

Topologies execute across one or more worker processes. Each worker process is a physical JVM and executes a subset of all the tasks for the topology. For example, if the combined parallelism of the topology is 300 and 50 workers are allocated, then each worker will execute 6 tasks (as threads within the worker). Storm tries to spread the tasks evenly across all the workers.

- Resources:
	- [Config.TOPOLOGY_WORKERS]: this config sets the number of workers to allocate for executing the topology

---
---
---

## [Scheduler](http://storm.apache.org/releases/1.1.0/Storm-Scheduler.html)

Storm now has 4 kinds of built-in schedulers: DefaultScheduler, IsolationScheduler, [MultitenantScheduler](https://github.com/apache/storm/blob/v1.1.0/storm-core/src/jvm/org/apache/storm/scheduler/multitenant/MultitenantScheduler.java), [ResourceAwareScheduler]http://storm.apache.org/releases/1.1.0/Resource_Aware_Scheduler_overview.html().

### Pluggable scheduler

You can implement your own scheduler to replace the default scheduler to assign executors to workers. You configure the class to use the "storm.scheduler" config in your storm.yaml, and your scheduler must implement the [IScheduler](http://github.com/apache/storm/blob/v1.1.0/storm-core/src/jvm/org/apache/storm/scheduler/IScheduler.java) interface.

```java
public interface IScheduler {
	void prepare(Map conf);

	/**
	 * Set assignments for the topologies which needs scheduling. The new assignments is 
	 * through `cluster.getAssignments()`
	 *
	 * @param topologies 
	 *			all the topologies in the cluster, some of them need schedule.Topologies
	 *			object here only contains static information about topology.Infomation like
	 *			assignments, slots are all in the `cluster` object.
	 *@param cluster
	 			the cluster these topologies are running in. `Cluster` contains everything user
				need to develop a new scheduling logic. e.g: supervisors information, available
				slots, current assignment for all the topologies etc. User can set the new
				assignment for topologies using cluster.setAssignmentById().
	 */
	void schedule(Topologies topologies, Cluster cluster);
}
```

### Isolation Scheduler

The isolation scheduler makes it easy and safe to share a cluster among many topologies. The isolation scheduler lets you specify which topologies should be "isolated", meaning that they run on a dedicated set of machines within the cluster where no other topologies will be running. These isolated topologies are given priority on the cluster, so resources will be allocated to isolated topologies if there's competition with non-isolated topologies, and resources will be taken away from non-isolated topologies if necessary to get resources for an isolated topology. Once all isolated topologies are allocated, the remaining machines on the cluster are shared among all non-isolated topologies.

You can configure the isolation scheduler in the Nimbus configuration by setting "storm.scheduler" to "org.apache.storm.scheduler.IsolationScheduler". Then, use the "isolation.scheduler.machines" config to specify how many machines each topology should get. This configuration is a map from topology name to the number of isolated machines allocated to this topology. For example:


```yaml
isolation.scheduler.machines:
	"my-topology": 8
	"tiny-topology": 1
	"some-other-topology": 3
```

Any topologies submitted to the cluster not listed there will not be isolated. Note that there is no way for a user of Storm to affect their isolation settings – this is only allowed by the administrator of the cluster (this is very much intentional).

The isolation scheduler solves the multi-tenancy problem – avoiding resource contention between topologies – by providing full isolation between topologies. The intention is that "productionized" topologies should be listed in the isolation config, and test or in-development topologies should not. The remaining machines on the cluster serve the dual role of failover for isolated topologies and for running the non-isolated topologies.

## [Configuration](http://storm.apache.org/releases/1.1.0/Configuration.html)

Storm has a variety of configurations for tweaking the behavior of nimbus, supervisors, and running topologies. Some configurations are system configurations and cannot be modified on a topology by topology basis, whereas other configurations can be modified per topology.

Every configuration has a default value defined in [defaults.yaml](http://github.com/apache/storm/blob/v1.1.0/conf/defaults.yaml) in the Storm codebase. You can override these configurations by defining a storm.yaml in the classpath of Nimbus and the supervisors. Finally, you can define a topology-specific configuration that you submit along with your topology when using [StormSubmitter](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/StormSubmitter.html). However, the topology-specific configuration can only override configs prefixed with "TOPOLOGY".

- Resources:
	- [Config](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html): a listing of all configurations as well as a helper class for creating topology specific configurations
	- [defaults.yaml](http://github.com/apache/storm/blob/v1.1.0/conf/defaults.yaml): the default values for all configurations
	- [Setting up a Storm cluster](http://storm.apache.org/releases/1.1.0/Setting-up-a-Storm-cluster.html): explains how to create and configure a Storm cluster
	- [Running topologies on a production cluster](http://storm.apache.org/releases/1.1.0/Running-topologies-on-a-production-cluster.html): lists useful configurations when running topologies on a cluster
	- [Local mode](http://storm.apache.org/releases/1.1.0/Local-mode.html): lists useful configurations when using local mode

### Setting up a Storm Cluster

If you run into difficulties with your Storm cluster, first check for a solution is in the [Troubleshooting](http://storm.apache.org/releases/1.1.0/Troubleshooting.html) page. Otherwise, email the mailing list.

- Here's a summary of the steps for setting up a Storm cluster:
	- Set up a Zookeeper cluster
	- Install dependencies on Nimbus and worker machines
	- Download and extract a Storm release to Nimbus and worker machines
	- Fill in mandatory configurations into storm.yaml
	- Launch daemons under supervision using "storm" script and a supervisor of your choice

#### Set up a Zookeeper cluster

Storm uses Zookeeper for coordinating the cluster. **Zookeeper is not used for message passing**, so the load Storm places on Zookeeper is quite low. Single node Zookeeper clusters should be sufficient for most cases, but if you want failover or are deploying large Storm clusters you may want larger Zookeeper clusters. Instructions for deploying Zookeeper are [here](http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html).

- A few notes about Zookeeper deployment:
	- It's critical that you run Zookeeper under supervision, since Zookeeper is fail-fast and will exit the process if it encounters any error case. See [here](http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html#sc_supervision) for more details.
	- It's critical that you set up a cron to compact Zookeeper's data and transaction logs. The Zookeeper daemon does not do this on its own, and if you don't set up a cron, Zookeeper will quickly run out of disk space. See [here](http://zookeeper.apache.org/doc/r3.3.3/zookeeperAdmin.html#sc_maintenance) for more details.

#### Install dependencies on Nimbus and worker machines

Next you need to install Storm's dependencies on Nimbus and the worker machines. These are:
- Java7
- Python 2.6.6

These are the versions of the dependencies that have been tested with Storm. Storm may or may not work with different versions of Java and/or Python.

#### Download and extract a Storm release to Nimbus and worker machines

Next, download a Storm release and extract the zip file somewhere on Nimbus and each of the worker machines. 

#### Fill in mandatory configurations into storm.yaml

The Storm release contains a file at `conf/storm.yaml` that configures the Storm daemons. You can see the default configuration values here. storm.yaml overrides anything in defaults.yaml. There's a few configurations that are mandatory to get a working cluster:

1. **storm.zookeeper.servers**: This is a list of the hosts in the Zookeeper cluster for your Storm cluster. 
2. **storm.local.dir**: The Nimbus and Supervisor daemons require a directory on the local disk to store small amounts of state (like jars, confs, and things like that). You should create that directory on each machine, give it proper permissions, and then fill in the directory location using this config.
3. **nimbus.seeds**: The worker nodes need to know which machines are the candidate of master in order to download topology jars and confs. You're encouraged to fill out the value to list of `machine's FQDN`(Fully Qualified domain name). If you want to set up Nimbus H/A, you have to address all machines' FQDN which run nimbus. You may want to leave it to default value when you just want to set up 'pseudo-distributed' cluster, but you're still encouraged to fill out FQDN.
4. **supervisor.slots.ports**: For each worker machine, you configure how many workers run on that machine with this config. Each worker uses a single port for receiving messages, and this setting defines which ports are open for use.If you define five ports here, then Storm will allocate up to five workers to run on this machine. If you define three ports, Storm will only run up to three. By default, this setting is configured to run 4 workers on the ports 6700, 6701, 6702, and 6703. 

```yaml
storm.zookeeper.servers:
	- "111.222.333.444"
	- "555.666.777.888"
storm.local.dir: "/mnt/storm"
nimbus.seeds: ["111.222.333.44"]
supervisor.slots.ports:
	- 6700
	- 6701
	- 6702
	- 6703
```

### Monitoring Health of Supervisors

Storm provides a mechanism by which administrators can configure the supervisor to run administrator supplied scripts periodically to determine if a node is healthy or not. Administrators can have the supervisor determine if the node is in a healthy state by performing any checks of their choice in scripts located in storm.health.check.dir. If a script detects the node to be in an unhealthy state, it must print a line to standard output beginning with the string ERROR. The supervisor will periodically run the scripts in the health check dir and check the output. If the script’s output contains the string ERROR, as described above, the supervisor will shut down any workers and exit.

If the supervisor is running with supervision "/bin/storm node-health-check" can be called to determine if the supervisor should be launched or if the node is unhealthy.

The health check directory location can be configured with:

```yaml
storm.health.check.dir: "healthchecks"
```

The scripts must have execute permissions. The time to allow any given healthcheck script to run before it is marked failed due to timeout can be configured with:

```yaml
storm.health.check.timeout.ms: 5000
```

### Launch daemons under supervision using "storm" script and a supervisor of your choice

The last step is to launch all the Storm daemons. It is critical that you run each of these daemons under supervision.Storm is a fail-fast system which means the processes will halt whenever an unexpected error is encountered. Storm is designed so that it can safely halt at any point and recover correctly when the process is restarted.This is why Storm keeps no state in-process -- if Nimbus or the Supervisors restart, the running topologies are unaffected. Here's how to run the Storm daemons:

- **Nimbus**: Run the command "bin/storm nimbus" under supervision on the master machine.
- **Supervisor**: Run the command "bin/storm supervisor" under supervision on each worker machine. The supervisor daemon is responsible for starting and stopping worker processes on that machine.
- **UI**: Run the Storm UI (a site you can access from the browser that gives diagnostics on the cluster and topologies) by running the command "bin/storm ui" under supervision. The UI can be accessed by navigating your web browser to http://{ui host}:8080.

### Cluster Architecture

Apache Storm has two type of nodes, `Nimbus` (master node) and `Supervisor` (worker node). Nimbus is the central component of Apache Storm. The main job of Nimbus is to run the Storm topology. Nimbus analyzes the topology and gathers the task to be executed. Then, it will distributes the task to an available supervisor.

A supervisor will have one or more worker process. Supervisor will delegate the tasks to worker processes. Worker process will spawn as many executors as needed and run the task. Apache Storm uses an internal distributed messaging system for the communication between nimbus and supervisors.

| Components | Description |
|------------|-------------|
| Nimbus | Nimbus is a master node of Storm cluster. All other nodes in the cluster are called as **worker nodes**. Master node is responsible for distributing data among all the worker nodes, assign tasks to worker nodes and monitoring failures.|
| Supervisor | The nodes that follow instructions given by the nimbus are called as Supervisors. A supervisor has multiple worker processes and it governs worker processes to complete the tasks assigned by the nimbus. |
| Worker process | A worker process will execute tasks related to a specific topology. A worker process will not run a task by itself, instead it creates executors and asks them to perform a particular task. A worker process will have multiple executors. |
| Executor | An executor is nothing but a single thread spawn by a worker process. An executor runs one or more tasks but only for a specific spout or bolt. |
| Task | A task performs actual data processing. So, it is either a spout or a bolt. |
| ZooKeeper framework | Apache ZooKeeper is a service used by a cluster (group of nodes) to coordinate between themselves and maintaining shared data with robust synchronization techniques. Nimbus is stateless, so it depends on ZooKeeper to monitor the working node status.\n ZooKeeper helps the supervisor to interact with the nimbus. It is responsible to maintain the state of nimbus and supervisor. |

Storm is stateless in nature. Even though stateless nature has its own disadvantages, it actually helps Storm to process real-time data in the best possible and quickest way.

Storm is not entirely stateless though. **It stores its state in Apache ZooKeeper**. Since the state is available in Apache ZooKeeper, a failed nimbus can be restarted and made to work from where it left. Usually, service monitoring tools like monit will monitor Nimbus and restart it if there is any failure.


## [Guaranteeing message processing](http://storm.apache.org/releases/1.1.0/Guaranteeing-message-processing.html)

Storm offers several different levels of guaranteed message processing, including best effort, at least once, and exactly once through [Trident](http://storm.apache.org/releases/1.1.0/Trident-tutorial.html). This page describes how Storm can guarantee at least once processing.

### What does it mean for a message to be "fully processed"?

A tuple coming off a spout can trigger thousands of tuples to be created based on it. Consider, for example, the streaming word count topology:

>the tutorial under below is outdated in 1.1.0

```java
TopologyBuilder builder = new TopologyBuilder();
builder.setSpout("sentences", new KestrelSpout("kestrel.backtype.com",
                                               22133,
                                               "sentence_queue",
                                               new StringScheme()));
builder.setBolt("split", new SplitSentence(), 10)
        .shuffleGrouping("sentences");
builder.setBolt("count", new WordCount(), 20)
        .fieldsGrouping("split", new Fields("word"));
```

This topology reads sentences off of a Kestrel queue, splits the sentences into its constituent words, and then emits for each word the number of times it has seen that word before. A tuple coming off the spout triggers many tuples being created based on it: a tuple for each word in the sentence and a tuple for the updated count for each word. The tree of messages looks something like this:

![tree of messages looks something like this](http://storm.apache.org/releases/1.1.0/images/tuple_tree.png)

**Storm considers a tuple coming off a spout "fully processed" when the tuple tree has been exhausted and every message in the tree has been processed**. A tuple is considered failed when its tree of messages fails to be fully processed within a specified timeout. This timeout can be configured on a topology-specific basis using the Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS configuration and defaults to 30 seconds.

### What happens if a message is fully processed or fails to be fully processed?

To understand this question, let's take a look at the lifecycle of a tuple coming off of a spout. For reference, here is the interface that spouts implement:

```java
public interface ISpout extends Serializable {
	void open(Map conf, TopologyContext context, SpoutOutputCollector collector);
	void close();
	void nextTuple();
	void ack(Object msgId);
	void fail(Object msgId);
}
```

First, Storm requests a tuple from the `Spout` by calling the `nextTuple` method on the Spout. The `Spout` uses the `SpoutOutputCollector` provided in the open method to emit a tuple to one of its output streams. When emitting a tuple, the Spout provides a "message id" that will be used to identify the tuple later. For example, the KestrelSpout reads a message off of the kestrel queue and emits as the "message id" the id provided by Kestrel for the message. Emitting a message to the SpoutOutputCollector looks like this:

```java
_collector.emit(new Values("field1", "field2", 3) , msgId);
```

Next, the tuple gets sent to consuming bolts and Storm takes care of tracking the tree of messages that is created. If Storm detects that a tuple is fully processed, Storm will call the `ack` method on the originating `Spout` task with the message id that the `Spout` provided to Storm.Likewise, if the tuple times-out Storm will call the `fail` method on the `Spout`. **Note that a tuple will be `acked` or `failed` by the exact same `Spout` task that created it.** So if a Spout is executing as many tasks across the cluster, a tuple won't be acked or failed by a different task than the one that created it.

Let's use `KestrelSpout` again to see what a `Spout` needs to do to guarantee message processing. <u>**When KestrelSpout takes a message off the Kestrel queue, it "opens" the message. This means the message is not actually taken off the queue yet, but instead placed in a "pending" state waiting for acknowledgement that the message is completed.**</u>While in the pending state, a message will not be sent to other consumers of the queue. Additionally, if a client disconnects all pending messages for that client are put back on the queue. When a message is opened, Kestrel provides the client with the data for the message as well as a unique id for the message. The `KestrelSpout` uses that exact id as the "message id" for the tuple when emitting the tuple to the `SpoutOutputCollector`.Sometime later on, when `ack` or `fail` are called on the `KestrelSpout`, the KestrelSpout sends an ack or fail message to Kestrel with the message id to take the message off the queue or have it put back on. 

### What is Storm's reliability API?

There are two things you have to do as a user to benefit from Storm's reliability capabilities. First, you need to tell Storm whenever you're creating a new link in the tree of tuples. Second, you need to tell Storm when you have finished processing an individual tuple. By doing both these things, Storm can detect when the tree of tuples is fully processed and can ack or fail the spout tuple appropriately. Storm's API provides a concise way of doing both of these tasks.

Specifying a link in the tuple tree is called `anchoring`. Anchoring is done at the same time you emit a new tuple. Let's use the following bolt as an example. This bolt splits a tuple containing a sentence into a tuple for each word:

```java
public class SplitSentence extends BaseRichBolt {

    OutputCollector _collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String sentence = input.getString(0);
        for (String word : sentence.split(" ")) {
            _collector.emit(input, new Values(word));
        }
        _collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
```

```java
/**
 * Emits a new tuple to the default stream anchored on a single tuple.The emitted values must
 * be immutable.
 *
 * @param anchor the tuple to anchor to
 * @param tuple the new output tuple from the bolt
 * @return the list of tasks ids that this new tuple was sent to
 */
public List<Integer> emit(Tuple anchor, List<Object> tuple) {
	return emit(Utils.DEFAULT_STREAM_ID, anchor, tuple);
}
```

Each word tuple is `anchored` by specifying the input tuple as the first argument to `emit`.Since the word tuple is anchored, the spout tuple at the root of the tree will be replayed later on if the word tuple failed to be processed downstream. In contrast, let's look at what happens if the word tuple is emitted like this:

```java
_collector.emit(new Values(word));
```

Emitting the word tuple this way causes it to be `unanchored`.If the tuple fails be processed downstream, the root tuple will not be replayed. Depending on the fault-tolerance guarantees you need in your topology, sometimes it's appropriate to emit an unanchored tuple.

>An output tuple can be anchored to more than one input tuple. This is useful when doing streaming joins or aggregations.A multi-anchored tuple failing to be processed will cause multiple tuples to be replayed from the spouts. Multi-anchoring is done by specifying a list of tuples rather than just a single tuple. For example:

```java
List<Tuple> anchors = new ArrayList<Tuple>();
anchors.add(tuple1);
anchors.add(tuple2);
_collector.emit(anchors, new Values(1, 2, 3));
```
Multi-anchoring adds the output tuple into multiple tuple trees. Note that it's also possible for multi-anchoring to break the tree structure and create tuple DAGs, like so:

![multi-anchoring DAGs](http://storm.apache.org/releases/1.1.0/images/tuple-dag.png)

>Anchoring is how you specify the tuple tree -- the next and final piece to Storm's reliability API is specifying when you've finished processing an individual tuple in the tuple tree. 

This is done by using the `ack` and `fail` methods on the `OutputCollector`. If you look back at the SplitSentence example, you can see that the input tuple is acked after all the word tuples are emitted.

You can use the `fail` method on the `OutputCollector` to immediately fail the spout tuple at the root of the tuple tree. For example, your application may choose to catch an exception from a database client and explicitly fail the input tuple. By failing the tuple explicitly, the spout tuple can be replayed faster than if you waited for the tuple to time-out.

Every tuple you process must be acked or failed. Storm uses memory to track each tuple, so if you don't ack/fail every tuple, the task will eventually run out of memory.

>A lot of bolts follow a common pattern of reading an input tuple, emitting tuples based on it, and then acking the tuple at the end of the `execute` method.

**These bolts fall into the categories of filters and simple functions**. Storm has an interface called `BasicBolt` that encapsulates this pattern for you. The SplitSentence example can be written as a BasicBolt like follows:

```java
public class SplitSentence extends BaseBasicBolt {
   public void execute(Tuple tuple, BasicOutputCollector collector) {
       String sentence = tuple.getString(0);
       for(String word: sentence.split(" ")) {
           collector.emit(new Values(word));
       }
   }

   public void declareOutputFields(OutputFieldsDeclarer declarer) {
       declarer.declare(new Fields("word"));
   }        
}
```

This implementation is simpler than the implementation from before and is semantically identical. Tuples emitted to `BasicOutputCollector` are **automatically anchored** to the input tuple, and the input tuple **is acked for you automatically** when the execute method completes.

### How do I make my applications work correctly given that tuples can be replayed?

As always in software design, the answer is "it depends." If you really want exactly once semantics use the [Trident](http://storm.apache.org/releases/1.1.0/Trident-tutorial.html) API. In some cases, like with a lot of analytics, dropping data is OK so disabling the fault tolerance by setting the number of acker bolts to 0 [Config.TOPOLOGY_ACKERS_EXECUTOR](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html#TOPOLOGY_ACKERS). 

### How does Storm implement reliability in an efficient way?

The best way to understand Storm's reliability implementation is to look at the lifecycle of tuples and tuple DAGs. When a tuple is created in a topology, whether in a spout or a bolt, it is given a random 64 bit id. These ids are used by ackers to track the tuple DAG for every spout tuple.

Every tuple knows the ids of all the spout tuples for which it exists in their tuple trees. When you emit a new tuple in a bolt, the spout tuple ids from the tuple's anchors are copied into the new tuple. When a tuple is acked, it sends a message to the appropriate acker tasks with information about how the tuple tree changed. In particular it tells the acker "I am now completed within the tree for this spout tuple, and here are the new tuples in the tree that were anchored to me".

For example, if tuples "D" and "E" were created based on tuple "C", here's how the tuple tree changes when "C" is acked:

![Ackers](http://storm.apache.org/releases/1.1.0/images/ack_tree.png)

Since "C" is removed from the tree at the same time that "D" and "E" are added to it, the tree can never be prematurely completed.

There are a few more details to how Storm tracks tuple trees. As mentioned already, you can have an arbitrary number of acker tasks in a topology. This leads to the following question: when a tuple is acked in the topology, how does it know to which acker task to send that information?

Storm uses mod hashing to map a spout tuple id to an acker task. Since every tuple carries with it the spout tuple ids of all the trees they exist within, they know which acker tasks to communicate with.

Acker tasks do not track the tree of tuples explicitly. For large tuple trees with tens of thousands of nodes (or more), tracking all the tuple trees could overwhelm the memory used by the ackers. Instead, the ackers take a different strategy that only requires a fixed amount of space per spout tuple (about 20 bytes). This tracking algorithm is the key to how Storm works and is one of its major breakthroughs.

>An acker task stores a map from a spout tuple id to a pair of values.The first value is the task id that created the spout tuple which is used later on to send completion messages.The second value is a 64 bit number called the "ack val".The ack val is a representation of the state of the entire tuple tree, no matter how big or how small. **It is simply the xor of all tuple ids that have been created and/or acked in the tree**.

When an acker task sees that an "ack val" has become 0, then it knows that the tuple tree is completed. Since tuple ids are random 64 bit numbers, the chances of an "ack val" accidentally becoming 0 is extremely small. If you work the math, at 10K acks per second, it will take 50,000,000 years until a mistake is made. And even then, it will only cause data loss if that tuple happens to fail in the topology.

Now that you understand the reliability algorithm, let's go over all the failure cases and see how in each case Storm avoids data loss:

- **A tuple isn't acked because the task died**: In this case the spout tuple ids at the root of the trees for the failed tuple will time out and be replayed.
- **Acker task dies**: In this case all the spout tuples the acker was tracking will time out and be replayed.
- **Spout task dies**: In this case the source that the spout talks to is responsible for replaying the messages. For example, queues like Kestrel and RabbitMQ will place all pending messages back on the queue when a client disconnects.


![Strom ackers](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/storm-acker.png)

### Tuning reliability

Acker tasks are lightweight, so you don't need very many of them in a topology. You can track their performance through the Storm UI (component id "__acker"). If the throughput doesn't look right, you'll need to add more acker tasks.

If reliability isn't important to you -- that is, you don't care about losing tuples in failure situations -- then you can improve performance by not tracking the tuple tree for spout tuples. Not tracking a tuple tree halves the number of messages transferred since normally there's an ack message for every tuple in the tuple tree. Additionally, it requires fewer ids to be kept in each downstream tuple, reducing bandwidth usage.

There are three ways to remove reliability. 

- The first is to set Config.TOPOLOGY_ACKERS to 0. In this case, Storm will call the ack method on the spout immediately after the spout emits a tuple. The tuple tree won't be tracked.
- The second way is to remove reliability on a message by message basis. You can turn off tracking for an individual spout tuple by omitting a message id in the `SpoutOutputCollector.emit` method.
- Finally, if you don't care if a particular subset of the tuples downstream in the topology fail to be processed, you can emit them as unanchored tuples. Since they're not anchored to any spout tuples, they won't cause any spout tuples to fail if they aren't acked.


## Daemon Fault Tolerance

Storm has several different daemon processes. Nimbus that schedules workers, supervisors that launch and kill workers, the log viewer that gives access to logs, and the UI that shows the status of a cluster.

### What happens when a worker dies?

When a worker dies, the supervisor will restart it. If it continuously fails on startup and is unable to heartbeat to Nimbus, Nimbus will reschedule the worker.

### What happens when a node dies?

The tasks assigned to that machine will time-out and Nimbus will reassign those tasks to other machines.

### What happens when Nimbus or Supervisor daemons die?

>The Nimbus and Supervisor daemons are designed to be fail-fast (process self-destructs whenever any unexpected situation is encountered) and stateless (all state is kept in Zookeeper or on disk). As described in Setting up a Storm cluster, the Nimbus and Supervisor daemons must be run under supervision using a tool like daemontools or monit. So if the Nimbus or Supervisor daemons die, they restart like nothing happened.

Most notably, no worker processes are affected by the death of Nimbus or the Supervisors. This is in contrast to Hadoop, where if the JobTracker dies, all the running jobs are lost.

### Is Nimbus a single point of failure?

If you lose the Nimbus node, the workers will still continue to function. Additionally, supervisors will continue to restart workers if they die. However, without Nimbus, workers won't be reassigned to other machines when necessary (like if you lose a worker machine).

## Highly Available Nimbus Design

### Problem Statement:

Currently the storm master aka nimbus, is a process that runs on a single machine under supervision. In most cases the nimbus failure is transient and it is restarted by the supervisor. However sometimes when disks fail and networks partitions occur, nimbus goes down. Under these circumstances the topologies run normally but no new topologies can be submitted, no existing topologies can be killed/deactivated/activated and if a supervisor node fails then the reassignments are not performed resulting in performance degradation or topology failures. With this project we intend to resolve this problem by running nimbus in a primary backup mode to guarantee that even if a nimbus server fails one of the backups will take over.

### Requirements:

- Increase overall availability of nimbus.
- Allow nimbus hosts to leave and join the cluster at will any time. A newly joined host should auto catch up and join the list of potential leaders automatically.
- No topology resubmissions required in case of nimbus fail overs.
- No active topology should ever be lost.

### Leader Election:

The nimbus server will use the following interface:

```java
public interface ILeaderElector {
    /**
     * queue up for leadership lock. The call returns immediately and the caller                 
     * must check isLeader() to perform any leadership action.
     */
    void addToLeaderLockQueue();

    /**
     * Removes the caller from the leader lock queue. If the caller is leader
     * also releases the lock.
     */
    void removeFromLeaderLockQueue();

    /**
     *
     * @return true if the caller currently has the leader lock.
     */
    boolean isLeader();

    /**
     *
     * @return the current leader's address , throws exception if noone has has    lock.
     */
    InetSocketAddress getLeaderAddress();

    /**
     * 
     * @return list of current nimbus addresses, includes leader.
     */
    List<InetSocketAddress> getAllNimbusAddresses();
}
```

On startup nimbus will check if it has code for all active topologies available locally. Once it gets to this state it will call addToLeaderLockQueue() function. When a nimbus is notified to become a leader it will check if it has all the code locally before assuming the leadership role. If any active topology code is missing, the node will not accept the leadership role instead it will release the lock and wait till it has all the code before requeueing for leader lock.

The first implementation will be Zookeeper based. If the zookeeper connection is lost/resetted resulting in loss of lock or the spot in queue the implementation will take care of updating the state such that isLeader() will reflect the current status.The leader like actions must finish in less than minimumOf(connectionTimeout, SessionTimeout) to ensure the lock was held by nimbus for the entire duration of the action.If a nimbus that is not leader receives a request that only a leader can perform it will throw a RunTimeException.

Following steps describes a nimbus failover scenario: * Let’s say we have 4 topologies running with 3 nimbus nodes and code-replication-factor = 2. We assume that the invariant “The leader nimbus has code for all topologies locally” holds true at the beginning. nonleader-1 has code for the first 2 topologies and nonLeader-2 has code for the other 2 topologies. *Leader nimbus dies, hard disk failure so no recovery possible. *nonLeader-1 gets a zookeeper notification to indicate it is now the new leader. before accepting the leadership it checks if it has code available for all 4 topologies(these are topologies under `/storm/storms/`).It realizes it only has code for 2 topologies so it relinquishes the lock and looks under `/storm/code-distributor/topologyId` to find out from where can it download the code/metafile for the missing topologies. it finds entries for the leader nimbus and nonleader-2. It will try downloading from both as part of its retry mechanism. * nonLeader-2’s code sync thread also realizes that it is missing code for 2 topologies and follows the same process described in step-3 to download code for missing topologies. * eventually at least one of the nimbuses will have all the code locally and will accept leadership. This sequence diagram describes how leader election and failover would work with multiple components.

![Leader election and failover](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/nimbus_ha_leader_election_and_failover.png)

### Nimbus state store:

>Currently the nimbus stores 2 kind of data **Meta information like supervisor info, assignment info which is stored in zookeeper**, **Actual topology configs and jars that is stored on nimbus host’s local disk**.

To achieve fail over from primary to backup servers nimbus state/data needs to be replicated across all nimbus hosts or needs to be stored in a distributed storage. Replicating the data correctly involves state management, consistency checks and it is hard to test for correctness.However many storm users do not want to take extra dependency on another replicated storage system like HDFS and still need high availability.Eventually, we want to move to the bittorrent protocol for code distribution given the size of the jars and to achieve better scaling when the total number of supervisors is very high. The current file system based model for code distribution works fine with systems that have file system like structure but it fails to support a non file system based approach like bit torrent. To support bit torrent and all the file system based replicated storage systems we propose the following interface:

```java
/**
 * Interface responsible to distribute code in the cluster.
 */
public interface ICodeDistributor {
    /**
     * Prepare this code distributor.
     * @param conf
     */
    void prepare(Map conf);

    /**
     * This API will perform the actual upload of the code to the distributed implementation.
     * The API should return a Meta file which should have enough information for downloader 
     * so it can download the code e.g. for bittorrent it will be a torrent file, in case of something         
     * like HDFS or s3  it might have the actual directory or paths for files to be downloaded.
     * @param dirPath local directory where all the code to be distributed exists.
     * @param topologyId the topologyId for which the meta file needs to be created.
     * @return metaFile
     */
    File upload(Path dirPath, String topologyId);

    /**
     * Given the topologyId and metafile, download the actual code and return the downloaded file's list.
     * @param topologyid
     * @param metafile 
     * @param destDirPath the folder where all the files will be downloaded.
     * @return
     */
    List<File> download(Path destDirPath, String topologyid, File metafile);

    /**
      * Given the topologyId, returns number of hosts where the code has been replicated.
      */
    int getReplicationCount(String topologyId);

   /**
     * Performs the cleanup.
     * @param topologyid
     */
    void cleanup(String topologyid);

    /**
     * Close this distributor.
     * @param conf
     */
    void close(Map conf);
}
```

To support replication we will allow the user to define a code replication factor which would reflect number of nimbus hosts to which the code must be replicated before starting the topology. With replication comes the issue of consistency. We will treat zookeeper’s list of active topologies as our authority for topologies for which the code must exist on a nimbus host.Any nimbus host that does not have all the code for all the topologies which are marked as active in zookeeper will relinquish it’s lock so some other nimbus host could become leader. A background thread on all nimbus host will continuously try to sync code from other hosts where the code was successfully replicated so eventually at least one nimbus will accept leadership as long as at least one seed hosts exists for each active topology. 

Following steps describe code replication amongst nimbus hosts for a topology: * When client uploads jar, nothing changes. * When client submits a topology, leader nimbus calls code distributor’s upload function which will create a metafile stored locally on leader nimbus. Leader nimbus will write new entries under `/storm/code-distributor/topologyId` to notify all nonleader nimbuses that they should download this new code.* We wait on the leader nimbus to ensure at least N non leader nimbus has the code replicated, with a user configurable timeout.* When a non leader nimbus receives the notification about new code, it downloads the meta file from leader nimbus and then downloads the real code by calling code distributor’s download function with metafile as input. * Once non leader finishes downloading code, it will write an entry under `/storm/code-distributor/topologyId` to indicate it is one of the possible places to download the code/metafile in case the leader nimbus dies. * leader nimbus goes ahead and does all the usual things it does as part of submit topologies.

The following sequence diagram describes the communication between different components involved in code distribution.

![Nimbus HA topology submission sequence diagram](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/nimbus_ha_topology_submission.png)

### Thrift and Rest API

In order to avoid `workers/supervisors/ui` talking to zookeeper for getting master nimbus address we are going to modify the `getClusterInfo` API so it can also return nimbus information. getClusterInfo currently returns `ClusterSummary` instance which has a list of `supervisorSummary` and a list of `topologySummary` instances.We will add a list of `NimbusSummary` to the ClusterSummary. See the structures below:

```thrift
struct ClusterSummary {
  1: required list<SupervisorSummary> supervisors;
  3: required list<TopologySummary> topologies;
  4: required list<NimbusSummary> nimbuses;
}

struct NimbusSummary {
  1: required string host;
  2: required i32 port;
  3: required i32 uptime_secs;
  4: required bool isLeader;
  5: required string version;
}
```

This will be used by StormSubmitter, Nimbus clients,supervisors and ui to discover the current leaders and participating nimbus hosts. Any nimbus host will be able to respond to these requests. The nimbus hosts can read this information once from zookeeper and cache it and keep updating the cache when the watchers are fired to indicate any changes,which should be rare in general case.

### Configuration

You can use nimbus ha with default configuration , however the default configuration assumes a single nimbus host so it trades off replication for lower topology submission latency. Depending on your use case you can adjust following configurations: 

- `storm.codedistributor.class`: This is a string representing fully qualified class name of a class that implements `org.apache.storm.codedistributor.ICodeDistributor`.The default is set to `org.apache.storm.codedistributor.LocalFileSystemCodeDistributor`. This class leverages local file system to store both meta files and code/configs. This class adds extra load on zookeeper as even after downloading the code-distrbutor meta file it contacts zookeeper in order to figure out hosts from where it can download actual code/config and to get the current replication count. An alternative is to use `org.apache.storm.hdfs.ha.codedistributor.HDFSCodeDistributor` which relies on HDFS but does not add extra load on zookeeper and will make topology submission faster. 
- `topology.min.replication.count`: Minimum number of nimbus hosts where the code must be replicated before leader nimbus can mark the topology as active and create assignments. Default is 1.
- ` topology.max.replication.wait.time.sec`: Maximum wait time for the nimbus host replication to achieve the nimbus.min.replication.count. Once this time is elapsed nimbus will go ahead and perform topology activation tasks even if required nimbus.min.replication.count is not achieved. The default is 60 seconds, a value of -1 indicates to wait for ever.
- `nimbus.code.sync.freq.secs`:  frequency at which the background thread on nimbus which syncs code for locally missing topologies will run. default is 5 minutes.

>Note: Even though all nimbus hosts have watchers on zookeeper to be notified immediately as soon as a new topology is available for code download, the callback pretty much never results in code download. 

In practice we have observed that the desired replication is only achieved once the background-thread runs. So you should expect your topology submission time to be somewhere between 0 to (2 * nimbus.code.sync.freq.secs) for any nimbus.min.replication.count > 1.

## [Understanding the parallelism of a Storm topology](http://storm.apache.org/releases/1.1.0/Understanding-the-parallelism-of-a-Storm-topology.html)

### What makes a running topology: worker processes, executors and tasks

Storm distinguishes between the following three main entities that are used to actually run a topology in a Storm cluster:

1. Worker processes
2. Executors (threads)
3. Tasks

![simple illustration of their relationships](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/relationships-worker-processes-executors-tasks.png)

>Each executor runs one or more tasks **of the same component(spout or bolt)**

A `worker process` executes a subset of a topology. A worker process belongs to a specific topology and may run one or more executors for one or more components (spouts or bolts) of this topology.

An `executor` is a thread that is spawned by a worker process. It may run one or more tasks for the same component (spout or bolt).

A task performs the actual data processing — each spout or bolt that you implement in your code executes as many tasks across the cluster. The number of tasks for a component is always the same throughout the lifetime of a topology, but the number of executors (threads) for a component can change over time. This means that the following condition holds true: `#threads ≤ #tasks` . By default, the number of tasks is set to be the same as the number of executors, i.e. Storm will run one task per thread.

### Configuring the parallelism of a topology

Note that in Storm’s terminology "parallelism" is specifically used to describe the so-called `parallelism hint`, `which means the initial number of executor (threads) of a component`. In this document though we use the term "parallelism" in a more general sense to describe how you can configure not only the number of executors but also the number of worker processes and the number of tasks of a Storm topology. We will specifically call out when "parallelism" is used in the normal, narrow definition of Storm.

The following sections give an overview of the various configuration options and how to set them in your code. There is more than one way of setting these options though, and the table lists only some of them. Storm currently has the following [order of precedence for configuration settings](http://storm.apache.org/releases/1.1.0/Configuration.html)

#### Number of worker processes

- Description: How many worker processes to create for the `topology` across machines in the cluster.
- Configuration option: [TOPOLOGY_WORKERS](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html#TOPOLOGY_WORKERS)
- How to set in your code (examples): [Config#setNumWorkers](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html)

#### Number of executors (threads)

- Description: How many executors to spawn per component.
- Configuration option: None (pass `parallelism_hint` parameter to `setSpout` or `setBolt`)
- How to set in your code (examples):
	- [TopologyBuilder#setSpout()](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/TopologyBuilder.html)
	- [TopologyBuilder#setBolt()](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/TopologyBuilder.html)
	- Note that as of Storm 0.8 the parallelism_hint parameter now specifies the initial number of executors (not tasks!) for that bolt.

#### Number of tasks

- Description: How many tasks to create per component.
- Configuration option: [TOPOLOGY_TASKS](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/Config.html#TOPOLOGY_TASKS)
- How to set in your code (examples):
	- [ComponentConfigurationDeclarer#setNumTasks()](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/ComponentConfigurationDeclarer.html)

```java
topologyBuilder.setBolt("green-bolt", new GreenBolt(), 2)
               .setNumTasks(4)
               .shuffleGrouping("blue-spout");
```

In the above code we configured Storm to run the bolt GreenBolt with an initial number of two executors and four associated tasks. Storm will run two tasks per executor (thread). If you do not explicitly configure the number of tasks, Storm will run by default one task per executor.

### Example of a running topology

The following illustration shows how a simple topology would look like in operation. The topology consists of three components: one spout called `BlueSpout` and two bolts called `GreenBolt` and `YellowBolt`. The components are linked such that `BlueSpout` sends its output to `GreenBolt`, which in turns sends its own output to `YellowBolt`.

![Example of a running topology](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/example-of-a-running-topology.png)

The `GreenBolt` was configured as per the code snippet above whereas `BlueSpout` and `YellowBolt` only set the parallelism hint (number of executors). Here is the relevant code:

```java
Config conf = new Config();
conf.setNumWorkers(2); // use two worker processes

topologyBuilder.setSpout("blue-spout", new BlueSpout(), 2); // set parallelism hint to 2

topologyBuilder.setBolt("green-bolt", new GreenBolt(), 2)
               .setNumTasks(4)
               .shuffleGrouping("blue-spout");

topologyBuilder.setBolt("yellow-bolt", new YellowBolt(), 6)
               .shuffleGrouping("green-bolt");

StormSubmitter.submitTopology(
        "mytopology",
        conf,
        topologyBuilder.createTopology()
    );
```

#### How to change the parallelism of a running topology

A nifty feature of Storm is that you can increase or decrease the number of worker processes and/or executors without being required to restart the cluster or the topology. The act of doing so is called rebalancing.

You have two options to rebalance a topology:

1. Use the Storm web UI to rebalance the topology.
2. Use the CLI tool storm rebalance as described below.

```bash
## Reconfigure the topology "mytopology" to use 5 worker processes,
## the spout "blue-spout" to use 3 executors and
## the bolt "yellow-bolt" to use 10 executors.

storm rebalance mytopology -n 5 -e blue-spout=3 -e yellow-bolt=10
```

## FAQ


