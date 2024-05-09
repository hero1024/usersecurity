package cn.edu.moe.user.security;

import cn.edu.moe.user.config.UrlsConfig;
import cn.hutool.json.JSONUtil;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 */
public class JwtFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    UrlsConfig urlsConfig;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenType}")
    private String tokenType;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException, AccessDeniedException {
        String requestURI = request.getRequestURI();
        System.out.println("JwtFilter：" + requestURI);
        //如果requestURI在白名单中直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String url : urlsConfig.getIgnored()) {
            if (pathMatcher.match(contextPath + url, requestURI)) {
                System.out.println("白名单通过..." + requestURI);
                chain.doFilter(request, response);
                return;
            }
        }
        //验证用户名和token
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenType)) {
            String authToken = authHeader.substring(tokenType.length());// The part after "Bearer "
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            System.out.println("用户" + username + "发起请求...");
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                    System.out.println("Jwt请求验证通过...");
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;
                }
            }
        }

        //未通过验证，直接返回错误
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSONUtil.parse(CommonResult.unauthorized("token验证失败")));
        response.getWriter().flush();
    }
}
