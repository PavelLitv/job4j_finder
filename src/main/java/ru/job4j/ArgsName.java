package ru.job4j;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String arg : args) {
            var value = arg.substring(1).split("=", 2);
            values.put(value[0], value[1]);
        }
    }

    public static ArgsName of(String[] args) {
        validate(args);
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }

    private static void validate(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        for (String arg : args) {
            if (!arg.startsWith("-")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' does not start with a '-' character", arg));
            }
            if (arg.startsWith("-=")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain a key", arg));
            }
            if (!arg.contains("=")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain an equal sign", arg));
            }
            if (arg.split("=").length == 1 && arg.endsWith("=")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain a value", arg));
            }
        }
    }
}
