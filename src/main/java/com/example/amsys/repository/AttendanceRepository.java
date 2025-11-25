package com.example.amsys.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.amsys.model.Attendance;
import com.example.amsys.model.AttendanceId;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {

    /**
     * 指定した日付とコマの出席情報を取得
     * @param date 日付
     * @param lessontimeCode コマコード
     * @return 出席情報リスト
     */
    @Query("SELECT a FROM Attendance a WHERE a.id.date = :date AND a.id.lessontimeCode = :lessontimeCode")
    List<Attendance> findByDateAndLessontimeCode(
            @Param("date") LocalDate date,
            @Param("lessontimeCode") Byte lessontimeCode);

}
