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

import com.funniray.lobbybalancer.handlers.LobbyJoinHandler;
import com.funniray.lobbybalancer.handlers.LobbyReconnectHandler;
import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.stream.Collectors;

public final class LobbyBalancer extends Plugin {

    private void createDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceFile("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static LobbyBalancer instance;

    @Override
    public void onEnable() {
        // Plugin startup logic

        createDefaultConfig();

        LobbyBalancer.instance = this;

        Utils.createLobby();

        this.getProxy().getScheduler().scheduleRepeating(()->
                CacheThread.runPings(this.getProxy().getServers()
                        .stream()
                        .filter(Utils::isServerLobby)
                        .collect(Collectors.toList()))
                ,this.getConfig().getInt("pingfrequency")*20*60,true);

        this.getProxy().setJoinHandler(new LobbyJoinHandler());
        this.getProxy().setReconnectHandler(new LobbyReconnectHandler());
        this.getProxy().getEventManager().subscribe(PreTransferEvent.class, Listeners::PreTransferHandler);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static LobbyBalancer getInstance() {
        return instance;
    }
}
