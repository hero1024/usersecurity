package cn.edu.moe.user.controller;

import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.entity.UserBindThirdLogin;
import cn.edu.moe.user.service.IUserBindThirdLoginService;
import cn.edu.moe.user.service.IUserService;
import cn.edu.moe.user.service.LoginService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

/**
 * @author songpeijiang
 * @since 2024/4/18
 */
@Slf4j
@Api(tags = "回调")
@RestController
public class CallbackController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IUserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private IUserBindThirdLoginService userBindThirdLoginService;

    @GetMapping("callback")
    @ApiOperation(value = "获取扫描人信息，添加数据", tags = "当用户扫描二维码登录后，会回调到本方法中")
    public String callback(String code, String state, HttpSession session) {
        log.info("用户微信扫描登陆之后开始进行回调： code = {}，state = {}", code, state);

        // 从redis中将state获取出来，和当前传入的state作比较(正确的做法)
        // 如果一致则放行，如果不一致则抛出异常：非法访问(正确的做法)

        try {
            // 1 获取code值，临时票据，类似于验证码
            // 2 拿着code请求 微信固定的地址，得到两个值 accsess_token 和 openid
            // 向认证服务器发送请求换取access_token
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            // 拼接三个参数 ：id  秘钥 和 code值
            String accessTokenUrl = String.format(baseAccessTokenUrl,
                    "WX_OPEN_APP_ID",
                    "WX_OPEN_APP_SECRET",
                    code);

            // 请求这个拼接好的地址，得到返回两个值 accsess_token 和 openid
            // 使用httpclient发送请求，得到返回结果
            String accessTokenInfo = restTemplate.getForObject(accessTokenUrl, String.class);
            log.info("accessTokenInfo = {}", accessTokenInfo);
            // 从accessTokenInfo字符串获取出来两个值 accsess_token 和 openid
            // 把accessTokenInfo字符串转换map集合，根据map里面key获取对应值
            // 使用json转换工具 Gson
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String accsess_token = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");

            log.info("成功得到accsess_token和openid  accsess_token = {}，openid = ", accsess_token, openid);
            // 把扫描人信息添加数据库里面
            // 判断数据表里面是否存在相同微信信息，根据openid判断（openid是唯一的）
            UserBindThirdLogin userBindThirdLogin = userBindThirdLoginService.getOne(Wrappers.<UserBindThirdLogin>lambdaQuery().eq(UserBindThirdLogin::getOpenId, openid).last("limit 1"));

            // 之前没有用微信登陆过
            if (null == userBindThirdLogin) {
                log.info("当前用户尚未使用过微信登录，将会该用户的信息存入数据库");
                // 3 拿着得到accsess_token 和 openid，再去请求微信提供固定的地址，获取到扫描人信息
                // 访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                //拼接两个参数
                String userInfoUrl = String.format(
                        baseUserInfoUrl,
                        accsess_token,
                        openid
                );
                // 发送请求，得到微信官方返回的用户信息
                HashMap userInfoMap = restTemplate.getForObject(userInfoUrl, HashMap.class);
                log.info("微信官方返回的用户信息为：{}", userInfoMap);
                //获取返回userinfo字符串扫描人信息
                String nickname = (String) userInfoMap.get("nickname");//昵称
                String headSculpture = (String) userInfoMap.get("headSculpture");
                Long phone = (Long) userInfoMap.get("phone");


                //将用户的信息和昵称存入数据库
                User user = new User();
                user.setUsername(nickname);
                user.setPassword(nickname);
                user.setNickname(nickname);
                user.setPhone(phone);
                loginService.register(user);

                userBindThirdLogin = new UserBindThirdLogin();
                userBindThirdLogin.setUserId(user.getId());
                userBindThirdLogin.setType("weixin");
                userBindThirdLogin.setOpenId(openid);
                userBindThirdLogin.setNickname(nickname);
                userBindThirdLogin.setHeadSculpture(headSculpture);
                userBindThirdLogin.setCreateTime(LocalDateTime.now());
                userBindThirdLoginService.save(userBindThirdLogin);

                log.info("成功将微信信息存入数据库中，存入的信息为：{}", user);
            }
            log.info("当前用户已经使用过微信进行登录，直接返回该用户的信息");
            // TODO 登录
            User user = userService.getById(userBindThirdLogin.getUserId());
            // 生成jwt
            String token = loginService.login(user.getUsername(), user.getPassword());

            // 存入cookie
            //CookieUtils.setCookie(request, response, "guli_jwt_token", token);

            //  因为端口号不同存在跨域问题，cookie不能跨域，所以这里使用url重写

            log.info("微信登陆成功后重定向到首页，携带的token信息为：{}", token );
            return "redirect:http://xxx:xxxx?token=" + token;

        } catch (Exception e) {
            log.error("微信登录失败，异常信息为：{}", e.toString());
            throw new RuntimeException("登录失败");
        }
    }

}
