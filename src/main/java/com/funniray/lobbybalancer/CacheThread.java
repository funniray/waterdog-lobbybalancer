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

import com.nukkitx.network.raknet.RakNetPong;
import dev.waterdog.waterdogpe.network.ServerInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheThread {

    private static final Map<String,Boolean> serverCache = new ConcurrentHashMap<>();

    public static void runPings(Collection<ServerInfo> servers) {
        for (ServerInfo server : servers) {
            serverCache.put(server.getServerName(), pingServer(server));
        }
    }

    private static boolean pingServer(ServerInfo server) {
        //If the server is null, then it's not online
        if (server == null) {
            return false;
        }

        //If there's players on the server, then it's probably online
        if (server.getPlayers().size() > 0) {
            return true;
        }

        //Otherwise, attempt to ping the server
        try {
            RakNetPong pong = server.ping(LobbyBalancer.getInstance().getConfig().getInt("pingtimeout"), TimeUnit.SECONDS).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            //e.printStackTrace(); //This will spam the console
            if (LobbyBalancer.getInstance().getConfig().getBoolean("logfailedpings")) {
                LobbyBalancer.getInstance().getLogger().warning("The server " + server.getServerName() + " appears to be offline.");
            }
            return false; //If it errors, it's offline
        }
    }

    public static boolean isServerOnline(ServerInfo server) {
        Boolean cached = serverCache.get(server.getServerName());
        if (cached == null) {
            return false;
        }
        return serverCache.get(server.getServerName());
    }
}
