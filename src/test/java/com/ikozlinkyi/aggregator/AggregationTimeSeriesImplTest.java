package com.ikozlinkyi.aggregator;

import static com.ikozlinkyi.aggregator.constants.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

class AggregationTimeSeriesImplTest {

  private AggregationTimeSeriesImpl timeSeries;
  private ZonedDateTime endTime;
  private AggregationStrategy aggregationStrategy = AggregationStrategyImpl.getInstance();

  @BeforeEach
  void initTest() {
    Duration timeFrame = Duration.ofMinutes(3);
    timeSeries = new AggregationTimeSeriesImpl(timeFrame);
    endTime = ZonedDateTime.now().with(LocalTime.of(3, 0));
  }

  @Test
  void shouldThrowExceptionWhenAddedBarsWithInconsistentDurations() {
    Duration barTimeFrame = Duration.ofMinutes(1);

    Bar bar1 = new BaseBar(
        barTimeFrame,
        endTime,
        timeSeries.numOf(10),
        timeSeries.numOf(20),
        timeSeries.numOf(8),
        timeSeries.numOf(14),
        timeSeries.numOf(15),
        timeSeries.numOf(3)
    );
    Bar bar2 = new BaseBar(
        barTimeFrame,
        endTime.plusMinutes(barTimeFrame.toMinutes()),
        timeSeries.numOf(10),
        timeSeries.numOf(20),
        timeSeries.numOf(8),
        timeSeries.numOf(14),
        timeSeries.numOf(15),
        timeSeries.numOf(3)
    );
    Bar inconsistentTimeBar = new BaseBar(
        barTimeFrame.plusMinutes(1),
        endTime.plusDays(barTimeFrame.toMinutes() * 2),
        timeSeries.numOf(10),
        timeSeries.numOf(20),
        timeSeries.numOf(8),
        timeSeries.numOf(14),
        timeSeries.numOf(15),
        timeSeries.numOf(3)
    );

    timeSeries.addBar(bar1, false);
    timeSeries.addBar(bar2, false);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> timeSeries.addBar(inconsistentTimeBar, false));

    assertEquals(INCONSISTENT_BARS_TIMEFRAME, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenAddedBarsWithTimePeriodGreaterThanTimeFrame() {
    Duration barTimeFrame = Duration.ofMinutes(10);

    Bar bar = new BaseBar(
        barTimeFrame,
        endTime,
        timeSeries.numOf(10),
        timeSeries.numOf(20),
        timeSeries.numOf(8),
        timeSeries.numOf(14),
        timeSeries.numOf(15),
        timeSeries.numOf(3)
    );

    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> timeSeries.addBar(bar, false));
    assertEquals(TIME_PERIOD_GREATER_THAN_TIME_FRAME, exception.getMessage());
  }

  @Test
  void shouldSetBarsTimePeriodAfterFirstBarAdded() {
    Duration barTimeFrame = Duration.ofMinutes(1);

    assertNull(timeSeries.getBarsTimePeriod());

    Bar bar = new BaseBar(
        barTimeFrame,
        endTime,
        timeSeries.numOf(10),
        timeSeries.numOf(20),
        timeSeries.numOf(8),
        timeSeries.numOf(14),
        timeSeries.numOf(15),
        timeSeries.numOf(3)
    );

    timeSeries.addBar(bar, false);

    assertEquals(bar.getTimePeriod(), timeSeries.getBarsTimePeriod());
  }

  @Test
  void shouldReturnTimeSeriesWithAggregatedBars() {
    Duration barTimeFrame = Duration.ofMinutes(1);

    Arrays.asList(
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
    ).forEach(bar -> timeSeries.addBar(bar, false));

    List<Bar> aggregatedBars =  timeSeries.aggregate().getBarData();

    assertEquals(2, aggregatedBars.size());
  }

  @Test
  void shouldCreateNewAggregatedBarAfterMidnight() {
    Duration barTimeFrame = Duration.ofMinutes(1);

    ZonedDateTime tomorrowMidnight =
        ZonedDateTime.now().plusDays(1).with(LocalTime.of(0, 0));

    List<Bar> beforeMidnightBarList = Arrays.asList(
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
            timeSeries.numOf(15),
            timeSeries.numOf(19),
            timeSeries.numOf(9),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight,
            timeSeries.numOf(10),
            timeSeries.numOf(12),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        )
    );

