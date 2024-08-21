package me.opkarol.oplibrary.injection.formatter;

import java.util.List;
import java.util.Map;

public interface IFormatter {

    String formatMessage(String input);

    String formatMessage(String input, Map<String, String> replacements);

    List<String> formatMessages(List<String> inputs);

    List<String> formatMessages(List<String> inputs, Map<String, String> replacements);
}
