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
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.ServerInfo;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Utils {

    public static ServerInfo findServer(@Nullable ServerInfo oldServer) {
        //We only want lobby servers that are online

        LobbyBalancer.getInstance().getLogger().debug(" >>> Got request to join server");

        List<ServerInfo> lobbies = ProxyServer.getInstance().getServers()
                .stream()
                .filter(Utils::isServerLobby)
                .filter((serverInfo -> !serverInfo.equals(oldServer)))
                .filter(Utils::isServerOnline)
                .collect(Collectors.toList());

        //If a server has less than the minimum players, prioritize them
        for(ServerInfo lobby : lobbies) {
            if (lobby.getPlayers().size() < LobbyBalancer.getInstance().getConfig().getInt("minplayers")) {
                LobbyBalancer.getInstance().getLogger().debug(" >>> Decided server based off of minimum players");
                return lobby;
            }
        }

        //Otherwise, return the server with the least amount of players
        ServerInfo lobby = lobbies.stream()
                .sorted(Comparator.comparingInt(a -> a.getPlayers().size()))
                .collect(Collectors.toList()).get(0);

        LobbyBalancer.getInstance().getLogger().debug(" >>> Decided server based off of the least amount of players");

        return lobby;
    }

    public static void createLobby() {
        String lobbyPrefix = LobbyBalancer.getInstance().getConfig().getString("lobbyprefix");

        //Ensure a server with the lobby prefix doesn't already exist
        if (ProxyServer.getInstance().getServerInfo(lobbyPrefix) != null) {
            LobbyBalancer.getInstance().getLogger().critical("A server with the name "+lobbyPrefix+" already exists. Players won't be able to join this server, as any attempts to join this server will make them join a pseudo-random lobby server.");
            return;
        }

        //We have to put in a random address
        ServerInfo baseInfo = findServer(null);

        ServerInfo baseLobby = new ServerInfo(lobbyPrefix,baseInfo.getAddress(), null);
        ProxyServer.getInstance().registerServerInfo(baseLobby);
    }

    public static boolean isServerOnline(@Nullable ServerInfo server) {
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
            LobbyBalancer.getInstance().getLogger().critical("The server "+server.getServerName()+" appears to be offline. This will delay players connecting to the server. Feel free to make an issue telling me to add ping caching.");
            return false; //If it errors, it's offline
        }
    }

    //Get all lobby servers as long as their name isn't exactly the lobby prefix
    public static boolean isServerLobby(ServerInfo server) {
        String lobbyPrefix = LobbyBalancer.getInstance().getConfig().getString("lobbyprefix");
        return server.getServerName().startsWith(lobbyPrefix) && !server.getServerName().equals(lobbyPrefix);
    }
}
