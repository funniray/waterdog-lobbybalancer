package com.funniray.lobbybalancer.commands;

import com.funniray.lobbybalancer.LobbyBalancer;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super(LobbyBalancer.getInstance().getConfig().getString("lobbyprefix"), CommandSettings.builder()
                .setDescription("Transfers the command executor to a lobby server")
                .setPermission("lobbybalancer.lobby")
                .build());
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("You must be a player to execute this command");
            return false;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        player.connect(ProxyServer.getInstance().getServerInfo(LobbyBalancer.getInstance().getConfig().getString("lobbyprefix")));
        return true;
    }

}
