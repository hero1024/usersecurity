package cn.edu.moe.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@TableName("user_bind_third_login")
public class UserBindThirdLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 平台类型
     */
    private String type;

    /**
     * 用户表主键ID
     */
    private Long userId;

    /**
     * 用户在第三方平台的唯一ID
     */
    @TableField("open_id")
    private String  openId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 第三方头像
     */
    private String headSculpture;

    /**
     * 绑定时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getHeadSculpture() {
        return headSculpture;
    }

    public void setHeadSculpture(String headSculpture) {
        this.headSculpture = headSculpture;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserBindThirdLogin{" +
            "id=" + id +
            ", type=" + type +
            ", userId=" + userId +
            ", openId=" + openId +
            ", nickname=" + nickname +
            ", headSculpture=" + headSculpture +
            ", createTime=" + createTime +
        "}";
    }
}
