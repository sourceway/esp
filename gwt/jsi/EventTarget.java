package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class EventTarget {

	public native <T, R> void addEventListener(String eventName,
	        Function<T, R> handler);
}
