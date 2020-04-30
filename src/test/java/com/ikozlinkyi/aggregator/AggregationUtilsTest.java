package com.ikozlinkyi.aggregator;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

class AggregationUtilsTest {

  private AggregationUtils aggregationUtils = AggregationUtils.getInstance();
  private Function<Number, Num> defaultFunction = PrecisionNum::valueOf;

  @Test
  void shouldReturnTrueForBarsWhichEndInSameDay() {
    ZonedDateTime endTime = ZonedDateTime.now().with(LocalTime.of(3, 0));

    Bar bar1 = new BaseBar(
        endTime,
        defaultFunction.apply(10),
        defaultFunction.apply(20),
        defaultFunction.apply(8),
        defaultFunction.apply(18),
        defaultFunction.apply(150),
        defaultFunction.apply(50)
    );
    Bar bar2 = new BaseBar(
        endTime.plusMinutes(5),
        defaultFunction.apply(10),
        defaultFunction.apply(20),
        defaultFunction.apply(8),
        defaultFunction.apply(18),
        defaultFunction.apply(150),
        defaultFunction.apply(50)
    );

    aggregationUtils.isBarsOfSameDayEnd(bar1, bar2);
  }

  @Test
  void shouldReturnFalseForBarsWhichEndInDifferentDays() {
    ZonedDateTime endTime = ZonedDateTime.now().with(LocalTime.of(0, 0));

    Bar bar1 = new BaseBar(
        endTime,
        defaultFunction.apply(10),
        defaultFunction.apply(20),
        defaultFunction.apply(8),
        defaultFunction.apply(18),
        defaultFunction.apply(150),
        defaultFunction.apply(50)
    );
    Bar bar2 = new BaseBar(
        endTime.plusMinutes(5),
        defaultFunction.apply(10),
        defaultFunction.apply(20),
        defaultFunction.apply(8),
        defaultFunction.apply(18),
        defaultFunction.apply(150),
        defaultFunction.apply(50)
    );

    aggregationUtils.isBarsOfSameDayEnd(bar1, bar2);
  }
}