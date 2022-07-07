package ua.od.wind.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @Size(min=3, message = "Username must be not less than 3 signs")
    private String username;

    @Size(min=6, message = "Password must be not less than 6 signs")
    @Column(name = "password")
    private String password;

    @Transient
    private String passwordConfirm;

    @Email(message = "Incorrect email")
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

}
