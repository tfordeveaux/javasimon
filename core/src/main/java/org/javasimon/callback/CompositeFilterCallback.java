package org.javasimon.callback;

import javax.script.ScriptException;

import org.javasimon.*;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This callback combines Composite and Filter behavior. Filter can be configured
 * via {@link #addRule(FilterRule.Type, String, String, Callback.Event...)}
 * method and if the rule is satisfied the event is propagated to all
 * children callbacks added via {@link #addCallback(Callback)}. XML facility for configuration
 * is provided via {@link org.javasimon.ManagerConfiguration#readConfig(java.io.Reader)}.
 * <p/>
 * Filter without any rules does not propagate events (default DENY behavior).
 * Any number of global rules (for {@link Callback.Event#ALL}) and per event rules can be added.
 * Event rules have higher priority and if the filter passes on event rules, global rules are not consulted.
 * Rules are checked in the order they were added to the filter.
 *
 * @author <a href="mailto:virgo47@gmail.com">Richard "Virgo" Richter</a>
 * @see FilterRule
 */
public final class CompositeFilterCallback implements FilterCallback {
	private CompositeCallback callback = new CompositeCallback();

	private Map<Event, List<FilterRule>> rules;

	/**
	 * Constructs composite filter callback.
	 */
	public CompositeFilterCallback() {
		rules = new EnumMap<Event, List<FilterRule>>(Event.class);
		for (Event event : Event.values()) {
			rules.put(event, new CopyOnWriteArrayList<FilterRule>());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Callback> callbacks() {
		return callback.callbacks();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addCallback(Callback callback) {
		this.callback.addCallback(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeCallback(Callback callback) {
		this.callback.removeCallback(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize() {
		callback.initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public void cleanup() {
		callback.cleanup();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reset(Simon simon) {
		if (rulesApplyTo(simon, Event.RESET)) {
			callback.reset(simon);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stopwatchAdd(Stopwatch stopwatch, long ns) {
		if (rulesApplyTo(stopwatch, Event.STOPWATCH_ADD, ns)) {
			callback.stopwatchAdd(stopwatch, ns);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stopwatchAdd(Stopwatch stopwatch, Split split) {
		if (rulesApplyTo(stopwatch, Event.STOPWATCH_ADD, split.runningFor())) {
			callback.stopwatchAdd(stopwatch, split);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stopwatchStart(Split split) {
		if (rulesApplyTo(split.getStopwatch(), Event.STOPWATCH_START, split)) {
			callback.stopwatchStart(split);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stopwatchStop(Split split) {
		if (rulesApplyTo(split.getStopwatch(), Event.STOPWATCH_STOP, split)) {
			callback.stopwatchStop(split);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void counterDecrease(Counter counter, long dec) {
		if (rulesApplyTo(counter, Event.COUNTER_DECREASE, dec)) {
			callback.counterDecrease(counter, dec);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void counterIncrease(Counter counter, long inc) {
		if (rulesApplyTo(counter, Event.COUNTER_INCREASE, inc)) {
			callback.counterIncrease(counter, inc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void counterSet(Counter counter, long val) {
		if (rulesApplyTo(counter, Event.COUNTER_SET, val)) {
			callback.counterSet(counter, val);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void simonCreated(Simon simon) {
		if (rulesApplyTo(simon, Event.CREATED)) {
			callback.simonCreated(simon);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void simonDestroyed(Simon simon) {
		if (rulesApplyTo(simon, Event.DESTROYED)) {
			callback.simonDestroyed(simon);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		if (rulesApplyTo(null, Event.CLEAR)) {
			callback.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void message(String message) {
		if (rulesApplyTo(null, Event.MESSAGE, message)) {
			callback.message(message);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void warning(String warning, Exception cause) {
		if (rulesApplyTo(null, Event.WARNING, cause)) {
			callback.warning(warning, cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRule(FilterRule.Type type, String condition, String pattern, Event... events) {
		SimonPattern simonPattern = null;
		if (pattern != null) {
			simonPattern = new SimonPattern(pattern);
		}
		FilterRule rule = new FilterRule(type, condition, simonPattern);
		for (Event event : events) {
			if (event != null) {
				rules.get(event).add(rule);
			}
		}
		if (events.length == 0) {
			rules.get(Event.ALL).add(rule);
		}
	}

	private boolean rulesApplyTo(Simon simon, Event checkedEvent, Object... params) {
		// only if event rules are empty, check rules for ALL as a fallback
		if (rules.get(checkedEvent).size() == 0) {
			return checkRules(simon, Event.ALL, params);
		}
		return checkRules(simon, checkedEvent, params);
	}

	private boolean checkRules(Simon simon, Event event, Object... params) {
		List<FilterRule> rulesForEvent = rules.get(event);
		if (rulesForEvent.size() == 0) { // empty rule list => DENY
			return false;
		}
		boolean allMustSatisfied = false;
		for (FilterRule rule : rulesForEvent) {
			boolean result = false;
			try {
				result = patternAndConditionCheck(simon, rule, params);
			} catch (ScriptException e) {
				warning("Script exception while evaluating rule expression", e);
			}

			if (!result && rule.getType().equals(FilterRule.Type.MUST)) { // fast fail on MUST condition
				return false;
			} else if (result && rule.getType().equals(FilterRule.Type.MUST)) { // MUST condition met, let's go on
				allMustSatisfied = true;
			} else if (result && rule.getType().equals(FilterRule.Type.MUST_NOT)) { // fast fail on MUST NOT condition
				return false;
			} else if (!result && rule.getType().equals(FilterRule.Type.MUST_NOT)) { // MUST NOT condition met, go on
				allMustSatisfied = true;
			} else if (result && rule.getType().equals(FilterRule.Type.SUFFICE)) { // fast success on SUFFICE condition
				return true;
			}
		}
		return allMustSatisfied;
	}

	private boolean patternAndConditionCheck(Simon simon, FilterRule rule, Object... params) throws ScriptException {
		if (simon != null && rule.getPattern() != null && !rule.getPattern().matches(simon.getName())) {
			return false;
		}
		return rule.checkCondition(simon, params);
	}
}