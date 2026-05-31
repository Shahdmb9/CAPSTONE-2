package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiResponse;
import org.example.capstone2.model.Rating;
import org.example.capstone2.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/get")
    public ResponseEntity<List<Rating>> getAll() {
        return ResponseEntity.status(200).body(ratingService.getAllRatings());
    }

    @PostMapping("/rate-worker/{userId}/{workerId}/{requestId}")
    public ResponseEntity<?> rate(@PathVariable Integer userId,@PathVariable Integer workerId,@PathVariable Integer requestId,@RequestBody @Valid  Rating rating) {

        ratingService.rateWorker(userId,workerId,requestId,rating);
        return ResponseEntity.status(200).body(new ApiResponse("Rating submitted successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid Rating rating) {

        ratingService.updateRating(id,rating);
        return ResponseEntity.status(200).body(new ApiResponse("Rating updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        ratingService.deleteRating(id);
        return ResponseEntity.status(200).body(new ApiResponse("Rating deleted successfully"));
    }


}
