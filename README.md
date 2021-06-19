[![release workflow badge](https://github.com/funniray/waterdog-lobbybalancer/actions/workflows/release.yml/badge.svg)](https://github.com/funniray/waterdog-lobbybalancer/releases/latest)  
## Waterdog-LobbyBalancer  
WaterdogPE plugin for balancing lobbies  
  
Lobby servers are gathered from the WaterdogPE config with the lobby prefix. By default, this is `lobby`.  
  
With the default configuration, lobby1, lobby2, and lobby3 would be detected as lobby servers, but game1 or game2 would not.  
  
You can transfer players to a pseudo-random lobby server by sending a transfer packet with the lobby prefix. By default, `/server lobby` will send them to a pseudo-random lobby server
  
### Downloads  
Download from [GitHub Releases](https://github.com/funniray/waterdog-lobbybalancer/releases/latest)  
  
### Building  
Build and compile with maven using `mvn package`  
  
### Config
```yaml
#A message that displays on startup explaining this plugin is a free plugin
showpiracywarning: true

#Any lobbies must start with this string. Anything after it doesn't matter
#Transfers to this prefix will send a player to a lobby, acting the same as if they join
lobbyprefix: lobby

#Minimum players in a lobby before balancing to another server
#A lobby must have x amount of players before another lobby starts getting players
#Helps your server in theory not look dead if all lobbies barely have players
#By default, lobby1 will fill up until it get to 10 players, then lobby2 will start filling up.
#Once all lobby servers get to the minimum players, then the player will join the server with the least amount of players
#This order is determined by the order of your servers in the WaterdogPE config
minplayers: 10

#Ping timeout in seconds for checking if a lobby is online
pingtimeout: 1

#Frequency servers are pinged in minutes
pingfrequency: 1

#If we should log to the console when a ping fails
logfailedpings: true
```  
  
### Commands/Permissions  
| Command          | Permission          | Description                      |
|------------------|---------------------|----------------------------------|
| /\<lobbyprefix\> | lobbybalancer.lobby | Sends executer to a lobby server |