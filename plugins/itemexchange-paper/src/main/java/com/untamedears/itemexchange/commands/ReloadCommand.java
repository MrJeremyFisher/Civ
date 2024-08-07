package com.untamedears.itemexchange.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.untamedears.itemexchange.ItemExchangePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ier|iereload")
@CommandPermission("itemexchange.reload")
public final class ReloadCommand extends BaseCommand {

    private final ItemExchangePlugin plugin;

    public ReloadCommand(ItemExchangePlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Reload's ItemExchange's config.")
    public void onReloadConfig(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be run by players", NamedTextColor.RED));
            return;
        }
        this.plugin.saveDefaultConfig();
        ItemExchangePlugin.config().reset();
        this.plugin.reloadConfig();
        ItemExchangePlugin.config().parse();
        sender.sendMessage(ChatColor.GREEN + "ItemExchange's config has been reloaded.");
    }

}
