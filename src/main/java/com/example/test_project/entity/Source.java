package com.example.test_project.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Source extends BaseEntity {
    private String randomDate;
    private String latinSymbols;
    private String cyrillicSymbols;
    private int wholeDigit;
    private double fractionalDigit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileModel fileModel;



}
