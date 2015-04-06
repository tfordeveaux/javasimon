# System timers granularity #

When you want to measure elapsed time you have generally two options:
  * `System.currentTimeMillis()` or
  * `System.nanoTime()`

We used [other.SystemTimersPrecision](http://code.google.com/p/javasimon/source/browse/trunk/examples/src/main/java/other/SystemTimersPrecision.java) class to test how often both timer change. Test configurations was Intel Q6600 Core 2 Quad @2.4GHz, 2GB RAM and we used Windows XP, JDK 1.6.0\_05 and Ubuntu Linux 8.04 with 2.6.24 kernel, JDK 1.6.0\_06.

## Windows XP results ##

```
Round: 5
msChanges: 188 during 2937 ms
deltaCount = {15=71, 16=117}
nsChanges: 10000000 during 2934537656 ns

ns1 = 6363881652561
ns2 = 6363881652813 (diff: 252)
ns2 = 6363881653060 (diff: 247)
ns2 = 6363881653311 (diff: 251)
ns2 = 6363881653559 (diff: 248)
```

As you can see the result returned by `currentTimeMillis` changed only 188 times during nearly 3 seconds. It changed 71 times by 15 ms and 117 times by 16 ms - that indicates timer precision somewhere between these values. 15.625 ms is reported ms timer frequency on multi-processor HW (or multicore as in my case) while 10 ms are reported on single processor (see the link in the Conclusion).

Nanoseconds changed on every call and typical difference between two successive calls was ~250 ns. That is effective overhead of this call on Windows XP. (Updated: See the comments for more results, some of them less satisfying.)

## Linux results ##

```
Round: 5
msChanges: 3162 during 3162 ms
deltaCount = {1=3162}
nsChanges: 10000000 during 3161736190 ns

ns1 = 6858922149416
ns2 = 6858922149570 (diff: 154)
ns2 = 6858922149720 (diff: 150)
ns2 = 6858922149873 (diff: 153)
ns2 = 6858922150027 (diff: 154)
```

Linux shows perfect ms accuracy while nanoseconds can't be judged because the call itself takes around 150 ns. Millis were not perfect at the start of the application:

```
Round: 1
msChanges: 3174 during 3217 ms
deltaCount = {1=3172, 20=1, 25=1}
nsChanges: 10000000 during 3217662606 ns
```

But later after some warmup ms timer is really convincing. If there is any other difference than 1 ms I'd probably suspect OS scheduler granularity rather than ms timer itself.

## Windows 7, Intel Core i5 ##

(Added: October 2011)

Much better results in overall - and not only on this HW. Windows 7 is highly usable with nanoTimer and I'm glad to say that.

```
Round: 5
msChanges: 1183 during 1183 ms
deltaMsCount = {1=1183}
nsChanges: 2679102 during 1182913314 ns
deltaNsCount = {427=735059, 428=1941060, 855=608, 856=543, 1283=313, 1284=81, 1710=35, 1711=223, 2138=60, 2139=93,
2566=52, 2567=22, 2994=47, 2995=5, 3421=10, 3422=44, 3849=16, 3850=18, 4277=23, 4278=12, 4704=1, 4705=23, 5132=7,
5133=7, 5560=14, 5561=8, 5988=26, 5989=3, 6415=6, 6416=24, 6843=8, 6844=12, 7271=11, 7272=6, 7699=9, 7700=3, 8126=2,
8127=6, 8554=6, 8555=4, 8982=3, 8983=1, 9410=2, 9837=5, 9838=6, 10265=6, 10266=6, 10693=14, 10694=1, 11121=13,
11548=5, 11549=5, 11976=6, 11977=3, 12404=7, 12831=2, 12832=8, 13259=1, 13260=4, 13687=4, 13688=1, 14114=1, 14115=6,
14542=1, 14543=1, 14970=3, 14971=1, 15398=3, 15399=1, 15825=1, 15826=6, 16253=4, 16254=5, 16681=5, 16682=3, 17109=3,
17536=2, 17537=9, 17964=2, 17965=2, 18392=11, 18393=2, 18819=1, 18820=9, 19247=3, 19248=3, 19675=3, 19676=2,
20103=4, 20104=1, 20530=1, 20531=5, 20958=4, 20959=4, 21386=4, 21814=6, 22242=1, 22669=3, 22670=5, 23097=2, 23524=2,
23525=3, 23952=4, 23953=3, 24380=2, 24381=3, 24808=8, 25235=2, 25236=6, 25663=3, 25664=2, 26091=4, 26092=1, 26519=4,
26946=4, 26947=4, 27374=6, 27375=3, 27802=8, 27803=1, 28229=3, 28230=3, 28657=2, 28658=3, 29085=2, 29086=2, 29513=2,
29940=5, 29941=5, 30368=3, 30369=1, 30796=5, 30797=1, 31224=4, 31651=5, 31652=7, 32079=3, 32080=2, 32507=2, 32934=2,
...
385809=1, 393935=1, 397357=1, 405912=1, 416604=1, 420027=1, 420454=1, 429436=1, 517548=1, 535512=1}
currentTimeMillis msCount = 24181918
nanoTime msCount = 7668026
Ratio ms/ns: 3.1536040696784284

ns1 = 21525415142503
ns2 = 21525415142503 (diff: 0)
ns3 = 21525415142503 (diff: 0)
ns4 = 21525415142930 (diff: 427)
ns5 = 21525415142930 (diff: 0)

Change after 8 calls
Change after 8 calls
Change after 9 calls
Change after 9 calls
Change after 8 calls
```

We used new version of the example that counts `nanoTimer` calls between it returns different value and also prints map of delta distribution for nanoTimer (as it did for ms before). Most of the deltas are 427 or 428 ns, but then there are random longer diffs, that are likely caused by OS multitasking or whatever else. It can give you the idea of possible errors of the measurement. Suffice to say that none delta is bigger than 0.6 ms and most of them are under 0.5 us - that means that any repeated measurement can give you good enough and valid results for times where ms timer is too coarse.

## Conclusion ##

On the reference HW Linux has slower millis but the granularity is as good as millis need. Slower call is not really the problem because you can't measure very short durations with millis anyway. Nanosecond timer is slightly faster on Linux than on Windows XP (update: Windows 7 gives the best results from the provided ones) and its usability is roughly the same on both platforms. The most important thing is that it is viable to measure time spans in nanoseconds - it's even better to do so because current HW is very fast and many spots in your application are often n-times shorter than 1 ms.

If you want to learn more about various timers you can use and their reported precision on various platform, check this link: http://www.ibm.com/developerworks/java/library/j-benchmark1.html#table1