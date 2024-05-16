package cn.edu.moe.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author songpeijiang
 * @since 2024/4/19
 */
@Data
public class UserVo {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "username为必填项")
    private String username;
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "password为必填项")
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
