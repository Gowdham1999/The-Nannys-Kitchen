package com.cma.main.DAO;

import com.cma.main.POJO.User;
import com.cma.main.Wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    //    Named Query is written in User.java file for the below getAllUsers() method
    List<UserWrapper> getAllUsers();

    //    It is important to add the Transactional and Modifying annotation for the update:
    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);


    List<String> getAllAdmins();
}
