package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class XMLHttpRequest {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private Callback onload;

	public String responseText;

	public short status;

	@JsOverlay
	public final void onLoad(Callback onload) {
		this.onload = onload;
	}

	public native String getResponseHeader(String header);

	public native void open(String method, String url);

	public native void send();

	public native void send(FormData data);

	public native void setRequestHeader(String header, String value);
}
