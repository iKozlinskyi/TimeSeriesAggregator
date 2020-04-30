package com.ikozlinkyi.aggregator;

import static com.ikozlinkyi.aggregator.constants.ErrorMessages.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;

/**
 * The class is responsible for handling of time series.
 * The main purpose of it - to merge low timeframe bars into higher timeframe bars
 */
public class AggregationTimeSeriesImpl extends BaseTimeSeries implements AggregationTimeSeries {

  /** higher timeframe for bars to be aggregated into */
  private Duration aggregationTimeFrame;

  /** time period of underlying bars */
  private Duration barsTimePeriod;

  private static AggregationStrategy aggregationStrategy = AggregationStrategyImpl.getInstance();
  private static AggregationUtils aggregationUtils = AggregationUtils.getInstance();

  /**
   *
   * @param aggregationTimeFrame Timeframe for bars to be merged into
   */
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

  /**
   *
   * @inheritDoc
   *
   * The method ensures that added bar has the same time duration as already added bars
   * @throws IllegalArgumentException if time period of added bar is greater then current series
   * aggregationTimeFrame exception is thrown
   *
   * @throws IllegalArgumentException if time period of added bar is not the same as for previously
   * added bars
   */
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

  /**
   * Creates a shallow copy of bar data, packs bars into "buckets". Splits bars with
   * different date endTime into separate buckets, even if previous bucket is not full
   *
   * @return List of bar Lists
   */
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
