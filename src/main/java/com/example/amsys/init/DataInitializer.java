package com.example.amsys.init;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.amsys.model.LessonTime;
import com.example.amsys.model.User;
import com.example.amsys.model.User.UserRole;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * システム起動時に初期データを挿入するクラス
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LessonTimeRepository lessonTimeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeLessonTimes();
        initializeUsers();
    }

    /**
     * レッスン時間データを初期化
     */
    private void initializeLessonTimes() {
        if (lessonTimeRepository.count() == 0) {
            List<LessonTime> lessonTimes = Arrays.asList(
                new LessonTime((byte) 0, "HR", LocalTime.of(8, 50, 0), LocalTime.of(8, 59, 0)),
                new LessonTime((byte) 1, "1限", LocalTime.of(9, 0, 0), LocalTime.of(10, 30, 0)),
                new LessonTime((byte) 2, "2限", LocalTime.of(10, 40, 0), LocalTime.of(12, 10, 0)),
                new LessonTime((byte) 3, "3限", LocalTime.of(13, 0, 0), LocalTime.of(14, 30, 0)),
                new LessonTime((byte) 4, "4限", LocalTime.of(14, 40, 0), LocalTime.of(16, 10, 0))
            );
            lessonTimeRepository.saveAll(lessonTimes);
            log.info("レッスン時間データを初期化しました。{}件のデータを挿入しました。", lessonTimes.size());
        } else {
            log.info("レッスン時間データは既に存在します。スキップします。");
        }
    }

    /**
     * ユーザーデータを初期化
     */
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            List<User> users = createInitialUsers();
            userRepository.saveAll(users);
            log.info("ユーザーデータを初期化しました。{}件のデータを挿入しました。", users.size());
        } else {
            log.info("ユーザーデータは既に存在します。スキップします。");
        }
    }

    /**
     * 初期ユーザーデータを作成
     */
    private List<User> createInitialUsers() {
        List<User> users = new ArrayList<>();
        
        // 教師データを追加
        users.add(createTeacher("Admin01", "試験", "太郎", "しけん", "たろう", "Teacher2025"));
        
        // 学生データを追加
        users.addAll(Arrays.asList(
            createStudent("T22001", (byte) 4, "秋山", "政人", "あきやま", "まさと", "T22001"),
            createStudent("T22002", (byte) 4, "秋山", "祐二", "あきやま", "ゆうじ", "T22002"),
            createStudent("T22004", (byte) 4, "伊倉", "旦陽", "いぐら", "あさひ", "T22004"),
            createStudent("T22005", (byte) 4, "泉", "陽翔", "いずみ", "はると", "T22005"),
            createStudent("T22006", (byte) 4, "勝又", "夢叶", "かつまた", "ゆうと", "T22006"),
            createStudent("T22008", (byte) 4, "岸本", "真征", "きしもと", "しんせい", "T22008"),
            createStudent("T22010", (byte) 4, "小林", "輝流", "こばやし", "ひかる", "T22010"),
            createStudent("T22011", (byte) 4, "酒井", "竣", "さかい", "しゅん", "T22011"),
            createStudent("T22012", (byte) 4, "三瓶", "航太郎", "さんぺい", "こうたろう", "T22012"),
            createStudent("T22013", (byte) 4, "杉山", "紘生", "すぎやま", "ひろき", "T22013"),
            createStudent("T22014", (byte) 4, "鈴木", "颯太", "すずき", "そうた", "T22014"),
            createStudent("T22015", (byte) 4, "鈴木", "陽大", "すずき", "ひなた", "T22015"),
            createStudent("T22016", (byte) 4, "武井", "風樹", "たけい", "ふうき", "T22016"),
            createStudent("T22017", (byte) 4, "山上", "結史", "やまがみ", "ゆうし", "T22017"),
            createStudent("T22018", (byte) 4, "山口", "瀬奈", "やまぐち", "せな", "T22018"),
            createStudent("T22019", (byte) 4, "由布", "美織", "ゆふ", "みおり", "T22019"),
            createStudent("T22020", (byte) 4, "湯山", "公晴", "ゆやま", "こうせい", "T22020"),
            createStudent("T22021", (byte) 4, "渡辺", "爽流", "わたなべ", "そうる", "T22021"),
            createStudent("T22022", (byte) 4, "綿抜", "唯織", "わたぬき", "いおり", "T22022")
        ));
        
        return users;
    }

    /**
     * 教師ユーザーオブジェクトを作成
     */
    private User createTeacher(String userId, String lastName, String firstName, 
                              String lastKanaName, String firstKanaName, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setGradeCode(null); // 教師には学年コードは不要
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setLastKanaName(lastKanaName);
        user.setFirstKanaName(firstKanaName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.TEACHER);
        return user;
    }

    /**
     * 学生ユーザーオブジェクトを作成
     */
    private User createStudent(String userId, Byte gradeCode, String lastName, String firstName, 
                              String lastKanaName, String firstKanaName, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setGradeCode(gradeCode);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setLastKanaName(lastKanaName);
        user.setFirstKanaName(firstKanaName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.STUDENT);
        return user;
    }
}
