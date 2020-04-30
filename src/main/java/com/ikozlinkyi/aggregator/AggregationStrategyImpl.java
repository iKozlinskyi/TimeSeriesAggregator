package com.ikozlinkyi.aggregator;

import java.util.List;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

public class AggregationStrategyImpl implements AggregationStrategy {

  private static AggregationStrategyImpl INSTANCE = new AggregationStrategyImpl();

  private AggregationStrategyImpl() { }

  public static AggregationStrategyImpl getInstance() {
    return INSTANCE;
  }

  @Override
  public Bar aggregateList(List<Bar> bars) {
    if (bars.isEmpty()) {
      return null;
    }

    if (bars.size() == 1) {
      return bars.get(0);
    }

    if (bars.size() == 2) {
      return aggregatePair(bars.get(0), bars.get(1));
    }

    int middleIndex = bars.size() / 2;
    return aggregatePair(
        aggregateList(bars.subList(0, middleIndex)),
        aggregateList(bars.subList(middleIndex, bars.size())
        )
    );
  }

  @Override
  public Bar aggregatePair(Bar bar1, Bar bar2) {
    return new BaseBar(
        bar1.getTimePeriod().plus(bar2.getTimePeriod()),
        bar2.getEndTime(),
        bar1.getOpenPrice(),
        bar1.getMaxPrice().max(bar2.getMaxPrice()),
        bar1.getMinPrice().min(bar2.getMinPrice()),
        bar2.getClosePrice(),
        bar1.getVolume().plus(bar2.getVolume()),
        bar1.getAmount().plus(bar2.getAmount())
    );
  }
}
