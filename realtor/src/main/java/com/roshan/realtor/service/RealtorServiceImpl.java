package com.roshan.realtor.service;

import com.roshan.realtor.model.House;
import com.roshan.realtor.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealtorServiceImpl implements RealtorService{

    @Autowired
    private HouseRepository houseRepository;

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    @Override
    public List<House> getAllHousesByRealtorName(String name) {
        return houseRepository.findAllByBrokerName(name);
    }
}
