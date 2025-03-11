package com.roshan.realtor.repository;

import com.roshan.realtor.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HouseRepository extends JpaRepository<House, UUID> {

    @Query(
            value = "SELECT * FROM house h WHERE h.broker_id = (select id from broker where name = :brokerName)",
            nativeQuery = true
    )
    List<House> findAllByBrokerName(@Param("brokerName") String brokerName);
}
