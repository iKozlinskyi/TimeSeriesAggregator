package com.ikozlinkyi.aggregator;

import org.ta4j.core.Bar;

public class AggregationUtils {
  private static AggregationUtils INSTANCE = new AggregationUtils();

  /**
   * Container of utility methods
   */
  private AggregationUtils() { }

  public static AggregationUtils getInstance() {
    return INSTANCE;
  }

  /**
   * returns true if bars have end time in different days (end time of 00:00 is treated as previous
   * day)
   * @param bar1
   * @param bar2
   * @return
   */
  public boolean isBarsOfSameDayEnd(Bar bar1, Bar bar2) {
    return !(bar1.getEndTime().getDayOfMonth() == bar2.getEndTime().getDayOfMonth()) ||
        bar2.getEndTime().getMinute() != 0;
  }
}
