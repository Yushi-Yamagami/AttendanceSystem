package com.example.amsys.init;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.amsys.model.LessonTime;
import com.example.amsys.model.User;
import com.example.amsys.model.User.UserRole;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.repository.UserRepository;

/**
 * DataInitializerのテスト
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonTimeRepository lessonTimeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments applicationArguments;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(userRepository, lessonTimeRepository, passwordEncoder);
    }

    @Test
    void testInitializeUsersWhenEmpty() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> 
            "{bcrypt}encoded_" + invocation.getArgument(0));

        // When
        dataInitializer.run(applicationArguments);

        // Then
        verify(userRepository).deleteAll();
        verify(lessonTimeRepository).deleteAll();
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        assertEquals(20, savedUsers.size(), "20人のユーザー（教師1人 + 学生19人）が挿入されるべきです");
        
        // 最初のユーザー（教師）を確認
        User firstUser = savedUsers.get(0);
        assertEquals("Admin01", firstUser.getUserId());
        assertNull(firstUser.getGradeCode(), "教師には学年コードがないべきです");
        assertEquals("試験", firstUser.getLastName());
        assertEquals("太郎", firstUser.getFirstName());
        assertEquals("しけん", firstUser.getLastKanaName());
        assertEquals("たろう", firstUser.getFirstKanaName());
        assertEquals(UserRole.TEACHER, firstUser.getRole());
        assertTrue(firstUser.getPassword().contains("encoded_Teacher2025"), 
            "パスワードがハッシュ化されるべきです");
        
        // 2番目のユーザー（学生）を確認
        User secondUser = savedUsers.get(1);
        assertEquals("T22001", secondUser.getUserId());
        assertEquals((byte) 4, secondUser.getGradeCode());
        assertEquals("秋山", secondUser.getLastName());
        assertEquals("政人", secondUser.getFirstName());
        assertEquals("あきやま", secondUser.getLastKanaName());
        assertEquals("まさと", secondUser.getFirstKanaName());
        assertEquals(UserRole.STUDENT, secondUser.getRole());
        assertTrue(secondUser.getPassword().contains("encoded_T22001"), 
            "パスワードがハッシュ化されるべきです");
    }

    @Test
    void testInitializeUsersWhenExisting() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then - データが存在する場合でも削除して再挿入される
        verify(userRepository).deleteAll();
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void testInitializeLessonTimesWhenEmpty() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
        verify(lessonTimeRepository).deleteAll();
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LessonTime>> lessonTimeCaptor = ArgumentCaptor.forClass(List.class);
        verify(lessonTimeRepository).saveAll(lessonTimeCaptor.capture());
        
        List<LessonTime> savedLessonTimes = lessonTimeCaptor.getValue();
        assertEquals(5, savedLessonTimes.size(), "5件のレッスン時間データが挿入されるべきです");
        
        // HRを確認
        LessonTime hr = savedLessonTimes.get(0);
        assertEquals((byte) 0, hr.getLessontimeCode());
        assertEquals("HR", hr.getLessontimeName());
        assertEquals(LocalTime.of(8, 50, 0), hr.getStartTime());
        assertEquals(LocalTime.of(8, 59, 0), hr.getFinishTime());
        
        // 1限を確認
        LessonTime lesson1 = savedLessonTimes.get(1);
        assertEquals((byte) 1, lesson1.getLessontimeCode());
        assertEquals("1限", lesson1.getLessontimeName());
        assertEquals(LocalTime.of(9, 0, 0), lesson1.getStartTime());
        assertEquals(LocalTime.of(10, 30, 0), lesson1.getFinishTime());
    }

    @Test
    void testInitializeLessonTimesWhenExisting() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then - データが存在する場合でも削除して再挿入される
        verify(lessonTimeRepository).deleteAll();
        verify(lessonTimeRepository).saveAll(anyList());
    }

    @Test
    void testPasswordsAreHashed() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> 
            "{bcrypt}$2a$10$hashedpassword_" + invocation.getArgument(0));

        // When
        dataInitializer.run(applicationArguments);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        for (User user : savedUsers) {
            assertTrue(user.getPassword().startsWith("{bcrypt}"), 
                "パスワードはBCryptでハッシュ化されるべきです: " + user.getUserId());
        }
        
        // パスワードエンコーダーが20回呼ばれたことを確認（教師1人 + 学生19人）
        verify(passwordEncoder, times(20)).encode(anyString());
    }

    @Test
    void testUsersHaveCorrectRoles() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        
        // 最初のユーザーは教師
        assertEquals(UserRole.TEACHER, savedUsers.get(0).getRole(), 
            "最初のユーザーはTEACHERロールを持つべきです");
        
        // 残りのユーザーは学生
        for (int i = 1; i < savedUsers.size(); i++) {
            assertEquals(UserRole.STUDENT, savedUsers.get(i).getRole(), 
                "2番目以降のユーザーはSTUDENTロールを持つべきです: " + savedUsers.get(i).getUserId());
        }
    }

    @Test
    void testStudentsHaveGradeCode4() throws Exception {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        
        // 教師には学年コードがない
        assertNull(savedUsers.get(0).getGradeCode(), 
            "教師には学年コードがないべきです");
        
        // 全ての学生は学年コード4を持つ
        for (int i = 1; i < savedUsers.size(); i++) {
            assertEquals((byte) 4, savedUsers.get(i).getGradeCode(), 
                "全ての学生は学年コード4を持つべきです: " + savedUsers.get(i).getUserId());
        }
    }
}
