package com.effectello;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public interface TreeNode {

    List<DirTreeNode> nested();

    List<DirTreeNode> nested(List<Predicate<File>> filters);

    Path path();
}
