package com.game.service;

import com.game.entity.Player;

import java.util.List;

public interface PlayerService {
    List<Player> getPlayersList();

    Player getPlayer(Long id);

    void deletePlayer(Long id);

    void savePlayer(Player player);




}
