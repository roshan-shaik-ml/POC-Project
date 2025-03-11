package com.roshan.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PreferenceRequestDTO {

    @NotNull
    private int minPrice;
    private int maxPrice;
    private String city;
    private String state;
    private int beds;
    private int baths;
    private double minArea;
    private String type;
    private List<String> zipcodes;
}
