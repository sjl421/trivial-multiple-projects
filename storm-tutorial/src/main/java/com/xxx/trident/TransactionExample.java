package com.xxx.trident;

import com.xxx.spout.RandomSentenceSpout;
import com.xxx.trident.function.SplitFunction;
import org.apache.storm.LocalCluster;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;

import java.util.Collections;

/**
 * Created by dhy on 17-4-14.
 *
 */
public class TransactionExample {
    public static void main(String[] args) {
        RandomSentenceSpout spout = new RandomSentenceSpout();
        TridentTopology topology = new TridentTopology();
        topology.newStream("spout", spout)
                .each(new Fields("sentence"), new SplitFunction(), new Fields("word"))
                .project(new Fields("word"))
                .peek(tuple -> System.out.println(tuple.getString(0)))
                .name("split")
                .groupBy(new Fields("word"));
        LocalCluster local = new LocalCluster();
        local.submitTopology("topology", Collections.emptyMap(), topology.build());
    }
}
