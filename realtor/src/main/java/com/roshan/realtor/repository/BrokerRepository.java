package com.roshan.realtor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.roshan.realtor.model.Broker;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrokerRepository extends JpaRepository<Broker, UUID> {

}
