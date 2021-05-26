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

package com.funniray.lobbybalancer.handlers;

import com.funniray.lobbybalancer.Utils;
import dev.waterdog.waterdogpe.network.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IReconnectHandler;

public class LobbyReconnectHandler implements IReconnectHandler {
    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer player, ServerInfo oldServer, String kickMessage) {
        return Utils.findServer(oldServer);
    }
}
