package com.example.capestone2.Service;

import com.example.capestone2.Model.Game;
import com.example.capestone2.Model.Rank;
import com.example.capestone2.Repository.GameRepository;
import com.example.capestone2.Repository.PlayerRepository;
import com.example.capestone2.Repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankService {
  private final RankRepository rankRepository;
  private final GameRepository gameRepository;
  private final PlayerRepository playerRepository;


public List<Rank>getRanks(){
  return rankRepository.findAll();
}

public boolean addRank(Rank rank){
  Game existingGame=gameRepository.findGameById(rank.getGameId());
  if(existingGame==null){
    return false;
  }
  rankRepository.save(rank);
  return true;
}

public int updateRank(Integer id,Rank rank){
  Rank oldRank=rankRepository.findRankById(id);
  Game existingGame=gameRepository.findGameById(rank.getGameId());
  if(oldRank==null){
    return 0;//rank not found
  }
  if(existingGame==null){
    return 1;//game id not found
  }
  if (!oldRank.getGameId().equals(rank.getGameId())) {
    if (playerRepository.existsByRankId(id)) {
      return 2; // rank is in use, cannot change its game
    }
  }
  oldRank.setName(rank.getName());
  oldRank.setTierOrder(rank.getTierOrder());
  oldRank.setGameId(rank.getGameId());
  rankRepository.save(oldRank);
  return 3;

}

public int deleteRank(Integer id){
  Rank existingRank=rankRepository.findRankById(id);
  if(existingRank==null){
    return 0;
  }
  if (playerRepository.existsByRankId(id)) {
    return 1; //  "rank is in use"
  }
  rankRepository.delete(existingRank);
  return 2;
}




















}
