package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.model.Notification;
import org.example.capstone2.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/add-notification")
    public ResponseEntity<?> add(@RequestBody @Valid Notification notification){

        notificationService.addNotification(notification);
        return ResponseEntity.status(200).body("Notification added successfully");
    }

    @GetMapping("/get-notification")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(notificationService.getAllNotification());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        notificationService.deleteNotification(id);
        return ResponseEntity.status(200).body("Notification deleted successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid Notification notification){

        notificationService.updateNotification(id,notification);
        return ResponseEntity.status(200).body("Notification updated successfully");
    }
}
