package plx.update.thiz.client.jsi;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface Callback {

	void apply();
}
