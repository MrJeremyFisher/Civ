package vg.civcraft.mc.namelayer.command.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.namelayer.GroupManager.PlayerType;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.BaseCommandMiddle;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.GroupPermission;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class ListPermissions extends BaseCommandMiddle {

    @CommandAlias("nllp|listperms|perms|permissions")
    @Syntax("<group> [rank (e.g: MEMBERS)]")
    @Description("Show permissions for a PlayerType in a specific group.")
    @CommandCompletion("@NL_Groups @NL_Ranks")
    public void execute(CommandSender sender, String groupName, @Optional String playerRank) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be run by players", NamedTextColor.RED));
            return;
        }
        Group g = gm.getGroup(groupName);
        if (groupIsNull(sender, groupName, g)) {
            return;
        }
        UUID uuid = NameAPI.getUUID(player.getName());
        PlayerType playerType = g.getPlayerType(uuid);
        if (playerType == null) {
            player.sendMessage(ChatColor.RED + "You do not have access to this group.");
            return;
        }
        String perms = null;
        GroupPermission gPerm = gm.getPermissionforGroup(g);
        if (playerRank != null) {
            if (!gm.hasAccess(g, uuid, PermissionType.getPermission("LIST_PERMS"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission in this group to run this command.");
                return;
            }
            PlayerType check = PlayerType.getPlayerType(playerRank);
            if (check == null) {
                PlayerType.displayPlayerTypes(player);
                return;
            }
            perms = gPerm.listPermsforPlayerType(check);
        } else
            perms = gPerm.listPermsforPlayerType(playerType);

        player.sendMessage(ChatColor.GREEN + perms);
    }
}
