package com.game.repository;

import com.game.entity.Player;

import java.util.List;

public interface PlayerRepository {
    List<Player> getPlayersList();

    Player getPlayer(Long id);

    void deletePlayer(Long id);

    Player savePlayer(Player player);
}
