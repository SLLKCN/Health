package com.example.eat.model.po.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("expert_info")
public class ExpertInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String post;
    private String initial;
}
