package com.ikozlinkyi.aggregator;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;


public class Main {
  public static void main(String[] args) {
    Duration barTimeFrame = Duration.ofMinutes(1);
    ZonedDateTime tomorrowMidnight =
        ZonedDateTime.now().plusDays(1).with(LocalTime.of(0, 0));

    Duration timeFrame = Duration.ofMinutes(3);
    AggregationTimeSeries timeSeries = new AggregationTimeSeriesImpl(timeFrame);

    Bar[] bars = {
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight.minusMinutes(2),
            timeSeries.numOf(10),
            timeSeries.numOf(20),
            timeSeries.numOf(8),
            timeSeries.numOf(14),
            timeSeries.numOf(15),
            timeSeries.numOf(3)
        ),
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight.minusMinutes(1),
            timeSeries.numOf(14),
            timeSeries.numOf(19),
            timeSeries.numOf(9),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight,
            timeSeries.numOf(15),
            timeSeries.numOf(19),
            timeSeries.numOf(10),
            timeSeries.numOf(18),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        ),
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight.plusMinutes(1),
            timeSeries.numOf(14),
            timeSeries.numOf(18),
            timeSeries.numOf(5),
            timeSeries.numOf(12),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight.plusMinutes(2),
            timeSeries.numOf(13),
            timeSeries.numOf(18),
            timeSeries.numOf(10),
            timeSeries.numOf(17),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        )
    };

    Arrays.stream(bars).forEach(bar -> timeSeries.addBar(bar, false));

    AggregationTimeSeries aggregatedSeries =  timeSeries.aggregate();
    System.out.println(aggregatedSeries.getBarData());
  }

}
