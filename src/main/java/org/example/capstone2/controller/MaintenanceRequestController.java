package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiResponse;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.service.MaintenanceRequestService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService requestService;

    // POST /api/requests
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody MaintenanceRequest request) {

       requestService.submitRequest(request);
       return ResponseEntity.status(200).body(new ApiResponse("Request submitted successfully"));
    }

    @GetMapping("/get")
    public ResponseEntity<List<MaintenanceRequest>> getAll() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid MaintenanceRequest request) {

        requestService.updateRequest(id,request);
        return ResponseEntity.status(200).body(new ApiResponse("Request updated successfully"));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        requestService.deleteRequest(id);
        return ResponseEntity.status(200).body(new ApiResponse("Request deleted successfully"));
    }

    @PutMapping("auto-assign/{requestid}")
    public ResponseEntity<?> autoAssign(@PathVariable Integer requestid){
        requestService.autoAssignToBestWorker(requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request assigned successfully"));
    }

    @PutMapping("/assign-to-closet-worker/{requestid}")
    public ResponseEntity<?> assignToClosetWorker(@PathVariable Integer requestid){
        requestService.assignToCloserWorker(requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request assigned to closet worker successfully"));
    }

    @GetMapping("/get-request-sorted-by-latest")
    public ResponseEntity<?> getRequestSortedByLatest(){
        return ResponseEntity.status(200).body(requestService.getRequestsByLatest());
    }

    @GetMapping("/get-earliest-request")
    public ResponseEntity<?> getEarliestRequest(){
        return ResponseEntity.status(200).body(requestService.getRequestsByOldest());
    }

    @GetMapping("/get-sorted-urgent-request")
    public ResponseEntity<?> getUrgentOrders(){
        return ResponseEntity.status(200).body(requestService.getRequestsByUrgentAndOldest());
    }

    @GetMapping("/get-closet-Workers/{userid}/{categoryid}")
    public ResponseEntity<?> getBest5ClosetWorkers(@PathVariable Integer userid,@PathVariable Integer categoryid){
        return ResponseEntity.status(200).body(requestService.getClosetWorkersInCategory(userid,categoryid));
    }

    @GetMapping("/get-stats")
    public ResponseEntity<?> getStats(){
        return ResponseEntity.status(200).body(requestService.getStats());
    }

    @GetMapping("get-stats-of-category")
    public ResponseEntity<?> getStatsOfCategory(){
        return ResponseEntity.status(200).body(requestService.getStatsOfCategory());
    }

    @GetMapping("/get-request-by-status/{status}")
    public ResponseEntity<?> getRequestByStatus(@PathVariable String status){
        return ResponseEntity.status(200).body(requestService.getRequestsByStatus(status));
    }

    @GetMapping("/get-request-by-category/{categoryId}")
    public ResponseEntity<?> getRequestByCategory(@PathVariable Integer categoryId){
        return ResponseEntity.status(200).body(requestService.getRequestsByCategory(categoryId));
    }

    @GetMapping("/get-urgent-request")
    public ResponseEntity<?> getUrgentRequest(){
        return ResponseEntity.status(200).body(requestService.getUrgentRequests());
    }

    @PutMapping("/cancel-request/{userid}/{requestid}")
    public ResponseEntity<?> cancelRequest(@PathVariable Integer userid,@PathVariable Integer requestid){
        requestService.cancelRequest(userid,requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request canceled successfully"));
    }

    @PutMapping("/assign-worker-request/{userid}/{workerid}/{requestid}")
    public ResponseEntity<?> assignWorkerRequest(@PathVariable Integer userid,@PathVariable Integer workerid,@PathVariable Integer requestid){
        requestService.AdminAssiningWorker(userid,workerid,requestid);
        return ResponseEntity.status(200).body(new ApiResponse("Request assigned successfully"));
    }



    @GetMapping("/calculate-total-cost/{userid}/{requesid}")
    public ResponseEntity<?> calculateTotalCost(@PathVariable Integer userid,@PathVariable Integer requesid){
        return ResponseEntity.status(200).body(requestService.calculateTotalCost(userid,requesid));
    }


}
