package com.connectus.entity;

import com.connectus.entity.enums.EStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    private String photo;
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT") // Opsiyonel, Hibernate kullanıyorsanız TEXT sütunu oluşturmasını sağlar
    private String description;

}
