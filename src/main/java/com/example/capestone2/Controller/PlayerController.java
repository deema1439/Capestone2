package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Player;
import com.example.capestone2.Service.EmailService;
import com.example.capestone2.Service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;




    @GetMapping("/get")
    public ResponseEntity<?> getPlayer(){
        return ResponseEntity.status(200).body(playerService.getPlayer());
    }






    @PostMapping("/add")
    public ResponseEntity<?> addPlayer(@RequestBody @Valid Player player, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        boolean isAdded = playerService.addPlayer(player);
        if(!isAdded){
            return ResponseEntity.status(400).body(new ApiRes("rank not found"));
        }
        return ResponseEntity.status(200).body(new ApiRes("player added"));
    }







    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable Integer id, @RequestBody @Valid Player player, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = playerService.updatePlayer(id, player);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("player not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("rank not found"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("player updated"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Integer id){
        int result = playerService.deletePlayer(id);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("player not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("player owns a squad, cannot delete"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("player is a member of a squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("player deleted"));
        }
    }


    private final EmailService emailService;

    @GetMapping("/test-email")
    public ResponseEntity<?> testEmail(){
        emailService.sendEmail("1starsinnight@gmail.com", "Test", "the email works!");
        return ResponseEntity.status(200).body(new ApiRes("email sent"));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedPlayers(){
        return ResponseEntity.status(200).body(playerService.top5RatingPlayers());
    }


}
