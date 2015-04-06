# Monitoring the data layer #

From your application server, you use the Simon JDBC Driver to wrap the real JDBC driver:
  * Oracle
    * **Driver class name:** `org.javasimon.jdbc4.Driver` instead of `oracle.jdbc.OracleDriver`
    * **URL:** `jdbc:simon:oracle:thin:@host:1521/database` instead of `jdbc:oracle:thin:@host:1521/database`
  * MySQL
    * **Driver class name:** `org.javasimon.jdbc4.Driver` instead of `com.mysql.jdbc.Driver`
    * **URL:** `jdbc:simon:mysql://host:3306/database` instead of `jdbc:mysql://host:3306/database`
  * PostgreSQL
    * **Driver class name:** `org.javasimon.jdbc4.Driver` instead of `org.postgresql.Driver`
    * **URL:**  `jdbc:simon:postgresql://host:5432/database` instead of  `jdbc:postgresql://host:5432/database`

From your application, you can wrap the JDBC connection...
```
Connection connection=dataSource.getConnection();
Connection simonConnection = new SimonConnection(connection,"yourschema");
simonConnection.createStatement();
```
or the whole JDBC DataSource
```
DataSource realDataSource=...
DataSource dataSource=new WrappingSimonDataSource(realDataSource)
```
You can do the same as above with Spring as well
```
	<jee:jndi-lookup id="realDataSource" expected-type="javax.sql.DataSource" jndi-name="jdbc/DataSource"/>
	<bean id="dataSource" class="org.javasimon.jdbcx4.WrappingSimonDataSource">
		<property name="dataSource" ref="realDataSource"/>
	</bean>
```

# Monitoring the business layer #

## In the Spring world ##

You can monitor all bean calls if you annotate the interface with `@Monitored` - alternatively you can annotate only selected methods.

Following configuration is required in your `applicationContext.xml`:
```
	<bean id="monitoringInterceptor" class="org.javasimon.spring.MonitoringInterceptor"/>

	<bean id="monitoringAdvisor" class="org.springframework.aop.support.DefaultPointcutAdvisor">
		<property name="advice" ref="monitoringInterceptor"/>
		<property name="pointcut">
			<bean class="org.javasimon.spring.MonitoredMeasuringPointcut"/>
		</property>
	</bean>

	<!-- this may not be needed... not sure when and why -->
	<bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"/>
```

If you don't want to introduce `@Monitored` in your code, you can still monitor any Spring bean:
```
	<bean id="monitoringInterceptor" class="org.javasimon.spring.MonitoringInterceptor"/>

	<aop:config>
		<!-- name of the class or interface -->
		<aop:pointcut id="monitoringPointcut" expression="execution(* some.package.BeanImpl.*(..))"/>
		<aop:advisor advice-ref="monitoringInterceptor" pointcut-ref="monitoringPointcut"/>
	</aop:config>
```

