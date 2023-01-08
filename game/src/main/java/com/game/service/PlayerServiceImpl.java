package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    @Transactional
    public List<Player> getPlayersList(){
        return playerRepository.getPlayersList();
    }

    @Override
    @Transactional
    public Player getPlayer(Long id) {
        return playerRepository.getPlayer(id);
    }

    @Override
    @Transactional
    public void deletePlayer(Long id){
        playerRepository.deletePlayer(id);
    }
    @Override
    @Transactional
    public void savePlayer(Player player){
        playerRepository.savePlayer(player);
    }
}
