# Java Simon - Simple Monitoring API #

Java Simon is a simple monitoring API that allows you to follow and better understand your application. Monitors (familiarly called Simons) are placed directly into your code and you can choose whether you want to count something or measure time/duration.

## Release 4.0.0 - and moved to GitHub! ##

October, 29th, 2014 - project can be found on its new GitHub home from now on:
https://github.com/virgo47/javasimon

From there we have managed our first successfull (hopefully :-)) release, which is initial version of 4.0.0. This is more or less similar to 3.5.2, except it does not support Java 6 anymore.

Our new History/Release page: https://github.com/virgo47/javasimon/blob/master/docs/History.md

Goodbye to Google Code, it was a blast. :-)

## News ##

### October, 13th, 2014 - Java Simon 3.5.2 released ###

Minor patch version with better `EnabledManager` thread safety was released.
This fixes https://code.google.com/p/javasimon/issues/detail?id=129

### October, 5th, 2014 - Java Simon is moving to GitHub! ###

Right now it's only the source: https://github.com/virgo47/javasimon

You can still add Issues here if you need to, but better file your new requests here: https://github.com/virgo47/javasimon/issues

More news coming later...

### May, 31st, 2014 - version 3.5.1 released ###

Version 3.5.1 fixes NPE in `SimonServletFilter` when init-param `stopwatch-source-props` is not present (empty value would be probably workaround). If you are using servlet filter to measure your requests, this fix is definitely for you.

### May, 8th, 2014 - version 3.5.0 released ###

