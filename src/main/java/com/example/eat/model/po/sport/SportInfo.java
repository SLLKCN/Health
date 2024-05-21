package com.example.eat.model.po.sport;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sport_info")
public class SportInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String introduce;
    private String type;
    private String image;
    private Integer intensity;
}
