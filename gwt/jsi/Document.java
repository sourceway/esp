package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import plx.update.thiz.client.jsi.events.Event;
import plx.update.thiz.client.jsi.events.MouseEvent;
import elemental.dom.Element;
import elemental.dom.NodeList;
import elemental.html.Location;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class Document extends EventTarget {

	public Location location;

	@JsProperty(name = "document", namespace = JsPackage.GLOBAL)
	public static native Document get();

	public native Element createElement(String tag);

	public native Element getElementById(String id);

	public native NodeList getElementsByTagName(String meta);

	@JsOverlay
	public final void addClickHandler(Function<MouseEvent, Boolean> handler) {
		addEventListener("click", handler);
	}

	@JsOverlay
	public final void addSubmitHandler(Function<Event, Boolean> handler) {
		addEventListener("submit", handler);
	}

}
