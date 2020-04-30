package com.ikozlinkyi.aggregator;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

class AggregationStrategyImplTest {

  private Function<Number, Num> defaultFunction = PrecisionNum::valueOf;
  private AggregationStrategy aggregationStrategy = AggregationStrategyImpl.getInstance();

  @Test
  void shouldReturnInstance() {
    assertSame(AggregationStrategyImpl.getInstance().getClass(), AggregationStrategyImpl.class);
  }

  @Test
  void shouldReturnSingleton() {
    assertSame(AggregationStrategyImpl.getInstance(), AggregationStrategyImpl.getInstance());
  }

  @Test
  void shouldAggregateBarPair() {
    ZonedDateTime endTime = ZonedDateTime.now();
    Bar bar1 = createBar(endTime, 10, 15 ,8, 18, 10, 3);
    Bar bar2 = createBar(endTime.plusMinutes(5), 11, 13 ,9, 13, 8, 5);

    Bar aggregatedBar = aggregationStrategy.aggregatePair(bar1, bar2);
    assertEquals(bar1.getOpenPrice(), aggregatedBar.getOpenPrice());
    assertEquals(defaultFunction.apply(15), aggregatedBar.getMaxPrice());
    assertEquals(defaultFunction.apply(8), aggregatedBar.getMinPrice());
    assertEquals(bar2.getClosePrice(), aggregatedBar.getClosePrice());
    assertEquals(defaultFunction.apply(18), aggregatedBar.getVolume());
    assertEquals(defaultFunction.apply(8), aggregatedBar.getAmount());
  }

  @Test
  void shouldAggregateBarList() {
    ZonedDateTime endTime = ZonedDateTime.now();
    List<Bar> barList = Arrays.asList(
        createBar(endTime, 10, 15 ,8, 18, 152, 3),
        createBar(endTime.plusMinutes(5), 17, 19,13, 18, 184, 6),
        createBar(endTime.plusMinutes(10), 17, 20,12, 14, 156, 10),
        createBar(endTime.plusMinutes(15), 15, 18,13, 13, 146, 8),
        createBar(endTime.plusMinutes(20), 14, 14,9, 10, 155, 4)
    );

    Bar aggregatedBar = aggregationStrategy.aggregateList(barList);

    assertEquals(barList.get(0).getOpenPrice(), aggregatedBar.getOpenPrice());
    assertEquals(defaultFunction.apply(20), aggregatedBar.getMaxPrice());
    assertEquals(defaultFunction.apply(8), aggregatedBar.getMinPrice());
    assertEquals(barList.get(barList.size() - 1).getClosePrice(), aggregatedBar.getClosePrice());
    assertEquals(defaultFunction.apply(793), aggregatedBar.getVolume());
    assertEquals(defaultFunction.apply(31), aggregatedBar.getAmount());
  }

  @Test
  void shouldReturnUnchangedBarIfItIsOneInAList() {
    ZonedDateTime endTime = ZonedDateTime.now();

    Bar bar = createBar(endTime, 10, 15 ,8, 18, 152, 3);
    List<Bar> barList = new ArrayList<>();
    barList.add(bar);

    Bar aggregatedBar = aggregationStrategy.aggregateList(barList);

    assertEquals(bar.getOpenPrice(), aggregatedBar.getOpenPrice());
    assertEquals(bar.getMaxPrice(), aggregatedBar.getMaxPrice());
    assertEquals(bar.getMinPrice(), aggregatedBar.getMinPrice());
    assertEquals(bar.getClosePrice(), aggregatedBar.getClosePrice());
    assertEquals(bar.getVolume(), aggregatedBar.getVolume());
    assertEquals(bar.getAmount(), aggregatedBar.getAmount());
  }

  @Test
  void shouldReturnNullIfBarListEmpty() {
    List<Bar> barList = new ArrayList<>();

    Bar aggregatedBar = aggregationStrategy.aggregateList(barList);
    assertNull(aggregatedBar);
  }

  Bar createBar(ZonedDateTime endTime, double openPrice, double highPrice, double lowPrice,
                double closePrice, double volume, double amount) {

    return new BaseBar(
        endTime,
        defaultFunction.apply(openPrice),
        defaultFunction.apply(highPrice),
        defaultFunction.apply(lowPrice),
        defaultFunction.apply(closePrice),
        defaultFunction.apply(volume),
        defaultFunction.apply(amount)
    );
  }
}