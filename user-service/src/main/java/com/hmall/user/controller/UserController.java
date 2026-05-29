package com.hmall.user.controller;

import com.hmall.api.dto.DeductDTO;
import com.hmall.user.domain.dto.LoginFormDTO;
import com.hmall.user.domain.vo.UserLoginVO;
import com.hmall.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("用户登录接口")
    @PostMapping("login")  // 登录接口
    public UserLoginVO login(@RequestBody // 从请求体中获取参数  /////////////////
                                 @Validated  // 开启参数校验
                                 // 校验规则：@Validated 会对 LoginFormDTO 中的字段进行校验
                                 // 如果校验失败，会抛出异常，异常信息会被 SpringMVC 捕获
                                 // 然后将异常信息返回给客户端，客户端可以根据异常信息进行处理
                                 LoginFormDTO loginFormDTO // 登录表单实体
                            ){
        return userService.login(loginFormDTO);  // 调用用户服务的登录方法
    }

    @ApiOperation("扣减余额")
    @PutMapping("/money/deduct")
    public void deductMoney(@RequestBody DeductDTO deductDTO){
        userService.deductMoney(deductDTO.getPw(), deductDTO.getAmount());
    }
}

