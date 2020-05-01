# Time Series Aggregator

The purpose of this README is to provide more information for the way this task was done. <br/>
As I understood, the main task was to aggregate time series of bars with a shorter timeframe (e. g. 1 min) 
into a series of bars with a higher timeframe (e. g. 5 min). <br>
For any questions, please contact the author directly:
- Email: ihor.kozlinskyi.dev@gmail.com
- Skype: live:.cid.1e5f262a47dcc989
- tel: 0994018433

## How to aggregate time series
1. Create an instance of AggregationTimeSeriesImpl, pass desired higher timeframe to the constructor.
2. Create bars, which you want to be aggregated. **Bars should have defined timePeriod property, and 
it should be the same for all bars, and not greater than timeframe passed to constructor in previous step**
3. Add bars to instance of AggregationTimeSeriesImpl by *addBar(Bar, boolean)* method.
4. So far, the bars are not aggregated. Call aggregate() method on the created instance of AggregationTimeSeriesImpl.
It returns an instance of AggregationTimeSeriesImpl with bars aggregated.

## Why aggregation is lazy (executed on calling aggregate() but not of adding on the bar)

Lazy aggregation makes possible to use recursive divide and conquer algorithm (AggregationStrategy#aggregateList(List<Bar>)
has time complexity of log(n))

## Notable classes, interfaces and its purpose

The main class which is an entry point for aggregation is AggregationTimeSeriesImpl. It extends functionality
of ta4j BaseTimeSeries and implements *AggregationTimeSeries* interface. Also, it has overridden method 
*void addBar(Bar, boolean)* - with this method the passed bars are checked for timeframe consistency. <br>

The AggregationTimeSeries interface extends ta4j TimeSeries and adds the only method *aggregate()*- the implementation
of it should aggregate the underlying bar list into a higher timeframe.

Interface *AggregationStrategy* defines methods for aggregation of bar pairs and lists. The AggregationTimeSeriesImpl
uses instance that implements the aforementioned interface for aggregation.

## Why original addBar(Bar, boolean) method was overridden?

With the original method TimeSeries#addBar(Bar, boolean) it is possible to add a bar with any timeframe.
But, it means that bars with frames of 1 min, 1.5 min, 5 min could be added to series. And it would not 
be possible to merge them to a fixed timeframe without losing data. Method defines standard timeframe duration
when the first bar added, and if subsequent bars are inconsistent - it throws *IllegalArgumentException*.

Another caveat that should be noted - the resulting higher timeframe is provided to TimeSeries
with constructor (by the task). If we an create instance of AggregationTimeSeriesImpl with a timeframe of 5 min,
and then try to add a bar with timeframe of 10 min - the method would throw *IllegalArgumentException*.

After the aforementioned checks, superclass` addBar(Bar, boolean) is called.

