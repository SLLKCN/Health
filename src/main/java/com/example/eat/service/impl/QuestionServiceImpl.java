package com.example.eat.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.component.XfXhStreamClient;
import com.example.eat.config.XfXhConfig;
import com.example.eat.dao.question.ExpertInfoDao;
import com.example.eat.dao.question.ExpertiseInfoDao;
import com.example.eat.dao.question.QuestionInfoDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.question.MsgDTO;
import com.example.eat.model.dto.param.question.QuestionDto;
import com.example.eat.model.dto.res.question.ExpertResponser;
import com.example.eat.model.dto.res.question.QuestionRes;
import com.example.eat.model.dto.res.question.QuestionsGetRes;
import com.example.eat.model.po.question.ExpertInfo;
import com.example.eat.model.po.question.QuestionInfo;
import com.example.eat.service.QuestionService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import com.example.eat.util.XfXhWebSocketListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionInfoDao, QuestionInfo> implements QuestionService {

    @Resource
    private XfXhStreamClient xfXhStreamClient;

    @Resource
    private XfXhConfig xfXhConfig;

    @Autowired
    private ExpertInfoDao expertInfoDao;

    @Autowired
    private ExpertiseInfoDao expertiseInfoDao;
    @Autowired
    private QuestionInfoDao questionInfoDao;


    @Override
    public CommonResult<QuestionRes> sentQuestion(QuestionDto questionDto) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        String question="";
        if(questionDto.getExpert()==null){
            questionDto.setExpert(10);
        }else {
            try{
                ExpertInfo expertInfo=expertInfoDao.selectById(questionDto.getExpert());
                question+=expertInfo.getInitial()+"\n你将扮演这个角色，并按照对应的知识回答我的问题，不需要多余话语，请直接回答我接下来的问题:\n";

            }catch (Exception e){
                e.printStackTrace();
                return CommonResult.fail("未找到该顾问");
            }
        }
        question+=questionDto.getQuestion();
        QuestionRes questionRes=new QuestionRes();

        try {
            //发送问题，接收答案
            String answer = "";
            if(!checkQuestion(questionDto.getQuestion())){
                answer=getAnswer(questionDto.getQuestion());
            }else {



                // 如果是无效字符串，则不对大模型进行请求
                if (StrUtil.isBlank(questionDto.getQuestion())) {
                    return CommonResult.fail("无效问题，请重新输入");
                }
                // 获取连接令牌
                if (!xfXhStreamClient.operateToken(XfXhStreamClient.GET_TOKEN_STATUS)) {
                    return CommonResult.fail("当前大模型连接数过多，请稍后再试");
                }

                // 创建消息对象
                MsgDTO msgDTO = MsgDTO.createUserMsg(question);
                // 创建监听器
                XfXhWebSocketListener listener = new XfXhWebSocketListener();
                // 发送问题给大模型，生成 websocket 连接
                WebSocket webSocket = xfXhStreamClient.sendMsg(UUID.randomUUID().toString().substring(0, 10), Collections.singletonList(msgDTO), listener);
                if (webSocket == null) {
                    // 归还令牌
                    xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
                    return CommonResult.fail("系统内部错误，请联系管理员");
                }
                try {
                    int count = 0;
                    // 为了避免死循环，设置循环次数来定义超时时长
                    int maxCount = xfXhConfig.getMaxResponseTime() * 5;
                    while (count <= maxCount) {
                        Thread.sleep(200);
                        if (listener.isWsCloseFlag()) {
                            break;
                        }
                        count++;
                    }
                    if (count > maxCount) {
                        return CommonResult.fail("大模型响应超时，请联系管理员");
                    }
                    // 响应大模型的答案
                    answer = listener.getAnswer().toString();


                } catch (InterruptedException e) {
                    log.error("错误：" + e.getMessage());
                    return CommonResult.fail("系统内部错误，请联系管理员");
                } finally {
                    // 关闭 websocket 连接
                    webSocket.close(1000, "");
                    // 归还令牌
                    xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
                }

            }

            if(answer.contains("讯飞星火")){
                answer="这个问题我还没学会，问我一些关于健康的问题吧";
            }


            QuestionInfo questionInfo=new QuestionInfo();
            questionInfo.setUserId(userId);
            questionInfo.setQuestion(questionDto.getQuestion());
            questionInfo.setAnswer(answer);
            questionInfo.setExpertId(questionDto.getExpert());
            this.save(questionInfo);

            questionRes.setUserId(userId);
            questionRes.setQuestion(questionDto.getQuestion());
            questionRes.setAnswer(answer);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("询问失败");
        }
        return CommonResult.success("询问成功",questionRes);
    }

    @Override
    public CommonResult<QuestionsGetRes> getQuestionHistory(Integer pageNum, Integer pageSize,Integer expert) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        if(expert==null){
            expert=10;
        }
        QuestionsGetRes questionsGetRes;
        try{
            Page<QuestionInfo> questionInfoPage=new Page<>(pageNum,pageSize);
            QueryWrapper<QuestionInfo> questionInfoQueryWrapper=new QueryWrapper<>();
            questionInfoQueryWrapper.eq("user_id",userId);
            questionInfoQueryWrapper.eq("expert_id",expert);
            questionInfoQueryWrapper.orderByDesc("time");
            IPage<QuestionInfo> questionInfoIPage=page(questionInfoPage,questionInfoQueryWrapper);
            questionsGetRes=new QuestionsGetRes(questionInfoIPage.getRecords());
            questionsGetRes.setTotal(questionInfoIPage.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查找历史问题失败");
        }
        return CommonResult.success("查找历史问题成功",questionsGetRes);
    }

    @Override
    public CommonResult<List<ExpertResponser>> getExpert() {
        List<ExpertResponser> expertResponserList=null;
        try{
            expertResponserList=expertInfoDao.getExperts();
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取助手失败");
        }


        return CommonResult.success(expertResponserList);
    }

    private Boolean checkQuestion(String question){
        if(question.equals("你好")){
            return false;
        }
        if(question.equals("你是谁")){
            return false;
        }
        if(question.equals("你是什么")){
            return false;
        }
        if(question.equals("你是什么东西")){
            return false;
        }
        if(question.equals("介绍一下自己")){
            return false;
        }
        return true;
    }
    private String getAnswer(String question){
        if(question.equals("你好")){
            return "你好，我是你的健康顾问，帮你解答关于健康的问题";
        }
        if(question.equals("你是谁")){
            return "我是你的健康顾问，帮你解答关于健康的问题";
        }
        if(question.equals("你是什么")){
            return "我是你的健康顾问，帮你解答关于健康的问题";
        }
        if(question.equals("你是什么东西")){
            return "我是你的健康顾问，帮你解答关于健康的问题";
        }
        if(question.equals("介绍一下自己")){
            return "我是你的健康顾问，帮你解答关于健康的问题";
        }
        return "";
    }
}
