package kr.go.togetherschool.tosweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.go.togetherschool.tosweb.domain.AccessTokenAuthenticationProvider;
import kr.go.togetherschool.tosweb.filter.JsonUsernamePasswordAuthenticationFilter;
import kr.go.togetherschool.tosweb.filter.JwtAuthenticationFilter;
import kr.go.togetherschool.tosweb.filter.OAuth2AccessTokenAuthenticationFilter;
import kr.go.togetherschool.tosweb.filter.SilentReAuthenticationFilter;
import kr.go.togetherschool.tosweb.handler.CustomAccessDeniedHandler;
import kr.go.togetherschool.tosweb.handler.CustomAuthenticationEntryPoint;
import kr.go.togetherschool.tosweb.handler.LoginFailureHandler;
import kr.go.togetherschool.tosweb.handler.LoginSuccessJWTProvideHandler;
import kr.go.togetherschool.tosweb.repository.MemberRepository;
import kr.go.togetherschool.tosweb.service.CustomUserDetailsService;
import kr.go.togetherschool.tosweb.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.sql.DataSource;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Lazy
public class WebSecurityConfig {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final AccessTokenAuthenticationProvider provider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // For H2 DB
//                .securityMatcher("/", "/**", "/resources/**")
                .authorizeHttpRequests(requests -> requests
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers("/api/member/signup", "/", "/api/member/isDuplicated", "/api/member/login/oauth").permitAll()
                        .requestMatchers("/api/email/send", "/api/member/password", "/api/email/confirm", "/api/member/find", "/api/member/delete").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/", "/index", "/login", "/join", "/public").permitAll()
                        .anyRequest().authenticated()
                )
//                .formLogin(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
//                .logout(logout -> logout.permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        http
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new CustomAccessDeniedHandler(jwtService))
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .addFilterAfter(silentReAuthenticationFilter(), LogoutFilter.class)
                .addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(auth2AccessTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                ;

        return http.build();
    }

    @Bean
    public SilentReAuthenticationFilter silentReAuthenticationFilter() {
        return new SilentReAuthenticationFilter(jwtService, memberRepository);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordLoginFilter;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {//2 - AuthenticationManager 등록
        DaoAuthenticationProvider provider = daoAuthenticationProvider();//DaoAuthenticationProvider 사용
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler() {
        return new LoginSuccessJWTProvideHandler(jwtService, memberRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationFilter(jwtService, memberRepository);
    }

//    @Bean
//    OAuth2AccessTokenAuthenticationFilter auth2AccessTokenAuthenticationFilter() {
//        return new OAuth2AccessTokenAuthenticationFilter(provider, loginSuccessJWTProvideHandler(), loginFailureHandler());
//    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers(
                            "/error",
                            "/resources/**",
                            "/static/**",
                            "/assets/**",
                            "/api-document/**",
                            "/swagger-ui/**",
                            "/swagger-resources/**", "/v3/api-docs/**"

                    );
        };
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

}