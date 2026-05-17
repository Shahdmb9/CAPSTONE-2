package org.example.capstone2.repository;


import jakarta.transaction.Transactional;
import org.example.capstone2.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Notification findNotificationById(Integer id);

    List<Notification> findNotificationByWorkerId(Integer workerId);

    @Transactional
    @Modifying
    void deleteAllByWorkerId(Integer workerId);
}
