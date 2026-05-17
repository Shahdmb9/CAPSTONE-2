package org.example.capstone2.service;

import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.model.Notification;
import org.example.capstone2.model.User;
import org.example.capstone2.model.Worker;
import org.example.capstone2.repository.NotificationRepository;
import org.example.capstone2.repository.UserRepository;
import org.example.capstone2.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    final private NotificationRepository notificationRepository;
    final private UserRepository userRepository;
    final private WorkerRepository workerRepository;
    final private WhatsAppService whatsAppService;

    public List<Notification> getAllNotification(){
        return notificationRepository.findAll();
    }

    //adding notification for users to notify them when specific worker is available
    public void addNotification(Notification notification){
        User user = userRepository.findUserById(notification.getUserId());
        if(user==null)
            throw new ApiException("User not found: " );
        Worker worker = workerRepository.findWorkerById(notification.getWorkerId());
        if(worker==null)
            throw new ApiException("Worker not found: " );
        notificationRepository.save(notification);
    }

    public void updateNotification(Integer id, Notification notification){
        Notification oldNotification = notificationRepository.findNotificationById(id);

        if(oldNotification==null)
            throw new ApiException("Notification not found: " );
        notificationRepository.save(notification);
    }

    public void deleteNotification(Integer id){

        Notification notification = notificationRepository.findNotificationById(id);
        if(notification==null)
            throw new ApiException("Notification not found: " );
        notificationRepository.delete(notification);
    }

    public void notifyUsers(Integer workerId){
        Worker worker = workerRepository.findWorkerById(workerId);
        if(worker==null)
            throw new ApiException("Worker not found: " );

        List<Notification> notification = notificationRepository.findNotificationByWorkerId(workerId);
        if(notification.isEmpty())
            throw new ApiException("no notification for this worker" );
        for(Notification n:notification){
            User user = userRepository.findUserById(n.getUserId());
            whatsAppService.sendChatMessage(user.getPhone(),"The Worker: "+worker.getName()+" is now available!!");
        }
        //delete all notifications for a worker after the users got notify
        notificationRepository.deleteAllByWorkerId(workerId);
    }

    public void notifyUsersNotAvailably(List<MaintenanceRequest> requests, Integer workerId){
        Worker worker = workerRepository.findWorkerById(workerId);
        if(requests.isEmpty())
            return; // no other requests for this worker
        for(MaintenanceRequest r:requests){
            User user = userRepository.findUserById(r.getUserId());
            whatsAppService.sendChatMessage(user.getPhone(),"The Worker: "+worker.getName()+" is not available!!");
        }
    }



}
