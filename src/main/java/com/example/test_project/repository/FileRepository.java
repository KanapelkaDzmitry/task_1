package com.example.test_project.repository;

import com.example.test_project.entity.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileModel, Long> {
}
