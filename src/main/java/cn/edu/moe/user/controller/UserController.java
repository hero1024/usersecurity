package cn.edu.moe.user.controller;


import cn.edu.moe.user.entity.User;
import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author songpeijiang
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public CommonResult<List<User>> listUser(){
        return CommonResult.success(userService.list());
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteUser(@PathVariable Long id){
        return CommonResult.success(userService.removeById(id));
    }

    @PostMapping("/save")
    public CommonResult<Boolean> saveUser(@RequestBody User user){
        Pattern bcryptPattern = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");
        Matcher matcher = bcryptPattern.matcher(user.getPassword());
        if (!matcher.matches()) {
            String encodePassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
        }
        return CommonResult.success(userService.saveOrUpdate(user));
    }

}

