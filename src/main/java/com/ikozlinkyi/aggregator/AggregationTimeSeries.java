package com.ikozlinkyi.aggregator;

import org.ta4j.core.TimeSeries;

/**
 * This interface adds <b>aggregate()</b> method to TimeSeries interface
 */
public interface AggregationTimeSeries extends TimeSeries {

  /**
   * @return TimeSeries with underlying bar data aggregated
   */
  AggregationTimeSeries aggregate();
}
