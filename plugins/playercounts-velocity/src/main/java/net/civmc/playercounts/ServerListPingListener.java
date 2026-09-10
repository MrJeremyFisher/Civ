package net.civmc.playercounts;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class ServerListPingListener {
    private final ProxyServer server;
    private final Logger LOGGER;
    private final Map<String, String> serverMap;

    public ServerListPingListener(ProxyServer server, Logger logger, CommentedConfigurationNode config) {
        this.server = server;
        this.LOGGER = logger;
        List<CommentedConfigurationNode> servers = config.node("servers").childrenList();
        this.serverMap = new HashMap<>(servers.size());
        servers.forEach(s -> serverMap.put(s.node("host").getString(), s.node("servername").getString()));
    }

    @Subscribe
    public EventTask onPingRecv(ProxyPingEvent ppe) {
        InetSocketAddress virtualHost = ppe.getConnection().getVirtualHost().orElse(null);
        if (virtualHost == null) return null;

        String host = virtualHost.getHostString();
        String serverName = serverMap.get(host);
        if (serverName == null) return null; // Delegate to Velocity if the incoming address isn't known, it's probably the IP and so we don't know where it goes

        RegisteredServer retrievedServer = server.getServer(serverName.strip()).orElse(null);
        if (retrievedServer == null) {
            LOGGER.warn("Failed to retrieve server with configured server name \"{}\" for hostname \"{}\"", serverName, host);
            LOGGER.warn("Registered servers (by name) are:\n{}",
                server.getAllServers().stream().map(s -> s.getServerInfo().getName()).collect(Collectors.joining("\n"))
            );
            return null;
        }

        ServerPing.Builder pingBuilder = ppe.getPing().asBuilder();
        return EventTask.resumeWhenComplete(retrievedServer.ping().thenAccept((serverPing) -> {
                ServerPing.Players players = serverPing.getPlayers().orElse(null);
                if (players == null) {
                    return;
                }
                pingBuilder.onlinePlayers(players.getOnline());
                pingBuilder.maximumPlayers(players.getMax());
                ppe.setPing(pingBuilder.build());
            }
        ).exceptionally((throwable) -> {
            // Force Velocity to not show all players when pinging a server that is offline
            pingBuilder.onlinePlayers(0);
            ppe.setPing(pingBuilder.build());
            LOGGER.error("{} did not respond", retrievedServer.getServerInfo().getName());
            return null;
        }));
    }
}
