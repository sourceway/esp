package plx.update.thiz.client.jsi.events;

import static jsinterop.annotations.JsPackage.GLOBAL;

import elemental.dom.Element;
import elemental.util.ArrayOf;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class Event {

	public Event() {
	}

	public Event(String type) {
	}

	public native void preventDefault();

	public ArrayOf<Element> path;

	public Element target;
}
