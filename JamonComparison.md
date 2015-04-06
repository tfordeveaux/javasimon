# JAMon vs. Simon comparison #

## Little story ##

The truth is that the whole Simon idea started with one of us using JAMon. After some time we found out that we missed few things among them the most serious being:
  * some way to organize all those monitors
  * better than ms precision

We sat for a while and we were seriously thinking about contributing to JAMon... for a minute. Than we realized that JAMon being well established project is not that easy to change. Because the whole "some monitors with something around" isn't such a big deal (so we thought at the beginning) we decided not to change, not to fork - but to start from scratch. Sorry for this little history, but now you can see why we started with our Simon API with two big differences to JAMon:
  * our monitors are organized in the tree for better manageability;
  * we believe that the [time for nanoseconds is here](SystemTimersGranularity.md).

## What do you need? ##

If these things are not important for you, try Jamon first because now we miss some of its features:
  * more statistical information about measurements (still think about ms limitation though!)
  * generally more features, tools, etc.

However JAMon with its milliseconds is probably not good for microbenchmarks or to sum up durations of all calls to the short method. It's also highly probable that Simon will be enhanced with better statistics and - most importantly - with JMX interface where you can appreciate the tree organization of all Simons.

## Performance? ##

It's difficult to compare performance of both libraries because:
  * timer calls (ms/ns) show various performance overheads on various platforms;
  * JAMon is currently better when it comes to statistics - which means it has more work to do.

Anyway... we have a really simple test in the class `org.javasimon.examples.JamonComparison`. Run it and wait a few rounds to warm up the JVM. Then you can clearly see how fast both APIs do the most common thing - getting the monitor, starting it and stopping it.

### Windows XP ###

```
Jamon start/stop: 63.0 max: 16.0 min: 0.0 real: 455 ms
Jamon get+start/stop: 47.0 max: 16.0 min: 0.0 real: 589 ms
Simon start/stop: 276 ms max: 555 us min: 258 ns real: 583 ms
Simon get+start/stop: 271 ms max: 123 us min: 262 ns real: 635 ms
```

As you can see, Jamon performs better on Windows even with all its calculations - thanks to much faster `currentTimeMillis` call. The difference will be bigger if you use some stat-processor.

![http://chart.apis.google.com/chart?chs=450x300&cht=bvg&chbh=32,10,50&chco=4d89f9,c6d9fd&chxt=x,x,y&chtt=Windows+XP+results&chxr=2,0,800&chds=0,800&chxl=0:|Jamon|Jamon-get|Simon|Simon-get|1:|455+ms|589+ms|583+ms|635+ms|2:|0|100|200|300|400|500|600|700|800&chd=t:455,589,583,635&.png](http://chart.apis.google.com/chart?chs=450x300&cht=bvg&chbh=32,10,50&chco=4d89f9,c6d9fd&chxt=x,x,y&chtt=Windows+XP+results&chxr=2,0,800&chds=0,800&chxl=0:|Jamon|Jamon-get|Simon|Simon-get|1:|455+ms|589+ms|583+ms|635+ms|2:|0|100|200|300|400|500|600|700|800&chd=t:455,589,583,635&.png)

### Linux with 2.6 kernel ###

On Linux ns timer is faster than ms timer which makes Simon faster here:

```
Jamon start/stop: 182.0 max: 1.0 min: 0.0 real: 814 ms
Jamon get+start/stop: 178.0 max: 1.0 min: 0.0 real: 977 ms
Simon start/stop: 272 ms max: 188 us min: 262 ns real: 559 ms
Simon get+start/stop: 268 ms max: 35.0 us min: 262 ns real: 615 ms
```

While `Stopwatch` uses ms timer to initialize usage data later on it relies only on ns calls and only one ns call is made for any operation. You can also notice that while "get monitor" overhead is ~60 ms for Simon it is ~160 ms for JAMon. That's probably caused by the fact that JAMon creates new object as a facade to monitor data.

![http://chart.apis.google.com/chart?chs=450x300&cht=bvg&chbh=32,10,50&chco=4d89f9,c6d9fd&chxt=x,x,y&chtt=Linux+2.6+results&chxr=2,0,1000&chds=0,1000&chxl=0:|Jamon|Jamon-get|Simon|Simon-get|1:|814+ms|977+ms|559+ms|615+ms|2:|0|200|400|600|800|1000&chd=t:814,977,559,615&.png](http://chart.apis.google.com/chart?chs=450x300&cht=bvg&chbh=32,10,50&chco=4d89f9,c6d9fd&chxt=x,x,y&chtt=Linux+2.6+results&chxr=2,0,1000&chds=0,1000&chxl=0:|Jamon|Jamon-get|Simon|Simon-get|1:|814+ms|977+ms|559+ms|615+ms|2:|0|200|400|600|800|1000&chd=t:814,977,559,615&.png)

## Conclusion ##

If you are interested in monitoring overhead you can perform your own tests on your configuration and then do some math to estimate the impact of the monitoring - or you can take some deterministic part of your application and test the impact on that. However if you don't put monitor into every method or inside the loop you'll be satisfied with minimal influence of the monitoring. That being said you should pick up the right API in terms of your needs, what features you need, etc. Performance differences between both APIs are less important if - for instance - ms precision is not good enough for you.