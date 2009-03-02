package org.javasimon.jdbc;

import org.javasimon.Split;
import org.javasimon.CallbackSkeleton;
import org.javasimon.utils.SimonUtils;

import java.util.Date;
import java.util.logging.*;
import java.text.SimpleDateFormat;
import java.io.IOException;

/**
 * Trieda LoggingCallback.
 * Logging structure:
 * <ul>
 * <li>timestamp</li>
 * <li>exec/life time</li>
 * <li>conn id</li>
 * <li>stmt id</li>
 * <li>action code</li>
 * <li>text (values)</li>
 * <li>javasimon</li>
 * </ul>
 *
 * Actions:
 * <ul>
 * <li>conn - open</li>
 * <li>conn - close</li>
 * <li>stmt - open</li>
 * <li>stmt - close</li>
 * <li>sql - exec</li>
 * <li>resultset - open</li>
 * <li>resultset - close</li>
 * </ul>
 *
 * <pre>
 CompositeFilterCallback filter = new CompositeFilterCallback();
 filter.addRule(FilterCallback.Rule.Type.MUST, null, "org.javasimon.jdbc.*");
 filter.addCallback(new JdbcLogCallback());

 SimonManager.callback().addCallback(filter);
 * </pre>
 * @author Radovan Sninsky
 * @version $Revision$ $Date$
 * @created 30.1.2009 15:26:00
 * @since 2
 */
final class JdbcLogCallback extends CallbackSkeleton {

	private final class HumanFormatter extends SimonFormatter {

		private static final String ID = "human";

		private final Date dat = new Date();
		private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		private final String lineSeparator = System.getProperty("line.separator");

		protected String formatRecord(LogRecord record, LogParams params) {
			dat.setTime(record.getMillis());
			StringBuilder sb = new StringBuilder();
			sb.append(dateTimeFormat.format(dat)).append(' ');
			sb.append(record.getThreadID()).append(' ');

			String ln = params.getLocalName();
			sb.append('[').append(ln.equals("conn") || ln.equals("stmt") ? ln : "sql ")
				.append(isStart(params) ? "->" : "-<").append("]").append(' ');
			sb.append(params.getFullName()).append(' ');
			sb.append(SimonUtils.presentNanoTime(params.getSplit())).append(' ');
			sb.append(note(params)).append(lineSeparator);
			return sb.toString();
		}
	}

	private final class CsvFormatter extends SimonFormatter {

		private static final String ID = "human";

		private final String lineSeparator = System.getProperty("line.separator");

		protected String formatRecord(LogRecord record, LogParams params) {
			StringBuilder sb = new StringBuilder();
			sb.append(record.getMillis()).append('|');
			sb.append(record.getThreadID()).append('|');

			String ln = params.getLocalName();
			sb.append(ln.equals("conn") || ln.equals("stmt") ? ln : "sql")
				.append(isStart(params) ? "|>" : "|<").append('|');
			sb.append(params.getFullName()).append('|');
			sb.append(params.getSplit()).append('|');
			sb.append(note(params)).append(lineSeparator);
			return sb.toString();
		}
	}

	private String logFilename;
	private String loggerName;
	private boolean logToConsole;
	private String logFormat;

	private Logger logger;

	private boolean initialized = false;

	public JdbcLogCallback() {
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public void setLogToConsole(boolean logToConsole) {
		this.logToConsole = logToConsole;
	}

	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}

	private void initialize() {
		logger = Logger.getLogger(loggerName != null && loggerName.length() > 0 ? loggerName : "simon_jdbc_logger");

		if (logFilename != null && logFilename.length() > 0) {
			logger.setUseParentHandlers(false);
			try {
				FileHandler fh = new FileHandler(logFilename);
				SimonFormatter f = formatter();
				if (f != null) {
					fh.setFormatter(f);
				}
				logger.addHandler(fh);
			} catch (IOException e) {
				// todo do something
			}
		}

		if (logToConsole) {
			ConsoleHandler ch = new ConsoleHandler();
			SimonFormatter f = formatter();
			if (f != null) {
				ch.setFormatter(f);
			}
			logger.addHandler(ch);
		}
		
		initialized = true;
	}

	private SimonFormatter formatter() {
		if (logFormat != null) {
			if (logFormat.equalsIgnoreCase(HumanFormatter.ID)) {
				return new HumanFormatter();
			} else if (logFormat.equalsIgnoreCase(CsvFormatter.ID)) {
				return new CsvFormatter();
			} else {
				try {
					Object o = Class.forName(logFormat).newInstance();
					if (o instanceof SimonFormatter) {
						return (SimonFormatter)o;
					}
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}

	public void stopwatchStart(Split split) {
		if (!initialized) {
			initialize();
		}

		String fullName = split.getStopwatch().getName();
		String localName = SimonUtils.localName(fullName);
		if (localName.equals("conn") || localName.equals("stmt")) {
			logger.log(Level.INFO, null, new Object[] {fullName, "start", null, split.getStopwatch().getNote()});
		}
	}

	public void stopwatchStop(Split split) {
		if (!initialized) {
			initialize();
		}

		String fullName = split.getStopwatch().getName();
		logger.log(Level.INFO, null, new Object[] {fullName, "stop", split.runningFor(), split.getStopwatch().getNote()});
	}
}