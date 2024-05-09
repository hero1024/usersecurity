package cn.edu.moe.user.service;

import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.model.UserVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author songpeijiang
 * @since 2024/4/18
 */
public interface LoginService {

    User getUserByUsername(String username);

    Page<User> getList(Page<User> page, Wrapper<User> queryWrapper);

    String login(String username, String password);

    int register(User user);

    boolean changePwd(UserVo userVo);

    boolean validatePermission(String username, String originalUri);

}
