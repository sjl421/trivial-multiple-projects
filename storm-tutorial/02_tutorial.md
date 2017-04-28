# Tutorial

In this tutorial, you'll learn how to create Storm topologies and deploy them to a Storm cluster. Java will be the main language used, but a few examples will use Python to illustrate Storm's multi-language capabilities.

## Preliminaries

This tutorial uses examples from the [storm-starter][1] project. It's recommended that you clone the project and follow along with the examples. Read [Setting up a development environment][2] and [Creating a new Storm project][3] to get your machine set up.


### storm-starter overview

storm-starter contains a variety of examples of using Storm. If this is your first time working with Storm, check out these topologies first:

1. [ExclamationTopology][4]: Basic topology written in all Java
2. [WordCountTopology][5]: Basic topology that makes use of multilang by implementing one bolt in Python
3. [ReachTopology][6]: Example of complex DRPC on top of Storm

[1]:http://github.com/apache/storm/blob/v1.1.0/examples/storm-starter
[2]:http://storm.apache.org/releases/1.1.0/Setting-up-development-environment.html
[3]:http://storm.apache.org/releases/1.1.0/Creating-a-new-Storm-project.html
[4]:https://github.com/apache/storm/blob/v1.1.0/examples/storm-starter/src/jvm/org/apache/storm/starter/ExclamationTopology.java
[5]:https://github.com/apache/storm/blob/v1.1.0/examples/storm-starter/src/jvm/org/apache/storm/starter/WordCountTopology.java
[6]:https://github.com/apache/storm/blob/v1.1.0/examples/storm-starter/src/jvm/org/apache/storm/starter/ReachTopology.java

