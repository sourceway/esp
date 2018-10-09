package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import java.util.ArrayList;
import java.util.List;

import elemental.dom.Element;
import elemental.util.ArrayOfString;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
public class FormData {

	public FormData(Element element) {
	}

	private native JsIterator<ArrayOfString> entries();

	@JsMethod(namespace = GLOBAL)
	private static native String encodeURIComponent(String val);

	@JsOverlay
	public final String asQueryString() {
		List<String> args = new ArrayList<>();

		JsIterator<ArrayOfString> entries = entries();
		entries.forEachRemaining(entry -> {
			String key = encodeURIComponent(entry.get(0));
			String val = encodeURIComponent(entry.get(1));
			args.add(key + "=" + val);
		});

		return String.join("&", args);
	}
}
