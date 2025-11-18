package com.effectello;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CachedSuppler<T> implements Supplier<T> {

	private final Supplier<T> origin;
	private final AtomicReference<T> cached = new AtomicReference<>();

	CachedSuppler(final Supplier<T> supplier) {
		this.origin = supplier;
	}

	@Override
	public T get() {
		synchronized (this) {
			if (this.cached.get() == null) {
				this.cached.set(this.origin.get());
			}
			return this.cached.get();
		}
	}
}
