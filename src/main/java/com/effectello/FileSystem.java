package com.effectello;

import io.smallrye.mutiny.Uni;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileSystem {
	private final static Logger LOG = LogManager.getLogger(FileSystem.class.getName());

	public Uni<CloneResult> createDir(List<DirTreeNode> targetDirs) {
		final List<Uni<CloneResult>> resultUnis = targetDirs.stream().map(this::createDir).toList();
		return Uni.join().all(resultUnis).andFailFast().map(results ->
			results.stream().reduce(CloneResult::merge).orElseGet(CloneResult::new));
	}


	public Uni<CloneResult> createDir(final DirTreeNode dir) {
		return createDir(dir.path());
	}

	private Uni<CloneResult> createDir(final Path dir) {
		try {
			Files.createDirectories(dir);
			LOG.info("Directory created: {}", dir);
			return Uni.createFrom().item(new CloneResult(1, 1, 0));
		} catch (IOException e) {
			LOG.warn("Directory create failure", e);
			return Uni.createFrom().item(new CloneResult(1, 1, 0));
		}
	}

	public List<DirTreeNode> sourceFoldersFromTarget(final Path rootSourceFolderPath, final Path targetFolderPath) {
		final List<DirTreeNode> resultAccumulator = new ArrayList<>();
		final DirTreeNode rootFolderInSource = new DirTreeNode(rootSourceFolderPath);
		rootFolderInSource.nested().forEach(nestedFolderInSource ->
			folderResolve(nestedFolderInSource, resultAccumulator, rootFolderInSource, targetFolderPath));
		return resultAccumulator;
	}

	private void folderResolve(final DirTreeNode currentFolderInSource,
	                           final List<DirTreeNode> resultAccumulator,
	                           final DirTreeNode rootSourceFolder, final Path targetFolderPath)
	{
		final Path currentFolderInSourcePath = currentFolderInSource.path();
		resultAccumulator.add(sourceFolderFromTargetResolve(currentFolderInSourcePath, rootSourceFolder.path(), targetFolderPath));
		currentFolderInSource.nested().forEach(nestedFolderInSource ->
			folderResolve(nestedFolderInSource, resultAccumulator, rootSourceFolder, targetFolderPath));
	}

	private DirTreeNode sourceFolderFromTargetResolve(final Path currentNestedFolderInSource,
	                                                  final Path rootSourceFolder, Path targetFolderPath)
	{
		final String sourceFolderLocation = currentNestedFolderInSource.toAbsolutePath().toString().replace(rootSourceFolder.toString(), "");
		final String[] parts = sourceFolderLocation.split(Pattern.quote(File.separator));
		Path resolvedPath = targetFolderPath;
		for (String s : parts) {
			if (s.isEmpty()) {
				continue;
			}
			resolvedPath = resolvedPath.resolve(s);
		}
		return new DirTreeNode(resolvedPath);
	}
}
