package me.opkarol.oplibrary.commands;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.commands.annotations.*;
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

import static me.opkarol.oplibrary.translations.Messages.sendMessage;

public class Command extends BukkitCommand {
    private final Object classObject;
    private final Map<String, Method> subCommands = new HashMap<>();
    private Method commandMethod;
    private final Map<UUID, Long> cooldownMap = new HashMap<>();
    private Method noUseMethod;

    public Command(Class<?> clazz) {
        super(clazz.getAnnotation(me.opkarol.oplibrary.commands.annotations.Command.class).value());
        try {
            this.classObject = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // Set command default method
        Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Default.class)).findAny().ifPresent(method -> this.commandMethod = method);
        Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(NoUse.class)).findAny().ifPresent(method -> this.noUseMethod = method);

        // Set command subcommands
        for (Method method : Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Subcommand.class))
                .toList()) {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            String name = subcommand.value();
            while (subCommands.containsKey(name)) {
                name += "_";
            }
            subCommands.put(name, method);
        }
    }

    public void register() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            org.bukkit.command.Command command = commandMap.getCommand(this.getName());
            if (command == null || !command.isRegistered()) {
                commandMap.register(getName(), Plugin.getInstance().getName(), this);
            } else {
                throw new IllegalStateException("This command is already registered.");
            }
        } catch (Exception e) {
            unregister();
            Field bukkitCommandMap;
            try {
                bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap;
            try {
                commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            org.bukkit.command.Command command = commandMap.getCommand(this.getName());
            if (command == null || !command.isRegistered()) {
                commandMap.register(getName(), Plugin.getInstance().getName(), this);
            } else {
                throw new IllegalStateException("This command is already registered.");
            }
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

        // Check permission for the entire command class
        Permission classPermission = classObject.getClass().getAnnotation(Permission.class);
        if (!hasPermission(player, classPermission)) {
            sendMessage("commands.no_permission", player);
            return false;
        }

        if (args.length == 0 && commandMethod != null) {
            return executeMethod(player, commandMethod);
        }

        if (args.length == 1 && commandMethod != null && (Arrays.equals(commandMethod.getParameterTypes(), new Class[]{Player.class, OfflinePlayer.class}) || Arrays.equals(commandMethod.getParameterTypes(), new Class[]{Player.class, String.class}))) {
            return executeMethodWithDynamicParameters(player, commandMethod, args, 0);
        }

        if (args.length > 1 && commandMethod != null && commandMethod.getParameterCount() > 1 && commandMethod.getParameterTypes()[1].equals(String.class)) {
            String combinedArgs = String.join(" ", args);
            return executeMethodWithDynamicParameters(player, commandMethod, new String[]{combinedArgs}, 0);
        }

        if (args.length > 1 && commandMethod != null && commandMethod.getParameterCount() > 2 && commandMethod.getParameterTypes()[1].equals(OfflinePlayer.class) && commandMethod.getParameterTypes()[2].equals(String.class)) {
            String combinedArgs = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            return executeMethodWithDynamicParameters(player, commandMethod, new String[]{args[0], combinedArgs}, 0);
        }

        for (Map.Entry<String, Method> entry : subCommands.entrySet()) {
            String subcommandName = entry.getKey().replace("_", "");
            Method subcommandMethod = entry.getValue();

            String[] split = subcommandName.split(" ");
            Subcommand subcommandAnnotation = subcommandMethod.getAnnotation(Subcommand.class);
            if (subcommandAnnotation != null && args.length > 0 && split[0].equalsIgnoreCase(args[0])) {
                if (sameArgs(split, args)) {
                    if (subcommandMethod.getParameterCount() == 2 && args.length == split.length + 1) {
                        return executeMethodWithDynamicParameters(player, subcommandMethod, args, split.length);
                    }

                    if (subcommandMethod.getParameterCount() == 1 && args.length == split.length) {
                        return executeMethod(player, subcommandMethod);
                    }
                }
            }
        }

        try {
            noUseMethod.invoke(classObject, player, args);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
        }

        return true;
    }

    private boolean sameArgs(String @NotNull [] split, String @NotNull [] args) {
        if (args.length < split.length) {
            return false;
        }

        for (int i = 0; i < split.length; i++) {
            if (!split[i].equals(args[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean executeMethodWithDynamicParameters(Player player, @NotNull Method method, String[] args, int splitLength) {
        Permission subcommandPermission = method.getAnnotation(Permission.class);
        if (subcommandPermission != null && !hasPermission(player, subcommandPermission)) {
            sendMessage("commands.no_permission", player);
            return true;
        }

        // Check cooldown for the subcommand
        if (!checkAndUseCooldown(player, method)) {
            sendMessage("commands.on_cooldown", player);
            return true;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        // Check for method with Player and String parameters
        if (parameterTypes.length == 2 && parameterTypes[0] == Player.class && parameterTypes[1] == String.class && args.length > splitLength) {
            try {
                method.invoke(classObject, player, args[splitLength]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }

        // Check for method with Player and OfflinePlayer parameters
        if (parameterTypes.length == 2 && parameterTypes[0] == Player.class && parameterTypes[1] == OfflinePlayer.class && args.length > splitLength) {
            try {
                method.invoke(classObject, player, Bukkit.getOfflinePlayer(args[splitLength]));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (parameterTypes.length == 3 && parameterTypes[0] == Player.class && parameterTypes[1] == OfflinePlayer.class && parameterTypes[2] == String.class && args.length > splitLength + 1) {
            try {
                method.invoke(classObject, player, Bukkit.getOfflinePlayer(args[splitLength]), args[splitLength + 1]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }


    private boolean executeMethod(Player player, @NotNull Method method) {
        Permission subcommandPermission = method.getAnnotation(Permission.class);
        if (subcommandPermission != null && !hasPermission(player, subcommandPermission)) {
            sendMessage("commands.no_permission", player);
            return true;
        }

        // Check cooldown for the subcommand
        if (!checkAndUseCooldown(player, method)) {
            sendMessage("commands.on_cooldown", player);
            return true;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1 && parameterTypes[0] == Player.class) {
            try {
                method.invoke(classObject, player);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
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

        List<String> suggestions = new ArrayList<>(getAllSubcommandsForLength(args, args.length)
                .stream()
                .filter(subCommand -> subCommands.get(subCommand) == null || !subCommands.get(subCommand).isAnnotationPresent(Permission.class) || hasPermission(player, subCommands.get(subCommand).getAnnotation(Permission.class)))
                .filter(subCommand -> subCommands.get(subCommand) == null || checkCooldown(player, subCommands.get(subCommand)))
                .toList());

        // Check if the main command method requires an OfflinePlayer parameter
        if (commandMethod != null && args.length == 1 && commandMethod.getParameterTypes().length == 2 && commandMethod.getParameterTypes()[1] == OfflinePlayer.class) {
            String lastArg = args[0].toLowerCase();
            List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(lastArg))
                    .toList();
            suggestions.addAll(playerNames);
        }

        for (Map.Entry<String, Method> entry : subCommands.entrySet()) {
            String subcommandName = entry.getKey().replace("_", "");
            Method subcommandMethod = entry.getValue();

            String[] split = subcommandName.split(" ");
            Subcommand subcommandAnnotation = subcommandMethod.getAnnotation(Subcommand.class);
            if (subcommandAnnotation != null && args.length > 0 && split[0].equalsIgnoreCase(args[0])) {
                if (sameArgs(split, args)) {
                    if (subcommandMethod.getParameterCount() == 2 && subcommandMethod.getParameterTypes()[1].equals(OfflinePlayer.class) && args.length == split.length + 1) {
                        String lastArg = args[args.length - 1].toLowerCase();
                        List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(lastArg))
                                .toList();
                        suggestions.addAll(playerNames);
                    }
                }
            }
        }

        return suggestions;
    }

    private boolean hasPermission(Player player, me.opkarol.oplibrary.commands.annotations.Permission permission) {
        return permission == null || player.hasPermission(permission.value());
    }

    public List<String> getAllSubcommandsForLength(String[] args, int length) {
        List<String> matchingSubcommands = new ArrayList<>();

        if (length == 1) {
            // Return all top-level subcommands that start with the given argument
            matchingSubcommands.addAll(subCommands.keySet().stream()
                    .map(subcommand -> subcommand.replace("_", "").split(" ")[0])
                    .filter(subcommand -> subcommand.replace("_", "").startsWith(args[length - 1]))
                    .toList());
        } else {
            // Return subcommands for the specified length
            String parentSubcommand = String.join(" ", Arrays.copyOfRange(args, 0, length - 1));
            matchingSubcommands.addAll(subCommands.keySet().stream()
                    .filter(subcommand -> subcommand.replace("_", "").startsWith(parentSubcommand + " "))
                    .map(subcommand -> subcommand.replace("_", "").substring(parentSubcommand.length() + 1))
                    .filter(subcommand -> !subcommand.replace("_", "").contains(" "))
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