package cn.edu.moe.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ThirdUserVo {

    /**
     * 用户在第三方平台的唯一ID
     */
    @ApiModelProperty(value = "用户唯一ID", required = true)
    @NotBlank(message = "openId为必填项")
    private String  openId;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank(message = "nickname为必填项")
    private String nickname;

    /**
     * 平台类型
     */
    @ApiModelProperty(value = "类型", allowableValues = "UNBIND,BIND")
    private String type;

    /**
     * 第三方头像
     */
    @ApiModelProperty(value = "头像")
    private String headSculpture;

    @ApiModelProperty(value = "BIND用户名")
    private String username;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "BIND邮箱")
    private String email;

    @ApiModelProperty(value = "BIND手机号")
    private Long phone;

    private Integer code;

}
