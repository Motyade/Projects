package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;


import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.List;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private static final Date MIN_DATA = Timestamp.valueOf(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0));
    private static final Date MAX_DATA = Timestamp.valueOf(LocalDateTime.of(3000, Month.JANUARY, 1, 0, 0, 0));


    @Override
    public List<Player> getPlayersList() {
        return entityManagerFactory.createEntityManager().createQuery("from Player ", Player.class).getResultList();
    }

    @Override
    public Player getPlayer(Long id){
        Player player = entityManagerFactory.createEntityManager().find(Player.class, id);
        return player;
    }

    @Override
    public void deletePlayer(Long id){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Player player = entityManager.find(Player.class, id);

        entityManager.getTransaction().begin();
        entityManager.remove(player);
        entityManager.getTransaction().commit();
    }

    @Override
    public Player savePlayer(Player player){
        if (player.getName() == null || player.getExperience() == 0 || player.getProfession() == null
                || player.getBirthday() == null || player.getTitle() == null || player.getRace() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if(player.getBanned() == null){
            player.setBanned(false);
        }

        if (player.getTitle().length() > 30 || player.getName().length() > 12 || player.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Date playerBirthday = player.getBirthday();
        if (playerBirthday.getTime() < 0 || playerBirthday.before(MIN_DATA) || playerBirthday.after(MAX_DATA)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (player.getExperience() < 0 || player.getExperience() > 10000000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Integer playerExperience = player.getExperience();
        Integer playerLevel = (int) ((Math.sqrt(2500+200*playerExperience)-50)/100);
        Integer playerUntilNextLevel = 50*(playerLevel+1)*(playerLevel+2)-playerExperience;
        player.setLevel(playerLevel);
        player.setUntilNextLevel(playerUntilNextLevel);

        Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        session.saveOrUpdate(player);
        return player;

        //entityManagerFactory.createEntityManager().merge(player);
    }

}
