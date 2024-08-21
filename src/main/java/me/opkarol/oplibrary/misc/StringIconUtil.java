package me.opkarol.oplibrary.misc;

import me.opkarol.oplibrary.injection.IgnoreInject;

@IgnoreInject
public class StringIconUtil {

    public static String getReturnedEmojiFromBoolean(boolean bool) {
        return bool ? "&a✔" : "&c❌";
    }
}
