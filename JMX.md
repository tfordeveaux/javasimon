# Enabling JMX #

## Pure Java ##
JavaSimon has several classes that that were designed to be registered as JMX beans.

To access Manager through JMX one need to register SimonManagerMXBeanImpl class as JMX bean:
```
    // Acquire bean server
    MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
    // Create bean's name
    ObjectName beanObjectName = new ObjectName("org.javasimon.jmx.manager:type=Manager");
    // Check if bean has already been registered
    if (beanServer.isRegistered(beanObjectName)) {
        // Unregister existing bean with the same name
        beanServer.unregisterMBean(beanObjectName);
    }
    
    // Manager that will be accessed by the JMX bean
    Manager manager = SimonManager.manager();
    // Create SimonManagerMXBeanImpl instance
    SimonManagerMXBeanImpl bean = new SimonManagerMXBeanImpl(manager);
    // Register bean
    beanServer.registerMBean(bean, beanObjectName);
```

There are two ways of accessing individual Simons through JMX:

  * Through methods of SimonManagerMXBeanImpl
  * By registering CounterMXBeanImpl and StopwatchMXBeanImpl instances as JMX beans

CounterMXBeanImpl and StopwatchMXBeanImpl provides access to Counter and Stopwatch instances correspondingly. To simplify registering beans for individual Simons a JmxRegisterCallback should be registered in a manager:
```
    // Acquire bean server
    MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
    Manager manager = SimonManager.manager();
    // JmxRegisterCallback will register beans for the manager
    manager.callback().addCallback(new JmxRegisterCallback(beanServer, "org.javasimon.jmx.simons"));
```


When a new Simon is created a JmxRegisterCallback's instance is notified and registers corresponding JMX bean.

## JmxReporter ##

A simpler method to register JMX beans is to use JmxReporter class:

```
JmxReporter reporter = JmxReporter.forDefaultManager() // Create reporter for SimonManager.manager()
        .start(); // this performs actual MXBean registration
```

Under the hood it does the same thing as manual JMX beans registration.

It is easy to register individual beans using JmxReporter:

```
JmxReporter reporter = JmxReporter.forDefaultManager()
        .registerSimons() // add MBean for every Simon
        .registerExistingSimons() // register also already existing ones (ExistingStopwatch in this case)
        .start(); // this performs actual MXBean registration + JmxRegisterCallback is added to manager
```

## With Spring ##

```
	<bean id="simonManager" class="org.javasimon.spring.ManagerFactoryBean" >
		<property name="callbacks">
			<list>
				<bean class="org.javasimon.jmx.JmxRegisterCallback">
					<constructor-arg index="0" ref="mBeanServer"/>
					<constructor-arg index="1" value="com.mycompany.myapp"/>
				</bean>
			</list>
		</property>
	</bean>
	<bean id="simonManagerMBean" class="org.javasimon.jmx.SimonManagerMXBeanImpl">
		<constructor-arg index="0" ref="simonManager"/>
	</bean>
	<bean id="mBeanServer" class="org.springframework.jmx.support.MBeanServerFactoryBean">
		<property name="locateExistingServerIfPossible" value="true"/>
	</bean>
	<bean class="org.springframework.jmx.export.MBeanExporter">
		<property name="beans">
			<map>
				<entry key="com.mycompany.myapp:name=SimonManager" value-ref="simonManagerMBean"/>
			</map>
		</property>
		<property name="registrationBehaviorName" value="REGISTRATION_REPLACE_EXISTING"/>
	</bean>
```

# Using MBeans #