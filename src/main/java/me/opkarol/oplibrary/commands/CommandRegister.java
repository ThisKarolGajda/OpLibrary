package me.opkarol.oplibrary.commands;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@IgnoreInject
public class CommandRegister {
    private final List<Command> commandList = new ArrayList<>();

    public void registerCommand(Command command) {
        commandList.add(command);
        command.register();
    }

    public void registerClassObject(@NotNull Object object) {
        registerCommand(new Command(object.getClass()));
    }

    public void registerClass(Class<?> object) {
        registerCommand(new Command(object));
    }

    public void unregisterCommands() {
        for (Command command : commandList) {
            command.unregister();
            commandList.remove(command);
        }
    }

    public List<Command> getCommandList() {
        return commandList;
    }
}
