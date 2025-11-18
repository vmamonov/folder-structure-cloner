package com.effectello;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DirTreeNode implements TreeNode {
    private final Path currentNodePath;

    public DirTreeNode(final Path path) {
        this.currentNodePath = path;
    }

    @Override
    public List<DirTreeNode> nested() {
       return nested(List.of());
    }

    @Override
    public List<DirTreeNode> nested(final List<Predicate<File>> filters) {
        final File[] nestedNodes = this.currentNodePath.toFile().listFiles();
        return Optional.ofNullable(nestedNodes).stream()
            .flatMap(nodes -> this.nestedResolve(nodes, filters)).toList();
    }

    @Override
    public Path path() {
        return this.currentNodePath;
    }

    private Stream<DirTreeNode> nestedResolve(final File[] nodes, final List<Predicate<File>> filters) {
        return Stream.of(nodes)
            .filter(nodeItem -> checkNode(nodeItem, filters))
            .map(n -> new DirTreeNode(n.toPath()));
    }

    private boolean checkNode(final File node, final List<Predicate<File>> filters) {
        return filters.stream().allMatch(f -> f.test(node));
    }

    @Override
    public String toString() {
        return this.currentNodePath.toFile().getAbsolutePath();
    }
}
