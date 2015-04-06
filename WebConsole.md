# Introduction #

Java Simon Web Console allows to watch Simons meterings through a web interface.

# Screenshots #

![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-1.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-1.png)
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-2.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-2.png)
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-3.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-3.png)

# Installation #

The Java Simon console is a very light web application which supports 2 setup modes:
  * **Embedded**: add a Jar and a Servlet in your application
  * **Shared**: add a Jar and a Web application in your application server

The web console has been successfully tested on several application servers:
| **Name**     | **Version** | **Shared lib folder location**            |
|:-------------|:------------|:------------------------------------------|
| GlassFish  | 3.1       | glassfish/lib                           |
| Tomcat     | 6.0, 7.0  | tomcat_(home or base)_/lib              |
| JBoss      | 6.1       | jboss/server/_default_/lib or jboss/lib   |
| Jetty      | 6.0       |                                         |
| WebLogic   | 10.3      |                                         |
Others may probably work as well but have not been tested.

## Embedded installation ##
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-embedded.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-embedded.png)

  1. Place javasimon-core and javasimon-console-embed Jar files in your application's classpath, usually in the WEB-INF/lib folder
  1. Register JavaSimon Console Servlet in your web.xml (see below)
  1. Start your application as usual
  1. Open your browser on http//hostname:port/yourapp/javasimon-console/

To register the JavaSimon Console Servlet, you should had to your web.xml:
```
	<servlet>
		<servlet-name>SimonConsoleServlet</servlet-name>
		<servlet-class>org.javasimon.console.SimonConsoleServlet</servlet-class>
		<init-param>
			<param-name>url-prefix</param-name>
			<param-value>/javasimon-console</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>SimonConsoleServlet</servlet-name>
		<url-pattern>/javasimon-console/*</url-pattern>
	</servlet-mapping>
```
The "javasimon-console" prefix is customisable by changing the init-param **and** the url-pattern accordingly.

