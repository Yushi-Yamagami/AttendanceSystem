package com.example.amsys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
