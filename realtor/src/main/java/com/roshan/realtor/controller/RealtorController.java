package com.roshan.realtor.controller;

import com.roshan.realtor.dto.HouseDTO;
import com.roshan.realtor.dto.HouseResponseDTO;
import com.roshan.realtor.model.House;
import com.roshan.realtor.service.RealtorServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/realtors")
public class RealtorController {

    @Autowired
    RealtorServiceImpl houseService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/houses")
    public ResponseEntity<List<HouseResponseDTO>> getAllHouses(@RequestBody HouseDTO houseDTO) {

        List<House> houses;

        houses = houseService.getAllHousesByRealtorName(houseDTO.getName());
        List<HouseResponseDTO> houseResponseDtoList = houses.stream().map(h -> modelMapper.map(h, HouseResponseDTO.class)).toList();
        return new ResponseEntity<>(houseResponseDtoList, HttpStatus.OK);
    }
}
