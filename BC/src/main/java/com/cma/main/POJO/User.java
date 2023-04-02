package com.cma.main.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

//Named Query for Get specific details of users
@NamedQuery(name="User.getAllUsers", query = "select new com.cma.main.Wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) from User u where u.role='user'")

//Named Query for updating the status of the user
@NamedQuery(name="User.updateStatus", query = "update User u set u.status =:status where u.id=:id")

//Named Query for getting all the emails of the admins
@NamedQuery(name="User.getAllAdmins", query = "select u.email from User u where u.role='admin'")



@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
public class User implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private String contactNumber;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String status;

    @Column
    private String role;
}
