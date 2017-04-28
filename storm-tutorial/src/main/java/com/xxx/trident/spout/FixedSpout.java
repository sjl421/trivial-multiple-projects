package com.xxx.trident.spout;

import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * Created by dhy on 17-4-14.
 *
 */
public class FixedSpout extends FixedBatchSpout {
    public FixedSpout() {
        super(new Fields("sentence"), 3,
                new Values("the cow jumped over the moon"),
                new Values("the man went to the store and bought some candy"),
                new Values("four score and seven years ago"),
                new Values("how many apples can you eat"));
    }
}
