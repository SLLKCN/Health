package com.example.eat.controller;

import com.example.eat.aop.Pass;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.user.PostUserLogin;
import com.example.eat.model.dto.param.user.PostUserRegister;
import com.example.eat.model.dto.param.user.PutUser;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.model.dto.res.wish.WishResponse;
import com.example.eat.service.UserService;
import com.example.eat.util.MinioUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
public class UserController {
    @Autowired
    MinioUtil minioUtil;
    @Autowired
    UserService userService;
    @Pass
    @PostMapping("/register")
    CommonResult<LoginRes> register(@Valid @RequestBody PostUserRegister postUserRegister){
        return userService.register(postUserRegister);
    }
    @Pass
    @PostMapping("/login")
    CommonResult<LoginRes> login(@Valid @RequestBody PostUserLogin postUserLogin){
        return userService.login(postUserLogin);
    }
    @PutMapping("/user")
    CommonResult<BlankRes> updateUserInfo(@Valid @RequestBody PutUser putUser){
        return userService.updateUserInfo(putUser);
    }
    @GetMapping("/users/{userId}")
    CommonResult<UserRes> getOnesUserInfo(@NotNull(message = "userId不能为空") @PathVariable("userId") String userId){
        return userService.getOnesUserInfo(Integer.parseInt(userId));
    }
    @GetMapping("/users/my")
    CommonResult<UserRes> getMyUserInfo(){
        return userService.getMyUserInfo();
    }
    @PutMapping("/user/my/status")
    CommonResult<BlankRes> updateUserStatus(@NotNull(message = "status不能为空") @RequestParam("status") String status){
        return userService.updateUserStatus(status);
    }
    @PutMapping("/user/my/signature")
    CommonResult<BlankRes> updateUserSignature(@NotNull(message = "signature不能为空") @RequestParam("signature") String signature){
        return userService.updateUserSignature(signature);
    }
    @Pass
    @PostMapping("/telephone")
    CommonResult<BlankRes> getCode(@RequestParam String telephone){
        return userService.getCode(telephone);
    }

    @PostMapping("/wish")
    CommonResult<BlankRes> addWish(@Size(max = 50, message = "心愿过长") @RequestParam String content){
        return userService.addWish(content);
    }

    @GetMapping("/wish")
    CommonResult<WishResponse> getWish(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize){
        return userService.getWish(pageNum,pageSize);
    }
}
