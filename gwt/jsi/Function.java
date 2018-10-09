package plx.update.thiz.client.jsi;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface Function<T, R> {

	R apply(T t);
}
