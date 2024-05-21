package com.example.eat.model.po.wish;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wish_info")
public class WishInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String content;
}
