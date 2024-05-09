package cn.edu.moe.user.service;

import cn.edu.moe.user.entity.UserBindThirdLogin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * 需求说明：
 *
 *
 * 1、
 *
 *         // 1. 登录
 *         // 1.1 Oauth2登录 发现uid有登录用户，uid查询用户信息
 *         // 1.2 Oauth2登录,请求微信、授权、重定向，查询用户信息，UID没有查到对应用户，说明未绑定，
 *         - 直接创建一个账号，并绑定，返回成功认证信息
 *         - 未绑定时，记录当前平台用户ID，重定向到注册页面，用户填写信息，注册后重新向到首页地址
 *         - 未绑定时，重定向到输入手机号，查询当前手机号的用户，存在绑定，不存在使用手机号创建用户。
 *
 *         弹出注册页面
 *
 * 2、用户绑定、解绑第三方平台
 *
 *         // 2. 登录后
 *         // 2.1 绑定，发起授权、登录、回调接收用户信息进行绑定
 *         // 2.2 解绑 直接删除
 *
 *
 *
 *
 *
 *
 * @author songpeijiang
 * @since 2024-04-30
 */
public interface IUserBindThirdLoginService extends IService<UserBindThirdLogin> {

    /**
     * 根据用户ID查询所有第三方平台绑定关系
     *
     * @param userId 用户ID
     * @return 绑定
     */
    List<UserBindThirdLogin> selectListByUserId(Long userId);

    /**
     *  根据第三方平台类型 + 第三方用户ID 查询绑定的当前平台用户ID
     */
    UserBindThirdLogin selectOne(String type,String thirdUserId);

    /**
     *  删除绑定
     */
    void removeBind(String type,String userId);
}
