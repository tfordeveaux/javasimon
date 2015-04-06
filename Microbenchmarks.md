# Introduction #

This page compares how fast timer calls are and how fast/slow Stopwatch is compared to them. Current version of the code is to be found in this class: [org.javasimon.examples.NanoMillisComparison](http://code.google.com/p/javasimon/source/browse/trunk/examples/src/main/java/org/javasimon/examples/NanoMillisComparison.java)

We run the test program with and without `-server` option. The other recommended options are `-Xbatch -Xmx256m -Xms256m` that effectively eliminate problems with the lazy class compilation to the native code and garbage collection (mostly). Warmup is taken care of by using `org.javasimon.utils.BenchmarkUtils` (2 warmup runs, 5 measured runs). Test will be run on various HW/OS versions, feel free to add comment with your own results (copy&paste the results + link with the Google chart).

We'll run loops (10 million iterations) with various bodies: empty, `currentTimeMillis` call, `nanoTime` call, the same two calls with assignment (check client results to see why), followed by Stopwatch start, start/stop and get/start/stop combo. Check the source code of the example class to learn more.

## Windows 7, 64bit, Intel Core2 Quad/2.4 GHz ##

JDK 1.6.0 update 27, i586 (32 bit)

### with -server ###

```
empty: Simon Stopwatch: total 7.26 us, counter 5, max 3.41 us, min 854 ns, mean 1.45 us [empty INHERIT]
millis: Simon Stopwatch: total 1.39 s, counter 5, max 280 ms, min 274 ms, mean 278 ms [millis INHERIT]
nanos: Simon Stopwatch: total 3.52 s, counter 5, max 715 ms, min 695 ms, mean 703 ms [nanos INHERIT]
assign-ms: Simon Stopwatch: total 1.39 s, counter 5, max 279 ms, min 276 ms, mean 278 ms [assign-ms INHERIT]
assign-ns: Simon Stopwatch: total 3.51 s, counter 5, max 712 ms, min 695 ms, mean 701 ms [assign-ns INHERIT]
just-start: Simon Stopwatch: total 7.28 s, counter 5, max 1.51 s, min 1.43 s, mean 1.46 s [just-start INHERIT]
start-stop: Simon Stopwatch: total 17.5 s, counter 5, max 3.61 s, min 3.46 s, mean 3.50 s [start-stop INHERIT]
get-start-stop: Simon Stopwatch: total 20.3 s, counter 5, max 4.09 s, min 4.04 s, mean 4.06 s [get-start-stop INHERIT]
```

### without -server (client VM) ###

```
empty: Simon Stopwatch: total 29.0 ms, counter 5, max 5.98 ms, min 5.70 ms, mean 5.80 ms [empty INHERIT]
millis: Simon Stopwatch: total 29.0 ms, counter 5, max 6.07 ms, min 5.68 ms, mean 5.79 ms [millis INHERIT]
nanos: Simon Stopwatch: total 29.0 ms, counter 5, max 5.88 ms, min 5.70 ms, mean 5.79 ms [nanos INHERIT]
assign-ms: Simon Stopwatch: total 1.64 s, counter 5, max 330 ms, min 328 ms, mean 329 ms [assign-ms INHERIT]
assign-ns: Simon Stopwatch: total 3.48 s, counter 5, max 697 ms, min 695 ms, mean 696 ms [assign-ns INHERIT]
just-start: Simon Stopwatch: total 11.6 s, counter 5, max 2.32 s, min 2.31 s, mean 2.32 s [just-start INHERIT]
start-stop: Simon Stopwatch: total 24.5 s, counter 5, max 4.93 s, min 4.89 s, mean 4.91 s [start-stop INHERIT]
get-start-stop: Simon Stopwatch: total 31.9 s, counter 5, max 6.39 s, min 6.38 s, mean 6.38 s [get-start-stop INHERIT]
```

### Comparison ###

Server VM is equally fast/slow with and without assignments (I'd expect it the other way, server being more aggressively optimized). Server millis are a bit faster. Running Simon's start/stop, server VM clearly is the winner.

We're glad to report, that Windows 7 doesn't has awfully slow nano timer (like Windows XP for instance - nano timer test took 2500 ms on the same HW being tenfold slower than ms).

![http://chart.apis.google.com/chart?chs=740x320&cht=bvg&chbh=a,3,25&chco=2d69f9,a6c9fd,d0eeff&chxt=y,x,x,x&chtt=10M-loop+duration&chxr=2,0,7000&chds=0,7000&chxl=0:|0|1000|2000|3000|4000|5000|6000|7000|1:|empty|millis|nanos|assign-ms|assign-ns|just-start|start-stop|get-start-stop|2:|5.80+ms|5.79+ms|5.79+ms|328.96+ms|695.76+ms|2315.59+ms|4907.59+ms|6381.53+ms|3:|0.00+ms|277.62+ms|703.37+ms|277.56+ms|701.41+ms|1456.91+ms|3504.10+ms|4063.52+ms&chd=t:5.80,5.79,5.79,328.96,695.76,2315.59,4907.59,6381.53|0.00,277.62,703.37,277.56,701.41,1456.91,3504.10,4063.52&chdl=client|server&.png](http://chart.apis.google.com/chart?chs=740x320&cht=bvg&chbh=a,3,25&chco=2d69f9,a6c9fd,d0eeff&chxt=y,x,x,x&chtt=10M-loop+duration&chxr=2,0,7000&chds=0,7000&chxl=0:|0|1000|2000|3000|4000|5000|6000|7000|1:|empty|millis|nanos|assign-ms|assign-ns|just-start|start-stop|get-start-stop|2:|5.80+ms|5.79+ms|5.79+ms|328.96+ms|695.76+ms|2315.59+ms|4907.59+ms|6381.53+ms|3:|0.00+ms|277.62+ms|703.37+ms|277.56+ms|701.41+ms|1456.91+ms|3504.10+ms|4063.52+ms&chd=t:5.80,5.79,5.79,328.96,695.76,2315.59,4907.59,6381.53|0.00,277.62,703.37,277.56,701.41,1456.91,3504.10,4063.52&chdl=client|server&.png)

(Chart is manually composed from both chart outputs by an application.)

## Linux, 64bit, Intel Core2 Quad/2.4 GHz ##

JDK 1.6.0 update 20, 64 bit
(Output from older version of the example code.)

### -server VM ###

```
Round: 5
Empty: Simon Stopwatch: total 1.36 us, counter 1, max 1.36 us, min 1.36 us, mean 1.36 us [empty INHERIT]
Millis: Simon Stopwatch: total 675 ms, counter 1, max 675 ms, min 675 ms, mean 675 ms [millis INHERIT]
Nanos: Simon Stopwatch: total 615 ms, counter 1, max 615 ms, min 615 ms, mean 615 ms [nanos INHERIT]
Assign ms: Simon Stopwatch: total 668 ms, counter 1, max 668 ms, min 668 ms, mean 668 ms [assign-ms INHERIT]
Assign ns: Simon Stopwatch: total 621 ms, counter 1, max 621 ms, min 621 ms, mean 621 ms [assign-ns INHERIT]
Stopwatch start/stop: Simon Stopwatch: total 1.88 s, counter 1, max 1.88 s, min 1.88 s, mean 1.88 s [stopwatch INHERIT]
```

### client VM ###

```
Round: 5
Empty: Simon Stopwatch: total 1.89 us, counter 1, max 1.89 us, min 1.89 us, mean 1.89 us [empty INHERIT]
Millis: Simon Stopwatch: total 669 ms, counter 1, max 669 ms, min 669 ms, mean 669 ms [millis INHERIT]
Nanos: Simon Stopwatch: total 630 ms, counter 1, max 630 ms, min 630 ms, mean 630 ms [nanos INHERIT]
Assign ms: Simon Stopwatch: total 674 ms, counter 1, max 674 ms, min 674 ms, mean 674 ms [assign-ms INHERIT]
Assign ns: Simon Stopwatch: total 627 ms, counter 1, max 627 ms, min 627 ms, mean 627 ms [assign-ns INHERIT]
Stopwatch start/stop: Simon Stopwatch: total 1.85 s, counter 1, max 1.85 s, min 1.85 s, mean 1.85 s [stopwatch INHERIT]
```

### Comparison ###

Server or client, times are virtually the same. Millis are a bit slower than nanos. Nanos are a bit faster than on Windows 7, millis are considerably slower. General Simon start/stop performance is significantly faster.

![http://chart.apis.google.com/chart?chs=700x300&cht=bvg&chbh=32,8,20&chco=4d89f9,c6d9fd&chxt=x,x,y,x&chtt=10M-loop+duration&chxr=2,0,2000&chds=0,2000&chxl=0:|empty|millis|nanos|assign-ms|assign-ns|stopwatch|1:|0.00+ms|675.22+ms|614.95+ms|668.41+ms|621.46+ms|1877.98+ms|2:|0|1000|2000|3:|0.00+ms|668.62+ms|629.78+ms|673.79+ms|627.48+ms|1849.37+ms&chd=t:0.00,675.22,614.95,668.41,621.46,1877.98|0.00,668.62,629.78,673.79,627.48,1849.37&chdl=-server|client&.png](http://chart.apis.google.com/chart?chs=700x300&cht=bvg&chbh=32,8,20&chco=4d89f9,c6d9fd&chxt=x,x,y,x&chtt=10M-loop+duration&chxr=2,0,2000&chds=0,2000&chxl=0:|empty|millis|nanos|assign-ms|assign-ns|stopwatch|1:|0.00+ms|675.22+ms|614.95+ms|668.41+ms|621.46+ms|1877.98+ms|2:|0|1000|2000|3:|0.00+ms|668.62+ms|629.78+ms|673.79+ms|627.48+ms|1849.37+ms&chd=t:0.00,675.22,614.95,668.41,621.46,1877.98|0.00,668.62,629.78,673.79,627.48,1849.37&chdl=-server|client&.png)

Strangely enough JDK 7 32 bit runs significantly slower (5x), while JDK 7 64 bit runs with more or less the same outputs.

## Conclusion ##

Microbenchmarking is much more complicated topic than we showed now. If you want to do it seriously, you should rather start here: http://www.ibm.com/developerworks/java/library/j-benchmark1.html

What we want to say is:
  * You can use Simon for simple microbancharks (see class `org.javasimon.utils.BenchmarkUtils`) - mainly because it uses nanoseconds.
  * You can use accompanying utils to generate Google charts. ;-)