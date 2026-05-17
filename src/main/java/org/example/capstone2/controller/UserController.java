package org.example.capstone2.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiResponse;
import org.example.capstone2.model.User;
import org.example.capstone2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.status(200).body(userService.getAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
        }
        userService.add(user);
        return ResponseEntity.status(200).body(new ApiResponse("User registered successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid User user,Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());
        }
        userService.update(id,user);
        return ResponseEntity.status(200).body("User updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.status(200).body("User deleted successfully");
    }

    @PostMapping("/login/{password}/{email}")
    public ResponseEntity<?> login(@PathVariable String password,@PathVariable String email) {
        return ResponseEntity.status(200).body(userService.login(password,email));
    }

    @DeleteMapping("/delete/{userid}/{password}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable Integer userid,@PathVariable String password) {
        userService.deleteAccount(userid,password);
        return ResponseEntity.status(200).body("User deleted successfully");
    }

    @PutMapping("/forget-password/{userId}/{email}")
    public ResponseEntity<?> forgetPassword(@PathVariable Integer userId,@PathVariable String email) {
        userService.forgetPassword(userId,email);
        return ResponseEntity.status(200).body("new password generated successfully");
    }



    @GetMapping("get-user-requests/{userid}")
    public ResponseEntity<?> getAllUserRequest(@PathVariable Integer userid){
        return ResponseEntity.status(200).body(userService.getAllRequestsOfUser(userid));
    }

    @GetMapping("get-user-request-by-status/{userId}/{status}")
    public ResponseEntity<?> getUserRequestByStatus(@PathVariable Integer userId,@PathVariable String status){
        return ResponseEntity.status(200).body(userService.getUserRequestsByStatus(userId,status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(userService.getUserById(id));
    }



}
