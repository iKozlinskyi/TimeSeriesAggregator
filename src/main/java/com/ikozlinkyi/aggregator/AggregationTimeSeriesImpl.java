package com.ikozlinkyi.aggregator;

import static com.ikozlinkyi.aggregator.constants.ErrorMessages.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;


public class AggregationTimeSeriesImpl extends BaseTimeSeries implements AggregationTimeSeries {

  private Duration aggregationTimeFrame;
  private Duration barsTimePeriod;

  private static AggregationStrategy aggregationStrategy = AggregationStrategyImpl.getInstance();
  private static AggregationUtils aggregationUtils = AggregationUtils.getInstance();

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

    if ((barCount == 0) ||
        (barCount == 1 && replace)) {
      setBarsTimePeriod(bar);
    } else {
      checkNewBarTimePeriod(bar);
    }

    super.addBar(bar, replace);
  }

  private List<List<Bar>> splitBarList() {
    int barsPerFrame = (int) (this.aggregationTimeFrame.toMillis() / this.barsTimePeriod.toMillis());
    List<List<Bar>> barsSplitPerTimeFrame = new ArrayList<>();

    List<Bar> bars = super.getBarData();

    int barIdx = 0;
    int bucketIdx = 0;
    while (barIdx < bars.size()) {

      barsSplitPerTimeFrame.add(new ArrayList<>());

      for (int bucketCounter = 0; bucketCounter < barsPerFrame && barIdx < bars.size(); bucketCounter++) {

        if (bucketCounter != 0 &&
            !aggregationUtils.isBarsOfSameDayEnd(bars.get(barIdx - 1), bars.get(barIdx))) {
          break;
        }

        barsSplitPerTimeFrame.get(bucketIdx).add(bars.get(barIdx));
        barIdx++;
      }
      bucketIdx++;
    }

    return barsSplitPerTimeFrame;
  }

  private void setBarsTimePeriod(Bar newBar) {
    Duration timePeriod = newBar.getTimePeriod();

    if (timePeriod.compareTo(this.aggregationTimeFrame) > 0) {
      throw new IllegalArgumentException(TIME_PERIOD_GREATER_THAN_TIME_FRAME);
    }

    this.barsTimePeriod = timePeriod;
  }

  private void checkNewBarTimePeriod(Bar bar) {
    if (!bar.getTimePeriod().equals(barsTimePeriod)) {
      throw new IllegalArgumentException(INCONSISTENT_BARS_TIMEFRAME);
    }
  }
}
