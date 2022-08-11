package com.example.test_project.repository;

import com.example.test_project.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Procedure("GET_SUM_OF_WHOLE_DIGITS")
    Long getSumOfWholeDigits();

    @Procedure("GET_MEDIAN_OF_FRACTIONAL_DIGITS")
    Double getMedianOfFractionalDigits();
}
