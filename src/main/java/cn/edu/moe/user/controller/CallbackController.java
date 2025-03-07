package cn.edu.moe.user.controller;

import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.entity.UserBindThirdLogin;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.model.ThirdUserVo;
import cn.edu.moe.user.model.UserBindThirdEnum;
import cn.edu.moe.user.service.IUserBindThirdLoginService;
import cn.edu.moe.user.service.LoginService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author songpeijiang
 * @since 2024/4/18
 */
@Slf4j
@Api(tags = "回调")
@RestController
public class CallbackController {

    @Autowired
    private IUserBindThirdLoginService userBindThirdLoginService;

    @Autowired
    private LoginService loginService;

    @Value("${secure.defaultPwd}")
    private String defaultPwd;

    @Operation(description = "保存用户信息")
    @PostMapping("callback")
    @ApiOperation(value = "获取扫码人信息，添加数据", tags = "当用户扫描二维码登录后，会回调到本方法中")
    public CommonResult<UserBindThirdLogin> callback(@RequestBody ThirdUserVo thirdUser) {
        log.info("用户扫码登陆之后开始进行回调： thirdUser = {}", thirdUser);
        // 把扫描人信息添加数据库里面
        UserBindThirdLogin userBindThirdLogin = new UserBindThirdLogin();
        BeanUtils.copyProperties(thirdUser, userBindThirdLogin);
        userBindThirdLogin.setCreateTime(LocalDateTime.now());
        if (!StringUtils.hasText(userBindThirdLogin.getType())){
            userBindThirdLogin.setType(UserBindThirdEnum.UNBIND.name());
        } else if (UserBindThirdEnum.BIND.name().equals(userBindThirdLogin.getType())){
            //将用户的信息和昵称存入数据库
            User user = new User();
            BeanUtils.copyProperties(thirdUser, user);
            user.setPassword(defaultPwd);
            loginService.register(user);
            user = loginService.getUserByUsername(thirdUser.getUsername());
            userBindThirdLogin.setUserId(user.getId());
        }
        UpdateWrapper<UserBindThirdLogin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("open_id", userBindThirdLogin.getOpenId());
        userBindThirdLoginService.saveOrUpdate(userBindThirdLogin, updateWrapper);
        return CommonResult.success(userBindThirdLogin);
    }

}
