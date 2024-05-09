package cn.edu.moe.user.model;

import lombok.Data;

/**
 * @author songpeijiang
 * @since 2024/4/19
 */
@Data
public class UserVo {

    private String username;

    private String password;

    private String newPwd;

    private String confirmPwd;

    /**
     * 姓名
     */
    private String nickname;


    /**
     * 邮箱
     */
    private String email;

    private Long phone;

    private Integer code;

}
