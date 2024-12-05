package com.connectus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Entity
@Table(name = "tblproject")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employer; // işveren
    @Lob
    @Column(columnDefinition = "TEXT") // Opsiyonel, Hibernate kullanıyorsanız TEXT sütunu oluşturmasını sağlar
    private String title; // işin adı
    private String location; //yer
    private String date; // tarih
    @Lob
    @Column(columnDefinition = "TEXT") // Opsiyonel, Hibernate kullanıyorsanız TEXT sütunu oluşturmasını sağlar
    private String description;  // kapsam

}
