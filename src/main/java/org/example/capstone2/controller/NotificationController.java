package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiResponse;
import org.example.capstone2.model.Notification;
import org.example.capstone2.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/add-notification/{userId}/{workerId}")
    public ResponseEntity<?> add(@PathVariable Integer userId,@PathVariable Integer workerId){

        notificationService.addNotification(userId,workerId);
        return ResponseEntity.status(200).body(new ApiResponse("Notification added successfully"));
    }

    @GetMapping("/get-notification")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(notificationService.getAllNotification());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        notificationService.deleteNotification(id);
        return ResponseEntity.status(200).body(new ApiResponse("Notification deleted successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid Notification notification){

        notificationService.updateNotification(id,notification);
        return ResponseEntity.status(200).body(new ApiResponse("Notification updated successfully"));
    }
}
