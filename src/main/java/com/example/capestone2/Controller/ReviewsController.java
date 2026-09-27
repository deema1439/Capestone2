package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Reviews;
import com.example.capestone2.Service.ReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewsController {
    private final ReviewsService reviewsService;
    @GetMapping("/get")
    public ResponseEntity<?> getReviews(){
        return ResponseEntity.status(200).body(reviewsService.getReviews());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReviews(@RequestBody @Valid Reviews reviews, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = reviewsService.addReviews(reviews);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("reviewer not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("reviewee not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("squad not found"));
            case 3:
                return ResponseEntity.status(400).body(new ApiRes("you can't review yourself"));
            case 4:
                return ResponseEntity.status(400).body(new ApiRes("both players must be in the same squad"));
            case 5:
                return ResponseEntity.status(400).body(new ApiRes("you already reviewed this player in this squad"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("review added"));
        }
    }

    @PutMapping("/update/{id}/{reviewerId}")
    public ResponseEntity<?> updateReviews(@PathVariable Integer id, @PathVariable Integer reviewerId,
                                           @RequestBody @Valid Reviews reviews, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = reviewsService.updateReviews(id, reviewerId, reviews);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("review not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the reviewer can update this review"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("review updated"));
        }
    }



    @DeleteMapping("/delete/{id}/{reviewerId}")
    public ResponseEntity<?> deleteReviews(@PathVariable Integer id, @PathVariable Integer reviewerId){
        int result = reviewsService.deleteReviews(id, reviewerId);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("review not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("only the reviewer can delete this review"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("review deleted"));
        }
    }

    @GetMapping("/summary/{playerId}")
    public ResponseEntity<?> summarizePlayerReviews(@PathVariable Integer playerId){
        String summary = reviewsService.summarizePlayerReviews(playerId);

        if(summary.equals("Player Not Found") || summary.equals("No Reviews Yet")){
            return ResponseEntity.status(400).body(new ApiRes(summary));
        }
        return ResponseEntity.status(200).body(new ApiRes(summary));
    }

}
