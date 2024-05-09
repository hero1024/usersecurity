package cn.edu.moe.user.controller;

import cn.edu.moe.user.config.UrlsConfig;
import cn.edu.moe.user.entity.Role;
import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.model.UserInfo;
import cn.edu.moe.user.model.UserVo;
import cn.edu.moe.user.service.IRoleService;
import cn.edu.moe.user.service.IUserBindThirdLoginService;
import cn.edu.moe.user.service.LoginService;
import cn.edu.moe.user.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author songpeijiang
 * @since 2024/4/18
 */
@Slf4j
@Api(tags = "门户入口")
@RestController
public class LoginController {

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
    private IRoleService roleService;
    @Autowired
    private IUserBindThirdLoginService userBindThirdLoginService;

    @Operation(description = "验证token")
    @RequestMapping("/auth")
    public ResponseEntity<String> auth(HttpServletRequest request, HttpServletResponse response){
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
                        log.info("权限校验通过， username:{}, originalUri：{}", username, originalUri);
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("X-User-ID", String.valueOf(user.getId()));
                        return new ResponseEntity<>(authToken, headers, HttpStatus.OK);
                    }
                }
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", urlsConfig.getLogin());
//        return new ResponseEntity<>("token验证失败", headers, HttpStatus.FOUND);
        return new ResponseEntity<>("token验证失败", headers, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/userinfo")
    public CommonResult<UserInfo> userinfo(HttpServletRequest request) {
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenType)) {
            String authToken = authHeader.substring(tokenType.length());// The part after "Bearer "
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UserInfo userInfo = new UserInfo();
                    User user = loginService.getUserByUsername(username);
                    BeanUtils.copyProperties(user, userInfo);
                    userInfo.setRole(roleService.getById(user.getRoleId()));
                    userInfo.setUserBindThirdList(userBindThirdLoginService.selectListByUserId(user.getId()));
                    return CommonResult.success(userInfo);
                }
            }
        }
        return CommonResult.failed();
    }

    @Operation(description = "登录")
    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody UserVo userVo) {
        String token = loginService.login(userVo.getUsername(), userVo.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        if (token.equals("-1")) {
            return CommonResult.validateFailed("账号被禁用");
        }
        return CommonResult.success(token);
    }

    @Operation(description = "注册")
    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody UserVo userVo) {
        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        int result = loginService.register(user);
        if (result == -1) {
            return CommonResult.failed("该账号已注册");
        }
        return CommonResult.success("注册成功");
    }

    @Operation(description = "修改密码")
    @PostMapping("/change/password")
    public CommonResult<String> changePwd(@RequestBody UserVo userVo) {
        if (!loginService.changePwd(userVo)) {
            return CommonResult.failed("用户名或密码错误");
        }
        return CommonResult.success("修改成功");
    }

    @GetMapping("/refresh/{token}")
    public CommonResult<String> refreshToken(@PathVariable String token) {
        return CommonResult.success(jwtTokenUtil.refreshHeadToken(token));
    }

}
