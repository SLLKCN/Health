package com.example.eat.dao.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.dto.res.question.ExpertResponser;
import com.example.eat.model.po.question.ExpertInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExpertInfoDao extends BaseMapper<ExpertInfo> {

    List<ExpertResponser> getExperts();
}
