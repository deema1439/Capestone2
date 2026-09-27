package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Game;
import com.example.capestone2.Service.GameService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/get")
    public ResponseEntity<?>getGames(){
        List<Game>getGames=gameService.getGames();
        return ResponseEntity.status(200).body(getGames);
    }


    @PostMapping("/add")
    public ResponseEntity<?> addGame(@RequestBody @Valid Game game, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        gameService.addGame(game);
        return ResponseEntity.status(200).body(new ApiRes("game added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Integer id, @RequestBody @Valid Game game, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        boolean isUpdated = gameService.updateGame(id, game);
        if(!isUpdated){
            return ResponseEntity.status(400).body(new ApiRes("game not found"));
        }
        return ResponseEntity.status(200).body(new ApiRes("game updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?>deleteGame(@PathVariable Integer id){

        int deleteGame=gameService.deleteGame(id);

        switch (deleteGame){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("game has squads cant delete"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("game deleted"));
        }

    }








}
