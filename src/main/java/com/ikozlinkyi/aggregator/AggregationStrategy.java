package com.ikozlinkyi.aggregator;

import java.util.List;
import org.ta4j.core.Bar;

public interface AggregationStrategy {

  Bar aggregateList(List<Bar> bars);

  Bar aggregatePair(Bar bar1, Bar bar2);
}
