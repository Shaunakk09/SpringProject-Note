package com.example.LogGuard.Model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@Table(name = "userDB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
    @Id
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private int age;
}
