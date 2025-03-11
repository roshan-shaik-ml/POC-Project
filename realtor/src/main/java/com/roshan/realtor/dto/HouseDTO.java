package com.roshan.realtor.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Getter
@Setter
public class HouseDTO {

    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String name;
}
