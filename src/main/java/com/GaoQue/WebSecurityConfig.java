package com.GaoQue;

import com.GaoQue.service.user.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // Tài nguyên công khai, bao gồm trang đăng nhập và đăng ký
                        .requestMatchers("/assets/**", "/forms/**", "/images/**", "/FormAdmin/**",
                                "/Product/**", "/AINhanDien/**", "/Home/**", "/","/register")
                        .permitAll() // Cho phép truy cập không cần đăng nhập
                        .requestMatchers("/Admin/**") // Chỉ cho phép ADMIN truy cập vào các trang có "/Admin"
                        .hasAuthority("ADMIN") // Sử dụng ROLE_ADMIN cho việc xác thực quyền ADMIN
                        .anyRequest().authenticated() // Các yêu cầu còn lại cần phải xác thực
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // GET - Hiển thị form đăng nhập
                        .loginProcessingUrl("/login") // POST - Xử lý đăng nhập
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("gaoque") // Key cho tính năng nhớ đăng nhập
                        .rememberMeCookieName("gaoque") // Tên cookie cho tính năng remember-me
                        .tokenValiditySeconds(24 * 60 * 60) // Thời gian giữ đăng nhập là 24 giờ
                        .userDetailsService(userDetailsService()) // Dịch vụ người dùng cho remember-me
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403") // Trang lỗi khi người dùng không có quyền truy cập
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1) // Giới hạn một phiên đăng nhập trên mỗi tài khoản
                        .expiredUrl("/login") // URL khi phiên đăng nhập hết hạn
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("GaoQue") // Tên miền cho xác thực cơ bản
                )
                .build();
    }

}

