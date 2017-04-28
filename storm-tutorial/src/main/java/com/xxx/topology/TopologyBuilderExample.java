package com.xxx.topology;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.testing.TestGlobalCount;
import org.apache.storm.testing.TestWordCounter;
import org.apache.storm.testing.TestWordSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhy on 17-4-6.
 *
 */
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

        StormSubmitter.submitTopology("myTopology", conf, builder.createTopology());
    }
}
