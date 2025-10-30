package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.security.CustomLoginSuccessHandler;
import com.example.demo.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //パスワードのハッシュ化
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            //認証用のサービスクラス登録
            .userDetailsService(userDetailsServiceImpl)
            
            // csrf対策 - 一時的に無効化（本番環境では有効化すべき）
            .csrf(c -> c.disable())
        
            .cors(AbstractHttpConfigurer::disable)
            
            //urlの権限を付与
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/css/**", "/js/**", "/fonts/**").permitAll() 
                    .requestMatchers("/", "/index", "/login", "/register").permitAll()//ログインなしで閲覧可能
                    .requestMatchers("/admin/**").hasRole("TEACHER") // 管理者機能は教師のみ
                    .anyRequest().authenticated()
            )
            
            //ログイン画面の設定
            .formLogin(form -> form
                    .loginPage("/login") //ログイン画面のurl
                    .successHandler(customLoginSuccessHandler) //ログイン成功時のリダイレクト先
                    .failureUrl("/login")
            )
            
            //ログアウト画面の設定
            .logout(logout -> logout
                    .logoutUrl("/logout") //ログアウト画面のurl
                    .logoutSuccessUrl("/login")//ログアウト成功時のリダイレクト先
            );

        return http.build();

    }
}