Simon name defaults to `fully.qualified.ClassName.methodName`, this can be customized
  * with annotation attributes (see [Monitored](http://code.google.com/p/javasimon/source/browse/trunk/core/src/main/java/org/javasimon/aop/Monitored.java) javadoc for more).
  * injecting a different stopwatch source in the `MonitoringInterceptor`, you may extend the default one: `SpringStopwatchSource`.

## In the EJB3 world ##

You can monitor all Session bean calls, by declaring the SimonEjbInterceptor globally in the ejb-jar.xml
```
<ejb-jar xmlns = "http://java.sun.com/xml/ns/javaee" 
         version = "3.0" 
         xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation = "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_0.xsd">
    <interceptors>
        <interceptor>
            <interceptor-class>org.javasimon.javaee.SimonEjbInterceptor</interceptor-class>
        </interceptor>
    </interceptors>
    <assembly-descriptor>
        <interceptor-binding>
            <ejb-name>*</ejb-name>
            <interceptor-class>org.javasimon.javaee.SimonEjbInterceptor</interceptor-class>
        </interceptor-binding>
    </assembly-descriptor>
</ejb-jar>
```

You can customize the EJB Interceptor behaviour by overriding it:
```
public class CustomSimonEjbInterceptor extends SimonEjbInterceptor {
    /**
     * Determine whether method invocation should be monitored
     */
    @Override
    protected boolean isMonitored(InvocationContext context) {
        return context.getMethod().getName().startsWith("find");
    }
    /**
     * Determiner Simon name for given method call
     */
    @Override
    protected String getSimonName(InvocationContext context) {
        String className = context.getMethod().getDeclaringClass().getName();
        String methodName = context.getMethod().getName();
        return "ejb." + className + "." + methodName;
    }
}
```

# Monitoring the Web layer #

You can monitor all incoming HTTP request, by declaring the `SimonServletFilter` in the `web.xml`:
```
<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
	<filter>
		<filter-name>simon-filter</filter-name>
		<filter-class>org.javasimon.javaee.SimonServletFilter</filter-class>
		<!-- optional -->
		<init-param>
			<param-name>prefix</param-name>
			<param-value>com.my.app.web</param-value>
		</init-param>
		<!-- optional basic plain-text console -->
		<init-param>
			<param-name>console-path</param-name>
			<param-value>/simon-filter</param-value>
		</init-param>
		<!-- optional, necessary when long requests should be reported -->
		<init-param>
			<param-name>report-threshold-ms</param-name>
			<param-value>1000</param-value>
		</init-param>
		<!-- optional, used by reporting mechanism - default implementation uses Manager.message
			to log stuff, this goes to stdout -->
		<init-param>
			<param-name>request-reporter-class</param-name>
			<param-value>org.javasimon.javaee.reqreporter.StandardRequestReporter</param-value>
		</init-param>
		<!-- optional, used to specify parameters for StopwatchSource instance -->
		<init-param>
			<param-name>stopwatch-source-props</param-name>
			<param-value>includeHttpMethodName=ALWAYS</param-value>
		</init-param>
		<!-- see Javadoc for org.javasimon.javaee.SimonServletFilter for more about available parameters -->
	</filter>

	<!-- this is related to optional Web Console (not the basic ones included in the filter above) - NOT NECESSARY
		for monitoring itself if you use different way how to get the results (JMX, own mechanism, ...) -->
	<filter>
		<filter-name>simon-console-filter</filter-name>
		<filter-class>org.javasimon.console.SimonConsoleFilter</filter-class>
		<init-param>
			<param-name>url-prefix</param-name>
			<param-value>/simon-console</param-value>
		</init-param>
	</filter>

	<!-- in case Console is used it is recommended to put it in front of the monitoring filter,
		so it is not monitored -->
	<filter-mapping>
		<filter-name>simon-console-filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<filter-mapping>
		<filter-name>simon-filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
</web-app>
```

You can customize `SimonServletFilter` behaviour by
  1. overriding the Filter itself - this is not recommended and we're enhancing the Filter to offer ways how to avoid it;
  1. overriding `HttpStopwatchSource` class and specifing it in filter parameter `stopwatch-source-class`;
  1. overriding reporting mechanism for requests over threshold - extend from `RequestReporter` class (or any of the provided subclasses `DefaultRequestReporter`, `PlainRequestReporter`, `StandardRequestReporter`).

All this is documented in `SimonServletFilter` [Javadoc](http://javasimon.googlecode.com/svn/javadoc/api-3.2/org/javasimon/javaee/SimonServletFilter.html). Most of the decision-wise behavior customization are in `org.javasimon.javaee.HttpStopwatchSource` (see [Javadoc](http://javasimon.googlecode.com/svn/javadoc/api-3.2/org/javasimon/javaee/HttpStopwatchSource.html)) which can be subclassed to override various methods (typically `isMonitored` and `getMonitorName`).
## Stopwatch source ##

An instance of an implementation of StopwatchSource interface is used to select a Stopwatch that will be used to measure time spend for generating particular HTTP response. Particular implementation can be selected with specifying "stopwatch-source-class" init parameter in SimonServletFilter.

"stopwatch-source-props" parameter is used to configure a stopwatch source. Configuration should be presented in format "prop1=value1;prop2=value2;..." where prop1/prop2 - names of JavaBean properties. String values are converted using SimonBeanUtils class, that supports conversion from String to primitive types and their boxing counterparts. Converters for other types can be added using SimonBeanUtils#registerConverter method.

Default monitor source ignores typical image/JS resources and handles (ignores) most forms of JSESSIONID (feel free to report any problems). It also supports appending HTTP method name to the name of Simon (useful for monitoring REST services where different actions are usually mapped on a single URI and distinguished by HTTP method).