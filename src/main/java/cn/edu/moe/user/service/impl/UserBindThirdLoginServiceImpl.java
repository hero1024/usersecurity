package cn.edu.moe.user.service.impl;

import cn.edu.moe.user.entity.UserBindThirdLogin;
import cn.edu.moe.user.mapper.UserBindThirdLoginMapper;
import cn.edu.moe.user.service.IUserBindThirdLoginService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserBindThirdLoginServiceImpl extends ServiceImpl<UserBindThirdLoginMapper, UserBindThirdLogin> implements IUserBindThirdLoginService {

    @Override
    public List<UserBindThirdLogin> selectListByUserId(Long userId) {
        LambdaQueryWrapper<UserBindThirdLogin> query = Wrappers.lambdaQuery();
        query.eq(UserBindThirdLogin::getUserId,userId);
        return list(query);
    }

    @Override
    public UserBindThirdLogin selectOne(String type, String thirdUserId) {
        LambdaQueryWrapper<UserBindThirdLogin> query = Wrappers.lambdaQuery();
        query.eq(UserBindThirdLogin::getType,type);
        query.eq(UserBindThirdLogin::getOpenId,thirdUserId);
        return getOne(query);
    }

    @Override
    @Transactional
    public void removeBind(String type, String userId) {
        LambdaQueryWrapper<UserBindThirdLogin> delete = Wrappers.lambdaQuery();
        delete.eq(UserBindThirdLogin::getType,type);
        delete.eq(UserBindThirdLogin::getOpenId,userId);
        remove(delete);
    }
}
