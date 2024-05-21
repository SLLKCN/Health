package com.example.eat.model.po.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("expertise_info")
public class ExpertiseInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer expertId;
    private String expertise;
}
