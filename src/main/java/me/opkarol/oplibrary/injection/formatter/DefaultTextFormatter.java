package me.opkarol.oplibrary.injection.formatter;

import me.opkarol.oplibrary.injection.config.Config;
import me.opkarol.oplibrary.injection.wrapper.ColorWrapper;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Config(path = "formatter")
public class DefaultTextFormatter implements IFormatter {
    @Config
    private static boolean enabled = true;
    @Config
    public static ColorWrapper primaryColor = new ColorWrapper(Color.ORANGE);
    @Config
    public static ColorWrapper secondaryColor = new ColorWrapper(Color.YELLOW);
    @Config
    public static ColorWrapper basicColor = new ColorWrapper(Color.GRAY);
    @Config
    private static String messageStartText = primaryColor.toCode() + "☁ " + basicColor.toCode();
    @Config
    private static String messageReplacementText = secondaryColor.toCode() + "%replace%" + basicColor.toCode();
    @Config
    private static String titleStartText = basicColor.toCode();
    @Config
    private static String itemNameStartText = primaryColor.toCode();

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(.*?)%");

    @Override
    public String formatMessage(String input) {
        return enabled ? format(input, null) : input;
    }

    @Override
    public String formatMessage(String input, Map<String, String> replacements) {
        return enabled ? format(input, replacements) : replacePlaceholders(input, replacements);
    }

    @Override
    public List<String> formatMessages(@NotNull List<String> inputs) {
        List<String> formattedMessages = new ArrayList<>();
        for (String input : inputs) {
            formattedMessages.add(formatMessage(input));
        }
        return formattedMessages;
    }

    @Override
    public List<String> formatMessages(@NotNull List<String> inputs, Map<String, String> replacements) {
        List<String> formattedMessages = new ArrayList<>();
        for (String input : inputs) {
            formattedMessages.add(formatMessage(input, replacements));
        }
        return formattedMessages;
    }

    @Override
    public String formatTitle(String input) {
        return titleStartText + input;
    }

    @Override
    public String formatItemName(String input) {
        return itemNameStartText + input;
    }

    @Override
    public List<String> formatItemLore(List<String> input) {
        return input;
    }

    private @NotNull String format(String input, Map<String, String> replacements) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = replacements != null ? replacements.getOrDefault(placeholder, placeholder) : placeholder;
            matcher.appendReplacement(result, messageReplacementText.replace("%replace%", "%" + replacement + "%"));
        }
        matcher.appendTail(result);

        return messageStartText + result;
    }

    private String replacePlaceholders(String input, @NotNull Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }
        return input;
    }
}