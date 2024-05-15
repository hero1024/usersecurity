package cn.edu.moe.user.config;

import cn.edu.moe.user.security.*;
import cn.edu.moe.user.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();
        //不需要保护的资源路径允许访问
        for (String url : urlsConfig().getIgnored()) {
            System.out.println("白名单:" + url);
            registry.antMatchers(url).permitAll();
        }
        //允许跨域请求的OPTIONS请求
        registry.antMatchers(HttpMethod.OPTIONS).permitAll();
        // 任何请求需要身份认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                //支持跨域访问
                .and()
                .cors()
                .configurationSource(corsConfigurationSource())
                // 关闭跨站请求防护及不使用session
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 自定义权限拒绝处理类
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler())//授权
                .authenticationEntryPoint(restAuthenticationEntryPoint())//认证
                // 自定义权限拦截器JWT过滤器
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //动态权限校验过滤器
        registry.and().addFilterBefore(securityFilter(), FilterSecurityInterceptor.class);
    }

    /**
     * 开启 Spring Security 对跨域的支持
     *
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(Duration.ofHours(1));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 白名单
     *
     * @return
     */
    @Bean
    public UrlsConfig urlsConfig() {
        return new UrlsConfig();
    }

    /**
     * 自定义无权限返回结果
     *
     * @return
     */
    @Bean
    public JwtAccessDeniedHandler restfulAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    /**
     * 自定义未登录或登录过期返回结果
     *
     * @return
     */
    @Bean
    public JwtAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    /**
     * JWT登录授权过滤器（对请求进行授权验证过滤）
     *
     * @return
     */
    @Bean
    public JwtFilter jwtAuthenticationTokenFilter() {
        return new JwtFilter();
    }

    /**
     * Jwt生成的工具类
     *
     * @return
     */
    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilter securityFilter() {
        return new SecurityFilter();
    }

    @Bean
    public SecurityAccessDecisionManager securityAccessDecisionManager() {
        return new SecurityAccessDecisionManager();
    }

    @Bean
    public SecurityMetadataSource securityMetadataSource() {
        return new SecurityMetadataSource();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
