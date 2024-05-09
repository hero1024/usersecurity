package cn.edu.moe.user.controller;


import cn.edu.moe.user.entity.Role;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.service.IRoleService;
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
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @GetMapping("/list")
    public CommonResult<List<Role>> listRole(){
        return CommonResult.success(roleService.list());
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteRole(@PathVariable Long id){
        return CommonResult.success(roleService.removeById(id));
    }

    @PostMapping("/save")
    public CommonResult<Boolean> saveRole(@RequestBody Role role){
        return CommonResult.success(roleService.saveOrUpdate(role));
    }

}

