package me.opkarol.oplibrary.misc;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@IgnoreInject
@SuppressWarnings("unused")
public class StringUtil {

    public static int getIntFromString(String s) {
        if (s != null) {
            try {
                return Optional.of(s.replaceAll("\\s+", "")
                                .replaceAll("[^-0-9]", ""))
                        .filter(s1 -> !s1.isEmpty())
                        .map(Integer::parseInt)
                        .orElse(-1);
            } catch (NumberFormatException ignore) {
                return -1;
            }
        }
        return -1;
    }

    public static double getDoubleFromString(String s) {
        if (s != null) {
            try {
                return Optional.of(s.replaceAll("\\s+", "")
                                .replaceAll("[^-.,0-9]", ""))
                        .filter(s1 -> !s1.isEmpty())
                        .map(Double::parseDouble).orElse(-1D);
            } catch (NumberFormatException ignore) {
                return -1D;
            }
        }
        return -1D;
    }

    public static float getFloatFromString(String s) {
        if (s != null) {
            try {
                if (!s.startsWith("-") && s.contains("-")) {
                    s = s.replace("-", "e-");
                }

                return Optional.of(s.replaceAll("\\s+", "")
                                .replaceAll("[^-.e,0-9]", ""))
                        .filter(s1 -> !s1.isEmpty())
                        .map(Float::parseFloat).orElse(-1F);
            } catch (NumberFormatException ignore) {
                return -1F;
            }
        }
        return -1F;
    }

    public static <K extends Enum<K>> boolean containsEnumFromString(String s, Class<K> e) throws IllegalArgumentException {
        Enum<K> anEnum;
        if (s == null) {
            return false;
        }
        try {
            anEnum = Enum.valueOf(e, s);
        } catch (IllegalArgumentException ignore) {
            return false;
        }
        return anEnum.getClass().equals(e);
    }

    public static <K extends Enum<K>> Optional<K> getEnumValue(String s, Class<K> e) {
        if (!StringUtil.containsEnumFromString(s, e)) {
            return Optional.empty();
        }
        return Optional.of(Enum.valueOf(e, s));
    }

    public static Material getMaterialFromString(String s) {
        if (s != null) {
            Material material;
            try {
                material = Material.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException ignore) {
                return Material.STONE;
            }
            return material;
        }
        return Material.STONE;
    }

    public static boolean getBooleanFromObject(Object obj) {
        if (obj != null) {
            try {
                return (boolean) obj;
            } catch (ClassCastException ignore) {
                if (obj instanceof String s) {
                    return Objects.equals(s, "true");
                }
            }
        }
        return false;
    }

    public static boolean isBoolean(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return (boolean) obj;
        } catch (ClassCastException ignore) {
            if (obj instanceof String) {
                String s = ((String) obj).toLowerCase();
                return Objects.equals(s, "true") || Objects.equals(s, "false");
            }
        }
        return false;
    }

    public static double getDouble(Object object) {
        if (object == null) {
            return getDoubleFromString(null);
        }
        return getDoubleFromString(object.toString());
    }

    public static float getFloat(Object object) {
        if (object == null) {
            return getFloatFromString(null);
        }
        return getFloatFromString(object.toString());
    }

    public static int getInt(Object object) {
        if (object == null) {
            return getIntFromString(null);
        }
        return getIntFromString(object.toString());
    }

    @NotNull
    public static List<String> copyPartialMatches(String token, @NotNull List<String> originals) {
        return originals.stream()
                .filter(s -> startsWithIgnoreCase(s, token))
                .sorted().collect(Collectors.toList());
    }

    public static boolean startsWithIgnoreCase(@NotNull String string, @NotNull String prefix) {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
