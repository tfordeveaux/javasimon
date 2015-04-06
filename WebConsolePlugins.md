Java Simon Web Console can be extended with plugins.

# Quantiles plugin #
## Description ##
It's sometimes not satisfactory to only know min/max/average execution time. Median and 90nth percentile are better indicators of application performance. This plugins aims at gathering information in splits distribution and then compute quantiles.
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-quantilesplugin-1.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-quantilesplugin-1.png)

Each Stopwatch has a list of buckets, each bucket represents a duration range and tracks how many splits occured in the range. When a split is added, it's sorted in the appropriate bucket.
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-quantilesplugin-2.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-quantilesplugin-2.png)

The above snapshot shows median, 90% percentile and splits distribution, that is to say how buckets are filled.

The hardest part is to determine the upper and lower of durations and the best bucket number. All splits shouldn't go in same bucket, if it occurs you won't be able to compute any quantile. As you'll see later there are several ways to configure, for each Stopwatch, how many buckets should be created and how wide they should be.

## Installation & Configuration ##
First, register the QuantilesCallback in the SimonManager
```
	SimonManager.callback().addCallback(new AutoQuantilesCallback(100, 8));
```
Then, register the QuantilesDetailPlugin in the Simon console
```
	<servlet>
		<servlet-name>SimonConsoleServlet</servlet-name>
		<servlet-class>org.javasimon.console.SimonConsoleServlet</servlet-class>
		<init-param>
			<param-name>plugin-classes</param-name>
			<param-value>org.javasimon.console.plugin.QuantilesDetailPlugin</param-value>
		</init-param>
	</servlet>
```
There are 3 QuantilesCallback implementation (and you can easily design your own by extending the QuantilesCallback class):
  * FixedQuantilesCallback: configuration is constant, stopwatches have the same values: it's the simplest implementation!
  * PropertiesQuantilesCallback: configuration is read from a properties file, each Stopwatch can have it's own configuration inherit from parent one
  * AutoQuantilesCallback: configuration is automatically computed after a warmup period. The first N splits are not sorted they are kept in memory. When the last Nth split arrives, warmup period ends. The min-max range is computed from retained splits, buckets are built, and retained splits are sorted. See JavaDoc for details.

# Timeline plugin #
## Description ##
The aim is to get information on application performance evolution in time.

![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-timelineplugin-1.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-timelineplugin-1.png)

Each Stopwatch has a rolling list of buckets, each bucket represents a timestamp range and tracks splits for a period of time. When a split is added, it's placed in the appropriate bucket, which can be a new one.
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-timelineplugin-2.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-timelineplugin-2.png)

In the above snapshot, each row in the table and each column in the chart represents a time range bucket.

## Installation & Configuration ##
As in previous plugin, you should register both a Callback in the SimonManager and a Plugin in the Web Console.
```
	SimonManager.callback().addCallback(new TimelineCallback(10, 60000L));
```
```
	<servlet>
		<servlet-name>SimonConsoleServlet</servlet-name>
		<servlet-class>org.javasimon.console.SimonConsoleServlet</servlet-class>
		<init-param>
			<param-name>plugin-classes</param-name>
			<param-value>org.javasimon.console.plugin.TimelineDetailPlugin</param-value>
		</init-param>
	</servlet>
```
The only necessary configuration is the ring buffer size and the width (in milliseconds) of each bucket. In the example above, with 10 buckets of 1 minute each, we can draw the evolution of the Stopwatch for the last 10 minutes.

# Calltree plugin #
## Description ##
When a performance problem occurs, you often want to drill down and find the main cause of problem so as to optimise it. Provided you have several nested Stopwatches (one for each layer for instance: web, business, data access), the call tree plugin can be used for this purpose.

When a split is added, the plugin tries to relate to a parent split. When the root (the parent of the parent...) split ends and exceededs a threshold duration, the complete execution tree is stored in the stopwatch for later analisys.
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-calltreeplugin-2.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-calltreeplugin-2.png)
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-calltreeplugin-1.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-calltreeplugin-1.png)

The example in the snapshot above reads as follows: Y.A took 332ms, it's made of Y.B (62% 206ms) and Y.D (37% 125ms). Y.B it self is essentially (98%) made of 3 executions of Y.C (66ms each, 203ms total).

## Installation & Configuration ##
As usual there is a CallTreeCallback and a CallTreeDetailPlugin to register.

The only necessary configuration is the duration threshold (in milliseconds) above which the call tree is kept in the Stopwatch:
```
	SimonManager.callback().addCallback(new CallTreeCallback(200L));
```

# Plugins performance #
Simon core is simple and fast. By introducing some intelligence, callbacks may be more expensive from a memory and CPU stand point. This chapter gives some performance tips.

## Memory usage ##
These callbacks store information among Simon attributes, as result this data is never garbage collected.
  * **Limit Simons** by controlling which Simons are concerned a callback: Override the appropriate callback or use CompositeFilterCallback to select which Simons are observed by the callback.
  * **Limit information** by controlling callback settings. Configure conveniently the callbacks, avoid huge buffer sizes or bucket numbers.

## Response time ##
Callbacks are executed in the same thread that Simon core. As a consequence, calling ''stopwatch.stop()'' invokes some callbacks, consume time and slow down user response.

To improve this, you can delegate callback processing to Simon daemon thread, a benefit from multicores. For example:
```
QuantilesCallback quantilesCallback=new AutoQuantilesCallback(100, 8));
Callback asyncCallback=new AsyncCallbackProxyFactory(quantilesCallback).newProxy();
SimonManager.callback().addCallback(asyncCallback);
```
This will execute the Quantiles callback in separate thread. To execute multiple callbacks asynchronously use the CompositeCallback:
```
CompositeCallback compositeCallback=new CompositeCallbackImpl();
compositeCallback.addCallback(new FixedQuantilesCallback(0L, 200L, 8));
compositeCallback.addCallback(new TimelineCallback(60, 60000L));
Callback asyncCallback=new AsyncCallbackProxyFactory(compositeCallback).newProxy();
SimonManager.callback().addCallback(asyncCallback);
```
Some callbacks may not support to be executed in a different thread, this is the case of the CallTreeCallback which uses a ThreadLocal variable. As a result, the CallTreeCallback doesn't support asynchronism.