package cn.edu.moe.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author songpeijiang
 * @since 2024-04-18
 */
public class Permission implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String path;

    @TableField("role_ids")
    private String roleIds;

    private String description;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Permissions{" +
        "id=" + id +
        ", path=" + path +
        ", roleIds=" + roleIds +
        ", description=" + description +
        "}";
    }
}
