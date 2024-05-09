package cn.edu.moe.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.edu.moe.user.model.MyUserDetails;
import cn.edu.moe.user.entity.Permission;
import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.mapper.PermissionMapper;
import cn.edu.moe.user.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    UserMapper userMapper;
    @Resource
    PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        //如果用户被禁用，则不再查询权限表
        if (user != null) {
            return new MyUserDetails(user, user.getStatus() == 1 ? getResourceList(user.getRoleId()) : new ArrayList<>());
        }
        return null;
    }

    /**
     * 获取该用户角色拥有的权限（可访问的url）
     *
     * @return
     */
    public List<Permission> getResourceList(int roleId) {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("where find_in_set('" + roleId + "',role_ids)");
        return permissionMapper.selectList(queryWrapper);
    }

}
