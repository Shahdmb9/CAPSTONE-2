package org.example.capstone2.service;


import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.model.Notification;
import org.example.capstone2.model.User;
import org.example.capstone2.repository.MaintenanceRequestRepository;
import org.example.capstone2.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final NotificationService notificationService;
    private final MailService mailService;



    // ── CRUD ──────────────────────────────────────────────────────────────
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void add(User user) {
         userRepository.save(user);
    }

    public void delete(Integer userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new ApiException("User not found: ");
        }
        userRepository.delete(user);
    }

    public User update(Integer userId, User user) {
        User oldUser = getUserById(userId);
        if(oldUser==null)
            throw new ApiException("User not found: " + userId);
        if (!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new ApiException("New password must be at least 8 characters");
        }
        oldUser.setName(user.getName());
        oldUser.setAddress(user.getAddress());
        oldUser.setApartment(user.getApartment());
        oldUser.setPhone(user.getPhone());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setRole(user.getRole());
        oldUser.setSubscriptionType(user.getSubscriptionType());
        return userRepository.save(oldUser);
    }

    // ── EXTRA

    public User login(String password, String email) {
        User user = userRepository.findUserByPasswordAndEmail(password,email);
        if(user==null)
            throw  new ApiException("login failed, your credentials are wrong");
        return user;
    }


    //get user requests
    public List<MaintenanceRequest> getAllRequestsOfUser(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user==null)
            throw new ApiException("User not found: " + userId);
        if(requestRepository.findMaintenanceRequestByUserId(userId).isEmpty())
            throw new ApiException("User has no requests");
        return requestRepository.findMaintenanceRequestByUserId(userId);
    }

    //get user request by status
    public List<MaintenanceRequest> getUserRequestsByStatus(Integer userId, String status) {
        User user =userRepository.findUserById(userId);
        if(user==null)
                throw new ApiException("User not found: ");
        if(requestRepository.findMaintenanceRequestByUserIdAndStatus(userId, status.toUpperCase()).isEmpty())
            throw new ApiException("User has no "+ status +" requests");
        return requestRepository.findMaintenanceRequestByUserIdAndStatus(userId, status.toUpperCase());
    }

    // Change password

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!user.getPassword().equals(oldPassword)) {
            throw new ApiException("Old password is incorrect");
        }
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new ApiException("New password must be at least 8 characters");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    //delete account

    public void deleteAccount(Integer userId, String password) {
        User user = getUserById(userId);
        if (!user.getPassword().equals(password)) {
            throw new ApiException("Password is wrong ");
        }
        userRepository.delete(user);
    }

    public void forgetPassword(Integer userId, String email){
        User user = getUserById(userId);
        if(!user.getEmail().equals(email)){
            throw new ApiException("Email is wrong");
        }
        String newPassword="";
        do {
            newPassword= generatePassword(10);

        }while (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"));
        user.setPassword(newPassword);
        userRepository.save(user);
        mailService.sendPlainText(user.getEmail(), "Password Changed", "Your new password is: "+ newPassword);


    }

    // helper

    public User getUserById(Integer id) {
        User user=userRepository.findUserById(id);
        if(user==null)
            throw new ApiException("User not found: " + id);
        return user;
    }
    public String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(pool.length());
            builder.append(pool.charAt(index));
        }
        return builder.toString();
    }

}
