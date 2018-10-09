package plx.update.thiz.client.jsi;

import static jsinterop.annotations.JsPackage.GLOBAL;

import java.util.function.Consumer;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Iterator", namespace = GLOBAL)
public class JsIterator<T> {

	private native ItVal<T> next();

	@JsOverlay
	public final void forEachRemaining(Consumer<T> consumer) {
		JsIterator.ItVal<T> next = next();
		do {
			consumer.accept(next.value);
			next = next();
		} while (!next.done);
	}

	@JsType(isNative = true, name = "Object", namespace = GLOBAL)
	private static class ItVal<V> {

		boolean done;
		V value;
	}
}