    List<Bar> afterMidnightBarList = Arrays.asList(
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight.plusMinutes(1),
            timeSeries.numOf(14),
            timeSeries.numOf(18),
            timeSeries.numOf(5),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            tomorrowMidnight.plusMinutes(2),
            timeSeries.numOf(10),
            timeSeries.numOf(12),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        )
    );

    beforeMidnightBarList.forEach(bar -> timeSeries.addBar(bar, false));
    afterMidnightBarList.forEach(bar -> timeSeries.addBar(bar, false));

    List<Bar> aggregatedBars =  timeSeries.aggregate().getBarData();

    assertEquals(2, aggregatedBars.size());

    Bar expectedBeforeMidnightBar = aggregationStrategy.aggregateList(beforeMidnightBarList);
    Bar expectedAfterMidnightBar = aggregationStrategy.aggregateList(afterMidnightBarList);

    Bar beforeMidnightBar = aggregatedBars.get(0);
    Bar afterMidnightBar = aggregatedBars.get(1);

    assertTrue(isEqualBars(expectedBeforeMidnightBar, beforeMidnightBar));
    assertTrue(isEqualBars(expectedAfterMidnightBar, afterMidnightBar));

    assertAll("starts new bar at midnight",
        () -> assertEquals(0, afterMidnightBar.getBeginTime().getHour()),
        () -> assertEquals(0, afterMidnightBar.getBeginTime().getMinute()));
  }

  @Test
  void shouldCreateUnfilledBarOnTimeSeriesEnd() {
    Duration barTimeFrame = Duration.ofMinutes(1);

    List<Bar> preOverflowBars = Arrays.asList(
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
            endTime.plusMinutes(1),
            timeSeries.numOf(15),
            timeSeries.numOf(19),
            timeSeries.numOf(9),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            endTime.plusMinutes(2),
            timeSeries.numOf(10),
            timeSeries.numOf(12),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        )
    );

    List<Bar> overflowBars = Arrays.asList(
        new BaseBar(
            barTimeFrame,
            endTime.plusMinutes(3),
            timeSeries.numOf(14),
            timeSeries.numOf(18),
            timeSeries.numOf(5),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(2)
        ),
        new BaseBar(
            barTimeFrame,
            endTime.plusMinutes(4),
            timeSeries.numOf(10),
            timeSeries.numOf(12),
            timeSeries.numOf(13),
            timeSeries.numOf(10),
            timeSeries.numOf(10),
            timeSeries.numOf(10)
        )
    );

    preOverflowBars.forEach(bar -> timeSeries.addBar(bar, false));
    overflowBars.forEach(bar -> timeSeries.addBar(bar, false));

    List<Bar> aggregatedBars =  timeSeries.aggregate().getBarData();

    assertEquals(2, aggregatedBars.size());

    Bar expectedAggregatedOverflow = aggregationStrategy.aggregateList(overflowBars);
    Bar actualAggregatedOverflow = aggregatedBars.get(1);

    assertTrue(isEqualBars(expectedAggregatedOverflow, actualAggregatedOverflow));
  }

  private boolean isEqualBars(Bar bar1, Bar bar2) {
    return (
        bar1.getTimePeriod().equals(bar2.getTimePeriod()) &&
        bar1.getEndTime().equals(bar2.getEndTime()) &&
        bar1.getTimePeriod().equals(bar2.getTimePeriod()) &&
        bar1.getClosePrice().equals(bar2.getClosePrice()) &&
        bar1.getOpenPrice().equals(bar2.getOpenPrice()) &&
        bar1.getMaxPrice().equals(bar2.getMaxPrice()) &&
        bar1.getMinPrice().equals(bar2.getMinPrice()) &&
        bar1.getVolume().equals(bar2.getVolume()) &&
        bar1.getAmount().equals(bar2.getAmount())
    );
  }
}