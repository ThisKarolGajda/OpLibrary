package me.opkarol.oplibrary.commands;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.commands.annotations.Default;
import me.opkarol.oplibrary.commands.annotations.Permission;
import me.opkarol.oplibrary.commands.annotations.Subcommand;
import me.opkarol.oplibrary.commands.annotations.Cooldown;
import me.opkarol.oplibrary.translations.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Command extends BukkitCommand {
    private final Object classObject;
    private final Map<String, Method> subCommands = new HashMap<>();
    private Method commandMethod;
    private final Map<UUID, Long> cooldownMap = new HashMap<>();

    public Command(Class<?> clazz) {
        super(clazz.getAnnotation(me.opkarol.oplibrary.commands.annotations.Command.class).value());
        try {
            this.classObject = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // Set command default method
        Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Default.class)).findAny().ifPresent(method -> this.commandMethod = method);

        // Set command subcommands
        for (Method method : Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Subcommand.class))
                .toList()) {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            subCommands.put(subcommand.value(), method);
        }
    }

    public void register() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            org.bukkit.command.Command command = commandMap.getCommand(this.getName());
            if (command == null || !command.isRegistered()) {
                commandMap.register(getName(), this);
            } else {
                throw new IllegalStateException("This command is already registered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void unregister() {
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            commandMap.setAccessible(true);
            knownCommands.setAccessible(true);
            ((Map<String, Command>) knownCommands.get(commandMap.get(Bukkit.getServer()))).remove(getName());
            this.unregister((CommandMap) commandMap.get(Bukkit.getServer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        TranslationManager messages = Plugin.getInstance().getMessagesManager();

        // Check permission for the entire command class
        Permission classPermission = classObject.getClass().getAnnotation(Permission.class);
        if (!hasPermission(player, classPermission)) {
            messages.sendMessage("commands.no_permission", player);
            return false;
        }

        if (args.length == 0 && commandMethod != null) {
            return executeMethod(player, messages, commandMethod);
        }

        for (Map.Entry<String, Method> entry : subCommands.entrySet()) {
            String subcommandName = entry.getKey();
            Method subcommandMethod = entry.getValue();

            String[] split = subcommandName.split(" ");
            Subcommand subcommandAnnotation = subcommandMethod.getAnnotation(Subcommand.class);
            if (subcommandAnnotation != null && args.length > 0 && split[0].equalsIgnoreCase(args[0])) {
                if (split.length == 1 || sameArgs(split, args)) {
                    if (subcommandMethod.getParameterCount() == 2) {
                        return executeMethodWithDynamicParameters(player, messages, subcommandMethod, args);
                    }

                    return executeMethod(player, messages, subcommandMethod);
                }
            }
        }

        return true;
    }

    @Contract(pure = true)
    private boolean sameArgs(String @NotNull [] args1, String @NotNull [] args2) {
        if (args2.length < args1.length) {
            return false;
        }

        for (int i = 0; i < args1.length; i++) {
            if (!args1[i].equals(args2[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean executeMethodWithDynamicParameters(Player player, TranslationManager messages, @NotNull Method method, String[] args) {
        Permission subcommandPermission = method.getAnnotation(Permission.class);
        if (subcommandPermission != null && !hasPermission(player, subcommandPermission)) {
            messages.sendMessage("commands.no_permission", player);
            return true;
        }

        // Check cooldown for the subcommand
        if (!checkAndUseCooldown(player, method)) {
            messages.sendMessage("commands.on_cooldown", player);
            return true;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();

        // Check for method with Player and String parameters
        if (parameterTypes.length == 2 && parameterTypes[0] == Player.class && parameterTypes[1] == String.class && args.length == 2) {
            try {
                method.invoke(classObject, player, args[1]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }

        // Check for method with Player and OfflinePlayer parameters
        if (parameterTypes.length == 2 && parameterTypes[0] == Player.class && parameterTypes[1] == OfflinePlayer.class && args.length == 2) {
            try {
                method.invoke(classObject, player, Bukkit.getOfflinePlayer(args[1]));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }


    private boolean executeMethod(Player player, TranslationManager messages, @NotNull Method method) {
        Permission subcommandPermission = method.getAnnotation(Permission.class);
        if (subcommandPermission != null && !hasPermission(player, subcommandPermission)) {
            messages.sendMessage("commands.no_permission", player);
            return true;
        }

        // Check cooldown for the subcommand
        if (!checkAndUseCooldown(player, method)) {
            messages.sendMessage("commands.on_cooldown", player);
            return true;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1 && parameterTypes[0] == Player.class) {
            try {
                method.invoke(classObject, player);
            } catch (IllegalAccessException | InvocationTargetException ignore) {}
            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        // Check permission for the entire command class
        if (!hasPermission(player, classObject.getClass().getAnnotation(Permission.class))) {
            return Collections.emptyList();
        }

        return new ArrayList<>(getAllSubcommandsForLength(args, args.length)
                .stream()
                .filter(subCommand -> subCommands.get(subCommand) == null || !subCommands.get(subCommand).isAnnotationPresent(Permission.class) || hasPermission(player, subCommands.get(subCommand).getAnnotation(Permission.class)))
                .filter(subCommand -> subCommands.get(subCommand) == null || checkCooldown(player, subCommands.get(subCommand)))
                .toList());
    }

    private boolean hasPermission(Player player, me.opkarol.oplibrary.commands.annotations.Permission permission) {
        return permission == null || player.hasPermission(permission.value());
    }

    public List<String> getAllSubcommandsForLength(String[] args, int length) {
        List<String> matchingSubcommands = new ArrayList<>();

        if (length == 1) {
            // Return all top-level subcommands that start with the given argument
            matchingSubcommands.addAll(subCommands.keySet().stream()
                    .map(subcommand -> subcommand.split(" ")[0])
                    .filter(subcommand -> subcommand.startsWith(args[length - 1]))
                    .toList());
        } else {
            // Return subcommands for the specified length
            String parentSubcommand = String.join(" ", Arrays.copyOfRange(args, 0, length - 1));
            matchingSubcommands.addAll(subCommands.keySet().stream()
                    .filter(subcommand -> subcommand.startsWith(parentSubcommand + " "))
                    .map(subcommand -> subcommand.substring(parentSubcommand.length() + 1))
                    .filter(subcommand -> !subcommand.contains(" "))
                    .toList());
        }

        return matchingSubcommands;
    }

    private boolean checkAndUseCooldown(@NotNull Player player, Method method) {
        long cooldown = getCooldown(player.getUniqueId(), method);
        long currentTime = System.currentTimeMillis();
        if (currentTime - cooldown >= 0) {
            // The cooldown has expired; reset it
            setCooldown(player.getUniqueId(), method, currentTime + getCooldownDuration(method));
            return true;
        } else {
            // The player is still on cooldown
            return false;
        }
    }

    private boolean checkCooldown(@NotNull Player player, Method method) {
        long cooldown = getCooldown(player.getUniqueId(), method);
        long currentTime = System.currentTimeMillis();
        return currentTime - cooldown >= 0;
    }

    private long getCooldown(UUID playerId, Method method) {
        return cooldownMap.getOrDefault(getCooldownKey(playerId, method), 0L);
    }

    private void setCooldown(UUID playerId, Method method, long expirationTime) {
        cooldownMap.put(getCooldownKey(playerId, method), expirationTime);
    }

    private long getCooldownDuration(@NotNull Method method) {
        Cooldown cooldownAnnotation = method.getAnnotation(Cooldown.class);
        if (cooldownAnnotation != null) {
            return cooldownAnnotation.unit().toMillis(cooldownAnnotation.value());
        } else {
            return 0L; // Default to no cooldown if annotation is not present
        }
    }

    @Contract("_, _ -> new")
    private @NotNull UUID getCooldownKey(@NotNull UUID playerId, @NotNull Method method) {
        return new UUID(playerId.getMostSignificantBits() ^ method.hashCode(), playerId.getLeastSignificantBits());
    }
}