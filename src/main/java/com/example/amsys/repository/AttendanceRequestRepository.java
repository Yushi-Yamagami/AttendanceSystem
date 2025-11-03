package com.example.amsys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.amsys.model.AttendanceRequest;

@Repository
public interface AttendanceRequestRepository extends JpaRepository<AttendanceRequest, Long> {

}