Java Simon presents the last 3.x version (sans patches if necessary) - 3.5.0! It brings some nice features as a promise to future 4.0 and also **it is the last version supporting Java SE 6** (again, some patches may come later).
  * We added `Clock` abstraction that allows you to feed Simons with nano/millis values. This allows you to shift timestamps in time (if needed), test it better, or even switch it to CPU user-time counter for instance.
  * Utility methods for aggregation over a subtree were added, allowing you to sum up all Stopwatches or Counters, or only the ones matching `SimonFilter`. Check `SimonUtils.calculate*Aggregate` methods.
  * Major addition is "incremental sampling". You can now sample incremental change since the last sample - and do it for multiple clients or periods on the same Simon. Check `Simon.sampleIncrement(key)`. **This effectively deprecates `sampleAndReset` and `reset` as well.** Both will be removed in 4.0.
  * And some bugfixes - check the whole [release notes here](https://code.google.com/p/javasimon/issues/list?can=1&q=Milestone%3D3.5).

Check the Maven OSS repository (see MavenSupport page) and [Javadocs](http://javasimon.googlecode.com/git-history/with-javadoc/api-3.5/index.html). In overall we are very positive about this version, especially because of the incremental sampling, which is definitelly the right way to go if we want to offer any cluster/aggregation support in the future. Other than that it's your good old Simon as you know it. :-) Enjoy

### August 22nd, 2013 - version 3.4.0 released ###

Slowly, but surely and proudly we are marching on - with another fixes + little features release. Release notes are [here](http://code.google.com/p/javasimon/issues/list?can=1&q=Milestone%3D3.4), with these highlights:
  * [Splits can now use attributes too](http://code.google.com/p/javasimon/issues/detail?id=100) - no out of box support yet, but you can for instance store actual parameters of the method call in them for your own Callbacks.
  * Some JDBC enhacements/fixes like [this](http://code.google.com/p/javasimon/issues/detail?id=102), [this](http://code.google.com/p/javasimon/issues/detail?id=103) and [this](http://code.google.com/p/javasimon/issues/detail?id=104).
  * [Console shows active field for Stopwatchs too](http://code.google.com/p/javasimon/issues/detail?id=101).
  * You can [sample multiple Simons with one JMX call](http://code.google.com/p/javasimon/issues/detail?id=109).
  * There are also [better defaults for Simon Servlet Filter](http://code.google.com/p/javasimon/issues/detail?id=110).
  * Anonymous split is [created with factory method](http://code.google.com/p/javasimon/issues/detail?id=105), not with constructor.

Check the Maven repository (see MavenSupport page) and [Javadocs](http://javasimon.googlecode.com/git-history/with-javadoc/api-3.4/index.html) too. There are minor interface changes (anonymous Split - anyone? :-)), but upgrades should be generally smooth. And there are bugfixes, so upgrades are recommended.

### October 12th, 2012 - version 3.3.0 released - mostly (harmless) fixes ###

**From now on we are releasing under [New BSD License](http://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22New_BSD_License.22_or_.22Modified_BSD_License.22.29) leaving GPL behind.** This should be generally good news for our users.

This is sort of wrap-up release of all the changes that went first into 3.2.1 and more that followed + at least one renaming (`SimonManagerMXBean`) justifies the minor version bump. Most fixes and enhacements went into Console and monitoring web-filter, but there is more to it - check it in [this list](http://code.google.com/p/javasimon/issues/list?can=1&q=label%3AMilestone-3.3%20status%3AFixed%2CWontFix%2CVerified) (combined with 3.2.1 fixes too).

### May 11th, 2012 - version 3.2.1 released - better console and performance ###

3.2.1 is more than just a few fixes, the list is actually [pretty long](http://code.google.com/p/javasimon/issues/list?can=1&q=label%3AMilestone-3.3%20status%3AFixed%2CWontFix%2CVerified)! Shortly:
  * Web Console UI overhaul, looks better and acts better :-) and doesn't cache on IE anymore.
  * A few performance fixes - especially disabled manager overhead is now 50% of the previous version (on get/start/stop scenario), also some synchronization was overhauled and get/start/stop with enabled Stopwatch got 5-25% better (depends on thread count, OS, CPU and planet positions of course).
  * `RequestReporters` were separated to own package + a few more useful implementations were added.
In overall I'm very excited about this version and I recommend update from 3.2.0 - should be smooth and fixes to Console are really nice! Javadoc was updated to the latest 3.2.1 - URL is [still the same](http://javasimon.googlecode.com/svn/javadoc/api-3.2/index.html). And of course, we are released to Maven central - check wiki page MavenSupport.

## Why Java Simon? ##

Back in 2008 we wanted to use something like JAMon for our products originally, but we lacked two important features:
  * better way (or any way for that matter) to organize all those monitors;
  * nanosecond resolution.

Simon API gives you **better control** over all those monitors in your big - possibly Java EE - application. Simons are **organized in a hierarchy** similar to what you can see in `java.util.logging` API.

Simons **can be disabled** which minimizes their overhead influencing your application. These operations can be performed on the whole subtrees of Simons which makes partial application monitoring easier. See SimonHierarchy for more.

**Simon measures times in nanos** - and since 2008 nanos have been getting better with every OS and JDK release. Believe it or not it can make the difference on current very fast machines. See `SystemTimersGranularity` page for more.

## Future plans ##

Our future goals include (the later goals are in a more distant future):

  * Web Console enhancements.
  * Better Java EE support.
  * Sampling, collecting, agregating with persistence backend (file/DB).
  * VisualVM plugin.

BTW: Where and when we will be there depends on your feedback as well. :-)

## Do you have something to tell us? ##

So tell us! Visit our [Google Group](http://groups.google.com/group/javasimon) (preferably) or file an [issue](http://code.google.com/p/javasimon/issues/list), whatever. We can't promise to fulfill all your dreams but we want to produce the library YOU like (and so do we - of course ;-)). So if you know how to make Simon better, without making it something it is not, let us know! We want to know.

## Do you like Java Simon? ##

For three years we've been developing it without asking for anything. However if you want to help us or just cheer us up click the button bellow. Maybe we will buy a beer for it, maybe we will use it to pay the fee for the javasimon.org domain. We put a lot of effort in it already - and we plan to put some more still.

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EAJFCHZ2PWLXQ&lc=SK&item_name=Java%20Simon&item_number=javasimon&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)

## Facts by Ohloh ##

While facts on Ohloh say otherwise, Java Simon is Java project in the first place. There's just a lot of JS code in the web console messing up with our stats. ;-)

&lt;wiki:gadget url="http://www.ohloh.net/p/20605/widgets/project\_factoids\_stats.xml" height="290" width="400" border="0"/&gt;