package com.example.amsys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.amsys.model.LessonTime;

@Repository
public interface LessonTimeRepository extends JpaRepository<LessonTime, Byte> {
	
	List<LessonTime> findAllByOrderByLessontimeCodeAsc();

}
