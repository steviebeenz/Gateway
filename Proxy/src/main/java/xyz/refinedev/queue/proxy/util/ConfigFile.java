package xyz.refinedev.queue.proxy.util;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/26/2021
 * Project: Gateway
 */

@Getter
public class ConfigFile {

    private final File file;
    private Configuration config;

    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    @SneakyThrows
    public ConfigFile(Plugin plugin, String name) {
        file = new File(plugin.getDataFolder(), name + ".yml");

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

        if (!file.exists()) {
            InputStream in = plugin.getResourceAsStream(name + ".yml");
            Files.copy(in, file.toPath());
        }

        config = provider.load(file);
    }

    @SneakyThrows
    public void save() {
        provider.save(config, file);
    }

    @SneakyThrows
    public void reload() {
        config = provider.load(file);
    }
}

