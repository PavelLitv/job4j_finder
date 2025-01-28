package ru.job4j;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FileSearchPredicate implements Predicate<Path> {
    private final String searchValue;
    private final String searchType;

    public FileSearchPredicate(String searchValue, String searchType) {
        this.searchValue = searchValue;
        this.searchType = searchType;
    }

    @Override
    public boolean test(Path fileName) {
        return switch (searchType) {
            case "name" -> fileName.toFile().getName().equals(searchValue);
            case "mask" -> matchesMask(fileName.toFile().getName());
            case "regex" -> matchesRegex(fileName.toFile().getName());
            default -> throw new IllegalArgumentException(String.format("Unknown search type: %s", searchType));
        };
    }

    private boolean matchesMask(String fileName) {
        String regex = searchValue
                .replace(".", "\\.")
                .replace("?", ".")
                .replace("*", ".*");
        return fileName.matches(regex);
    }

    private boolean matchesRegex(String fileName) {
        Pattern pattern = Pattern.compile(searchValue);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
}
