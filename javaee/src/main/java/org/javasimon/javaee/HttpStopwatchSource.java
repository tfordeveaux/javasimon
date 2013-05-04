package org.javasimon.javaee;

import org.javasimon.Manager;
import org.javasimon.Stopwatch;
import org.javasimon.source.AbstractStopwatchSource;
import org.javasimon.source.CacheMonitorSource;
import org.javasimon.source.MonitorSource;
import org.javasimon.utils.Replacer;

import javax.servlet.http.HttpServletRequest;

/**
 * Provide stopwatch source for HTTP Servlet request.
 * Used by {@link SimonServletFilter} as default stopwatch source.
 * Can be overridden to customize monitored HTTP Requests and their
 * related Simon name.
 * <p/>
 * To select which HTTP Request should be monitored method {@link #isMonitored} can be overridden. Default implementation monitors everything except for
 * typical resource-like requests (images, JS/CSS, ...).
 *
 * @author gquintana
 * @author <a href="mailto:virgo47@gmail.com">Richard "Virgo" Richter</a>
 */
public class HttpStopwatchSource extends AbstractStopwatchSource<HttpServletRequest> {
	/**
	 * Default prefix for web filter Simons if no "prefix" init parameter is used.
	 */
	public static final String DEFAULT_SIMON_PREFIX = "org.javasimon.web";

	/**
	 * Simon prefix, can be set to {@code null}.
	 */
	private String prefix = DEFAULT_SIMON_PREFIX;

	private Replacer unallowedCharacterReplacer = SimonServletFilterUtils.createUnallowedCharsReplacer("_");
	private Replacer jsessionParameterReplacer = new Replacer("[;&]?JSESSIONID=[^;?/&]*", "", Replacer.Modificator.IGNORE_CASE);

	public HttpStopwatchSource(Manager manager) {
		super(manager);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getReplaceUnallowed() {
		return unallowedCharacterReplacer.getTo();
	}

	public void setReplaceUnallowed(String replaceUnallowed) {
		unallowedCharacterReplacer.setTo(replaceUnallowed);
	}

	/**
	 * Returns Simon name for the specified HTTP request with the specified prefix. By default it contains URI without parameters with
	 * all slashes replaced for dots (slashes then determines position in Simon hierarchy). Method can NOT be overridden, but some of the
	 * following steps can:
	 * <ol>
	 * <li>the request is transformed to the string ({@link #requestToStringForMonitorName(javax.servlet.http.HttpServletRequest)}, can be overridden),</li>
	 * <li>the characters that are not allowed as part of the Simon name are replaced with underscore (_) - replacement regex can be changed with {@link #setReplaceUnallowed(String)},</li>
	 * <li>any subsequent slashes and dots are replaced with a single dot ({@link org.javasimon.Manager#HIERARCHY_DELIMITER})</li>
	 * </ol>
	 *
	 * @param request HTTP request
	 * @return fully qualified name of the Simon
	 * @see #requestToStringForMonitorName(javax.servlet.http.HttpServletRequest)
	 */
	protected final String getMonitorName(HttpServletRequest request) {
		String uri = requestToStringForMonitorName(request);
		String localName = SimonServletFilterUtils.getSimonName(uri, unallowedCharacterReplacer);
		if (prefix == null || prefix.isEmpty()) {
			return localName;
		}
		return prefix + Manager.HIERARCHY_DELIMITER + localName;
	}

	/**
	 * Performs the first step in getting the monitor name from the specified HTTP request - here any custom ignore logic should happen.
	 * By default the name is URI (without parameters - see {@link javax.servlet.http.HttpServletRequest#getRequestURI()}) with JSessionID
	 * removed (see {@link #removeJSessionIdFromUri(String)}). This method can be overridden for two typical reasons:
	 * <ul>
	 * <li>Name of the monitor (Stopwatch) should be based on something else then URI,</li>
	 * <li>there are other parts of the name that should be modified or ignored (e.g., REST parameters that are part of the URI).</li>
	 * </ul>
	 *
	 * @param request HTTP request
	 * @return preprocessed URI that will be converted to the Simon name
	 * @see #getMonitorName(javax.servlet.http.HttpServletRequest)
	 * @see #removeJSessionIdFromUri(String)
	 */
	protected String requestToStringForMonitorName(HttpServletRequest request) {
		String uri = request.getRequestURI();
		uri = removeJSessionIdFromUri(uri);
		return uri;
	}

	/**
	 * Called by default implementation of {@link #requestToStringForMonitorName(javax.servlet.http.HttpServletRequest)} and extracted
	 * so it can be used by any overriding implementation of the same method. Method can be overridden if the default behavior is not
	 * sufficient.
	 *
	 * @param uri preprocessed URI that may contain JSessionID
	 * @return preprocessed URI without JSessionID
	 * @see #requestToStringForMonitorName(javax.servlet.http.HttpServletRequest)
	 */
	protected String removeJSessionIdFromUri(String uri) {
		return jsessionParameterReplacer.process(uri);
	}

	/**
	 * Indicates whether the HTTP Request should be monitored - method is intended for override.
	 * Default behavior ignores URIs ending with .css, .png, .gif, .jpg and .js (ignores casing).
	 *
	 * @param httpServletRequest HTTP Request
	 * @return true to enable request monitoring, false either
	 */
	@Override
	public boolean isMonitored(HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI().toLowerCase();
		return !(uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".gif") || uri.endsWith(".jpg") || uri.endsWith(".js"));
	}

	/**
	 * Get a stopwatch for given HTTP request.
	 *
	 * @param request Method HTTP request
	 * @return Stopwatch for the HTTP request
	 */
	@Override
	public Stopwatch getMonitor(HttpServletRequest request) {
		final Stopwatch stopwatch = super.getMonitor(request);
		if (stopwatch.getNote() == null) {
			stopwatch.setNote(request.getRequestURI());
		}
		return stopwatch;
	}

	/**
	 * Wraps given stop watch source in a cache.
	 *
	 * @param stopwatchSource Stopwatch source
	 * @return Cached stopwatch source
	 */
	public static MonitorSource<HttpServletRequest, Stopwatch> newCacheStopwatchSource(MonitorSource<HttpServletRequest, Stopwatch> stopwatchSource) {
		return new CacheMonitorSource<HttpServletRequest, Stopwatch, String>(stopwatchSource) {
			@Override
			protected String getLocationKey(HttpServletRequest l) {
				return l.getRequestURI();
			}
		};
	}
}
