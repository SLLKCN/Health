package com.example.eat.model.dto.res.question;

import lombok.Data;

import java.util.List;

@Data
public class ExpertResponser {
    private Integer id;
    private String name;
    private String post;
    private List<String> expertise;
}
