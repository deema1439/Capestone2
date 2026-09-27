package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Offers;
import com.example.capestone2.Service.OffersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OffersController {
    private final OffersService offersService;

    @GetMapping("/get")
    public ResponseEntity<?> getOffers(){
        return ResponseEntity.status(200).body(offersService.getOffers());
    }


    @PostMapping("/make/{ownerId}")
    public ResponseEntity<?> makeOffer(@PathVariable Integer ownerId, @RequestBody @Valid Offers offers, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = offersService.makeOffer(offers, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the squad owner can make offers"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("player not found"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("role not found"));
            case 4:
                return ResponseEntity.status(400).body(new ApiRes("player already in squad"));
            case 5:
                return ResponseEntity.status(400).body(new ApiRes("offer already sent and still pending"));
            case 7:
                return ResponseEntity.status(400).body(new ApiRes("squad is closed"));
            case 9:
                return ResponseEntity.status(400).body(new ApiRes("role does not belong to this squad game"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("offer sent"));
        }
    }

    @PutMapping("/accept/{offerId}/{playerId}")
    public ResponseEntity<?> acceptOffer(@PathVariable Integer offerId, @PathVariable Integer playerId){
        int result = offersService.acceptOffer(offerId, playerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("offer not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("this offer was not sent to this player"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("offer already responded to"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 4:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            case 5:
                return ResponseEntity.status(400).body(new ApiRes("squad is full"));
            case 6:
                return ResponseEntity.status(400).body(new ApiRes("squad is closed"));
            case 7:
                return ResponseEntity.status(400).body(new ApiRes("player already in squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("offer accepted"));
        }
    }

    @PutMapping("/reject/{offerId}/{playerId}")
    public ResponseEntity<?> rejectOffer(@PathVariable Integer offerId, @PathVariable Integer playerId){
        int result = offersService.rejectOffer(offerId, playerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("offer not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("this offer was not sent to this player"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("offer already responded to"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("offer rejected"));
        }
    }

    @DeleteMapping("/delete/{offerId}/{ownerId}")
    public ResponseEntity<?> deleteOffer(@PathVariable Integer offerId, @PathVariable Integer ownerId){
        int result = offersService.deleteOffer(offerId, ownerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("offer not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("only the squad owner can withdraw it"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("offer already responded to, cannot withdraw"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("offer withdrawn"));
        }
    }

    @GetMapping("/pending/{playerId}")
    public ResponseEntity<?> getPendingOffers(@PathVariable Integer playerId){
        List<Offers> offers = offersService.getPendingOffers(playerId);
        if(offers==null){
            return ResponseEntity.status(400).body(new ApiRes("player not found"));
        }
        return ResponseEntity.status(200).body(offers);
    }


}
