# Java Simon - Simple Monitors for Java #

Version: 3.3.0

This software is distributed under the terms of the The BSD 3-Clause License:
  * check "license.txt" in the root directory of the project
  * or check it online http://opensource.org/licenses/BSD-3-Clause

Check this readme online for updates: http://code.google.com/p/javasimon/wiki/Readme

For development and building see: http://code.google.com/p/javasimon/wiki/Development

## Getting started ##

Easiest way to start with Java Simon is adding Maven depndencies into your Maven project. See http://code.google.com/p/javasimon/wiki/MavenSupport for more.

A monitors in Java Simon is called "Simon". There are two types of Simons available: `Counter` and `Stopwatch`. Counter tracks single long value, its maximum and minimum. Stopwatch measures time and tracks number of measurements (splits), total time, split minimum and maximum, etc.

### Simon Manager ###

You obtain Simons from the `SimonManager`:
```
Stopwatch stopwatch = SimonManager.getStopwatch("org.javasimon.examples.HelloWorld-stopwatch");
```

Here we obtained stopwatch Simon. If the Simon is accessed first time it is created. If you access existing Simon, type of the Simon must match - you can't create counter with the same name (unless you destroy the Simon first).

### Stopwatch ###

Using stopwatch is simple:
```
Split split = stopwatch.start(); // returns split object
// here goes the measured code
long time = split.stop(); // returns the split time in ns
```

After few runs of your measured code you can get additional information from stopwatch:
```
long totalNanos = stopwatch.getTotal();
long maxSplit = stopwatch.getMax();
long minSplit = stopwatch.getMin();
```

You can use convenient utility to print the results (note ns/us/ms/s unit after the number):
```
System.out.println("Total time: " + SimonUtils.presentNanoTime(totalNanos));
```

Or simply print the Simon itself, it has nice `toString` output.

For more check other Resources (lower), you may head to GettingStarted wiki page: http://code.google.com/p/javasimon/wiki/GettingStarted

## Resources ##

Project is hosted on Google Code as "javasimon":
  * Homepage: http://www.javasimon.org (Google+ http://gplus.to/javasimon, https://plus.google.com/b/115141838919870730025/115141838919870730025)
  * Project page: http://code.google.com/p/javasimon/
  * Download: http://code.google.com/p/javasimon/downloads/list
  * Javadoc API: http://javasimon.googlecode.com/svn/javadoc/api-3.3/index.html
  * Source browser: http://code.google.com/p/javasimon/source/browse/
  * Issue tracker: http://code.google.com/p/javasimon/issues/list
  * Ohloh page: http://www.ohloh.net/p/javasimon

Project uses following libraries (for Ant build, check dependencies in POMs for Maven build):
  * TestNG: http://testng.org/ (test only)
    * `testng-6.5.2.jar`
    * `jcommander-1.5.jar`
  * Spring (core/aop/beans/web/webmvc/test/...): http://www.springsource.org/ (Spring integration)
    * `org.springframework.*-3.1.1.RELEASE.jar`
    * `aopalliance-1.0.jar`
    * `commons-logging-1.1.1.jar`
  * SLF4J logging: http://www.slf4j.org/ (logging callback in core module, may not be required during runtime)
    * `slf4j-api-1.6.4.jar`
  * H2 Database: http://www.h2database.com/ (examples)
    * `h2-1.3.166.jar`
  * Java EE libraries for Java EE module compilation:
    * `servlet-api-2.5.jar`
    * `javaee-api-6.0.jar`
    * `geronimo-servlet_3.0_spec-1.0.jar` (this one is needed for test run)

## Java Simon name ##

**Java Simon** is the official name of the project with _Simple Monitoring API_ as a subtitle. Codename of the project is **javasimon**. We use word Simon as a synonym for a "monitor" in javadoc or on our wiki - of course we use it only for monitors based on the API. We write Simon mostly with capital S, Java Simon with space and javasimon as a one word with all lowercase. Word javasimon is probably best to use in search engines.