package me.cometkaizo.util;

import me.cometkaizo.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
    public static final String DEFAULT_FORMAT_TEMPLATE = "{}";
    public static final String DEFAULT_LIST_DELIMITER = ", ";
    public static final String PACKAGE_NAME = Main.class.getPackageName();

    public static String format(String message, Object... args) {
        return format(message, DEFAULT_FORMAT_TEMPLATE, args);
    }

    public static String format(String message, String template, Object... args) {
        if (args == null) return message;
        if (message == null || template == null) return null;

        StringBuilder result = new StringBuilder();
        int templateLen = template.length();
        int argIndex = -templateLen, lastArgIndex = argIndex;
        for (Object arg : args) {
            argIndex = message.indexOf(template, argIndex + templateLen);
            if (argIndex == -1) break;
            result.append(message, lastArgIndex + templateLen, argIndex)
                    .append(arg);
            lastArgIndex = argIndex;
        }
        result.append(message, lastArgIndex + templateLen, message.length());
        return result.toString();
    }

    public static String list(Object... elements) {
        return Arrays.stream(elements).map(String::valueOf).collect(Collectors.joining(DEFAULT_LIST_DELIMITER));
    }
    public static String listWithDelimiter(String delimiter, Object... elements) {
        return Arrays.stream(elements).map(String::valueOf).collect(Collectors.joining(delimiter));
    }
    public static String list(Collection<?> elements) {
        return elements.stream().map(String::valueOf).collect(Collectors.joining(DEFAULT_LIST_DELIMITER));
    }
    public static String listWithDelimiter(String delimiter, Collection<?> elements) {
        return elements.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
    }
    public static <T> String list(T[] elements, Function<T, ?> valueFunc) {
        return Arrays.stream(elements).map(valueFunc).map(String::valueOf).collect(Collectors.joining(DEFAULT_LIST_DELIMITER));
    }
    public static <T> String listWithDelimiter(String delimiter, T[] elements, Function<T, ?> valueFunc) {
        return Arrays.stream(elements).map(valueFunc).map(String::valueOf).collect(Collectors.joining(delimiter));
    }
    public static <T> String list(Collection<T> elements, Function<T, ?> valueFunc) {
        return elements.stream().map(valueFunc).map(String::valueOf).collect(Collectors.joining(DEFAULT_LIST_DELIMITER));
    }
    public static <T> String listWithDelimiter(String delimiter, Collection<T> elements, Function<T, ?> valueFunc) {
        return elements.stream().map(valueFunc).map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    public static String unicodeOf(char c) {
        return String.format("%04x", (int) c);
    }

    public static String join(List<String> strings) {
        StringBuilder builder = new StringBuilder();
        for (var string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }
    public static String join(List<String> strings, String delimiter) {
        if (strings.isEmpty()) return "";

        StringBuilder builder = new StringBuilder(strings.get(0));
        for (int i = 1; i < strings.size(); i++) {
            String string = strings.get(i);

            builder.append(delimiter);
            builder.append(string);
        }
        return builder.toString();
    }

    public String findLongestCommonPrefix(String a, String b) {
        int minLength = Math.min(a.length(), b.length());
        for (int i = 0; i < minLength; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, minLength);
    }

    public static String findLongestMatch(String regex, String s) {
        Pattern pattern = Pattern.compile("(" + regex + ")$");
        Matcher matcher = pattern.matcher(s);
        String longest = null;
        int longestLength = -1;
        for (int i = s.length(); i > longestLength; i--) {
            matcher.region(0, i);
            if (matcher.find() && longestLength < matcher.end() - matcher.start()) {
                longest = matcher.group();
                longestLength = longest.length();
            }
        }
        return longest;
    }


    public static String getFullMessage(Throwable e) {
        if (e == null) return "";
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    public static String getAbbreviatedMessage(Throwable e) {
        if (e == null) return "";
        StringBuilder builder = new StringBuilder(e.getClass().getSimpleName());
        builder.append(": ").append(e.getMessage()).append('\n');
        boolean isRelevant = true;
        for (StackTraceElement stackElement : e.getStackTrace()) {
            if (stackElement.getClassName().startsWith(PACKAGE_NAME)) {
                builder.append("\tat ").append(stackElement).append('\n');
                isRelevant = true;
            } else {
                if (isRelevant) builder.append("\tat <...>\n");
                isRelevant = false;
            }
        }
        if (e.getCause() != null) builder.append("Caused by ").append(getAbbreviatedMessage(e.getCause()));
        return builder.toString();
    }

    public static List<String> createLines(String text, int width, FontMetrics metrics) {
        List<String> lines = new ArrayList<>();
        for (String textLine : text.lines().toList()) {
            lines.addAll(createLinesIgnoreNewline(textLine, width, metrics));
        }
        return lines;
    }

    public static List<String> createLinesIgnoreNewline(String text, int width, FontMetrics metrics) {
        if (text.isEmpty()) return List.of(text);
        String textLeft = text;
        List<String> lines = new ArrayList<>();

        while (!textLeft.isEmpty()) {
            String line = trimStringToWidth(textLeft, width, metrics);
            textLeft = textLeft.substring(line.length());

            if (!line.endsWith(" ") && !textLeft.startsWith(" ") && line.contains(" ") && !textLeft.isEmpty()) {
                String truncatedWord = line.substring(line.lastIndexOf(" "));
                line = line.substring(0, line.lastIndexOf(" "));
                textLeft = truncatedWord + textLeft;
            }

            textLeft = textLeft.trim();
            lines.add(line);
        }

        return lines;
    }

    public static String trimStringToWidth(String text, int width, FontMetrics metrics) {
        if (metrics.stringWidth(text) <= width) return text;
        StringBuilder result = new StringBuilder();
        for (int c : text.chars().toArray()) {
            String nextChar = Character.toString(c);
            if (metrics.stringWidth(result + nextChar) > width) return result.toString();
            result.append(nextChar);
        }
        return result.toString();
    }

    public static String readString(File file) throws IOException {
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

}
