package cn.edu.moe.user.controller;

import cn.edu.moe.user.config.UrlsConfig;
import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.model.ForwardAuth;
import cn.edu.moe.user.service.IRoleService;
import cn.edu.moe.user.service.IUserBindThirdLoginService;
import cn.edu.moe.user.service.LoginService;
import cn.edu.moe.user.utils.AesSecureUtil;
import cn.edu.moe.user.utils.JwtTokenUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Api(tags = "鉴权")
@RestController
public class AuthController {


    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenType}")
    private String tokenType;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private UrlsConfig urlsConfig;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RestTemplate noSSLRestTemplate;

    @Operation(description = "验证token")
    @RequestMapping("/auth")
    public ResponseEntity<CommonResult<String>> auth(HttpServletRequest request, HttpServletResponse response){
        String sourceIP = request.getHeader("X-Forwarded-For");
        String originalUri = request.getHeader("X-Forwarded-Uri");
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenType)) {
            String authToken = authHeader.substring(tokenType.length());// The part after "Bearer "
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                    if (loginService.validatePermission(username, originalUri)) {
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                        User user = loginService.getUserByUsername(username);
                        log.info("========= 权限校验通过， username:{}, sourceIP:{}, originalUri：{}, authHeader:{} =========", username, sourceIP, originalUri, authHeader);
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("X-User-ID", String.valueOf(user.getId()));
                        headers.add("X-Forwarded-For", sourceIP);
                        return new ResponseEntity<>(CommonResult.success(authToken), headers, HttpStatus.OK);
                    }
                }
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", urlsConfig.getLogin());
        //return new ResponseEntity<>("token验证失败", headers, HttpStatus.FOUND);
        return new ResponseEntity<>(CommonResult.unauthorized("token验证失败"), headers, HttpStatus.UNAUTHORIZED);
    }


    @Operation(description = "获取加密token")
    @PostMapping("/encrypt/token")
    public CommonResult<String> gainToken(@RequestBody ForwardAuth forwardAuth)  {
        String encryptToken = jwtTokenUtil.gainEncryptToken(forwardAuth.getCookie());
        log.info("forwardAuth:{}, encryptToken:{}", forwardAuth, encryptToken);
        return CommonResult.success(encryptToken);
    }

    @Operation(description = "解密验证token")
    @RequestMapping("/forward/auth")
    public ResponseEntity<CommonResult<String>> forwardAuth(HttpServletRequest request, HttpServletResponse response){
        String sourceIP = request.getHeader("X-Forwarded-For");
        String originalUri = request.getHeader("X-Forwarded-Uri");
        String authHeader = request.getHeader(tokenHeader);
        JSONObject result = null;
        if (authHeader != null && authHeader.startsWith(tokenType)) {
            // The part after "Bearer "
            String authToken = authHeader.substring(tokenType.length());
            String subject = jwtTokenUtil.getSubjectFromEncryptToken(authToken);
            log.info("authToken:{}, subject:{}", authToken, subject);
            if (subject != null) {
                try {
                    // 自定义 header
                    HttpHeaders headers1 = new HttpHeaders();
                    // 自定义cookie
                    String cookie = "cookie_vjuid_login=" + subject;
                    List<String> cookies = new ArrayList<>();
                    cookies.add(cookie);
                    // 直接放进headers 中
                    headers1.put(HttpHeaders.COOKIE, cookies);
                    // 设置 body数据类型为json
                    headers1.setContentType(MediaType.APPLICATION_JSON);
                    // url  request  responseType uriva
                    ResponseEntity<JSONObject> stringResponseEntity = noSSLRestTemplate.exchange(urlsConfig.getForward(), HttpMethod.GET, new HttpEntity<>(headers1), JSONObject.class);
                    // 同样关注 body里的信息
                    result = stringResponseEntity.getBody();
                    log.info("forwardAuth result:{}, cookie:{}", result, cookie);
                } catch (Exception e) {
                    log.error("========= 权限校验失败， forwardAuth:{}, sourceIP:{}, originalUri：{}, authHeader:{} =========", urlsConfig.getForward(), sourceIP, originalUri, authHeader, e);
                }

                if (result != null && Objects.equals(result.getInteger("e"), 0) && !jwtTokenUtil.isTokenExpired(authToken)) {
                    log.info("========= 权限校验通过， forwardAuth:{}, sourceIP:{}, originalUri：{}, authHeader:{} =========", urlsConfig.getForward(), sourceIP, originalUri, authHeader);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("X-Forwarded-For", sourceIP);
                    return new ResponseEntity<>(CommonResult.success(result.toJSONString()), headers, HttpStatus.OK);
                }
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-For", sourceIP);
        String body = result == null ? "token验证失败" : result.toJSONString();
        return new ResponseEntity<>(CommonResult.unauthorized(body), headers, HttpStatus.UNAUTHORIZED);
    }

}