## Shared installation ##
![http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-shared.png](http://javasimon.googlecode.com/svn/wiki/WebConsole/javasimon-console-shared.png)

  1. Place javasimon-core Jar file in the shared library folder of your application server and remove it from your application's WEB-INF\lib folder. Said differently, the core Jar should be shared by both the console web application and you own application like a JDBC driver.
  1. Start the application server
  1. Deploy the javasimon-console War file along with your application
  1. Open your browser on http//hostname:port/javasimon-console/

# Usage #

## Web User Interface ##
  * **Pattern:** filters Simons on their name. The `*` character can be used as wildcard
  * **Type:** filters Simons on their type. Stopwatch is the most useful one.
  * **HTML:** export Simons as simple HTML which can printed.
  * **CSV:** export Simons as a CSV file
  * **XML:** export Simons as an XML file

## REST API ##
### JSON List ###
http://host:port/context/simon/path/data/list.json

Available HTTP parameters
| **pattern** | Simon name pattern, using `*` as wildcard |
|:------------|:------------------------------------------|
| **type** | Simon type: STOPWATCH, COUNTER or UNKNOWN |
| **timeFormat** | Time unit: NANOSECOND, MICROSECOND, MILLISECOND, SECOND, AUTO |
| **reset** | When value is "true", Simon is reset after being read |


### JSON Tree ###
http://host:port/context/simon/path/data/tree.json

Available HTTP parameters
| **name** | Name of the Simon used as tree root, use this parameter to retrieve a subtree. |
|:---------|:-------------------------------------------------------------------------------|
| **timeFormat** | Time unit: NANOSECOND, MICROSECOND, MILLISECOND, SECOND, AUTO |
| **reset** | When value is "true", Simon is reset after being read |

### XML Tree ###
http://host:port/context/simon/path/data/tree.xml

Available HTTP parameters
| **name** | Name of the Simon used as tree root, use this parameter to retrieve a subtree. |
|:---------|:-------------------------------------------------------------------------------|
| **timeFormat** | Time unit: NANOSECOND, MICROSECOND, MILLISECOND, SECOND, AUTO |
| **reset** | When value is "true", Simon is reset after being read |

### CSV Table ###
http://host:port/context/simon/path/data/table.csv

Available HTTP parameters
| **pattern** | Simon name pattern, using `*` as wildcard |
|:------------|:------------------------------------------|
| **type** | Simon type: STOPWATCH, COUNTER or UNKNOWN |
| **timeFormat** | Time unit: NANOSECOND, MICROSECOND, MILLISECOND, SECOND, AUTO |
| **reset** | When value is "true", Simon is reset after being read |

## Customization ##
You can change how data is displayed in Table and Tree views. To do it add a `JavaScript` file `org/javasimon/console/resource/js/javasimon-customization.js` in classpath (see [example](http://code.google.com/p/javasimon/source/browse/demoapp/src/main/resources/org/javasimon/console/resource/js/javasimon-customization.js) from demoapp project) and define callback functions that will be called when data on a webpage is redrawn.

In both of the following examples methods from `javasimon-util.js` were used. To use methods from this file you need to dynamically load it:
```

$.getScript("resource/js/javasimon-util.js", function(data, textStatus, jqxhr) {
    console.log("javasimon-utils were loaded");
});

```

In javasimon sample time is represented as a number. To specify time units that were used _timeUnit_ variable is used. It can have one of the following values: "SECOND", "MILLISECOND", "MICROSECOND", "NANOSECOND".

### Table view ###

To customize Tree view you need to define a function
```
javasimon.onTableData = function(json, timeUnit) {
...
}
```

where:

**json** (_array_) - array of samples that were displayed on a webpage

an example of an array of samples:
```
[
   {
      "name":"javasimon.demoapp.console-servlet.data.table_json",
      "type":"STOPWATCH",
      "counter":25,
      "total":107,
      "min":1,
      "mean":4,
      "last":2,
      "max":48,
      "standardDeviation":9,
      "firstUsage":"2013-11-26 22:38:14",
      "lastUsage":"2013-11-26 22:38:32",
      "note":"/console-servlet/data/table.json"
   },
   {
      "name":"javasimon.demoapp.console-servlet.index_html",
      "type":"STOPWATCH",
      "counter":1,
      "total":1,
      "min":1,
      "mean":2,
      "last":1,
      "max":1,
      "standardDeviation":0,
      "firstUsage":"2013-11-26 22:38:23",
      "lastUsage":"2013-11-26 22:38:23",
      "note":"/console-servlet/index.html"
   }
]
```

An example of a function that highlights every row that satisfies some conditions:
```

javasimon.onTableData = function(json, timeUnit) {
    $.each(json, function(index, sample) {
        var $sampleRow = javasimon.DOMUtil.fnGetSampleRow(sample.name);
        var maxTime = javasimon.TimeUtils.toMillis(sample.max, timeUnit);
        if (maxTime > 20) {
            $sampleRow.css('background-color','#ff8888');
        }
    });
};

```


### Tree view ###

To customize Table view you can define one of two functions:
```

javasimon.onTreeDataReceived(data, timeUnit)
javasimon.onTreeElementDrawn(node, timeUnit)
```

**onTreeDataReceived** - is called when JSON data that will be displayed on a web page is received. **data** parameter is a JSON object that contains all data that will be displayed
**onTreeElementDrawn** - is called when a row in a tree view on a webpage is displayed. **node** parameter is a JSON object that was displayed before callback call.

Example of **data** parameter:
```
{
   "lastUsage":0,
   "firstUsage":0,
   "lastReset":0,
   "name":"",
   "note":"",
   "type":"UNKNOWN",
   "enabled":"true",
   "state":"ENABLED",
   "children":[
      {
         "lastUsage":0,
         "firstUsage":0,
         "lastReset":0,
         "name":"javasimon",
         "note":"",
         "type":"UNKNOWN",
         "enabled":"true",
         "state":"INHERIT",
         "children":[
            {
               "lastUsage":0,
               "firstUsage":0,
               "lastReset":0,
               "name":"javasimon.demoapp",
               "note":"",
               "type":"UNKNOWN",
               "enabled":"true",
               "state":"INHERIT",
               "children":[
                  {
                     "lastUsage":0,
                     "firstUsage":0,
                     "lastReset":0,
                     "name":"javasimon.demoapp.console-servlet",
                     "note":"",
                     "type":"UNKNOWN",
                     "enabled":"true",
                     "state":"INHERIT",
                     "children":[
                        {
                           "min":0,
                           "total":0,
                           "max":0,
                           "last":0,
                           "lastReset":"",
                           "counter":1,
                           "variance":0,
                           "minTimestamp":"2013-11-26 23:16:02",
                           "maxActiveTimestamp":"2013-11-26 23:16:02",
                           "maxTimestamp":"2013-11-26 23:16:02",
                           "maxActive":1,
                           "lastUsage":"2013-11-26 23:16:02",
                           "firstUsage":"2013-11-26 23:16:02",
                           "name":"javasimon.demoapp.console-servlet.tree_html",
                           "standardDeviation":0,
                           "mean":1,
                           "active":0,
                           "varianceN":0,
                           "note":"/console-servlet/tree.html",
                           "type":"STOPWATCH",
                           "enabled":"true",
                           "state":"INHERIT",
                           "children":[

                           ]
                        },
                        {
                           "lastUsage":0,
                           "firstUsage":0,
                           "lastReset":0,
                           "name":"javasimon.demoapp.console-servlet.data",
                           "note":"",
                           "type":"UNKNOWN",
                           "enabled":"true",
                           "state":"INHERIT",
                           "children":[
                              {
                                 "min":3,
                                 "total":117,
                                 "max":18,
                                 "last":7,
                                 "lastReset":"",
                                 "counter":15,
                                 "variance":20916068070548,
                                 "minTimestamp":"2013-11-26 23:16:12",
                                 "maxActiveTimestamp":"2013-11-26 23:16:18",
                                 "maxTimestamp":"2013-11-26 23:16:03",
                                 "maxActive":1,
                                 "lastUsage":"2013-11-26 23:16:18",
                                 "firstUsage":"2013-11-26 23:16:03",
                                 "name":"javasimon.demoapp.console-servlet.data.tree_json",
                                 "standardDeviation":4,
                                 "mean":8,
                                 "active":1,
                                 "varianceN":19521663532511,
                                 "note":"/console-servlet/data/tree.json",
                                 "type":"STOPWATCH",
                                 "enabled":"true",
                                 "state":"INHERIT",
                                 "children":[

                                 ]
                              }
                           ]
                        }
                     ]
                  }
               ]
            }
         ]
      }
   ]
}
```

Example of code that highlights rows in a tree that satisfy some condition:
```
function isLeaf(treeElement) {
    return treeElement.bHasChildren === false;
}

javasimon.onTreeElementDrawn = function(treeElement, timeUnit) {
    if (isLeaf(treeElement)) {
        var $sampleRow = javasimon.DOMUtil.fnGetSampleRow(treeElement.oData.name);
        var maxTime = javasimon.TimeUtils.toMillis(treeElement.oData.max, timeUnit);
        if (maxTime > 20) {
            $sampleRow.css('background-color','#ff8888');
        }
    }
};
```