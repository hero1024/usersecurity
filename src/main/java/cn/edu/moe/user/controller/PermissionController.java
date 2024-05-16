package cn.edu.moe.user.controller;


import cn.edu.moe.user.entity.Permission;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.service.IPermissionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author songpeijiang
 * @since 2024-04-18
 */
@Api(tags = "权限管理")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @GetMapping("/list")
    public CommonResult<List<Permission>> listPermission(){
        return CommonResult.success(permissionService.list());
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deletePermission(@PathVariable Long id){
        return CommonResult.success(permissionService.removeById(id));
    }

    @PostMapping("/save")
    public CommonResult<Boolean> savePermission(@RequestBody Permission permission){
        return CommonResult.success(permissionService.saveOrUpdate(permission));
    }

}

