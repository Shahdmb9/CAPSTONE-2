package org.example.capstone2.repository;

import org.example.capstone2.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByWorkerId(Integer workerId);
    boolean existsByWorkerId(Integer workerId);

    Rating findRatingById(Integer id);
    Rating findRatingByRequestId(Integer id);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.workerId = ?1")
    Double getAverageScoreByWorkerId(Integer workerId);
}
