package com.roshan.realtor.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseResponseDTO {

    private String price;
    private String status;
    private String type;
    private String size;
    private double area;
    private int beds;
    private int baths;

    private String imageUrl;

    private String street;
    private String city;
    private String state;
    private String zip;

    private String brokerName;
}
