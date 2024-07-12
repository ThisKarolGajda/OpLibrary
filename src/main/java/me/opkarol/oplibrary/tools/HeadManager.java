package me.opkarol.oplibrary.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.runnable.OpRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class HeadManager {
    private static final Map<UUID, String> HEAD_CACHE = new HashMap<>();
    private static final Map<String, ItemStack> HEAD_CACHE_MINECRAFT_VALUE = new HashMap<>();

    public static @Nullable String getHeadValueFromRequest(String name) {
        try {
            String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(result, JsonObject.class);
            String id = obj.get("id").toString().replace("\"", "");
            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + id);
            obj = gson.fromJson(signature, JsonObject.class);
            String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));
            obj = gson.fromJson(decoded, JsonObject.class);
            String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
            return new String(Base64.getEncoder().encode(skinByte));
        } catch (Exception ignored) {
        }
        return null;
    }

    private static @NotNull String getURLContent(String urlStr) {
        URL url;
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
        }

        return sb.toString();
    }

    private static ItemStack getHeadValue(@NotNull String value) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(skull,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }

    public static void addHeadToPlayerInventory(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        if (HEAD_CACHE.containsKey(uuid)) {
            player.getInventory().addItem(getHeadValue(HEAD_CACHE.get(uuid)));
            return;
        }

        new OpRunnable(() -> {
            String value;
            value = getHeadValueFromRequest(player.getName());
            if (value == null) {
                value = "";
            }
            ItemStack item = getHeadValue(value);
            player.getInventory().addItem(item);
            HEAD_CACHE.put(uuid, value);
        }).runTaskAsynchronously();
    }

    public static ItemStack getHeadValue(@NotNull OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (HEAD_CACHE_MINECRAFT_VALUE.containsKey(uuid.toString())) {
            return HEAD_CACHE_MINECRAFT_VALUE.get(uuid.toString());
        }

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
        }

        skull.setItemMeta(skullMeta);
        HEAD_CACHE_MINECRAFT_VALUE.put(uuid.toString(), skull);

        return skull;
    }

    public static @NotNull CompletableFuture<ItemStack> getHeadValueAsync(@NotNull OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        if (HEAD_CACHE.containsKey(uuid)) {
            return CompletableFuture.completedFuture(getHeadValue(HEAD_CACHE.get(uuid)));
        }

        CompletableFuture<ItemStack> future = new CompletableFuture<>();

        new OpRunnable(() -> {
            String value = getHeadValueFromRequest(player.getName());
            if (value == null) {
                value = "";
            }

            ItemStack item = getHeadValue(value);
            HEAD_CACHE.put(uuid, value);

            future.complete(item);
        }).runTaskAsynchronously();

        return future;
    }

    public static Optional<String> getCurrentHeadValue(UUID uuid) {
        return Optional.ofNullable(HEAD_CACHE.get(uuid));
    }

    public static String getHeadValueAsData(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        Optional<String> optional = getCurrentHeadValue(uuid);
        if (optional.isPresent()) {
            return optional.get();
        }

        String value = getHeadValueFromRequest(player.getName());
        HEAD_CACHE.put(uuid, value);
        return value;
    }

    public static Optional<String> getCurrentHeadValue(@NotNull Player player) {
        return getCurrentHeadValue(player.getUniqueId());
    }

    public static void setHeadValue(Player player) {
        new OpRunnable(() -> {
            UUID uuid = player.getUniqueId();
            String value = getHeadValueFromRequest(player.getName());
            HEAD_CACHE.put(uuid, value);
        }).runTaskAsynchronously();
    }

    /**
     * @author mfnalex
     */
    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID()); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        return profile;
    }

    public static @Nullable ItemBuilder getHeadFromMinecraftValueUrl(@NotNull String texture) {
        if (HEAD_CACHE_MINECRAFT_VALUE.get(texture) != null) {
            return new ItemBuilder(HEAD_CACHE_MINECRAFT_VALUE.get(texture));
        }

        final String texturesMinecraftURL = "https://textures.minecraft.net/texture/";
        PlayerProfile profile = getProfile(texture.startsWith(texturesMinecraftURL) ? texture : texturesMinecraftURL + texture);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) {
            return null;
        }
        meta.setOwnerProfile(profile); // Set the owning player of the head to the player profile
        head.setItemMeta(meta);
        ItemBuilder itemBuilder = new ItemBuilder(head);
        HEAD_CACHE_MINECRAFT_VALUE.put(texture, itemBuilder);
        return itemBuilder;
    }

    private static @NotNull String getMinecraftValueUrlFromBase64(String base64) {
        byte[] decodedData = Base64.getDecoder().decode(base64);
        String minecraftUrlTexture = new String(decodedData);

        String urlPrefix = "\"url\":\"";
        int startIndex = minecraftUrlTexture.indexOf(urlPrefix);
        if (startIndex != -1) {
            startIndex += urlPrefix.length();
            int endIndex = minecraftUrlTexture.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return minecraftUrlTexture.substring(startIndex, endIndex);
            }
        }
        return base64;
    }

    public static ItemStack getHeadFromBase64(String base64) {
        return getHeadFromMinecraftValueUrl(getMinecraftValueUrlFromBase64(base64));
    }
}