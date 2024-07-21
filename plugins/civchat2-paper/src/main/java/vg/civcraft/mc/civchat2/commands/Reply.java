package vg.civcraft.mc.civchat2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civchat2.ChatStrings;
import vg.civcraft.mc.civchat2.CivChat2;
import vg.civcraft.mc.civchat2.CivChat2Manager;

public class Reply extends BaseCommand {

    @CommandAlias("reply|r")
    @Syntax("[message]")
    @Description("Replies to the last person you sent a message to or received one from")
    public void execute(CommandSender sender, @Optional String chatMessage) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be run by players", NamedTextColor.RED));
            return;
        }
        CivChat2Manager chatMan = CivChat2.getInstance().getCivChat2Manager();
        UUID receiverUUID = chatMan.getPlayerReply(player);

        Player receiver = Bukkit.getPlayer(receiverUUID);
        if (receiver == null) {
            player.sendMessage(ChatStrings.chatNoOneToReplyTo);
            return;
        }

        if (!(receiver.isOnline())) {
            player.sendMessage(ChatStrings.chatPlayerIsOffline);
            return;
        }

        if (player.getUniqueId().equals(receiver.getUniqueId())) {
            player.sendMessage(ChatStrings.chatCantMessageSelf);
            return;
        }

        if (!(chatMessage == null)) {
            chatMan.sendPrivateMsg(player, receiver, chatMessage);
            return;
        }
        // Player to chat with reply user
        chatMan.removeChannel(player);
        chatMan.addChatChannel(player, receiver);
        player.sendMessage(String.format(ChatStrings.chatNowChattingWith, receiver.getName()));
    }
}
