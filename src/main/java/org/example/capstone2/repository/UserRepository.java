package org.example.capstone2.repository;


import org.example.capstone2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    User findUserByPasswordAndEmail(String password, String email);

    User findUserByRole(String role);

}


