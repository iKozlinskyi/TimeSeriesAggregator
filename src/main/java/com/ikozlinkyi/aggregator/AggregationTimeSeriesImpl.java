package com.ikozlinkyi.aggregator;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;


public class AggregationTimeSeriesImpl extends BaseTimeSeries implements AggregationTimeSeries {

  private Duration aggregationTimeFrame;
  private Duration barsTimePeriod;
  private static AggregationStrategy aggregationStrategy = AggregationStrategyImpl.getInstance();

  public AggregationTimeSeriesImpl(Duration aggregationTimeFrame) {
    super();
    this.aggregationTimeFrame = aggregationTimeFrame;
  }

  public AggregationTimeSeriesImpl(List<Bar> bars) {
    super(bars);
  }

  public Duration getBarsTimePeriod() {
    return this.barsTimePeriod;
  }

  @Override
  public AggregationTimeSeries aggregate() {
    List<List<Bar>> barsSplitPerTimeFrame = splitBarList();
    List<Bar> aggregatedBars = barsSplitPerTimeFrame
        .stream()
        .map(aggregationStrategy::aggregateList)
        .collect(Collectors.toList());

    return new AggregationTimeSeriesImpl(aggregatedBars);
  }

  @Override
  public void addBar(Bar bar, boolean replace) {
    int barCount = super.getBarCount();

    if ((barCount == 1 && !replace) ||
        (barCount == 2 && replace)) {
      setBarsTimePeriod(bar);
    } else if (barCount != 0){
      checkNewBarTimePeriod(bar);
    }

    super.addBar(bar, replace);
  }

  private List<List<Bar>> splitBarList() {
    int barsPerFrame = (int) (this.aggregationTimeFrame.toMillis() / this.barsTimePeriod.toMillis());
    int framesNumber = super.getBarCount() / barsPerFrame;
    List<List<Bar>> barsSplitPerTimeFrame = new ArrayList<>();

    List<Bar> bars = super.getBarData();

    for (int i = 0; i <= framesNumber; i++) {
      int subListEndIdx = Math.min(barsPerFrame * (i + 1), super.getBarCount());

      List<Bar> stackedBars = bars.subList(barsPerFrame * i, subListEndIdx);
      barsSplitPerTimeFrame.add(stackedBars);
    }

    return barsSplitPerTimeFrame;
  }

  private void setBarsTimePeriod(Bar newBar) {
    ZonedDateTime bar0EndTime = super.getBar(0).getEndTime();
    ZonedDateTime bar1EndTime = newBar.getEndTime();

    Duration timePeriod = Duration.between(bar0EndTime, bar1EndTime);

    if (timePeriod.compareTo(this.aggregationTimeFrame) > 0) {
      throw new IllegalArgumentException("Time period for bars cannot be greater than aggregation time frame");
    }

    this.barsTimePeriod = timePeriod;
  }

  private void checkNewBarTimePeriod(Bar bar) {
    if (!bar.getTimePeriod().equals(barsTimePeriod)) {
      throw new IllegalArgumentException("Bars should have same end time difference");
    }
  }
}
