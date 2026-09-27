package com.example.capestone2.Service;

import com.example.capestone2.Model.Player;
import com.example.capestone2.Model.Rank;
import com.example.capestone2.Repository.PlayerRepository;
import com.example.capestone2.Repository.RankRepository;
import com.example.capestone2.Repository.SquadMembersRepository;
import com.example.capestone2.Repository.SquadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
 private final PlayerRepository playerRepository;
 private final RankRepository rankRepository;
 private final SquadRepository squadRepository;
 private final SquadMembersRepository squadMembersRepository;
 private final EmailService emailService;
 private final WhatsappService whatsappService;

public List<Player>getPlayer(){

    return playerRepository.findAll();
}

public boolean addPlayer(Player player){
    if(player.getRankId()!=null) {
        Rank existingRank = rankRepository.findRankById(player.getRankId());
        if (existingRank == null) {
            return false;//rank not found
        }
    }
    player.setRating(3.0);
playerRepository.save(player);
emailService.sendEmail(player.getEmail(), "Welcome to GG Squad!",
            "Hi " + player.getName() + ", welcome! Your account has been created. "
                    + "You can now join squads, receive offers, and find your perfect teammates for Games Have Fun.");

    whatsappService.sendMessage(player.getPhone(),
            "Hi " + player.getName() + ", welcome to GG Squad! 🎮 Your account has been created.");
return true;

}


public int updatePlayer(Integer id,Player player){
    Player oldPlayer=playerRepository.findPlayerById(id);
    if(oldPlayer==null){
        return 0;// Player not found
    }
    if (player.getRankId() != null) {
        Rank existingRank = rankRepository.findRankById(player.getRankId());
        if (existingRank == null) {
            return 1;
        }//RankNotFound
        oldPlayer.setRankId(player.getRankId());
    }
    oldPlayer.setName(player.getName());
    oldPlayer.setEmail(player.getEmail());
    playerRepository.save(oldPlayer);
    return 2;
}


    public int deletePlayer(Integer id){
        Player deletePlayer = playerRepository.findPlayerById(id);
        if(deletePlayer == null){
            return 0; // player not found
        }
        if(squadRepository.existsByOwnerId(id)){
            return 1; // player owns a squad, cannot delete
        }
        if(squadMembersRepository.existsByPlayerId(id)){
            return 2; // player is a member of a squad
        }

        playerRepository.delete(deletePlayer);
        return 3; // success
    }



  public List<Player>top5RatingPlayers(){
    return playerRepository.findTop5ByOrderByRatingDesc();
  }






















}
