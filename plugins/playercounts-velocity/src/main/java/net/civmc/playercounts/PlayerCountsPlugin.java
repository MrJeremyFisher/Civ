package net.civmc.playercounts;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
    id = "playercounts", name = "Player Counts", version = "1.0.0",
    description = "Displays player counts per-server with Velocity"
)
public class PlayerCountsPlugin {
    private final ProxyServer server;
    private final Logger LOGGER;
    private final Path dataDirectory;
    private CommentedConfigurationNode config;

    @Inject
    public PlayerCountsPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.LOGGER = logger;
        this.dataDirectory = dataDirectory;
        loadConfig();
        LOGGER.info("Initialized");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new ServerListPingListener(server, LOGGER, config));
    }

    private void loadConfig() {
        try {
            // ensure data directory exists
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
        } catch (IOException e) {
            LOGGER.error("Could not create data directory: {}", dataDirectory, e);
            return;
        }

        // create config file if it doesn't exist
        Path configFile = dataDirectory.resolve("config.yml");
        if (!Files.exists(configFile)) {
            try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile);
                    LOGGER.info("Default configuration file created.");
                } else {
                    LOGGER.error("Default configuration file is missing in resources!");
                    return;
                }
            } catch (IOException e) {
                LOGGER.error("Could not create default configuration file: {}", configFile, e);
            }
        }

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configFile).build();
        try {
            config = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load configuration file: " + configFile, e);
        }
    }
}
