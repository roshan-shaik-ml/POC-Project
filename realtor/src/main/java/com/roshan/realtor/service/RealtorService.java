package com.roshan.realtor.service;

import com.roshan.realtor.model.House;

import java.util.List;

public interface RealtorService {

    List<House> getAllHousesByRealtorName(String name);
}
