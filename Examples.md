# Examples #

**Build note:** Simon examples are pre-built with JDK 1.6. Most of them can be built with JDK 1.5, but you have to do it manually if you really need it. JDK 1.6 is used to satisfy dependencies of our JMX support JAR based on JMX 1.4.

**Windows note:** To separate classpath components use ; instead of :. Use backslashes (\) instead of slashes / in paths.

Examples are placed in the `examples` directory. To rebuild examples you have to enter the command:
```
$ ant clean-examples examples
```

You have all necessary libraries under the `lib` directory if you downloaded ZIP package. With compiled examples you can run them with command like this:
```
$ java -cp build/core:build/examples org.javasimon.examples.HelloWorld
```

You may want to add additional options if you really care about the results:
```
$ java -server -Xbatch -Xms256m -Xmx256m -cp build/core:build/examples org.javasimon.examples.FactoryVsStopwatchComparison
```

You also need to add additional JARs to your classpath for some examples (in this case JAMon JAR is not provided anymore and `other` subpackage is not prebuilt either):
```
$ java -server -Xbatch -Xms256m -Xmx256m -cp build/core:build/examples:lib/jamon-2.7.jar org.javasimon.examples.other.JamonComparison
```

Check `examples/org/javasimon/examples` directory for other examples. Most of them has class javadoc explaining the purpose of the example.

## Working with JDK 1.5 ##

You can't build or use JMX examples with JDK 1.5, but most of the rest will work. Go to the main project directory and compile the code:
```
$ javac -d build/ex5 -cp build/core examples/org/javasimon/examples/CallbackExample.java
```

This works if you ran build before and core classes are compiled out into `build/core` directory. Otherwise you have to use jar file from the `lib` directory. For some examples you might need other Simon jars or even 3rd party libs (all included in `lib`).

To run the example under JDK 1.5 issue following command:
```
$ java -cp build/ex5:build/core:lib/stax-api-1.0.1.jar:lib/sjsxp.jar org.javasimon.examples.CallbackExample
```

You have to provide path to StAX API and its implementation because Java 5 does not contain this API like Java 6 does.