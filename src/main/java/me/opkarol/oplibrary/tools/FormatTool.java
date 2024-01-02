package me.opkarol.oplibrary.tools;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class FormatTool {
    private static final Pattern HEX_PATTERN = Pattern.compile("#<[A-f0-9]{6}>");
    private static final Pattern BOLD_HEX_PATTERN = Pattern.compile("&l#<[A-f0-9]{6}>");

    private static final char COLOR_CHAR = '§';
    public static final String CENTER_PREFIX = "<CEN>";

    public static @NotNull String formatMessage(String message) {
        try {
            return format(hexFormatMessage(gradient(message)));
        } catch (Exception ignored) {
            return format(message);
        }
    }

    private static String format(String s) {
        if (s == null) {
            return null;
        }
        s = ChatColor.translateAlternateColorCodes('&', s);
        if (s.startsWith(CENTER_PREFIX)) {
            s = CenteredMessageTool.center(s.substring(CENTER_PREFIX.length()));
        }
        return s;
    }

    /**
     * This will be deleted, but to keep backwards-compatibility,
     * I don't know when.
     */
    @Deprecated
    public static @NotNull List<String> format(List<String> list) {
        return formatList(list);
    }

    public static @NotNull List<String> formatList(List<String> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(FormatTool::formatMessage)
                .toList();
    }

    public static String scrapMessage(String input) {
        return ChatColor.stripColor(input);
    }

    private static @NotNull String hexFormatMessage(@NotNull String message) {
        StringBuilder buffer1 = new StringBuilder(message.length() + 32);
        Matcher matcher2 = BOLD_HEX_PATTERN.matcher(message);
        while (matcher2.find()) {
            String group = matcher2.group(0);
            matcher2.appendReplacement(buffer1, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                    + COLOR_CHAR + group.charAt(6) + COLOR_CHAR + group.charAt(7)
                    + COLOR_CHAR + group.charAt(8) + COLOR_CHAR + group.charAt(9) + "&l");
        }

        StringBuilder buffer2 = new StringBuilder(message.length() + 32);
        Matcher matcher1 = HEX_PATTERN.matcher(matcher2.appendTail(buffer1).toString());
        while (matcher1.find()) {
            String group = matcher1.group(0);
            matcher1.appendReplacement(buffer2, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                    + COLOR_CHAR + group.charAt(6) + COLOR_CHAR + group.charAt(7));
        }

        return matcher1.appendTail(buffer2).toString();
    }

    public static @NotNull String gradient(String start, String end, @NotNull String text) {
        start = start.replace("<", "").replace(">", "");
        end = end.replace("<", "").replace(">", "");
        int r1 = Integer.valueOf(start.substring(1, 3), 16);
        int g1 = Integer.valueOf(start.substring(3, 5), 16);
        int b1 = Integer.valueOf(start.substring(5), 16);
        int r2 = Integer.valueOf(end.substring(1, 3), 16);
        int g2 = Integer.valueOf(end.substring(3, 5), 16);
        int b2 = Integer.valueOf(end.substring(5), 16);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            int r = (int) (r1 + (r2 - r1) * (i / (text.length() - 1.0)));
            int g = (int) (g1 + (g2 - g1) * (i / (text.length() - 1.0)));
            int b = (int) (b1 + (b2 - b1) * (i / (text.length() - 1.0)));
            String hex = String.format("#<%02x%02x%02x>", r, g, b);
            builder.append(hex).append(text.charAt(i));
        }
        return builder.toString();
    }

    public static @NotNull String gradient(String message) {
        String pattern = "#!<(\\w+)>(.*?)#!<(\\w+)>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(message);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (m.find()) {
            String start = m.group(1);
            String end = m.group(3);
            String text = m.group(2);
            sb.append(message, i, m.start());
            sb.append(gradient("#" + start, "#" + end, text));
            i = m.end();
        }
        sb.append(message.substring(i));
        return sb.toString();
    }
}
