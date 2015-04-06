In the following article we will show you how quickly enable monitors for your application with minimal coding. Monitors are not implemented in application (as they should be), but still you can obtain few important results from the application.

## HTTP Request Monitoring ##

TODO: I'll add some simple web filter example ASAP.

## JDBC Monitoring ##

With JDBC it is more tricky because there is much more variety in setup. While Glassfish uses data source to set up connection pools, JBoss AS needs to know only driver as it probably has its own data source/connection pool implementation around it. This changes when you need XA instead of normal data source. It is easy to setup JDBC proxy driver for standalone environment (as is also easier to debug any problems), it is a bit more difficult to set up XA data source for Glassfish. That's what we are going to do now - if you need to setup different application server you need some expertise but this example still should help you. :-) In case of success story with different AS you can send us HOWTO and we will gladly add it here.

### Glassfish (Sun AS) ###

The only thing you need to do to make Simon JDBC Proxy Driver work is:
  * put Java Simon JAR to class-path and restart the server;
  * set up connection pool as follows.

When creating new Connection Pool, choose appropriate Resource Type (for example `javax.sql.XADataSource`) and leave Database Vendor empty. Enter Datasource Classname `org.javasimon.jdbcx.SimonXADataSource` and add properties `User`, `Password` and `URL` with values appropriate for your real database. To "connect" proxy driver with the real one add another property `realdatasourceclassname` with appropriate value - for instance `oracle.jdbc.xa.client.OracleXADataSource` in case of Oracle. Optionally you may want to setup prefix for all predefined JDBC proxy driver related Simons - set property `prefix` to the requested value (for example `com.my.webapp.jdbc`).

Use this connection pool for your JDBC resources.

## How to get values to ... log? ##

Now Java Simon API lacks proper ways how to obtain values from running application. You have to come up with your own layer (JMX for example) or simply add some web action that does nothing else but prints the whole Simon tree into the log - which is easy with `SimonUtils` class.