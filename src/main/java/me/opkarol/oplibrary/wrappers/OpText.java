package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.tools.FormatTool;
import org.jetbrains.annotations.NotNull;

public final class OpText {
    private String text;

    public OpText(String text) {
        this.text = text;
    }

    public OpText() {
        this.text = null;
    }

    public @NotNull String getFormattedText() {
        if (text == null) {
            return "";
        }
        return FormatTool.formatMessage(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "OpText{" +
                "text='" + text + '\'' +
                '}';
    }
}
