package me.opkarol.oplibrary.tools;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@IgnoreInject
public class SmallLettersTool {
    private static final Map<Character, Character> SMALL_LETTER_MAP = new HashMap<>();
    //private static final Map<Character, String> SMALL_LETTER_MAP_STRING = new HashMap<>();

    static {
        SMALL_LETTER_MAP.put('a', 'ᴀ');
        SMALL_LETTER_MAP.put('b', 'ʙ');
        SMALL_LETTER_MAP.put('c', 'ᴄ');
        SMALL_LETTER_MAP.put('d', 'ᴅ');
        SMALL_LETTER_MAP.put('e', 'ᴇ');
        SMALL_LETTER_MAP.put('f', 'ғ');
        SMALL_LETTER_MAP.put('g', 'ɢ');
        SMALL_LETTER_MAP.put('h', 'ʜ');
        SMALL_LETTER_MAP.put('i', 'ɪ');
        SMALL_LETTER_MAP.put('j', 'ᴊ');
        SMALL_LETTER_MAP.put('k', 'ᴋ');
        SMALL_LETTER_MAP.put('l', 'ʟ');
        SMALL_LETTER_MAP.put('m', 'ᴍ');
        SMALL_LETTER_MAP.put('n', 'ɴ');
        SMALL_LETTER_MAP.put('o', 'ᴏ');
        SMALL_LETTER_MAP.put('p', 'ᴘ');
        SMALL_LETTER_MAP.put('q', 'ǫ');
        SMALL_LETTER_MAP.put('r', 'ʀ');
        SMALL_LETTER_MAP.put('s', 's');
        SMALL_LETTER_MAP.put('t', 'ᴛ');
        SMALL_LETTER_MAP.put('u', 'ᴜ');
        SMALL_LETTER_MAP.put('v', 'ᴠ');
        SMALL_LETTER_MAP.put('w', 'ᴡ');
        SMALL_LETTER_MAP.put('x', 'x');
        SMALL_LETTER_MAP.put('y', 'ʏ');
        SMALL_LETTER_MAP.put('z', 'ᴢ');
        SMALL_LETTER_MAP.put('ą', 'ą');
        SMALL_LETTER_MAP.put('ę', 'ę');
        SMALL_LETTER_MAP.put('ć', 'ć');
        SMALL_LETTER_MAP.put('ł', 'ʟ');
        SMALL_LETTER_MAP.put('ń', 'ń');
        SMALL_LETTER_MAP.put('ó', 'ó');
        SMALL_LETTER_MAP.put('ś', 'ś');
        SMALL_LETTER_MAP.put('ź', 'ź');
        SMALL_LETTER_MAP.put('ż', 'ż');

        //SMALL_LETTER_MAP_STRING.put('1', "\uD835\uDFF7");
        //SMALL_LETTER_MAP_STRING.put('2', "\uD835\uDFF8");
        //SMALL_LETTER_MAP_STRING.put('3', "\uD835\uDFF9");
        //SMALL_LETTER_MAP_STRING.put('4', "\uD835\uDFFA");
        //SMALL_LETTER_MAP_STRING.put('5', "\uD835\uDFFB");
        //SMALL_LETTER_MAP_STRING.put('6', "\uD835\uDFFC");
        //SMALL_LETTER_MAP_STRING.put('7', "\uD835\uDFFD");
        //SMALL_LETTER_MAP_STRING.put('8', "\uD835\uDFFE");
        //SMALL_LETTER_MAP_STRING.put('9', "\uD835\uDFFF");
        //SMALL_LETTER_MAP_STRING.put('0', "\uD835\uDFF6");
    }

    public static String replaceToSmallLetters(@NotNull String string) {
        StringBuilder result = new StringBuilder();
        for (char c : string.toLowerCase().toCharArray()) {
            //if (SMALL_LETTER_MAP_STRING.containsKey(c)) {
            //    result.append(SMALL_LETTER_MAP_STRING.get(c));
            //} else
            if (SMALL_LETTER_MAP.containsKey(c)) {
                result.append(SMALL_LETTER_MAP.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
