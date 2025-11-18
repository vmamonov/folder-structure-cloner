package com.effectello.commands;

import com.effectello.CloneResult;
import com.effectello.DirTreeNode;
import com.effectello.FileSystem;
import com.effectello.ValidPath;
import io.smallrye.mutiny.Uni;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

public final class Clone implements Callable<Uni<Void>> {
	private final static Logger LOG = LogManager.getLogger(Clone.class.getName());

	@CommandLine.Option(names = { "-s", "--source" }, paramLabel = "path/to/source/dir", description = "The source directory")
	private Path sourcePath;

	@CommandLine.Option(names = { "-t", "--target" }, paramLabel = "path/to/target/dir", description = "The target directory")
	private Path targetPath;

	@Override
	public Uni<Void> call() {
		LOG.info("Start directories clone ...");
		final FileSystem fileSystem = new FileSystem();
		/** todo search deep constrain */
		try {
			final List<DirTreeNode> targetNodes = fileSystem.sourceFoldersFromTarget(
				new ValidPath(this.sourcePath).get(),
				new ValidPath(this.targetPath).get());
			final Uni<CloneResult> result = fileSystem.createDir(targetNodes);
			result.subscribe().with(this::successLogWrite, this::failureLogWrite);
			return result.onFailure().recoverWithNull().replaceWithVoid();
		} catch (Exception ex) {
			failureLogWrite(ex);
			return Uni.createFrom().failure(ex);
		}
	}

	private void successLogWrite(final CloneResult result) {
		LOG.info("{}/{} directories created with {} errors",
			result.succeededCount(), result.succeededCount(), result.failureCount());
	}

	private void failureLogWrite(final Throwable ex) {
		LOG.info("Directories clone failure", ex);
	}
}
