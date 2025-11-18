package com.effectello;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

public class ValidPath implements Supplier<Path> {

	private final Supplier<Path> validPath;

	public ValidPath(Path path) {
		this.validPath = new CachedSuppler<>(() -> this.validPath(path));
	}

	@Override
	public Path get() {
		return this.validPath.get();
	}

	private Path validPath(final Path path) {
		return Optional.ofNullable(path)
			.filter(p -> valid(p.toFile())).stream().findAny()
			.orElseThrow(() -> new IllegalArgumentException("Path is not valid directory or can not be read"));
	}

	private boolean valid(final File file) {
		return file.isDirectory() && file.canRead();
	}
}
