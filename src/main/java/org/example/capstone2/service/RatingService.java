package org.example.capstone2.service;


import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.model.Rating;
import org.example.capstone2.model.User;
import org.example.capstone2.model.Worker;
import org.example.capstone2.repository.MaintenanceRequestRepository;
import org.example.capstone2.repository.RatingRepository;
import org.example.capstone2.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MaintenanceRequestRepository requestRepository;

    private final WorkerRepository workerRepository;

    // get all ratings

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    // delete a rating

    public void deleteRating(Integer ratingId) {
        Rating rating=ratingRepository.findRatingById(ratingId);
        if(rating==null)
                throw new ApiException("Rating not found: ");
        ratingRepository.delete(rating);
    }

    // rate a worker

    public void rateWorker(Rating rating) {
        MaintenanceRequest request=requestRepository.findMaintenanceRequestById(rating.getRequestId());
        if(request==null)
            throw new ApiException("Request not found: ");

        if(!request.getStatus().equalsIgnoreCase("RESOLVED"))
            throw new ApiException("Only RESOLVED requests can be rated");


        Worker worker=workerRepository.findWorkerById(rating.getWorkerId());

        if(worker == null)
            throw new ApiException("Worker not found: ");

        if(request.getWorkerId()!=rating.getWorkerId())
            throw new ApiException("Worker not assigned to this request");

        if (ratingRepository.findRatingByRequestId(rating.getRequestId())!=null) {
            throw new RuntimeException("You already rate the worker.");
        }

        rating.setRatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
    }

    public void updateRating(Integer ratingid,Rating rating) {
//
        Rating oldRating=ratingRepository.findRatingById(ratingid);
        if(oldRating==null)
            throw new ApiException("Rating not found: " );

        MaintenanceRequest request=requestRepository.findMaintenanceRequestById(rating.getRequestId());

        if(request==null)
            throw new ApiException("Request not found: " + rating.getRequestId());

        if(request.getUserId()!=rating.getUserId())
            throw new ApiException("User not authorized to rate this worker");
        Worker worker=workerRepository.findWorkerById(rating.getWorkerId());

        if(worker == null)
            throw new ApiException("Worker not found: ");

        if(request.getWorkerId()!=rating.getWorkerId())
            throw new ApiException("Worker not assigned to this request");


        oldRating.setScore(rating.getScore());
        oldRating.setRatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
    }





}
