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

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    private static HashMap<String, Boolean> onlineCache;

    public static ServerInfo findServer(@Nullable ServerInfo oldServer) {
        //We only want lobby servers that are online

        LobbyBalancer.getInstance().getLogger().debug(" >>> Got request to join server");

        List<ServerInfo> lobbies = ProxyServer.getInstance().getServers()
                .stream()
                .filter(Utils::isServerLobby)
                .filter((serverInfo -> !serverInfo.equals(oldServer)))
                .filter(CacheThread::isServerOnline)
                .collect(Collectors.toList());

        //If a server has less than the minimum players, prioritize them
        for(ServerInfo lobby : lobbies) {
            if (lobby.getPlayers().size() < LobbyBalancer.getInstance().getConfig().getInt("minplayers")) {
                LobbyBalancer.getInstance().getLogger().debug(" >>> Decided server based off of minimum players");
                return lobby;
            }
        }

        //Otherwise, return the server with the least amount of players
        List<ServerInfo> sortedLobbies = lobbies.stream()
                .sorted(Comparator.comparingInt(a -> a.getPlayers().size()))
                .collect(Collectors.toList());

        if (sortedLobbies.size()==0) {
            LobbyBalancer.getInstance().getLogger().fatal("Failed to find a valid server to join");
            return null;
        }

        LobbyBalancer.getInstance().getLogger().debug(" >>> Decided server based off of the least amount of players");

        return sortedLobbies.get(0);
    }

    public static void createLobby() {
        String lobbyPrefix = LobbyBalancer.getInstance().getConfig().getString("lobbyprefix");

        //Ensure a server with the lobby prefix doesn't already exist
        if (ProxyServer.getInstance().getServerInfo(lobbyPrefix) != null) {
            LobbyBalancer.getInstance().getLogger().fatal("A server with the name "+lobbyPrefix+" already exists. Players won't be able to join this server, as any attempts to join this server will make them join a pseudo-random lobby server.");
            return;
        }

        //We have to put in a random address
        ServerInfo baseInfo = ProxyServer.getInstance().getServers()
                .stream()
                .filter(Utils::isServerLobby)
                .collect(Collectors.toList())
                .get(0);

        ServerInfo baseLobby = new BedrockServerInfo(lobbyPrefix,baseInfo.getAddress(), null);
        ProxyServer.getInstance().registerServerInfo(baseLobby);
    }

    //Get all lobby servers as long as their name isn't exactly the lobby prefix
    public static boolean isServerLobby(ServerInfo server) {
        String lobbyPrefix = LobbyBalancer.getInstance().getConfig().getString("lobbyprefix");
        return server.getServerName().startsWith(lobbyPrefix) && !server.getServerName().equals(lobbyPrefix);
    }
}
