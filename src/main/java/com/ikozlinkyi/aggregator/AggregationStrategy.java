package com.ikozlinkyi.aggregator;

import java.util.List;
import org.ta4j.core.Bar;

/**
 * This interface defines method signatures for aggregation of bars
 */
public interface AggregationStrategy {

  /**
   * @param bars List of bars to be aggregated into one bar
   * @return Aggregated bar
   */
  Bar aggregateList(List<Bar> bars);

  /**
   *
   * @param bar1
   * @param bar2
   * @return Bar obtained after aggregation
   */
  Bar aggregatePair(Bar bar1, Bar bar2);
}
