package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import plx.update.thiz.client.jsi.events.Event;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class Window extends EventTarget {

	@JsProperty(name = "window", namespace = JsPackage.GLOBAL)
	public static native Window get();

	public native void dispatchEvent(Event evt);
}
