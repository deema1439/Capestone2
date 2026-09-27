package com.example.capestone2.Service;

import com.example.capestone2.Model.*;
import com.example.capestone2.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestsService {
    private final RequestsRepository requestsRepository;
    private final PlayerRepository playerRepository;
    private final SquadRepository squadRepository;
    private final RoleRepository roleRepository;
    private final SquadMembersRepository squadMembersRepository;
    private final GameRepository gameRepository;
    private final EmailService emailService;
    private final WhatsappService whatsappService;

    public List<Requests>getRequests(){
        return requestsRepository.findAll();
       }


    public int MakeRequest(Requests requests){
        Player player=playerRepository.findPlayerById(requests.getPlayerId());
        Squad squad=squadRepository.findSquadById(requests.getSquadId());
        Role role=roleRepository.findRoleById(requests.getRoleId());
        boolean s=squadMembersRepository.existsBySquadIdAndPlayerId(requests.getSquadId(),requests.getPlayerId());
        boolean hasPending =requestsRepository.existsBySquadIdAndPlayerIdAndStatus(requests.getSquadId(),requests.getPlayerId(),"PENDING");
        if(player==null){
            return 0;//player Not Found
        }
        if(squad==null){
            return 1;//Squad Not Found
        }
        if(role==null){
            return 2;//Role Not Found
        }
        if(!role.getGameId().equals(squad.getGameId())){
            return 3; // role does not belong to this squad's game
        }

        if(s){
            return 4;//player is already in squad
        }
        if(hasPending){
            return 5; // player is still pending
        }
        requests.setStatus("PENDING");
        requests.setCreatedAt(LocalDateTime.now());
        requestsRepository.save(requests);
         Player owner=playerRepository.findPlayerById(squad.getOwnerId());
         emailService.sendEmail(owner.getEmail(),"New join request", "Hi " + owner.getName() + ", " + player.getName() + " wants to join " + squad.getName() + ".");

        return 6;
    }



    public int acceptRequest(Integer requestId,Integer ownerId){
      Requests request=requestsRepository.findRequestsById(requestId);
      if(request==null){
          return 0;//requst not found
      }
      Squad squad=squadRepository.findSquadById(request.getSquadId());
      if(squad==null){
          return 1;//squad Not Found
      }
       if(!squad.getOwnerId().equals(ownerId)){
           return 2;// only the owner can accept
       }
       if(squad.getStatus().equals("Closed")){
        return 3;//Squad is Closed
       }
       if(!request.getStatus().equals("PENDING")){
           return 4; //Owner has already response.
       }

       long currentMembers = squadMembersRepository.countBySquadId(squad.getId());
       Game game=gameRepository.findGameById(squad.getGameId());
        if(game == null){
            return 5; // game not found
        }

        if(currentMembers + 1 >=game.getMaxPlayer()){
               return 6;//game is full
           }

        if(squadMembersRepository.existsBySquadIdAndPlayerId(squad.getId(), request.getPlayerId())){
            return 7; // player already in squad
        }

       request.setStatus("ACCEPTED");
        request.setRespondedAt(LocalDateTime.now());
       requestsRepository.save(request);
       SquadMembers s=new SquadMembers();
       s.setSquadId(request.getSquadId());
       s.setPlayerId(request.getPlayerId());
        s.setRoleId(request.getRoleId());
        squadMembersRepository.save(s);
        Player player=playerRepository.findPlayerById(request.getPlayerId());
        emailService.sendEmail(player.getEmail(),"Request accepted ","Hi "+player.getName()+", your request to join " +squad.getName()+" squad has been accepted!");
        whatsappService.sendMessage(player.getPhone(),
                "Hi " + player.getName() + ", your request to join " + squad.getName() + " has been accepted!");
        return 8;


    }


    public int deleteRequest(Integer requestId, Integer playerId) {
        Requests existingRequest = requestsRepository.findRequestsById(requestId);
        if (existingRequest == null) {
            return 0; // request not found
        }
        if (!existingRequest.getPlayerId().equals(playerId)) {
            return 1; // only the player who sent it can withdraw it
        }
        if (!existingRequest.getStatus().equals("PENDING")) {
            return 2; // already responded to, cannot withdraw
        }
        requestsRepository.delete(existingRequest);
        return 3; // success
    }

    public int rejectRequest(Integer requestId, Integer ownerId){
        Requests request = requestsRepository.findRequestsById(requestId);
        if(request == null){
            return 0; // request not found
        }
        Squad squad = squadRepository.findSquadById(request.getSquadId());
        if(squad == null){
            return 1; // squad not found
        }
        if(!squad.getOwnerId().equals(ownerId)){
            return 2; // only the owner can reject
        }
        if(!request.getStatus().equals("PENDING")){
            return 3; // already responded to
        }
        request.setStatus("REJECTED");
        request.setRespondedAt(LocalDateTime.now());
        requestsRepository.save(request);
        Player player=playerRepository.findPlayerById(request.getPlayerId());
        emailService.sendEmail(player.getEmail(),"Rejected Request","Hi "+player.getName()+", your request to join " +squad.getName()+" squad has been Rejected!");
        return 4; // success
    }


    public List<Requests>getPendingRequests(Integer squadId,Integer ownerId){
        Squad s=squadRepository.findSquadById(squadId);
        if(s==null){
            return null;//squad not found
        }
        if(!s.getOwnerId().equals(ownerId)){
            return null; // only the owner can see requests
        }
        return requestsRepository.findAllBySquadIdAndStatus(squadId,"PENDING");

    }























}
