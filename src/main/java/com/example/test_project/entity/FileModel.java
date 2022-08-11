package com.example.test_project.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "file")
public class FileModel extends BaseEntity{

    private String fileName;

    @OneToMany(mappedBy = "fileModel", fetch = FetchType.LAZY)
    private List<Line> lines;

}
