package plx.update.thiz.client.jsi.events;

import static jsinterop.annotations.JsPackage.GLOBAL;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class MouseEvent extends Event {

	public int which;

	public boolean metaKey;

	public boolean ctrlKey;
}
