# Development #

To use Java Simon you can use prepared JARs from the provided ZIP or use Maven dependency:
  * Download page: http://code.google.com/p/javasimon/downloads/list
  * Wiki page with Maven dependencies: MavenSupport

This page is for those who would like to play with Java Simon more.

## Get sources from git repository ##

To clone sources from repository execute the following command:
```
git clone https://code.google.com/p/javasimon/ 
```


## Dependencies ##

Java Simon requires Java SE 6 or higher to run, but it strictly requires JDK 1.6 to build. Separate module `javasimon-jdbc41` (not included as a module in javasimon-parent POM) requres JDK 1.7 to build because it is based on JDBC 4.1 which requires Java SE 7.

Generally you don't need any external library to use Java Simon core. Other modules need to run in environments where their dependencies are expected to be provided already (e.g. `javasimon-javaee` or `javasimon-spring`). You can check all the library dependencies in the POM files. ZIP with all 3rd party libs (required for `javasimon-examples` for instance) is not provided, Maven has to be used. Maven is currently stable way to build the project, Gradle is planned from version 4.0 onwards.


## Building javasimon from sources ##

To build javasimon you need both JDK6 and JDK7. This dependency on two version of JDK is required because javasimon provides implementation of JDBC interfaces and because of the differences on JDK6 and JDK7 you need both to build full javasimon distribution for both supported platforms.

To simplify Maven POMs we decided to require JDK 1.6 for build and to include `javasimon-jdbc4` as a module into main POM. Module `javasimon-jdbc41` must be build separately with JAVA\_HOME set to JDK 1.7.

Convenient `build.bat` is provided to build the whole project all the way ending with distribution ZIP. You need to set environment variables JAVA6\_HOME and JAVA7\_HOME that should contain paths to root directories of JDK6 and JDK7 respectively, then just execute `build.bat`.


### Build with Maven ###

To build any javasimon module separately or the whole project (parent) with Maven use the following command:
```
mvn clean package
```

Proper JDK has to be set to JAVA\_HOME (see above).


### Build with Gradle ###

Javasimon has experimental Gradle support aiming for future version 4.0. To build javasimon with Gradle use the following command:
```
gradle build
```


### Create full javasimon distribution ###

Use `build.bat` from root directory, resulting `dist.zip` will appear in root directory.