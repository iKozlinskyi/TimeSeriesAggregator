package com.ikozlinkyi.aggregator;

import org.ta4j.core.Bar;

public class AggregationUtils {
  private static AggregationUtils INSTANCE = new AggregationUtils();

  private AggregationUtils() { }

  public static AggregationUtils getInstance() {
    return INSTANCE;
  }

  public boolean isBarsOfSameDayEnd(Bar bar1, Bar bar2) {
    return !(bar1.getEndTime().getDayOfMonth() == bar2.getEndTime().getDayOfMonth()) ||
        bar2.getEndTime().getMinute() != 0;
  }
}
