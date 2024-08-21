package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.commands.annotations.Command;
import me.opkarol.oplibrary.commands.annotations.Default;
import org.bukkit.entity.Player;

@Command("test")
public class TestCommand {

    @Default
    public void testCommand(Player player) {
        Test.mainPlot.open(player);
    }
}
