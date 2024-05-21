package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.user.UserDao;
import com.example.eat.dao.wish.WishInfoDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.user.PostUserLogin;
import com.example.eat.model.dto.param.user.PostUserRegister;
import com.example.eat.model.dto.param.user.PutUser;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.cookbook.CookbooksGetRes;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.model.dto.res.wish.WishResponse;
import com.example.eat.model.po.cookbook.Cookbook;
import com.example.eat.model.po.user.User;
import com.example.eat.model.po.wish.WishInfo;
import com.example.eat.service.UserService;
import com.example.eat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    WishInfoDao wishInfoDao;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private MinioUtil minioUtil;
    private String signature="SIPC115";
    @Override
    public CommonResult<LoginRes> register(PostUserRegister postUserRegister) {
        LoginRes loginRes=new LoginRes();
        try{
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("account",postUserRegister.getAccount());
            if(this.baseMapper.exists(queryWrapper)){
                return CommonResult.fail("已存在该账号");
            }
            queryWrapper.clear();
            queryWrapper.eq("telephone",postUserRegister.getTelephone());
            if(this.baseMapper.exists(queryWrapper)){
                return CommonResult.fail("该手机号已注册");
            }


            String code=(String) redisUtil.get("verify_code::" + postUserRegister.getTelephone());

            if(!postUserRegister.getCode().equals(code)){
                return CommonResult.fail("验证码错误");
            }


            User user=new User();
            user.setAccount(SHA256Util.encrypt(postUserRegister.getAccount()+signature));
            user.setPassword( SHA256Util.encrypt(postUserRegister.getPassword()+signature));
            user.setTelephone( SHA256Util.encrypt(postUserRegister.getTelephone()+signature));
            this.save(user);

            loginRes.setToken(JwtUtils.sign(user));
        }catch (Exception e){
            log.error("用户注册失败");
            return CommonResult.fail("用户注册失败");
        }

        return CommonResult.success("注册成功",loginRes);
    }
    @Override
    public CommonResult<LoginRes> login(PostUserLogin postUserLogin) {
        LoginRes loginRes=new LoginRes();
        try{
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            if(postUserLogin.getAccount().length()<11){
                queryWrapper.eq("account", SHA256Util.encrypt(postUserLogin.getAccount()+signature));
            }else {
                queryWrapper.eq("telephone", SHA256Util.encrypt(postUserLogin.getAccount()+signature));
            }

            User user=userDao.selectOne(queryWrapper);
            if(user==null){
                return CommonResult.fail("账号或手机号不存在");
            }
            if(!user.getPassword().equals( SHA256Util.encrypt(postUserLogin.getPassword()+signature))){
                return CommonResult.fail("密码错误");
            }

            loginRes.setToken(JwtUtils.sign(user));
        }catch (Exception e){
            log.error("用户登录失败");
            return CommonResult.fail("用户登录失败");
        }



        return CommonResult.success("登陆成功",loginRes);
    }

    @Override
    public CommonResult<BlankRes> updateUserInfo(PutUser putUser) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        try{
            User user=new User();
            user.setId(userId);
            user.setNickname(putUser.getNickname());
            user.setAvatar(putUser.getAvatar());
            user.setSex(putUser.getSex());
            user.setAge(putUser.getAge());
            user.setHeight(putUser.getHeight());
            user.setWeight(putUser.getWeight());
            user.setIdentity(putUser.getIdentity());
            user.setDisease(putUser.getDisease());
            user.setSignature(putUser.getSignature());
            this.updateById(user);
        }catch (Exception e){
            log.error("更新用户信息失败");
            return CommonResult.fail("更新用户信息失败");
        }
        return CommonResult.success("更新用户信息成功");
    }

    @Override
    public CommonResult<UserRes> getOnesUserInfo(Integer userGetId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        UserRes userRes;
        try{
            userRes=getUserResById(userGetId);
        }catch (Exception e){
            log.error("查找指定用户信息失败");
            return CommonResult.fail("查找指定用户信息失败");
        }
        return CommonResult.success("查找指定用户信息成功",userRes);
    }

    @Override
    public CommonResult<UserRes> getMyUserInfo() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        UserRes userRes;
        try{
            userRes=getUserResById(userId);
        }catch (Exception e){
            log.error("查找我的用户信息失败");
            return CommonResult.fail("查找我的用户信息失败");
        }
        return CommonResult.success("查找我的用户信息成功",userRes);
    }

    @Override
    public CommonResult<BlankRes> updateUserStatus(String status) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            User user=new User();
            user.setId(userId);
            user.setStatus(status);
            this.updateById(user);
        }catch (Exception e){
            log.error("设置状态失败");
            return CommonResult.fail("设置状态失败");
        }
        return CommonResult.success("设置状态成功");
    }

    @Override
    public CommonResult<BlankRes> updateUserSignature(String signature) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            User user=new User();
            user.setId(userId);
            user.setSignature(signature);
            this.updateById(user);
        }catch (Exception e){
            log.error("更新用户信息失败");
            return CommonResult.fail("更新用户信息失败");
        }
        return CommonResult.success("更新用户信息成功");
    }

    @Override
    public CommonResult<BlankRes> getCode(String telephone) {
        try {
            QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
            userQueryWrapper.eq("telephone",telephone);
            if (userDao.exists(userQueryWrapper)){
                return CommonResult.fail("该手机号已注册");
            }
            String code = StringUtils.generateRandomCode(6);
            redisUtil.set("verify_code::" + telephone, code, Long.valueOf(1), TimeUnit.MINUTES);

            String mobile=telephone;

            String host = "https://gyytz.market.alicloudapi.com";
            String path = "/sms/smsSend";
            String method = "POST";
            String appcode = "214f0769dc774ced8c0708d369dfc960";
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", mobile);
            querys.put("param", "**code**:"+code+",**minute**:5");
            querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
            querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
            Map<String, String> bodys = new HashMap<String, String>();

            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(host + path);
            // 设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
            // 设置请求参数
            for (Map.Entry<String, String> entry : querys.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.fail("发送验证码失败");
        }
        return CommonResult.success("发送验证码成功");
    }

    @Override
    public CommonResult<BlankRes> addWish(String content) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        WishInfo wishInfo=new WishInfo();
        wishInfo.setUserId(userId);
        wishInfo.setContent(content);
        if (wishInfoDao.insert(wishInfo)==0){
            return CommonResult.fail("插入失败");
        }
        return CommonResult.success("添加心愿成功");
    }

    @Override
    public CommonResult<WishResponse> getWish(Integer pageNum, Integer pageSize) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        WishResponse wishResponse=new WishResponse();
        Page<WishInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<WishInfo> wishInfoQueryWrapper = new QueryWrapper<>();
        wishInfoQueryWrapper.eq("user_id",userId);
        IPage<WishInfo> wishInfoIPage=wishInfoDao.selectPage(page,wishInfoQueryWrapper);
        List<String> wishList=new ArrayList<>();
        for(WishInfo wishInfo:wishInfoIPage.getRecords()){
            wishList.add(wishInfo.getContent());
        }
        wishResponse.setContents(wishList);
        return CommonResult.success(wishResponse);
    }

    @Override
    public String getAvatar(Integer userId) {
        User user=this.getById(userId);
        return minioUtil.downloadFile(user.getAvatar());
    }

    @Override
    public String getNickName(Integer userId) {
        User user=this.getById(userId);
        return user.getNickname();
    }

    public UserRes getUserResById(Integer userId){
        UserRes userRes;
        try {
            User user=this.getById(userId);
            userRes=new UserRes(user);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return userRes;
    }
}
