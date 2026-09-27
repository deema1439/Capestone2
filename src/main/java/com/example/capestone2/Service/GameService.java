package com.example.capestone2.Service;

import com.example.capestone2.Model.Game;
import com.example.capestone2.Repository.GameRepository;
import com.example.capestone2.Repository.SquadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
 private final GameRepository gameRepository;
 private final SquadRepository squadRepository;

 public List<Game>getGames(){
     return gameRepository.findAll();
 }

 public void addGame(Game game){
     gameRepository.save(game);
 }

 public boolean updateGame(Integer id,Game game){
     Game oldGame=gameRepository.findGameById(id);
     if(oldGame==null){
         return false;
     }
     oldGame.setGenre(game.getGenre());
     oldGame.setMaxPlayer(game.getMaxPlayer());
     oldGame.setName(game.getName());
     gameRepository.save(oldGame);
     return true;
 }


 public int deleteGame(Integer id){
     Game existingGame=gameRepository.findGameById(id);
     if(existingGame==null){
         return 0;
     }
     if (squadRepository.existsByGameId(id)) {
         return 1; // game has squads, cannot delete
     }

     gameRepository.delete(existingGame);
     return 2;
 }








































}
