package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.messages.StringMessage;
import me.opkarol.oplibrary.tools.Heads;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Test {

    private static final StringMessage I_AM_MESSAGE = new StringMessage("Jestem %name%",
            player -> Map.of("%name%", player.getName()));

    public static final GlobalInventory mainPlot = GlobalInventory.row3("Zarządzanie działką")
            .add("test", "Kamień", List.of(), 10, Heads.get("1828522e8680c55b2fe5b9bcfc8ad06e3323f9783d8351ca50edeb431e63190f"), click -> {
                click.cancel();
                I_AM_MESSAGE.send(click);
                Player player = click.getPlayer();
                player.sendMessage("You clicked: " + click.getSlot());
            })
            .fillAllEmpty();

    // private static GlobalInventory mainPlot = new GlobalInventory()
    //  .add("test", click -> {
    //      Player player = click.getPlayer();
    //      int slot = click.getSlot();
    //      ActionType type = click.getType();
    //      PlayerClickEvent event = click.getEvent();
    //  })
    //  .fillWith(
    //      "test1",
    //      list,
    //      click -> {
    //          Player player = click.getPlayer();
    //          int slot = click.getSlot();
    //          ActionType type = click.getType();
    //          PlayerClickEvent event = click.getEvent();
    //          I item = click.getItem();
    //      }
    //  )
}
