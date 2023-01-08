package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/rest")
public class MyRESTController {
    @Autowired
    private PlayerService playerService;

    private static final Date MIN_DATA = Timestamp.valueOf(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0));
    private static final Date MAX_DATA = Timestamp.valueOf(LocalDateTime.of(3000, Month.JANUARY, 1, 0, 0, 0));

    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) Race race,
                                       @RequestParam(required = false) Profession profession,
                                       @RequestParam(required = false) Long after,
                                       @RequestParam(required = false) Long before,
                                       @RequestParam(required = false) Boolean banned,
                                       @RequestParam(required = false) Integer minExperience,
                                       @RequestParam(required = false) Integer maxExperience,
                                       @RequestParam(required = false) Integer minLevel,
                                       @RequestParam(required = false) Integer maxLevel,
                                       @RequestParam(required = false) PlayerOrder order,
                                       @RequestParam(required = false) Integer pageNumber,
                                       @RequestParam(required = false) Integer pageSize) {
        List<Player> allPlayers = playerService.getPlayersList();

        if (order == null) {
            order = PlayerOrder.ID;
        }

        Collections.sort(allPlayers, getComparatorByOrder(order));

        if (pageSize == null) {
            pageSize = 3;
        }

        if (pageNumber == null) {
            pageNumber = 0;
        }

        List<Player> rightPlayers = new ArrayList<>();

        for (Player player : allPlayers) {

            if (pageSize == rightPlayers.size()) {
                if (pageNumber == 0) {
                    break;
                } else {
                    rightPlayers.clear();
                    pageNumber--;
                }
            }

            if(isRightPlayer(player,name,title,race,profession,after,before,banned,minExperience,maxExperience,minLevel,maxLevel)){
                rightPlayers.add(player);
            }

        }
        return rightPlayers;
    }

    @GetMapping("/players/count")
    public Integer getPlayersCount(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Race race,
                                   @RequestParam(required = false) Profession profession,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean banned,
                                   @RequestParam(required = false) Integer minExperience,
                                   @RequestParam(required = false) Integer maxExperience,
                                   @RequestParam(required = false) Integer minLevel,
                                   @RequestParam(required = false) Integer maxLevel) {

        int count = 0;
        List<Player> allPlayers = playerService.getPlayersList();
        for (Player player : allPlayers) {
            if(isRightPlayer(player,name,title,race,profession,after,before,banned,minExperience,maxExperience,minLevel,maxLevel)){
                count++;
            }
        }

        return count;
    }

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable Long id) {
        Player player = playerService.getPlayer(id);

        if (id == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return player;
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable Long id) {

        Player player = getPlayer(id);

        if (id == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        playerService.deletePlayer(id);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player) {
        playerService.savePlayer(player);
        return player;
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {

        Player thisPlayer = playerService.getPlayer(id); //!

        if (id == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (thisPlayer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (player.getName() != null) {
            thisPlayer.setName(player.getName());
        }

        if (player.getTitle() != null) {
            thisPlayer.setTitle(player.getTitle());
        }

        if (player.getBirthday() != null) {
            thisPlayer.setBirthday(player.getBirthday());
        }

        if (player.getRace() != null) {
            thisPlayer.setRace(player.getRace());
        }

        if (player.getBanned() == null) {
            thisPlayer.setBanned(false);
        } else {
            thisPlayer.setBanned(player.getBanned());
        }

        if (player.getExperience() != null) {
            thisPlayer.setExperience(player.getExperience());
        }

        if (player.getProfession() != null) {
            thisPlayer.setProfession(player.getProfession());
        }

        playerService.savePlayer(thisPlayer);
        return thisPlayer;
    }

    private Comparator<Player> getComparatorByOrder(PlayerOrder order) {
        switch (order) {
            case ID:
                return new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return (o1.getId().compareTo(o2.getId()));
                    }
                };
            case NAME:
                return new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return (o1.getName()).compareTo(o2.getName());
                    }
                };
            case LEVEL:
                return new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return (o1.getLevel()).compareTo(o2.getLevel());
                    }
                };
            case BIRTHDAY:
                return new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getBirthday().compareTo(o2.getBirthday());
                    }
                };
            case EXPERIENCE:
                return new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return o1.getExperience().compareTo(o2.getExperience());
                    }
                };
        }
        return null;
    }

    private boolean isRightPlayer(Player player,
                                  String name,
                                  String title,
                                  Race race,
                                  Profession profession,
                                  Long after,
                                  Long before,
                                  Boolean banned,
                                  Integer minExperience,
                                  Integer maxExperience,
                                  Integer minLevel,
                                  Integer maxLevel) {
        boolean isRightPlayer = true;

        if (name != null) {
            if (!player.getName().contains(name)) {
                isRightPlayer = false;
            }
        }

        if (title != null) {
            if (!player.getTitle().contains(title)) {
                isRightPlayer = false;
            }
        }

        if (race != null) {
            if (!player.getRace().equals(race)) {
                isRightPlayer = false;
            }
        }

        if (profession != null) {
            if (!player.getProfession().equals(profession)) {
                isRightPlayer = false;
            }
        }

        if (after != null) {
            if (!(player.getBirthday().getTime() > after)) {
                isRightPlayer = false;
            }
        }

        if (before != null) {
            if (!(player.getBirthday().getTime() < before)) {
                isRightPlayer = false;
            }
        }

        if (banned != null) {
            if (!player.getBanned().equals(banned)) {
                isRightPlayer = false;
            }
        }

        if (minExperience != null) {
            if (!(player.getExperience() >= minExperience)) {
                isRightPlayer = false;
            }
        }

        if (maxExperience != null) {
            if (!(player.getExperience() <= maxExperience)) {
                isRightPlayer = false;
            }
        }

        if (minLevel != null) {
            if (!(player.getLevel() >= minLevel)) {
                isRightPlayer = false;
            }
        }

        if (maxLevel != null) {
            if (!(player.getLevel() <= maxLevel)) {
                isRightPlayer = false;
            }
        }

        return isRightPlayer;

    }
}
