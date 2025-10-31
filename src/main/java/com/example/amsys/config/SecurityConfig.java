package com.example.amsys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.amsys.security.CustomLoginSuccessHandler;
import com.example.amsys.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
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
            
            // csrf対策
            //.csrf(c -> c.disable())
        
             .cors(AbstractHttpConfigurer::disable)
            
            //urlの権限を付与
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/css/**", "/js/**").permitAll() 
                    //.requestMatchers("").anonymous()
                    .requestMatchers("/", "/index", "/login", "/register", "/students/**", "/teachers/**").permitAll()//ログインなしで閲覧可能
                    //.requestMatchers("/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
            )
            
            //ログイン画面の設定
            .formLogin(form -> form
                    .loginPage("/login") //ログイン画面のurl
                    .usernameParameter("user_id") //ユーザーIDのパラメータ名
                    .passwordParameter("password") //パスワードのパラメータ名
                    .successHandler(customLoginSuccessHandler) //ログイン成功時のリダイレクト先
                    .failureUrl("/login?error=true") //ログイン失敗時のリダイレクト先
                    .permitAll()
            )
            
            //ログアウト画面の設定
            .logout(logout -> logout
                    .logoutUrl("/logout") //ログアウト画面のurl
                    .logoutSuccessUrl("/login?logout=true") //ログアウト成功時のリダイレクト先
                    .permitAll()
            );

        return http.build();

    }
}
