package com.example.demo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * データベースを初期状態にリセットするサービス
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseResetService {

    private final DataSource dataSource;

    /**
     * データベースを初期状態にリセット
     * data-init.sqlスクリプトを実行して、テストデータを再投入します
     * 
     * @throws Exception リセット処理で問題が発生した場合
     */
    @Transactional
    public void resetDatabase() throws Exception {
        log.info("データベースのリセットを開始します");
        
        try {
            // SQLスクリプトファイルの読み込み
            ClassPathResource resource = new ClassPathResource("data-init.sql");
            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            
            // SQLスクリプトを実行
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // スクリプトを個別のSQL文に分割して実行
                // 注意: セミコロンで分割しているため、文字列リテラル内にセミコロンが
                // 含まれる場合は正しく動作しません。data-init.sqlでは文字列内に
                // セミコロンを使用しないでください。
                String[] sqlStatements = sql.split(";");
                for (String sqlStatement : sqlStatements) {
                    String trimmed = sqlStatement.trim();
                    // コメントと空行をスキップ
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        log.debug("SQL実行: {}", trimmed);
                        statement.execute(trimmed);
                    }
                }
                
                log.info("データベースのリセットが完了しました");
            }
        } catch (IOException e) {
            log.error("SQLスクリプトファイルの読み込みに失敗しました", e);
            throw new Exception("データベースのリセットに失敗しました: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("データベースのリセット中にエラーが発生しました", e);
            throw new Exception("データベースのリセットに失敗しました: " + e.getMessage(), e);
        }
    }
}