After you have familiarized yourself with these topologies, take a look at the other topopologies in [src/jvm/org/apache/storm/starter/](https://github.com/apache/storm/blob/v1.1.0/examples/storm-starter/src/jvm/org/apache/storm/starter) such as [RollingTopWords](https://github.com/apache/storm/blob/v1.1.0/examples/storm-starter/src/jvm/org/apache/storm/starter/RollingTopWords.java) for more advanced implementations.

```java
/**
 * Created by dhy on 17-4-11.
 * This is a basic example of a Storm topology
 */
public class ExclamationTopology {
    public static class ExclamationBolt extends BaseRichBolt {

        OutputCollector _collector;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            _collector = collector;
        }

        @Override
        public void execute(Tuple tuple) {
            System.out.println(tuple.getString(0));
            _collector.emit(tuple, new Values(tuple.getString(0) + "!!!", tuple.getString(0) + "???"));
            _collector.ack(tuple);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("wordWithExclamationBolt", "wordWithQuestion"));
        }
    }

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("word", new TestWordSpout(), 10);
        builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("word");
        builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");

        Config config = new Config();
//        config.setDebug(true);

        if (args != null && args.length > 0) {
            config.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", config, builder.createTopology());
            Utils.sleep(10000);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }
}
```

```java
public class WordCountTopology {

    public static class SplitSentence extends BaseRichBolt {

        private OutputCollector _collector;

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word"));
        }

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this._collector = collector;
        }

        @Override
        public void execute(Tuple tuple) {
            String text = tuple.getString(0);
            String[] words = text.split(" ");
            for (String word : words) {
                _collector.emit(tuple, new Values(word));
            }
            _collector.ack(tuple);
        }
    }

    public static class WordCount extends BaseBasicBolt {
        Map<String, Integer> counts = new HashMap<>();

        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String word = tuple.getString(0);
            Integer count = counts.get(word);
            if (count == null) {
                count = 0;
            }
            count++;
            System.out.println(word + ":" + count);
            counts.put(word, count);
            collector.emit(new Values(word, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word", "count"));
        }
    }

    public static void main(String[] args) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new RandomSentenceSpout(), 5);

        builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");
        builder.setBolt("count", new WordCount(), 12).fieldsGrouping("split", new Fields("word"));

        Config conf = new Config();
//        conf.setDebug(true);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

            Thread.sleep(10000);

            cluster.shutdown();
        }
    }

}
```

```java
/**
 * Created by dhy on 17-4-11.
 *
 * This is a good example of doing complex Distributed RPC on top of Storm. This program creates
 * a topology that can compute the reach for any URL on Twitter in realtime by parallelizing
 * the whole computation.
 * <p/>
 * Reach is the number of unique people exposed to a URL on Twitter.To compute reach, you
 * have to get all the people who tweeted the URL, get all the followers of all those people,
 * unique that set of followers, and then count the unique set. It's an intense computation
 * that can involve thousands of database calls and tens of millions of followers records.
 * <p/>
 * This storm topology does every piece of that computation in parallel, turning what would
 * be a computation that minutes on a single machine into one that takes just a couple seconds.
 * <p/>
 * For the purposes of demonstration, this topology replaces the use of actual DBs with
 * in-memory hashmaps.
 */
public class ReachTopology {

    public static class GetTweeters extends BaseBasicBolt {
        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            Object id = tuple.getValue(0);
            String url = tuple.getString(1);
            List<String> tweeters = TWEETERS_DB.get(url);
            if (tweeters != null) {
                for (String tweeter : tweeters) {
                    collector.emit(new Values(id, tweeter));
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "tweeter"));
        }
    }

    public static class GetFollowers extends BaseBasicBolt {
        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            Object id = tuple.getValue(0);
            String tweeter = tuple.getString(1);
            List<String> followers = FOLLOWERS_DB.get(tweeter);
            if (followers != null) {
                for (String follower : followers) {
                    collector.emit(new Values(id, follower));
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "follower"));
        }
    }

    public static class PartialUniquer extends BaseBatchBolt {
        BatchOutputCollector _collector;
        Object _id;
        Set<String> _followers = new HashSet<>();

        @Override
        public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
            _collector = collector;
            _id = id;
        }

        @Override
        public void execute(Tuple tuple) {
            _followers.add(tuple.getString(1));
        }

        @Override
        public void finishBatch() {
            _collector.emit(new Values(_id, _followers.size()));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "partial-count"));
        }
    }

    public static class CountAggregator extends BaseBatchBolt {
        BatchOutputCollector _collector;
        Object _id;
        int _count = 0;

        @Override
        public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
            _collector = collector;
            _id = id;
        }

        @Override
        public void execute(Tuple tuple) {
            _count += tuple.getInteger(1);
        }

        @Override
        public void finishBatch() {
            _collector.emit(new Values(_id, _count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "reach"));
        }
    }

    public static LinearDRPCTopologyBuilder construct() {
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("reach");
        builder.addBolt(new GetTweeters(), 4);
        builder.addBolt(new GetFollowers(), 12).shuffleGrouping();
        builder.addBolt(new PartialUniquer(), 6).fieldsGrouping(new Fields("id", "follower"));
        builder.addBolt(new CountAggregator(), 3).fieldsGrouping(new Fields("id"));
        return builder;
    }

    public static Map<String, List<String>> TWEETERS_DB = new HashMap<String, List<String>>() {{
        put("foo.com/blog/1", Arrays.asList("sally", "bob", "tim", "george", "nathan"));
        put("engineering.twitter.com/blog/5", Arrays.asList("adam", "david", "sally", "nathan"));
        put("tech.backtype.com/blog/123", Arrays.asList("tim", "mike", "john"));
    }};

    public static Map<String, List<String>> FOLLOWERS_DB = new HashMap<String, List<String>>() {{
        put("sally", Arrays.asList("bob", "tim", "alice", "adam", "jim", "chris", "jai"));
        put("bob", Arrays.asList("sally", "nathan", "jim", "mary", "david", "vivian"));
        put("tim", Arrays.asList("alex"));
        put("nathan", Arrays.asList("sally", "bob", "adam", "harry", "chris", "vivian", "emily", "jordan"));
        put("adam", Arrays.asList("david", "carissa"));
        put("mike", Arrays.asList("john", "bob"));
        put("john", Arrays.asList("alice", "nathan", "jim", "mike", "bob"));
    }};

    public static void main(String[] args) throws Exception {
        LinearDRPCTopologyBuilder builder = construct();

        Config conf = new Config();
        if (args == null || args.length == 0) {
            conf.setMaxTaskParallelism(3);
            LocalDRPC drpc = new LocalDRPC();
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("reach-drpc", conf, builder.createLocalTopology(drpc));

            String[] urlsToTry = new String[]{ "foo.com/blog/1", "engineering.twitter.com/blog/5", "notaurl.com" };
            for (String url : urlsToTry) {
                System.err.println("Reach of " + url + ": " + drpc.execute("reach", url));
            }

            cluster.shutdown();
            drpc.shutdown();
        }
        else {
            conf.setNumWorkers(6);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createRemoteTopology());
        }
    }
}
```

## Components of a Storm cluster

A Storm cluster is superficially similar to a Hadoop cluster. Whereas on Hadoop you run "MapReduce jobs", on Storm you run "topologies". "Jobs" and "topologies" themselves are very different -- one key difference is that a MapReduce job eventually finishes, whereas a topology processes messages forever (or until you kill it).

>There are two kinds of nodes on a Storm cluster: the master node and the worker nodes. The master node runs a daemon called "Nimbus" that is similar to Hadoop's "JobTracker". Nimbus is responsible for distributing code around the cluster, assigning tasks to machines, and monitoring for failures.

>Each worker node runs a daemon called the "Supervisor". The supervisor listens for work assigned to its machine and starts and stops worker processes as necessary based on what Nimbus has assigned to it. Each worker process executes a subset of a topology; a running topology consists of many worker processes spread across many machines.

![storm cluster](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/storm-cluster.png)

All coordination between Nimbus and the Supervisors is done through a Zookeeper cluster. Additionally, the Nimbus daemon and Supervisor daemons are fail-fast and stateless; all state is kept in Zookeeper or on local disk. This means you can kill -9 Nimbus or the Supervisors and they'll start back up like nothing happened. This design leads to Storm clusters being incredibly stable.

## Topologies

To do realtime computation on Storm, you create what are called "topologies". A topology is a graph of computation. Each node in a topology contains processing logic, and links between nodes indicate how data should be passed around between nodes.

Running a topology is straightforward. First, you package all your code and dependencies into a single jar. Then, you run a command like the following:

```bash
storm jar all-my-code.jar org.apache.storm.MyTopology arg1 arg2
```

This runs the class `org.apache.storm.MyTopology` with the arguments `arg1` and `arg2`. The main function of the class defines the topology and submits it to Nimbus. The `storm jar` part takes care of connecting to Nimbus and uploading the jar.

Since topology definitions are just Thrift structs, and Nimbus is a Thrift service, you can create and submit topologies using any programming language.The above example is the easiest way to do it from a JVM-based language. See [Running topologies on a production cluster](http://storm.apache.org/releases/1.1.0/Running-topologies-on-a-production-cluster.html) for more information on starting and stopping topologies.

## Streams

The core abstraction in Storm is the "stream". A stream is an unbounded sequence of tuples. **Storm provides the primitives for transforming a stream into a new stream in a distributed and reliable way**. For example, you may transform a stream of tweets into a stream of trending topics.

The basic primitives Storm provides for doing stream transformations are "spouts" and "bolts". Spouts and bolts have interfaces that you implement to run your application-specific logic.

A spout is a source of streams. For example, a spout may read tuples off of a [Kestrel](http://github.com/nathanmarz/storm-kestrel) queue and emit them as a stream. Or a spout may connect to the Twitter API and emit a stream of tweets.

A bolt consumes any number of input streams, does some processing, and possibly emits new streams. Complex stream transformations, like computing a stream of trending topics from a stream of tweets, require multiple steps and thus multiple bolts. Bolts can do anything from **run functions, filter tuples, do streaming aggregations, do streaming joins, talk to databases, and more.**

Networks of spouts and bolts are packaged into a "topology" which is the top-level abstraction that you submit to Storm clusters for execution. A topology is a graph of stream transformations where each node is a spout or bolt. Edges in the graph indicate which bolts are subscribing to which streams. When a spout or bolt emits a tuple to a stream, it sends the tuple to every bolt that subscribed to that stream.

![topology](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/topology.png)

Links between nodes in your topology indicate how tuples should be passed around. For example, if there is a link between Spout A and Bolt B, a link from Spout A to Bolt C, and a link from Bolt B to Bolt C, then everytime Spout A emits a tuple, it will send the tuple to both Bolt B and Bolt C. All of Bolt B's output tuples will go to Bolt C as well.

Each node in a Storm topology executes in parallel. In your topology, you can specify how much parallelism you want for each node, and then Storm will spawn that number of threads across the cluster to do the execution.

A topology runs forever, or until you kill it. Storm will automatically reassign any failed tasks. Additionally, Storm guarantees that there will be no data loss, even if machines go down and messages are dropped.

## Data model

Storm uses tuples as its data model. A tuple is a named list of values, and a field in a tuple can be an object of any type. Out of the box, Storm supports all the primitive types, strings, and byte arrays as tuple field values. To use an object of another type, you just need to implement a [serializer](http://storm.apache.org/releases/1.1.0/Serialization.html) for the type.

Every node in a topology must declare the output fields for the tuples it emits. For example, this bolt declares that it emits 2-tuples with the fields "double" and "triple":

```java
public class DoubleAndTripleBolt extends BaseRichBolt {
    private OutputCollectorBase _collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollectorBase collector) {
        _collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        int val = input.getInteger(0);        
        _collector.emit(input, new Values(val*2, val*3));
        _collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("double", "triple"));
    }    
}
```

The `declareOutputFields` function declares the output fields `["double", "triple"]` for the component. The rest of the bolt will be explained in the upcoming sections.

## A simple topology

Let's take a look at a simple topology to explore the concepts more and see how the code shapes up. Let's look at the `ExclamationTopology` definition from storm-starter:

```java
TopologyBuilder builder = new TopologyBuilder();        
builder.setSpout("words", new TestWordSpout(), 10);        
builder.setBolt("exclaim1", new ExclamationBolt(), 3)
        .shuffleGrouping("words");
builder.setBolt("exclaim2", new ExclamationBolt(), 2)
        .shuffleGrouping("exclaim1");
```

>`setBolt` returns an [InputDeclarer](http://storm.apache.org/releases/1.1.0/javadocs/org/apache/storm/topology/InputDeclarer.html) object that is used to define the inputs to the Bolt. 

Here, component "exclaim1" declares that it wants to read all the tuples emitted by component "words" using a shuffle grouping, and component "exclaim2" declares that it wants to read all the tuples emitted by component "exclaim1" using a shuffle grouping. "shuffle grouping" means that tuples should be randomly distributed from the input tasks to the bolt's tasks. There are many ways to group data between components. These will be explained in a few sections.

If you wanted component "exclaim2" to read all the tuples emitted by both component "words" and component "exclaim1", you would write component "exclaim2"'s definition like this:

```java
builder.setBolt("exclaim2", new ExclamationBolt(), 5)
            .shuffleGrouping("words")
            .shuffleGrouping("exclaim1");
```

Let's dig into the implementations of the spouts and bolts in this topology. Spouts are responsible for emitting new messages into the topology. `TestWordSpout` in this topology emits a random word from the list ["nathan", "mike", "jackson", "golda", "bertels"] as a 1-tuple every 100ms. The implementation of `nextTuple()` in TestWordSpout looks like this:

```java
public void nextTuple() {
    Utils.sleep(100);
    final String[] words = new String[] {"nathan", "mike", "jackson", "golda", "bertels"};
    final Random rand = new Random();
    final String word = words[rand.nextInt(words.length)];
    _collector.emit(new Values(word));
}
```

`ExclamationBolt` appends the string "!!!" to its input.

The `prepare` method provides the bolt with an `OutputCollector` that is used for emitting tuples from this bolt. Tuples can be emitted at anytime from the bolt -- in the `prepare`, `execute`, or `cleanup` methods, or even asynchronously in another thread. This prepare implementation simply saves the OutputCollector as an instance variable to be used later on in the execute method.

The `execute` method receives a tuple from one of the bolt's inputs. The `ExclamationBolt` grabs the first field from the tuple and emits a new tuple with the string "!!!" appended to it. If you implement a bolt that subscribes to multiple input sources, you can find out which component the `Tuple` came from by using the `Tuple#getSourceComponent` method.

There's a few other things going on in the `execute` method, namely that the input tuple is passed as the first argument to `emit` and the input tuple is acked on the final line. These are part of Storm's reliability API for guaranteeing no data loss and will be explained later in this tutorial.

The `cleanup` method is called when a Bolt is being shutdown and should cleanup any resources that were opened. **There's no guarantee that this method will be called on the cluster**: for example, if the machine the task is running on blows up, there's no way to invoke the method. The cleanup method is intended for when you run topologies in local mode (where a Storm cluster is simulated in process), and you want to be able to run and kill many topologies without suffering any resource leaks.

The `getComponentConfiguration` method allows you to configure various aspects of how this component runs. 

Methods like `cleanup` and `getComponentConfiguration` are often not needed in a bolt implementation. You can define bolts more succinctly by using a base class that provides default implementations where appropriate. ExclamationBolt can be written more succinctly by extending `BaseRichBolt`, like so:

## Stream groupings

A stream grouping tells a topology how to send tuples between two components. Remember, spouts and bolts execute in parallel as many tasks across the cluster. If you look at how a topology is executing at the task level, it looks something like this:

![topology tasks](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/topology-tasks.png)

>When a task for Bolt A emits a tuple to Bolt B, which task should it send the tuple to?

A "stream grouping" answers this question by telling Storm how to send tuples between sets of tasks. Before we dig into the different kinds of stream groupings, let's take a look at another topology from storm-starter. This WordCountTopology reads sentences off of a spout and streams out of WordCountBolt the total number of times it has seen that word before:

```java
TopologyBuilder builder = new TopologyBuilder();

builder.setSpout("sentences", new RandomSentenceSpout(), 5);        
builder.setBolt("split", new SplitSentence(), 8)
        .shuffleGrouping("sentences");
builder.setBolt("count", new WordCount(), 12)
        .fieldsGrouping("split", new Fields("word"));
```

`SplitSentence` emits a tuple for each word in each sentence it receives, and `WordCount` keeps a map in memory from word to count. Each time WordCount receives a word, it updates its state and emits the new word count.

The simplest kind of grouping is called a "shuffle grouping" which sends the tuple to a random task. A shuffle grouping is used in the `WordCountTopology` to send tuples from `RandomSentenceSpout` to the `SplitSentence` bolt. It has the effect of evenly distributing the work of processing the tuples across all of `SplitSentence` bolt's tasks.

A more interesting kind of grouping is the "fields grouping". A fields grouping is used between the SplitSentence bolt and the WordCount bolt. It is critical for the functioning of the WordCount bolt that the same word always go to the same task. Otherwise, more than one task will see the same word, and they'll each emit incorrect values for the count since each has incomplete information. A fields grouping lets you group a stream by a subset of its fields. This causes equal values for that subset of fields to go to the same task. Since WordCount subscribes to SplitSentence's output stream using a fields grouping on the "word" field, the same word always goes to the same task and the bolt produces the correct output.

>Fields groupings are the basis of implementing streaming **joins and streaming aggregations** as well as a plethora of other use cases. Underneath the hood, fields groupings are implemented using mod hashing.

## Distributed RPC

This tutorial showed how to do basic stream processing on top of Storm. There's lots more things you can do with Storm's primitives. One of the most interesting applications of Storm is Distributed RPC, where you parallelize the computation of intense functions on the fly. Read more about Distributed RPC [here](http://storm.apache.org/releases/1.1.0/Distributed-RPC.html).

The idea behind distributed RPC (DRPC) is to parallelize the computation of really intense functions on the fly using Storm.The Storm topology takes in as input a stream of function arguments, and it emits an output stream of the results for each of those function calls.

DRPC is not so much a feature of Storm as it is a pattern expressed from Storm's primitives of streams, spouts, bolts, and topologies. DRPC could have been packaged as a separate library from Storm, but it's so useful that it's bundled with Storm.

### High level overview

Distributed RPC is coordinated by a "DRPC server" (Storm comes packaged with an implementation of this). The DRPC server coordinates receiving an RPC request, sending the request to the Storm topology, receiving the results from the Storm topology, and sending the results back to the waiting client.From a client's perspective, a distributed RPC call looks just like a regular RPC call. For example, here's how a client would compute the results for the "reach" function with the argument "http://twitter.com":

```java
DRPCClient client = new DRPCClient("drpc-host", 3772);
String result = client.execute("reach", "http://twitter.com");
```

The distributed RPC workflow looks like this:

![drpc-workflow](http://hangyudu.oss-cn-shanghai.aliyuncs.com/03_storm_documentation/drpc-workflow.png)

A client sends the DRPC server the name of the function to execute and the arguments to that function. The topology implementing that function uses a `DRPCSpout` to receive a function invocation stream from the DRPC server.Each function invocation is tagged with a unique id by the DRPC server. The topology then computes the result and at the end of the topology a bolt called `ReturnResults` connects to the DRPC server and gives it the result for the function invocation id. The DRPC server then uses the id to match up that result with which client is waiting, unblocks the waiting client, and sends it the result.

### LinearDRPCTopologyBuilder

Storm comes with a topology builder called LinearDRPCTopologyBuilder that automates almost all the steps involved for doing DRPC. These include:

1. Setting up the spout
2. Returning the results to the DRPC server
3. Providing functionality to bolts for doing finite aggregations over groups of tuples

Let's look at a simple example. Here's the implementation of a DRPC topology that returns its input argument with a "!" appended:

```java
public static class ExclaimBolt extends BaseBasicBolt {
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String input = tuple.getString(1);
        collector.emit(new Values(tuple.getValue(0), input + "!"));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "result"));
    }
}

public static void main(String[] args) throws Exception {
    LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("exclamation");
    builder.addBolt(new ExclaimBolt(), 3);
    // ...
}
```

As you can see, there's very little to it. When creating the `LinearDRPCTopologyBuilder`, you tell it the name of the DRPC function for the topology. A single DRPC server can coordinate many functions, and the function name distinguishes the functions from one another. The first bolt you declare will take in as input 2-tuples, where **the first field is the request id and the second field is the arguments for that request**. LinearDRPCTopologyBuilder expects the last bolt to emit an output stream containing 2-tuples of the form [id, result]. Finally, all intermediate tuples must contain the request id as the first field.

### Local mode DRPC

DRPC can be run in local mode. Here's how to run the above example in local mode:

```java
LocalDRPC drpc = new LocalDRPC();
LocalCluster cluster = new LocalCluster();

cluster.submitTopology("drpc-demo", conf, builder.createLocalTopology(drpc));

System.out.println("Results for 'hello':" + drpc.execute("exclamation", "hello"));

cluster.shutdown();
drpc.shutdown();
```
