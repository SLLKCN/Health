package com.example.eat.model.dto.res.sport;

import lombok.Data;

import java.util.List;

@Data
public class SportsResponse {
    private List<SportResponse> sportList;
}
