package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Squad;
import com.example.capestone2.Service.SquadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/squad")
@RequiredArgsConstructor
public class SquadController {
    private final SquadService squadService;

    @GetMapping("/get")
    public ResponseEntity<?> getSquads(){
        return ResponseEntity.status(200).body(squadService.getSquads());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSquad(@RequestBody @Valid Squad squad, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = squadService.addSquad(squad);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("player not found"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("squad added"));
        }
    }

    @PutMapping("/close/{id}/{ownerId}")
    public ResponseEntity<?> closeSquad(@PathVariable Integer id, @PathVariable Integer ownerId){
        int result = squadService.closeSquad(id, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can close the squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("squad closed"));
        }
    }

    @PutMapping("/open/{id}/{ownerId}")
    public ResponseEntity<?> openSquad(@PathVariable Integer id, @PathVariable Integer ownerId){
        int result = squadService.openSquad(id, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can open the squad"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("squad is already open"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("squad opened"));
        }
    }


    @DeleteMapping("/delete/{id}/{ownerId}")
    public ResponseEntity<?> deleteSquad(@PathVariable Integer id, @PathVariable Integer ownerId){
        int result = squadService.deleteSquad(id, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can delete the squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("squad deleted"));
        }
    }


    @DeleteMapping("/kick/{squadId}/{ownerId}/{playerId}")
    public ResponseEntity<?> kickPlayer(@PathVariable Integer squadId, @PathVariable Integer ownerId, @PathVariable Integer playerId){
        int result = squadService.kickPlayer(squadId, ownerId, playerId);
        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can kick players"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("owner can't kick himself"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("player is not in this squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("player kicked"));
        }
    }


    @GetMapping("/members/{squadId}/{ownerId}")
    public ResponseEntity<?> getSquadMembers(@PathVariable Integer squadId, @PathVariable Integer ownerId){
        String details = squadService.getSquadMembers(squadId, ownerId);

        if(details.equals("Squad Not Found") || details.equals("Only Owner Can See members")){
            return ResponseEntity.status(400).body(new ApiRes(details));
        }
        return ResponseEntity.status(200).body(new ApiRes(details));
    }


    @DeleteMapping("/leave/{squadId}/{playerId}")
    public ResponseEntity<?> leaveSquad(@PathVariable Integer squadId, @PathVariable Integer playerId){
        int result = squadService.playerLeaveSquad(squadId, playerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("owner can't leave the squad, delete it instead"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("you are not in this squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("you left the squad"));
        }
    }




    @GetMapping("/describe/{squadId}")
    public ResponseEntity<?> describeSquad(@PathVariable Integer squadId){
        String description = squadService.describeSquad(squadId);
        if(description.equals("Squad Not Found")){
            return ResponseEntity.status(400).body(new ApiRes(description));
        }
        return ResponseEntity.status(200).body(new ApiRes(description));
    }


    @PutMapping("/transfer/{squadId}/{ownerId}/{newOwnerId}")
    public ResponseEntity<?> transferOwnerShip(@PathVariable Integer squadId, @PathVariable Integer ownerId, @PathVariable Integer newOwnerId){
        int result = squadService.transferOwnerShip(squadId, ownerId, newOwnerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can transfer ownership"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("you are already the owner"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("new owner must be a member of the squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("ownership transferred"));
        }
    }


    @GetMapping("/player-squads/{playerId}")
    public ResponseEntity<?> getPlayerSquads(@PathVariable Integer playerId){
        List<Squad> squads = squadService.getPlayerSquads(playerId);
        if(squads == null){
            return ResponseEntity.status(400).body(new ApiRes("player not found"));
        }
        return ResponseEntity.status(200).body(squads);
    }

    @GetMapping("/compatibility/{squadId}")
    public ResponseEntity<?> getSquadCompatibility(@PathVariable Integer squadId){
        String result = squadService.getSquadCompatibility(squadId);
        if(result.equals("Squad Not Found") || result.equals("Squad has No members")){
            return ResponseEntity.status(400).body(new ApiRes(result));
        }
        return ResponseEntity.status(200).body(new ApiRes(result));
    }

    @GetMapping("/compatibility/{squadId}/{playerId}")
    public ResponseEntity<?> getPlayerCompatibility(@PathVariable Integer squadId, @PathVariable Integer playerId){
        String result = squadService.getPlayerCompatibility(squadId, playerId);
        if(result.equals("there is no Squad") || result.equals("there is no player")
                || result.equals("Player Already In Squad")){
            return ResponseEntity.status(400).body(new ApiRes(result));
        }
        return ResponseEntity.status(200).body(new ApiRes(result));
    }


















}
