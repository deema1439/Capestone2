package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Requests;
import com.example.capestone2.Service.RequestsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;

    @GetMapping("/get")
    public ResponseEntity<?> getRequests(){
        return ResponseEntity.status(200).body(requestsService.getRequests());
    }


    @PostMapping("/make")
    public ResponseEntity<?> makeRequest(@RequestBody @Valid Requests requests, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = requestsService.MakeRequest(requests);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("player not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("role not found"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("role does not belong to this squad game"));
            case 4:
                return ResponseEntity.status(400).body(new ApiRes("player already in squad"));
            case 5:
                return ResponseEntity.status(400).body(new ApiRes("you already have a pending request for this squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("request sent"));
        }
    }


    @PutMapping("/accept/{requestId}/{ownerId}")
    public ResponseEntity<?> acceptRequest(@PathVariable Integer requestId, @PathVariable Integer ownerId){
        int result = requestsService.acceptRequest(requestId, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("request not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can accept"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("squad is closed"));
            case 4:
                return ResponseEntity.status(400).body(new ApiRes("request already responded to"));
            case 5:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            case 6:
                return ResponseEntity.status(400).body(new ApiRes("squad is full"));
            case 7:
                return ResponseEntity.status(400).body(new ApiRes("player already in squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("request accepted"));
        }
    }

    @PutMapping("/reject/{requestId}/{ownerId}")
    public ResponseEntity<?> rejectRequest(@PathVariable Integer requestId, @PathVariable Integer ownerId){
        int result = requestsService.rejectRequest(requestId, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("request not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("only the owner can reject"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("request already responded to"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("request rejected"));
        }
    }

    @DeleteMapping("/delete/{requestId}/{playerId}")
    public ResponseEntity<?> deleteRequest(@PathVariable Integer requestId, @PathVariable Integer playerId){
        int result = requestsService.deleteRequest(requestId, playerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("request not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the player who sent it can withdraw it"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("request already responded to, cannot withdraw"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("request withdrawn"));
        }
    }


    @GetMapping("/pending/{squadId}/{ownerId}")
    public ResponseEntity<?> getPendingRequests(@PathVariable Integer squadId, @PathVariable Integer ownerId){
        List<Requests> requests = requestsService.getPendingRequests(squadId, ownerId);
        if(requests == null){
            return ResponseEntity.status(400).body(new ApiRes("squad not found or you are not the owner"));
        }
        return ResponseEntity.status(200).body(requests);
    }





























}
