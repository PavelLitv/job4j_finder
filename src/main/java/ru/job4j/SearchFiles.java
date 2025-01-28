package ru.job4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.function.Predicate;

import static java.nio.file.FileVisitResult.CONTINUE;

public class SearchFiles implements FileVisitor<Path> {
    private ArrayList<Path> paths = new ArrayList<>();
    private Predicate<Path> filter;

    public SearchFiles(Predicate<Path> filter) {
        this.filter = filter;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directory,
                                             BasicFileAttributes attributes) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attributes) throws IOException {
        if (filter.test(file)) {
            paths.add(file);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exception) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory,
                                              IOException exception) throws IOException {
        return CONTINUE;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }
}
