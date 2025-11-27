package com.example.amsys.config;

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
        when(userRepository.count()).thenReturn(0L);
        when(lessonTimeRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> 
            "{bcrypt}encoded_" + invocation.getArgument(0));

        // When
        dataInitializer.run(applicationArguments);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        assertEquals(19, savedUsers.size(), "19人のユーザーが挿入されるべきです");
        
        // 最初のユーザーを確認
        User firstUser = savedUsers.get(0);
        assertEquals("T22001", firstUser.getUserId());
        assertEquals((byte) 4, firstUser.getGradeCode());
        assertEquals("秋山", firstUser.getLastName());
        assertEquals("政人", firstUser.getFirstName());
        assertEquals("あきやま", firstUser.getLastKanaName());
        assertEquals("まさと", firstUser.getFirstKanaName());
        assertEquals(UserRole.STUDENT, firstUser.getRole());
        assertTrue(firstUser.getPassword().contains("encoded_T22001"), 
            "パスワードがハッシュ化されるべきです");
    }

    @Test
    void testInitializeUsersWhenExisting() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(5L);
        when(lessonTimeRepository.count()).thenReturn(5L);

        // When
        dataInitializer.run(applicationArguments);

        // Then
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void testInitializeLessonTimesWhenEmpty() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(lessonTimeRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
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
        when(userRepository.count()).thenReturn(0L);
        when(lessonTimeRepository.count()).thenReturn(3L);
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
        verify(lessonTimeRepository, never()).saveAll(anyList());
    }

    @Test
    void testPasswordsAreHashed() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(lessonTimeRepository.count()).thenReturn(0L);
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
        
        // パスワードエンコーダーが19回呼ばれたことを確認
        verify(passwordEncoder, times(19)).encode(anyString());
    }

    @Test
    void testAllUsersHaveCorrectRole() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(lessonTimeRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        for (User user : savedUsers) {
            assertEquals(UserRole.STUDENT, user.getRole(), 
                "全てのユーザーはSTUDENTロールを持つべきです: " + user.getUserId());
        }
    }

    @Test
    void testAllUsersHaveGradeCode4() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(lessonTimeRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}encoded");

        // When
        dataInitializer.run(applicationArguments);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> userCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getValue();
        for (User user : savedUsers) {
            assertEquals((byte) 4, user.getGradeCode(), 
                "全てのユーザーは学年コード4を持つべきです: " + user.getUserId());
        }
    }
}
