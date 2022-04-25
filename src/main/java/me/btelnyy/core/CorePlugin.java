package me.btelnyy.core;

import me.btelnyy.core.command.*;
import me.btelnyy.core.listener.EventListener;
import me.btelnyy.core.service.ConfigLoaderService;
import me.btelnyy.core.service.TextFileMessageService;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class CorePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // check if our config exists
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                saveDefaultConfig();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Config.yml could not be created. Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // load the rules
        TextFileMessageService rulesMessageService = new TextFileMessageService(new File(getDataFolder(), "rules.txt"), getLogger());
        rulesMessageService.loadMessages();

        // load MOTD on plugin enable
        TextFileMessageService motdMessageService = new TextFileMessageService(new File(getDataFolder(), "random_motd.txt"), getLogger());
        motdMessageService.loadMessages();

        // load suicide messages on enable
        TextFileMessageService suicideMessageService = new TextFileMessageService(new File(getDataFolder(), "death_msg.txt"), getLogger());
        suicideMessageService.loadMessages();

        // load config
        ConfigLoaderService configLoaderService = new ConfigLoaderService(getConfig(), getLogger());
        configLoaderService.loadConfig();

        // event handle
        getServer().getPluginManager().registerEvents(new EventListener(motdMessageService), this);

        registerCommandExecutor("suicide",     new CommandSuicide(suicideMessageService));
        registerCommandExecutor("dc",          new CommandDisconnect());
        registerCommandExecutor("rules",       new CommandRules(rulesMessageService));
        registerCommandExecutor("vtp",         new CommandVTP());
        registerCommandExecutor("vote",        new CommandVote());
        registerCommandExecutor("ping",        new CommandPing());
        registerCommandExecutor("voterestart", new CommandVoteServerRestart());
        registerCommandExecutor("pvp",         new CommandPvp());
        registerCommandExecutor("hardcore",    new CommandHardcore());
        registerCommandExecutor("revive",      new CommandRevive());
        registerCommandExecutor("reviveall",   new CommandReviveAll());
        registerCommandExecutor("whereamI",    new CommandCoords());
        registerCommandExecutor("breload",     new CommandReload(configLoaderService, rulesMessageService, motdMessageService, suicideMessageService));
        registerCommandExecutor("myspawn", new CommandMySpawn());
        getLogger().log(Level.INFO, "Check out the project on GitHub!: https://github.com/BTELNYY/btelnyy-core");
    }

    private void registerCommandExecutor(String commandName, CommandExecutor commandExecutor) {
        PluginCommand command = this.getCommand(commandName);
        if (command == null)
            throw new NullPointerException(String.format("\"%s\" is not registered in the plugin.yml", commandName));
        command.setExecutor(commandExecutor);
    }
}
