/*
 *    LobbyBalancer - WaterdogPE plugin for balancing lobbies
 *    Copyright (C) 2021  Funniray
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    I am available for any questions/requests: funniray10@gmail.com
 */

package com.funniray.lobbybalancer;

import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;

public class Listeners {

    public static void PreTransferHandler(PreTransferEvent e) {
        if (e.getTargetServer().getServerName().equals(LobbyBalancer.getInstance().getConfig().getString("lobbyprefix"))){
            //Don't let the player join the server they're currently on
            ServerInfo info = Utils.findServer(e.getPlayer().getServerInfo());
            if (info == null) {
                e.getPlayer().sendMessage("§cUnable to find a suitable lobby server to join");
                e.setCancelled();
                return;
            }
            e.setTargetServer(info);
        }
    }
}
