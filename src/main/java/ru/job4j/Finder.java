package ru.job4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Finder {
    private final ArgsName argsName;
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class.getName());

    public Finder(String[] args) {
        this.argsName = ArgsName.of(args);
    }

    public void run() {
        validate();
        FileSearchPredicate filter = new FileSearchPredicate(argsName.get("n"), argsName.get("t"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(argsName.get("o"), StandardCharsets.UTF_8))) {
            List<String> files = search(
                    Path.of(argsName.get("d")), filter)
                    .stream()
                    .map(Path::toString).toList();
            writer.write(String.join(System.lineSeparator(), files));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    private void validate() {
        if (!new File(argsName.get("d")).isDirectory()) {
            throw new IllegalArgumentException(String.format("Incorrect path: %s", argsName.get("d")));
        }
        if (isFileName(Paths.get(argsName.get("o")).getFileName().toString())) {
            throw new IllegalArgumentException(String.format("Incorrect file path: %s", argsName.get("o")));
        }
        if (!"mask".equals(argsName.get("t")) && !"name".equals(argsName.get("t")) && !"regex".equals(argsName.get("t"))) {
            throw new IllegalArgumentException(String.format("Unknown search type: %s", argsName.get("t")));
        }
        if ("mask".equals(argsName.get("t")) && !(argsName.get("n").contains("*") || argsName.get("n").contains("&"))) {
            throw new IllegalArgumentException(String.format("Incorrect search value '%s' for search type 'mask'", argsName.get("n")));
        }
        if ("name".equals(argsName.get("t")) && isFileName(Paths.get(argsName.get("n")).getFileName().toString())) {
            throw new IllegalArgumentException(String.format("Incorrect search value '%s' for search type 'name'", argsName.get("n")));
        }
        if ("regex".equals(argsName.get("t")) && !iseRegex(argsName.get("n"))) {
            throw new IllegalArgumentException(String.format("Incorrect search value '%s' for search type 'regex'", argsName.get("n")));
        }
    }

    private boolean isFileName(String fileName) {
        return !fileName.contains(".") || fileName.startsWith(".") || fileName.endsWith(".");
    }

    private boolean iseRegex(String regex) {
        boolean result;
        try {
            Pattern.compile(regex);
            result = true;
        } catch (PatternSyntaxException e) {
            result = false;
        }
        return result;
    }
}
