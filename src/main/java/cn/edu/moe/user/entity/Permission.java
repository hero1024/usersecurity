package cn.edu.moe.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author songpeijiang
 * @since 2024-04-18
 */
@Data
public class Permission implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String path;

    @TableField("role_ids")
    private String roleIds;

    private String description;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(value = "update_time", update = "now()")
    private Date updateTime;

}
