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
    @Size(min = 3, message = "Username must be not less than 3 signs")
    private String username;

    @Size(min = 3, message = "Password must be not less than 3 signs")
    @Column(name = "password")
    private String password;

    @Transient
    private String passwordConfirm;

    @Email(message = "Incorrect email")
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "register_date")
    private String registerDate;

    @Column(name = "pay_date")
    private String payDate;

    @Column(name = "payment_id_base")
    private String paymentIdBase;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;


}
