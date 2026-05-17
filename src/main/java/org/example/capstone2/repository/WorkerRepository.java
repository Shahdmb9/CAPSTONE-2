package org.example.capstone2.repository;

import org.example.capstone2.model.Rating;
import org.example.capstone2.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Integer> {

    Worker findWorkerById(Integer id);
    List<Worker> findByAvailable(boolean available);
    List<Worker> findWorkerBySpecialtyAt(Integer specialtyAt);
    List<Worker> findWorkerByAvailableAndSpecialtyAt(boolean available, Integer specialtyAt);

    @Query("SELECT w FROM Worker w ORDER BY w.baseSalary")
    List<Worker> sortWorkersByPriceLowToHigh();

    @Query("SELECT w FROM Worker w ORDER BY w.baseSalary DESC ")
    List<Worker> sortWorkersByPriceHighToLow();

    @Query("SELECT w FROM Worker w WHERE w.specialtyAt=?1 ORDER BY w.baseSalary DESC  ")
    List<Worker> sortWorkersByPriceHighToLowWithSpecialty(Integer specialty);

    @Query("SELECT w FROM Worker w WHERE w.specialtyAt=?1 ORDER BY w.baseSalary DESC  ")
    List<Worker> sortWorkersByPriceLowToHighWithSpecialty(Integer specialty);



    @Query("SELECT w FROM Worker w JOIN Rating r ON r.workerId = w.id GROUP BY w ORDER BY AVG(r.score) DESC")
    List<Worker> findBestWorkers();

    @Query("SELECT w FROM Worker w WHERE w.baseSalary BETWEEN ?1 AND ?2")
    List<Worker> wokersWithBasePriceRange(double min, double max);

    @Query("SELECT w FROM Worker w WHERE w.available=true and w.id = (SELECT r.workerId FROM Rating r GROUP BY r.workerId ORDER BY AVG(r.score) DESC LIMIT 1)")
    Worker findWorkerWithHighestAverageRating();

    @Query("SELECT w FROM Worker w JOIN Rating r ON r.workerId = w.id WHERE w.specialtyAt = ?1 GROUP BY w ORDER BY AVG(r.score) DESC")
    List<Worker> findBestWorkersBySpeciality(Integer specialityId);

    @Query("SELECT w FROM Worker w JOIN Rating r ON r.workerId = w.id And w.available=true WHERE w.specialtyAt = ?1 GROUP BY w ORDER BY AVG(r.score) DESC")
    List<Worker> findBestWorkersBySpecialityAndAvailable(Integer specialityId);

}
