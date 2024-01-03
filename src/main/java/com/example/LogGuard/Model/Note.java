package com.example.LogGuard.Model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "noteDB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Note {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "note")
    private String note;
}
