package cn.edu.moe.user.service.impl;

import cn.edu.moe.user.entity.Permission;
import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.model.MyUserDetails;
import cn.edu.moe.user.model.UserVo;
import cn.edu.moe.user.service.IPermissionService;
import cn.edu.moe.user.service.IUserService;
import cn.edu.moe.user.service.LoginService;
import cn.edu.moe.user.utils.JwtTokenUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author songpeijiang
 * @since 2024/4/18
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private IUserService userService;
    @Autowired
    private IPermissionService permissionService;
    @Autowired
    UserDetailsService userDetailsService;
    @Resource
    JwtTokenUtil jwtTokenUtil;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.last("limit 1");
        return userService.getOne(queryWrapper);
    }

    @Override
    public Page<User> getList(Page<User> page, Wrapper<User> queryWrapper) {
        Page<User> userPage = userService.page(page, queryWrapper);
        List<User> list = userPage.getRecords();
        userPage.setRecords(list);
        return userPage;
    }


    @Override
    public String login(String username, String password) {
        MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(username);
        if (userDetails != null) {
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return null;
            }
            if (!userDetails.isEnabled()) {
                return "-1";
            }
            return jwtTokenUtil.generateToken(userDetails);
        }
        return null;
    }

    @Override
    public int register(User user) {
        if (userService.query().eq("username", user.getUsername()).one() != null) {
            return -1;//已注册
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        userService.save(user);
        return 0;
    }

    @Override
    public boolean changePwd(UserVo userVo) {
        MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(userVo.getUsername());
        if (userDetails != null) {
            if (!passwordEncoder.matches(userVo.getPassword(), userDetails.getPassword())) {
                return false;
            }
            if (!userVo.getNewPwd().equals(userVo.getConfirmPwd())) {
                return false;
            }
            User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, userVo.getUsername()).last("limit 1"));
            if (user != null) {
                user.setPassword(passwordEncoder.encode(userVo.getNewPwd()));
                userService.updateById(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validatePermission(String username, String originalUri) {
        try {
            log.info("权限校验 username:{}, originalUri：{}", username, originalUri);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            queryWrapper.last("limit 1");
            User user = userService.getOne(queryWrapper);
            if (StringUtils.hasText(originalUri)) {
                List<String> allPath = permissionService.list().stream().map(Permission::getPath).collect(Collectors.toList());
                PathMatcher pathMatcher = new AntPathMatcher();
                if (allPath.stream().anyMatch(path -> pathMatcher.match(path, originalUri))) {
                    LambdaQueryWrapper<Permission> lambdaQueryWrapper = Wrappers.lambdaQuery();
                    lambdaQueryWrapper.last("where find_in_set('" + user.getRoleId() + "',role_ids)");
                    List<String> rolePath = permissionService.list(lambdaQueryWrapper).stream().map(Permission::getPath).collect(Collectors.toList());
                    return rolePath.stream().anyMatch(path -> pathMatcher.match(path, originalUri));
                }
            }
            return true;
        } catch (Exception e) {
            log.error("权限校验失败，username:{}, originalUri：{}", username, originalUri, e);
            return false;
        }
    }


}
