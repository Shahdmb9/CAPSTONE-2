package org.example.capstone2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiResponse;
import org.example.capstone2.model.Worker;
import org.example.capstone2.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    // POST /api/workers
    @PostMapping("/add")
    public ResponseEntity<?> add( @RequestBody @Valid Worker worker , Errors errors) {
        if(errors.hasErrors())
            return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));
        workerService.add(worker);
        return ResponseEntity.status(200).body(new ApiResponse("Worker added successfully"));
    }

    // GET /api/workers
    @GetMapping("/get")
    public ResponseEntity<List<Worker>> getAll() {
        return ResponseEntity.status(200).body(workerService.getAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid Worker worker, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
        workerService.update(id,worker);
        return ResponseEntity.status(200).body(new ApiResponse("Worker updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        workerService.deleteWorker(id);
        return ResponseEntity.status(200).body(new ApiResponse("Worker deleted successfully"));
    }


    @PutMapping("/accebt-request/{workerid}/{requestid}")
    public ResponseEntity<?> acceptRequest(@PathVariable Integer workerid, @PathVariable Integer requestid) {
        workerService.acceptRequest(workerid,requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request accepted successfully"));
    }

    @PutMapping("/reject-request/{workerid}/{requestid}")
    public ResponseEntity<?> rejectRequest(@PathVariable Integer workerid, @PathVariable Integer requestid) {
        workerService.rejectRequest(workerid,requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request rejected successfully"));
    }

    @PutMapping("/start-request/{workerid}/{requestid}")
    public ResponseEntity<?> startRequest(@PathVariable Integer workerid, @PathVariable Integer requestid) {
        workerService.startRequest(workerid,requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request started successfully"));
    }

    @PutMapping("/finish-request/{workerid}/{requestid}")
    public ResponseEntity<?> finishRequest(@PathVariable Integer workerid, @PathVariable Integer requestid){
        workerService.resolveRequest(workerid,requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request finished successfully"));
    }

    @GetMapping("/get-worker-request/{workerid}")
    public ResponseEntity<?> getWorkerRequest(@PathVariable Integer workerid){
        return ResponseEntity.status(200).body(workerService.getWorkerRequests(workerid));
    }

    @GetMapping("/get-worker-request-by-status/{workerid}/{status}")
    public ResponseEntity<?> getWorkerRequestByStatus(@PathVariable Integer workerid,@PathVariable String status){
        return ResponseEntity.status(200).body(workerService.getWorkerRequestsByStatus(workerid,status));
    }

    @GetMapping("/get-available-workers")
    public ResponseEntity<?> getAvailableWorkers(){
        return ResponseEntity.status(200).body(workerService.getAvailableWorkers());
    }



    @GetMapping("/workers-by-specialty/{specialtyid}")
    public ResponseEntity<?> getWorkersBySpecialty(@PathVariable Integer specialtyid){
        return ResponseEntity.status(200).body(workerService.getWorkersBySpecialty(specialtyid));
    }

    @GetMapping("/get-workers-by-price-range/{min}/{max}")
    public ResponseEntity<?> getWorkersByPriceRange(@PathVariable Integer min,@PathVariable Integer max){
        return ResponseEntity.status(200).body(workerService.getWorkersByPriceRange(min,max));
    }

    @GetMapping("/sort-workers-by-rating-desc")
    public ResponseEntity<?> sortWorkersDesc(){
        return ResponseEntity.status(200).body(workerService.getBestWorkers());
    }

    @GetMapping("/sort-workers-by-rating-desc-in-specility/{specialtyid}")
    public ResponseEntity<?> sortWorkersInSpecility(@PathVariable Integer specialtyid){
        return ResponseEntity.status(200).body(workerService.findBestWorkerInSpeciality(specialtyid));
    }

    @GetMapping("/sort-available-workers-by-rating/{specialtyid}")
    public ResponseEntity<?> sortAvailableWorkersInSpecility(@PathVariable Integer specialtyid){
        return ResponseEntity.status(200).body(workerService.sortWorkersByRatingAndSpicilityAndAvailabilty(specialtyid));
    }


    @GetMapping("/sort-workers-by-price-low-high")
    public ResponseEntity<?> sortWorkersByPriceLowHigh(){
        return ResponseEntity.status(200).body(workerService.sortWorkersByPriceLowToHigh());
    }

    @GetMapping("/sort-workers-by-price-high-low")
    public ResponseEntity<?> sortWorkersByPriceHighLow(){
        return ResponseEntity.status(200).body(workerService.sortWorkersByPriceHighToLow());
    }

    @GetMapping("/sort-workers-by-price-low-high/{categoreyid}")
    public ResponseEntity<?> sortWorkersByPriceLowHighInCategory(@PathVariable Integer categoreyid){
        return ResponseEntity.status(200).body(workerService.sortWorkersByPriceLowToHighWithSpecialty(categoreyid));
    }

    @GetMapping("/sort-workers-by-price-high-low/{categoreyid}")
    public ResponseEntity<?> sortWorkersByPriceHighLowInCategory(Integer categoreyid){
        return ResponseEntity.status(200).body(workerService.sortWorkersByPriceHighToLowWithSpecialty(categoreyid));
    }

    @GetMapping("/earnings-this-month/{workerId}/")
    public ResponseEntity<?> getTotalEarningsThisMonth(@PathVariable Integer workerId) {
        return ResponseEntity.ok(workerService.getTotalEarningsThisMonth(workerId));
    }

    @GetMapping("/earnings-by-months/{workerid}/{date1}/{date2}")
    public ResponseEntity<?> getEarningsByMonths(@PathVariable Integer workerid, @PathVariable LocalDate date1, @PathVariable LocalDate date2){
        return ResponseEntity.status(200).body(workerService.getTotalEarningsMonthRange(workerid,date1,date2));
    }

    @GetMapping("/worker-stats/{workerid}")
    public ResponseEntity<?> workerStats(@PathVariable Integer workerid){
        return ResponseEntity.status(200).body(workerService.getWorkerStats(workerid));
    }

    @GetMapping("/get-available-worker-in-categorey/{categoryid}")
    public ResponseEntity<?> availableWorkerByCategory(@PathVariable Integer categoryid){
       return ResponseEntity.status(200).body(workerService.getAvailableWorkersBySpecialty(categoryid));
    }

    @PutMapping("/update-worker-availabilty/{workerId}")
    public ResponseEntity<?> updateWorkerAvailability(@PathVariable Integer workerId) {
        workerService.updateWorkerAvailability(workerId);
        return ResponseEntity.ok().body("Worker availability updated successfully");
    }

    @GetMapping("/estmated-time/{workerid}/{requestid}")
    public ResponseEntity<?> getEstimatedTime(@PathVariable Integer workerid,@PathVariable Integer requestid){
        return ResponseEntity.status(200).body(workerService.getRequiestEstmatedTime(workerid,requestid));
    }

    @GetMapping("/distance-to-request/{workerid}/{requestid}")
    public ResponseEntity<?> getDistanceToRequest(@PathVariable Integer workerid,@PathVariable Integer requestid){
        return ResponseEntity.status(200).body(workerService.getDistanceBetweenWorkersAndUser(workerid,requestid));
    }




}
