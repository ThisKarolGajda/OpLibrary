package me.opkarol.oplibrary.injection.formatter;

import me.opkarol.oplibrary.injection.config.Config;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.opkarol.oplibrary.injection.formatter.DefaultTextFormatter.*;

@SuppressWarnings("unused")
public class LoreBuilder {
    @Config
    private static int maxLoreCharsLength = 45;
    private List<String> lines;
    private String leftMouseButtonText;
    private String rightMouseButtonText;
    private String anyMouseButtonText;

    private LoreBuilder(List<String> lines) {
        this.lines = lines;
    }

    public LoreBuilder() {
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull LoreBuilder create() {
        return new LoreBuilder(new ArrayList<>());
    }

    public static @NotNull LoreBuilder create(String... args) {
        return new LoreBuilder(Arrays.stream(args).toList());
    }

    public LoreBuilder lines(List<String> lines) {
        this.lines = lines;
        return this;
    }

    public LoreBuilder anyMouseButtonText(String anyMouseButtonText) {
        this.anyMouseButtonText = anyMouseButtonText;
        return this;
    }

    public LoreBuilder anyMouseToMoveNext() {
        return anyMouseButtonText("przejść dalej");
    }

    public LoreBuilder leftMouseButtonText(String leftMouseButtonText) {
        this.leftMouseButtonText = leftMouseButtonText;
        return this;
    }

    public LoreBuilder rightMouseButtonText(String rightMouseButtonText) {
        this.rightMouseButtonText = rightMouseButtonText;
        return this;
    }

    public static void setMaxLoreCharsLength(int maxLoreCharsLength) {
        LoreBuilder.maxLoreCharsLength = maxLoreCharsLength;
    }

    public static int getMaxLoreCharsLength() {
        return maxLoreCharsLength;
    }

    public List<String> build() {
        List<String> formattedLore = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String line : lines) {
            currentLine.append(shadowColor.toCode()).append("&l»").append(basicColor.toCode());
            String[] words = line.split(" ");

            for (String word : words) {
                if (word.startsWith("<h>")) {
                    String highlightedText = word.substring(3);
                    word = secondaryColor.toCode() + highlightedText + basicColor.toCode();
                }

                if (getLength(currentLine.toString()) + getLength(word) + 1 > maxLoreCharsLength) {
                    formattedLore.add(currentLine.toString());
                    currentLine.setLength(0);
                    currentLine.append(basicColor.toCode());
                }

                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }

            if (!currentLine.isEmpty()) {
                formattedLore.add(currentLine.toString());
                formattedLore.add("");
            }

            currentLine.setLength(0);
        }

        addMouseButtonInstructions(formattedLore);

        return formattedLore;
    }

    private void addMouseButtonInstructions(List<String> formattedLore) {
        if (anyMouseButtonText != null) {
            formattedLore.add(shadowColor.toCode() + "&l» " + basicColor.toCode() + "Naciśnij " + secondaryColor.toCode() + "DPM" + basicColor.toCode() + ", aby " + anyMouseButtonText);
        }

        if (leftMouseButtonText != null) {
            formattedLore.add(shadowColor.toCode() + "&l» " + basicColor.toCode() + "Naciśnij " + secondaryColor.toCode() + "LPM" + basicColor.toCode() + ", aby " + leftMouseButtonText);
        }

        if (rightMouseButtonText != null) {
            formattedLore.add(shadowColor.toCode() + "&l» " + basicColor.toCode() + "Naciśnij " + secondaryColor.toCode() + "PPM" + basicColor.toCode() + ", aby " + rightMouseButtonText);
        }
    }

    private static int getLength(@NotNull String currentLine) {
        return currentLine.replace("<h>", "").replace("<SL>", "").replaceAll("(?i)#<[0-9a-fA-F]{6}>", "").replaceAll("&[0-9a-fA-F]", "").length();
    }
}