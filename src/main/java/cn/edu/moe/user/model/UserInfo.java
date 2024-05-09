package cn.edu.moe.user.model;

import cn.edu.moe.user.entity.Role;
import cn.edu.moe.user.entity.UserBindThirdLogin;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private Long id;

    private String username;

    /**
     * 姓名
     */
    private String nickname;


    /**
     * 邮箱
     */
    private String email;

    private Long phone;

    private Role role;

    private List<UserBindThirdLogin> userBindThirdList;

}
