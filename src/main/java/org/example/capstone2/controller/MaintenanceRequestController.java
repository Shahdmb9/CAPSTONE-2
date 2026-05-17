package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.service.MaintenanceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService requestService;

    // POST /api/requests
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody MaintenanceRequest request, Errors errors) {
        if(errors.hasErrors())
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
       requestService.submitRequest(request);
       return ResponseEntity.status(200).body("Request submitted successfully");
    }

    @GetMapping("/get")
    public ResponseEntity<List<MaintenanceRequest>> getAll() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid MaintenanceRequest request, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
        requestService.updateRequest(id,request);
        return ResponseEntity.status(200).body("Request updated successfully");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        requestService.deleteRequest(id);
        return ResponseEntity.status(200).body("Request deleted successfully");
    }

    @PutMapping("auto-assign/{requestid}")
    public ResponseEntity<?> autoAssign(@PathVariable Integer requestid){
        requestService.autoAssignToBestWorker(requestid);
        return ResponseEntity.status(200).body("Request assigned successfully");
    }

    @GetMapping("/get-stats")
    public ResponseEntity<?> getStats(){
        return ResponseEntity.status(200).body(requestService.getStats());
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
        return ResponseEntity.status(200).body("Request canceled successfully");
    }

    @PutMapping("/assign-worker-request/{userid}/{workerid}/{requestid}")
    public ResponseEntity<?> assignWorkerRequest(@PathVariable Integer userid,@PathVariable Integer workerid,@PathVariable Integer requestid){
        requestService.AdminAssiningWorker(userid,workerid,requestid);
        return ResponseEntity.status(200).body("Request assigned successfully");
    }



    @GetMapping("/calculate-total-cost/{userid}/{requesid}")
    public ResponseEntity<?> calculateTotalCost(@PathVariable Integer userid,@PathVariable Integer requesid){
        return ResponseEntity.status(200).body(requestService.calculateTotalCost(userid,requesid));
    }


}
