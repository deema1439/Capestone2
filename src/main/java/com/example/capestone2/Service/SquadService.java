package com.example.capestone2.Service;

import com.example.capestone2.Model.*;
import com.example.capestone2.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SquadService {
 private final SquadRepository squadRepository;
 private final GameRepository gameRepository;
 private final PlayerRepository playerRepository;
 private final SquadMembersRepository squadMembersRepository;
 private final EmailService emailService;
 private final RankRepository rankRepository;
 private final RoleRepository roleRepository;
 private final AiService aiService;


 public List<Squad>getSquads(){
  return squadRepository.findAll();
 }



 public int addSquad(Squad squad){
  Game existingGame=gameRepository.findGameById(squad.getGameId());
  Player existingPlayer=playerRepository.findPlayerById(squad.getOwnerId());
  if(existingGame==null){
   return 0;//gameNotFound
  }
  if(existingPlayer==null){
   return 1;//Player Not Found
  }
  squad.setStatus("OPEN");
  squad.setCreatedAt(LocalDateTime.now());
  squadRepository.save(squad);
  return 2;

 }

 public int closeSquad(Integer id, Integer ownerId) {
  Squad oldSquad = squadRepository.findSquadById(id);
  if (oldSquad == null) {
   return 0; // squad not found
  }
  if (!oldSquad.getOwnerId().equals(ownerId)) {
   return 1; // only the owner can close the squad
  }
  oldSquad.setStatus("Closed");
  squadRepository.save(oldSquad);
  return 2;
 }

 public int openSquad(Integer id,Integer ownerId){
 Squad oldsquad =squadRepository.findSquadById(id);
 if(oldsquad==null){
  return 0;//squad Not Found.
 }
 if(!oldsquad.getOwnerId().equals(ownerId)){
  return 1;// only the owner can open the squad
 }
 if(oldsquad.getStatus().equals("OPEN")){
  return 2;//squad is already open
 }
 oldsquad.setStatus("OPEN");
 squadRepository.save(oldsquad);
 return 3;//succes

 }

 @Transactional
 public int deleteSquad(Integer id, Integer ownerId) {
  Squad existingSquad = squadRepository.findSquadById(id);
  if (existingSquad == null) {
   return 0;
  }
  if (!existingSquad.getOwnerId().equals(ownerId)) {
   return 1;
  }
  squadMembersRepository.deleteBySquadId(id);   // ← يمسح سجلات العضوية المرتبطه أولاً
  squadRepository.delete(existingSquad);
  return 2;
 }


 public int kickPlayer(Integer squadId, Integer ownerId, Integer playerId){
  Squad s=squadRepository.findSquadById(squadId);

  if(s==null){
   return 0;//Squad Not Found
  }
  if(!s.getOwnerId().equals(ownerId)){
   return 1;//only the owner can kick players
  }
  if(ownerId.equals(playerId)){
   return 2; // owner can't kick himself
  }
  SquadMembers member =squadMembersRepository.findSquadMembersBySquadIdAndPlayerId(squadId,playerId);
  if(member==null){
   return 3; // player is not in this squad
  }
  squadMembersRepository.delete(member);
  Player player = playerRepository.findPlayerById(playerId);
  emailService.sendEmail(player.getEmail(), "Removed from squad",
          "Hi " + player.getName() + ", you have been removed from " + s.getName() + ".");

  return 4; // success

 }



 public String getSquadMembers(Integer squadId, Integer ownerId){
  Squad s = squadRepository.findSquadById(squadId);
  if(s == null){ return "Squad Not Found"; }
  if(!s.getOwnerId().equals(ownerId)){ return "Only Owner Can See members"; }

  Game game = gameRepository.findGameById(s.getGameId());
  List<SquadMembers> squadMembers = squadMembersRepository.findAllBySquadId(squadId);
  Player owner = playerRepository.findPlayerById(s.getOwnerId());

  String result = "Squad: " + s.getName() + " | Game: " + game.getName()
          + " | Members count: " + (squadMembers.size() + 1) + " | "
          + owner.getName() + " (" + getRankName(owner) + " - Owner), ";

  for(SquadMembers m : squadMembers){
   Player p = playerRepository.findPlayerById(m.getPlayerId());
   Role role = roleRepository.findRoleById(m.getRoleId());
   result += p.getName() + " (" + getRankName(p) + " - " + role.getName() + "), ";
  }
  return result;
 }


public int playerLeaveSquad(Integer squadId,Integer playerId){
  Squad s=squadRepository.findSquadById(squadId);
  if(s==null){
   return 0;//Squad Not Found
  }
  if(s.getOwnerId().equals(playerId)){
   return 1;//owner can't leave, he must delete the squad
  }
  SquadMembers member=squadMembersRepository.findSquadMembersBySquadIdAndPlayerId(squadId,playerId);
  if(member==null){
   return 3;// player is not in this squad
  }
  squadMembersRepository.delete(member);
 Player player = playerRepository.findPlayerById(playerId);
 Player owner = playerRepository.findPlayerById(s.getOwnerId());
 emailService.sendEmail(owner.getEmail(), "Player left your squad",
         "Hi " + owner.getName() + ", " + player.getName() + " has left " + s.getName() + ".");

 return 3; // success
}

 public String describeSquad(Integer squadId){
  Squad s = squadRepository.findSquadById(squadId);
  if(s == null){
   return "Squad Not Found";
  }
  Game game = gameRepository.findGameById(s.getGameId());
  List<SquadMembers> squadMembers = squadMembersRepository.findAllBySquadId(squadId);

  String roles = "";
  for(SquadMembers m : squadMembers){
   Role role = roleRepository.findRoleById(m.getRoleId());
   roles += role.getName() + ", ";
  }

  if(roles.isEmpty()){
   roles = "no members yet";
  }

  String prompt = "Write a short catchy description (2 sentences) for a gaming squad. "
          + "Squad name: " + s.getName()
          + ". Game: " + game.getName()
          + ". Members count: " + (squadMembers.size()+1) + " out of " + game.getMaxPlayer()
          + ". Current roles: " + roles
          + ". Status: " + s.getStatus()
          + ". Mention which roles they still need if the squad is not full.";

  return aiService.ask(prompt);
 }




 public int transferOwnerShip(Integer squadId, Integer ownerId, Integer newOwnerId) {
  Squad s = squadRepository.findSquadById(squadId);
  if (s == null) {
   return 0; // squad not found
  }
  if (!s.getOwnerId().equals(ownerId)) {
   return 1; // only the owner can transfer ownership
  }
  if (ownerId.equals(newOwnerId)) {
   return 2; // you are already the owner
  }
  SquadMembers newOwnerMember = squadMembersRepository.findSquadMembersBySquadIdAndPlayerId(squadId, newOwnerId);
  if (newOwnerMember == null) {
   return 3; // new owner must be a member of the squad
  }

  squadMembersRepository.delete(newOwnerMember);
  s.setOwnerId(newOwnerId);
  squadRepository.save(s);

  Player newOwner = playerRepository.findPlayerById(newOwnerId);
  emailService.sendEmail(newOwner.getEmail(), "You are the new squad owner",
          "Hi " + newOwner.getName() + ", you are now the owner of " + s.getName() + ".");

  return 4; // success
 }


 public List<Squad> getPlayerSquads(Integer playerId){
  Player player = playerRepository.findPlayerById(playerId);
  if(player == null){
   return null; // player not found
  }
  return squadRepository.findSquadsByPlayerId(playerId);
 }

 private String getRankName(Player player){
  Rank rank=rankRepository.findRankById(player.getRankId());
  if(rank==null){
   return "no Rank";
  }
    return rank.getName();
 }


 private String getSquadInfo(Squad s){
  Player owner = playerRepository.findPlayerById(s.getOwnerId());
  String info = "the Squad name is: " + s.getName()
          + " The owner of the Squad is: " + owner.getName()
          + " the rank of the Owner: " + getRankName(owner)
          + " the rating: " + owner.getRating() + "\n";

  List<SquadMembers> members = squadMembersRepository.findAllBySquadId(s.getId());
  for (SquadMembers m : members){
   Player p = playerRepository.findPlayerById(m.getPlayerId());
   Role r = roleRepository.findRoleById(m.getRoleId());
   info += "- " + p.getName() + ": rank " + getRankName(p) + ", role " + r.getName()
           + ", rating " + p.getRating() + "\n";
  }
  return info;
 }


 public String getSquadCompatibility(Integer squadId){
  Squad s=squadRepository.findSquadById(squadId);
  if(s==null){
   return "Squad Not Found";
  }
  if(squadMembersRepository.countBySquadId(squadId)==0){
   return "Squad has No members";
  }
  Game game=gameRepository.findGameById(s.getGameId());
  String prompt = "You are a gaming team analyst. Rate how compatible this " + game.getName()
          + " squad is as a team. Check if their ranks are close, if the roles are balanced, and their ratings.\n"
          + getSquadInfo(s)
          + "Reply in English in this exact format only:\n"
          + "Compatibility: X%\n"
          + "Reason: one short sentence";
          return aiService.ask(prompt);
 }


 public String getPlayerCompatibility(Integer squadId,Integer playerId){
  Squad s=squadRepository.findSquadById(squadId);
  if(s==null){
   return "there is no Squad";
  }
  Player p=playerRepository.findPlayerById(playerId);
  if(p==null){
   return "there is no player";
  }
  if(squadMembersRepository.existsBySquadIdAndPlayerId(squadId,playerId)||s.getOwnerId().equals(playerId)){
   return "Player Already In Squad";
  }
  Game game=gameRepository.findGameById(s.getGameId());
  String prompt = "You are a gaming team analyst. Rate how well this player would fit into this "
          + game.getName() + " squad, based on rank closeness, roles the squad still needs, and rating.\n"
          + "Player: " + p.getName() + ", rank " + getRankName(p) + ", rating " + p.getRating() + "\n"
          + "Squad members: (" + (squadMembersRepository.countBySquadId(squadId)+1)
          + " out of " + game.getMaxPlayer() + ")\n"
          + getSquadInfo(s)
          + "Reply in English in this exact format only:\n"
          + "Compatibility: X%\n"
          + "Reason: one short sentence";
  return aiService.ask(prompt);
 }







































}
