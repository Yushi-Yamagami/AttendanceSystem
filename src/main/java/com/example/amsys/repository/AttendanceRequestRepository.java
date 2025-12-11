package com.example.amsys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.amsys.dto.AttendanceRequestWithUserDto;
import com.example.amsys.model.AttendanceRequest;
import com.example.amsys.model.AttendanceRequest.RequestType;

@Repository
public interface AttendanceRequestRepository extends JpaRepository<AttendanceRequest, Long> {

    /**
     * 指定されたリクエストタイプの申請一覧を作成日時の降順で取得
     * @param requestType リクエストタイプ（PENDING, APPROVED）
     * @return 申請一覧
     */
    List<AttendanceRequest> findByRequestTypeOrderByCreatedAtDesc(RequestType requestType);

    /**
     * 指定されたリクエストタイプの申請一覧を学生情報と共に取得（昇順）
     * @param requestType リクエストタイプ（PENDING, APPROVED）
     * @return 申請一覧（学生情報付き）
     */
    @Query("SELECT new com.example.amsys.dto.AttendanceRequestWithUserDto(" +
           "ar.requestId, ar.studentId, ar.date, ar.lessontimeCode, ar.status, " +
           "ar.reason, ar.requestType, ar.teacherId, ar.createdAt, ar.updatedAt, " +
           "u.lastName, u.firstName, u.lastKanaName, u.firstKanaName) " +
           "FROM AttendanceRequest ar " +
           "LEFT JOIN User u ON ar.studentId = u.userId " +
           "WHERE ar.requestType = :requestType " +
           "ORDER BY ar.createdAt ASC")
    List<AttendanceRequestWithUserDto> findByRequestTypeWithUserOrderByCreatedAtAsc(@Param("requestType") RequestType requestType);

    /**
     * 指定されたリクエストタイプの申請一覧を学生情報と共に取得（降順）
     * @param requestType リクエストタイプ（PENDING, APPROVED）
     * @return 申請一覧（学生情報付き）
     */
    @Query("SELECT new com.example.amsys.dto.AttendanceRequestWithUserDto(" +
           "ar.requestId, ar.studentId, ar.date, ar.lessontimeCode, ar.status, " +
           "ar.reason, ar.requestType, ar.teacherId, ar.createdAt, ar.updatedAt, " +
           "u.lastName, u.firstName, u.lastKanaName, u.firstKanaName) " +
           "FROM AttendanceRequest ar " +
           "LEFT JOIN User u ON ar.studentId = u.userId " +
           "WHERE ar.requestType = :requestType " +
           "ORDER BY ar.createdAt DESC")
    List<AttendanceRequestWithUserDto> findByRequestTypeWithUserOrderByCreatedAtDesc(@Param("requestType") RequestType requestType);

}
