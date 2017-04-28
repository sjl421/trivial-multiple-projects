package com.xxx.trident;

import com.xxx.spout.RandomSentenceSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.Map;

/**
 * Created by dhy on 17-4-13.
 *
 */
public class SlidingWindowBolt extends BaseWindowedBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }


    @Override
    public void execute(TupleWindow inputWindow) {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------\n");
        for (Tuple tuple : inputWindow.get()) {
            // do the windowing computation
            sb.append(tuple.getString(0)).append("\n");
        }
        sb.append("----------------\n");
        System.out.println(sb.toString());
        collector.emit(new Values(""));
    }

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new RandomSentenceSpout(), 1);
        builder.setBolt("slidingWindowBolt",
                            new SlidingWindowBolt().withWindow(new Count(10), new Count(5)),
                            1)
                .shuffleGrouping("spout");
        Config config = new Config();
        config.setDebug(true);
        config.setNumWorkers(1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("local_topology", config, builder.createTopology());
    }
}
