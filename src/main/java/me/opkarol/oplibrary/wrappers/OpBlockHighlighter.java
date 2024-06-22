package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.runnable.OpTimerRunnable;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OpBlockHighlighter {
    private final Block block;
    private final Particle particle;

    public OpBlockHighlighter(@NotNull Location location, Particle particle) {
        this(location.getBlock(), particle);
    }

    public OpBlockHighlighter(Block block, Particle particle) {
        this.block = block;
        this.particle = particle;
    }

    public void highlightFor(Player player) {
        if (block.isEmpty()) {
            return;
        }

        List<Location> locations = getHollowCubeLocations(block.getLocation());
        for (Location location : locations) {
            new OpParticle(particle).setLocation(location).display(player);
        }
    }

    public void highlightFor(Player player, int seconds) {
        new OpTimerRunnable((r) -> {
            if (block.isEmpty()) {
                r.cancelTask();
                return;
            }

            highlightFor(player);
        }, seconds);
    }

    private static @NotNull List<Location> getHollowCubeLocations(@NotNull Location location) {
        List<Location> result = new ArrayList<>();
        World world = location.getWorld();
        double minX = location.getBlockX();
        double minY = location.getBlockY();
        double minZ = location.getBlockZ();
        double maxX = location.getBlockX() + 1;
        double maxY = location.getBlockY() + 1;
        double maxZ = location.getBlockZ() + 1;
        double particleDistance = 0.25;

        for (double x = minX; x <= maxX; x = Math.round((x + particleDistance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + particleDistance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + particleDistance) * 1e2) / 1e2) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
    }
}
