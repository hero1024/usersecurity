package cn.edu.moe.user.service.impl;

import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.mapper.UserMapper;
import cn.edu.moe.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author songpeijiang
 * @since 2024-04-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
