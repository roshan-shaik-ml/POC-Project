package com.roshan.realtor.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "broker", uniqueConstraints = @UniqueConstraint(columnNames = "name", name = "broker_name_unique"))
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Broker {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 255, nullable = false)
    private String name;

    @OneToMany(mappedBy = "broker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<House> houses;
}
