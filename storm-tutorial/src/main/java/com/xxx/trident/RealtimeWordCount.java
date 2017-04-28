package com.xxx.trident;

import org.apache.storm.Config;
import org.apache.storm.thrift.TException;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.operation.builtin.FilterNull;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.operation.builtin.Sum;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.trident.testing.Split;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.DRPCClient;

/**
 * Created by dhy on 17-4-13.
 * <ol>
 *     <li>Compute streaming word count from an input stream of sentences</li>
 *     <li>Implement queries to get the sum of the counts for a list of words</li>
 * </ol>
 */
public class RealtimeWordCount {
    public static void main(String[] args) throws TException {
        FixedBatchSpout spout = new FixedBatchSpout(new Fields("sentence"), 3,
                                                     new Values("the cow jumped over the moon"),
                                                        new Values("the man went to the store and bought some candy"),
                                                        new Values("four score and seven years ago"),
                                                        new Values("how many apples can you eat"));
        spout.setCycle(true);

        TridentTopology topology = new TridentTopology();
        TridentState wordCounts =
                topology.newStream("spout1", spout)
                        .each(new Fields("sentence"), new Split(), new Fields("word"))
                        .groupBy(new Fields("word"))
                        .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))
                        .parallelismHint(6);
        topology.newDRPCStream("words")
                .each(new Fields("args"), new Split(), new Fields("word"))
                .groupBy(new Fields("word"))
                .stateQuery(wordCounts, new Fields("word"), new MapGet(), new Fields("count"))
                .each(new Fields("count"), new FilterNull())
                .aggregate(new Fields("count"), new Sum(), new Fields("sum"));
        topology.build();

        DRPCClient client = new DRPCClient(new Config(),"127.0.0.1", 3772);

        System.out.println(client.execute("words", "cat dog the man"));
    }
}
