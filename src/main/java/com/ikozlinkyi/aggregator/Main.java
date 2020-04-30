package com.ikozlinkyi.aggregator;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;


public class Main {
  public static void main(String[] args) {
    ZonedDateTime endTime = ZonedDateTime.now();
    Duration barTimeFrame = Duration.ofMinutes(1);

    Duration timeFrame = Duration.ofMinutes(3);
    AggregationTimeSeries timeSeries = new AggregationTimeSeriesImpl(timeFrame);

    Bar[] bars = {
        new BaseBar(
            barTimeFrame,
            endTime,
            timeSeries.numOf(10),
            timeSeries.numOf(20),
            timeSeries.numOf(8),
            timeSeries.numOf(14),
            timeSeries.numOf(15),
            timeSeries.numOf(3)
        ),
        new BaseBar(
            barTimeFrame,
            endTime.plusNanos(barTimeFrame.toNanos()),
            timeSeries.numOf(15),
            timeSeries.numOf(19),
            timeSeries.numOf(9),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            endTime.plusNanos(2 * barTimeFrame.toNanos()),
            timeSeries.numOf(14),
            timeSeries.numOf(18),
            timeSeries.numOf(5),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            endTime.plusNanos(3 * barTimeFrame.toNanos()),
            timeSeries.numOf(10),
            timeSeries.numOf(12),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        ),
        new BaseBar(
            barTimeFrame,
            endTime.plusNanos(4 * barTimeFrame.toNanos()),
            timeSeries.numOf(10),
            timeSeries.numOf(12),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        )
    };

    Arrays.stream(bars).forEach(bar -> timeSeries.addBar(bar, false));

    AggregationTimeSeries aggregatedSeries =  timeSeries.aggregate();
    System.out.println(aggregatedSeries.getBarData());
  }

}
