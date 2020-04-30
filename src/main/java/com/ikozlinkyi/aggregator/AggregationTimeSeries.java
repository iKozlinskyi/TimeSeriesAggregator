package com.ikozlinkyi.aggregator;

import org.ta4j.core.TimeSeries;

public interface AggregationTimeSeries extends TimeSeries {
  AggregationTimeSeries aggregate();
}
