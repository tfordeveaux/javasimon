Downloads are not directly available on Google Code, use Maven repositories in your build, or download binaries from Maven Central directly here: http://search.maven.org/#search|ga|1|javasimon

# Maven 3 support #

Java Simon is now fully Maven 3.x compliant. Artifacts for Maven users are hosted on Maven Central (which means no need to name any repository in their POMs, hurray!). Dependecies go as follows:
```
	<properties>
...
		<javasimon.version>3.5.0</javasimon.version>
...
	</properties>

	<dependencies>
		<!-- Core JAR is absolutely necessary to use Simons (Stopwatch, etc.) -->
		<dependency>
			<groupId>org.javasimon</groupId>
			<artifactId>javasimon-core</artifactId>
			<version>${javasimon.version}</version>
		</dependency>
		<!-- This one allows monitoring JDBC calls (proxy driver), Java 6 version -->
		<dependency>
			<groupId>org.javasimon</groupId>
			<artifactId>javasimon-jdbc4</artifactId>
			<version>${javasimon.version}</version>
		</dependency>
		<!-- The same for JDBC 4.1 (Java 7) -->
		<dependency>
			<groupId>org.javasimon</groupId>
			<artifactId>javasimon-jdbc4</artifactId>
			<version>${javasimon.version}</version>
		</dependency>
		<!-- JavaEE support, servlet filter, EJB/CDI interceptor -->
		<dependency>
			<groupId>org.javasimon</groupId>
			<artifactId>javasimon-javaee</artifactId>
			<version>${javasimon.version}</version>
		</dependency>
		<!-- Spring support, AOP interceptor, MVC handler interceptor -->
		<dependency>
			<groupId>org.javasimon</groupId>
			<artifactId>javasimon-spring</artifactId>
			<version>${javasimon.version}</version>
		</dependency>
		<!-- Embedded Java Simon web console -->
		<dependency>
			<groupId>org.javasimon</groupId>
			<artifactId>javasimon-console-embed</artifactId>
			<version>${javasimon.version}</version>
		</dependency>
	</dependencies>
```

Of course, use only artifacts you really need. Sources and Javadocs are all available in the repo. Enjoy.