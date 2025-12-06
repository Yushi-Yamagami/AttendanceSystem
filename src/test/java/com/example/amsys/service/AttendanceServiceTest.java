package com.example.amsys.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.amsys.dto.AttendanceWithUserDto;
import com.example.amsys.model.Attendance;
import com.example.amsys.model.AttendanceId;
import com.example.amsys.model.Attendance.AttendanceStatus;
import com.example.amsys.model.LessonTime;
import com.example.amsys.model.User;
import com.example.amsys.model.User.UserRole;
import com.example.amsys.repository.AttendanceRepository;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.repository.UserRepository;

/**
 * AttendanceServiceのユニットテスト
 */
@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonTimeRepository lessonTimeRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private User testStudent1;
    private User testStudent2;
    private LessonTime testLessonTime;
    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        // テスト用の学生データ
        testStudent1 = new User();
        testStudent1.setId(1L);
        testStudent1.setUserId("S001");
        testStudent1.setRole(UserRole.STUDENT);
        testStudent1.setGradeCode((byte) 1);
        testStudent1.setLastName("山田");
        testStudent1.setFirstName("太郎");
        testStudent1.setLastKanaName("やまだ");
        testStudent1.setFirstKanaName("たろう");

        testStudent2 = new User();
        testStudent2.setId(2L);
        testStudent2.setUserId("S002");
        testStudent2.setRole(UserRole.STUDENT);
        testStudent2.setGradeCode((byte) 1);
        testStudent2.setLastName("鈴木");
        testStudent2.setFirstName("花子");
        testStudent2.setLastKanaName("すずき");
        testStudent2.setFirstKanaName("はなこ");

        // テスト用のコマ情報
        testLessonTime = new LessonTime();
        testLessonTime.setLessontimeCode((byte) 1);
        testLessonTime.setLessontimeName("1限");

        // テスト用の出欠情報
        testAttendance = new Attendance();
        AttendanceId attendanceId = new AttendanceId(LocalDate.now(), 1L, (byte) 1);
        testAttendance.setId(attendanceId);
        testAttendance.setStatusCode(AttendanceStatus.PRESENT);
    }

    @Test
    void testGetAttendanceListByDateGradeAndLessonTime_WithAttendance() {
        // モックの設定
        when(userRepository.findByRoleAndGradeCodeOrderByUserId(UserRole.STUDENT, (byte) 1))
                .thenReturn(List.of(testStudent1, testStudent2));
        when(lessonTimeRepository.findById((byte) 1)).thenReturn(Optional.of(testLessonTime));
        when(attendanceRepository.findById(any(AttendanceId.class)))
                .thenReturn(Optional.of(testAttendance))
                .thenReturn(Optional.empty());

        // テスト実行
        List<AttendanceWithUserDto> result = attendanceService.getAttendanceListByDateGradeAndLessonTime(
                LocalDate.now(), (byte) 1, (byte) 1);

        // 検証
        assertNotNull(result);
        assertEquals(2, result.size());

        // 1人目の学生（出席データあり）
        AttendanceWithUserDto dto1 = result.get(0);
        assertEquals("S001", dto1.getUserId());
        assertEquals(AttendanceStatus.PRESENT, dto1.getStatusCode());
        assertEquals("出席", dto1.getStatusName());

        // 2人目の学生（出席データなし）
        AttendanceWithUserDto dto2 = result.get(1);
        assertEquals("S002", dto2.getUserId());
        assertEquals(AttendanceStatus.NONE, dto2.getStatusCode());
        assertEquals("未記録", dto2.getStatusName());
    }

    @Test
    void testGetAttendanceListByDateGradeAndLessonTime_NoStudents() {
        // モックの設定（学生がいない）
        when(userRepository.findByRoleAndGradeCodeOrderByUserId(UserRole.STUDENT, (byte) 1))
                .thenReturn(List.of());
        when(lessonTimeRepository.findById((byte) 1)).thenReturn(Optional.of(testLessonTime));

        // テスト実行
        List<AttendanceWithUserDto> result = attendanceService.getAttendanceListByDateGradeAndLessonTime(
                LocalDate.now(), (byte) 1, (byte) 1);

        // 検証
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAttendanceListByDateGradeAndLessonTime_DifferentGrade() {
        // モックの設定（1年生のみを返す）
        when(userRepository.findByRoleAndGradeCodeOrderByUserId(UserRole.STUDENT, (byte) 1))
                .thenReturn(List.of(testStudent1));
        when(lessonTimeRepository.findById((byte) 1)).thenReturn(Optional.of(testLessonTime));
        when(attendanceRepository.findById(any(AttendanceId.class))).thenReturn(Optional.empty());

        // テスト実行（1年生のみを取得）
        List<AttendanceWithUserDto> result = attendanceService.getAttendanceListByDateGradeAndLessonTime(
                LocalDate.now(), (byte) 1, (byte) 1);

        // 検証（1年生のみが含まれる）
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("S001", result.get(0).getUserId());
    }

    @Test
    void testGetMonthlyAttendanceReport_WithFilters() {
        // モックの設定
        when(userRepository.findByRoleAndGradeCodeOrderByUserId(UserRole.STUDENT, (byte) 1))
                .thenReturn(List.of(testStudent1));
        when(lessonTimeRepository.findById((byte) 1)).thenReturn(Optional.of(testLessonTime));
        when(attendanceRepository.findById(any(AttendanceId.class)))
                .thenReturn(Optional.of(testAttendance));

        // テスト実行（2日間分）
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        List<AttendanceWithUserDto> result = attendanceService.getMonthlyAttendanceReport(
                startDate, endDate, (byte) 1, (byte) 1);

        // 検証（1人の学生、1つのコマ、2日分 = 2レコード）
        assertNotNull(result);
        assertEquals(2, result.size());

        // 両日とも同じ学生とコマ
        assertEquals("S001", result.get(0).getUserId());
        assertEquals("S001", result.get(1).getUserId());
        assertEquals((byte) 1, result.get(0).getLessontimeCode());
        assertEquals((byte) 1, result.get(1).getLessontimeCode());
    }

    @Test
    void testGetMonthlyAttendanceReport_NoFilters() {
        // モックの設定（全学年、全コマ）
        when(userRepository.findByRoleOrderByUserIdAsc(UserRole.STUDENT))
                .thenReturn(List.of(testStudent1, testStudent2));
        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc())
                .thenReturn(List.of(testLessonTime));
        when(attendanceRepository.findById(any(AttendanceId.class)))
                .thenReturn(Optional.empty());

        // テスト実行（1日分）
        LocalDate date = LocalDate.now();
        List<AttendanceWithUserDto> result = attendanceService.getMonthlyAttendanceReport(
                date, date, null, null);

        // 検証（2人の学生 × 1つのコマ × 1日 = 2レコード）
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetMonthlyAttendanceReport_MultipleStudentsAndLessons() {
        // 2つ目のコマを追加
        LessonTime testLessonTime2 = new LessonTime();
        testLessonTime2.setLessontimeCode((byte) 2);
        testLessonTime2.setLessontimeName("2限");

        // モックの設定
        when(userRepository.findByRoleOrderByUserIdAsc(UserRole.STUDENT))
                .thenReturn(List.of(testStudent1, testStudent2));
        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc())
                .thenReturn(List.of(testLessonTime, testLessonTime2));
        when(attendanceRepository.findById(any(AttendanceId.class)))
                .thenReturn(Optional.empty());

        // テスト実行（1日分）
        LocalDate date = LocalDate.now();
        List<AttendanceWithUserDto> result = attendanceService.getMonthlyAttendanceReport(
                date, date, null, null);

        // 検証（2人の学生 × 2つのコマ × 1日 = 4レコード）
        assertNotNull(result);
        assertEquals(4, result.size());
    }
}
