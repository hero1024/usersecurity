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
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private RestTemplate restTemplate;

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
        return CommonResult.success(jwtTokenUtil.gainEncryptToken(forwardAuth.getCookie()));
    }

    @Operation(description = "解密验证token")
    @RequestMapping("/forward/auth")
    public ResponseEntity<CommonResult<String>> forwardAuth(HttpServletRequest request, HttpServletResponse response){
        String sourceIP = request.getHeader("X-Forwarded-For");
        String originalUri = request.getHeader("X-Forwarded-Uri");
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenType)) {
            // The part after "Bearer "
            String authToken = authHeader.substring(tokenType.length());
            String subject = jwtTokenUtil.getSubjectFromEncryptToken(authToken);
            if (subject != null) {
                boolean validateToken = false;
                try {
                    validateToken = restTemplate.postForObject(urlsConfig.getForward(), subject, Boolean.class);
                } catch (Exception e) {
                    log.error("========= 权限校验失败， forwardAuth:{}, sourceIP:{}, originalUri：{}, authHeader:{} =========", urlsConfig.getForward(), sourceIP, originalUri, authHeader, e);
                }

                if (validateToken && !jwtTokenUtil.isTokenExpired(authToken)) {
                    log.info("========= 权限校验通过， forwardAuth:{}, sourceIP:{}, originalUri：{}, authHeader:{} =========", urlsConfig.getForward(), sourceIP, originalUri, authHeader);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("X-Forwarded-For", sourceIP);
                    return new ResponseEntity<>(CommonResult.success(authToken), headers, HttpStatus.OK);
                }
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-For", sourceIP);
        return new ResponseEntity<>(CommonResult.unauthorized("token验证失败"), headers, HttpStatus.UNAUTHORIZED);
    }

}
