package me.opkarol.oplibrary.extensions;

import me.opkarol.oplibrary.injection.IgnoreInject;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings("unused")
@IgnoreInject
public class Vault {
    private static Vault vault;
    private boolean enabled;
    private Economy economy;

    public Vault() {
        vault = this;
        enabled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (enabled) {
            enabled = setupEconomy();
        }
    }

    public static Vault getInstance() {
        return vault == null ? new Vault() : vault;
    }

    public static VAULT_RETURN_INFO remove(OfflinePlayer player, double amount) {
        return getInstance().withdraw(player, amount);
    }

    public static VAULT_RETURN_INFO add(OfflinePlayer player, double amount) {
        return getInstance().deposit(player, amount);
    }

    public static double getBalance(OfflinePlayer player) {
        return getInstance().get(player);
    }

    public boolean isEnabled() {
        return enabled;
    }

    private boolean setupEconomy() {
        if (!enabled) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public VAULT_RETURN_INFO withdraw(OfflinePlayer player, double amount) {
        if (isEnabled()) {
            if (has(player, amount)) {
                if (economy.withdrawPlayer(player, amount).transactionSuccess()) {
                    return VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL;
                }
                return VAULT_RETURN_INFO.WITHDRAW_NOT_SUCCESSFUL;
            }
            return VAULT_RETURN_INFO.WITHDRAW_TOO_BROKE;
        }
        return VAULT_RETURN_INFO.PLUGIN_NOT_ENABLED;
    }

    public VAULT_RETURN_INFO deposit(OfflinePlayer player, double amount) {
        if (isEnabled()) {
            if (economy.depositPlayer(player, amount).transactionSuccess()) {
                return VAULT_RETURN_INFO.DEPOSIT_SUCCESSFUL;
            }
            return VAULT_RETURN_INFO.DEPOSIT_NOT_SUCCESSFUL;
        }
        return VAULT_RETURN_INFO.PLUGIN_NOT_ENABLED;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    public double get(OfflinePlayer player) {
        if (!isEnabled()) {
            return 0;
        }

        return economy.getBalance(player);
    }

    public enum VAULT_RETURN_INFO {
        PLUGIN_NOT_ENABLED, WITHDRAW_SUCCESSFUL, WITHDRAW_NOT_SUCCESSFUL, WITHDRAW_TOO_BROKE, DEPOSIT_SUCCESSFUL, DEPOSIT_NOT_SUCCESSFUL
    }
}
