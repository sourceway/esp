package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import com.google.gwt.query.client.GQuery;

import plx.update.thiz.client.jsi.events.Event;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class History {

	public State state;

	/**
	 * Helper field to test if pushState is available in current browser
	 */
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private Object pushState;

	@JsProperty(name = "history", namespace = JsPackage.GLOBAL)
	public static native History get();

	@JsOverlay
	public final boolean isHistoryAvailable() {
		GQuery.console.log(pushState);
		return pushState != null;
	}

	private native void pushState(State state, String title, String url);

	private native void replaceState(State state, String title, String url);

	@JsOverlay
	public final void pushState(String url) {
		pushState(urlState(url), null, url);
		Window.get().dispatchEvent(new Event("espHistoryChange"));
	}

	@JsOverlay
	public final void replaceState(String url) {
		replaceState(urlState(url), null, url);
		Window.get().dispatchEvent(new Event("espHistoryChange"));
	}

	@JsOverlay
	private State urlState(String url) {
		State state = new State();
		state.url = url;
		return state;
	}

	@JsType(isNative = true, name = "Object", namespace = GLOBAL)
	public static class State {

		public String url;
	}
}
