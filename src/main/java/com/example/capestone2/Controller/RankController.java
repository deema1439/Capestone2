package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Rank;
import com.example.capestone2.Service.RankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rank")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;

    @GetMapping("/get")
    public ResponseEntity<?> getRanks(){
        return ResponseEntity.status(200).body(rankService.getRanks());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRank(@RequestBody @Valid Rank rank, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        boolean isAdded = rankService.addRank(rank);
        if(!isAdded){
            return ResponseEntity.status(400).body(new ApiRes("game not found"));
        }
        return ResponseEntity.status(200).body(new ApiRes("rank added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRank(@PathVariable Integer id, @RequestBody @Valid Rank rank, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = rankService.updateRank(id, rank);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("rank not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("rank is in use, cannot change its game"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("rank updated"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRank(@PathVariable Integer id){
        int result = rankService.deleteRank(id);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("rank not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("rank is in use, cannot delete"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("rank deleted"));
        }
    }






















}
