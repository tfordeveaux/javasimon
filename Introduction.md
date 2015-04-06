# Introduction #

Application performance measurement is pretty old discipline and we are sure that most developers already tried to measure performance in some way. Maybe you have tried it because some “obvious” optimization went wrong, maybe because your application was really slow – and maybe because you just wanted to track the performance for whatever reason.

Java Simon API is developed to help you to track your application – to measure parts you are interested in and to process these observations somehow. To achieve that the code must be “infected” by various monitors on various places. It's not the goal to put monitors everywhere, it's up to you – the developer – to decide what you want to measure. Aside from your needs there might be additional business needs you want to answer with such a monitoring as well. (You may find useful to check Java run-time monitoring three-part article to see where Java Simon fits in: http://www.ibm.com/developerworks/library/j-rtm1/index.html)

## Profiling vs. Monitoring ##

It is important not to mistake this task with profiling that differs in a few important aspects – even if it is done via explicit monitors in the code and so it might seem to be the same:

  * profiling solves primarily performance problems, trying to localize the cause of the slowness;
  * profiling is mostly development-time tool, it is used mostly when the problem occurs and its results are most important for developers;
  * profiling has often dramatic impact on the performance and is rarely used in production.

While typical use case for Java Simon is:

  * you know what you intend to measure and you place monitors exactly where you want;
  * you probably plan to track measured metrics for some time and process them later – to see them from the perspective, review trends, etc.;
  * results might be important for developers as well as for administrators or business staff.

Of course both worlds intersect in many areas and Simon API has also some functions that are close to profiling. Still there is the difference that the application with “Simon profiling” (for instance usage of the Simon JDBC proxy driver) can run in the production unless the performance is critical. This way you can track things that are hard to track during typical development-time profiling. For instance – you can be hunting some nasty resource leak that will likely not occur during short-term test.

Monitoring with Simon API is not performance tuning in the first place – it is _monitoring_. That does not mean you cannot use the results to tune the performance. Simons allow you to watch your application and it is up to you how you intend to use those results.

## Why Java Simon? ##

We already had some experiences with various aspects of monitoring and profiling. Even if it is not our primary domain we are still strongly interested in the area, working with JMX, knowing some performance tuning tricks but mainly keen on application monitoring.
Originally we started using JAMon API (http://jamonapi.sourceforge.net/) for our needs but JAMon was not good enough for us soon. While you are can manage all those monitors in your application there is nothing in JAMon that really helps with it. We missed something similar to `java.util.logging` tree-like organization. Another issue was that JAMon uses `System.currentTimeMillis()` timer that is not good for measuring short time spans. Java Simon was designed with monitors organized in a tree hierarchy to support management of the whole groups (subtrees). Simon's declarative configuration of monitors and JMX support is based on this feature. Aside from that Simon API uses `System.nanoTime()` to be more future proof. (See http://www.ibm.com/developerworks/java/library/j-benchmark1.html#table1 (article Robust Java benchmarking) for the granularity of the ms timer and SystemTimersGranularity for ms/ns timers comparison.)

If you search log files to see how parts of your application perform, if you often dumps durations of important methods or even if you just track count of something interesting from business perspective – you can use Java Simon to do that. You can than track these values over time, analyze them later, or you can start `jconsole` to monitor them real-time. You can even use it for micro-benchmarks! But than... it would not be exactly the reason why Java Simon project was found.

Java Simon was created to make Java application monitoring more fun, more useful and – in the first place – to make it easier.