package com.roshan.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Table(name = "preference")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Preference {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "min_price")
    private int minPrice;

    @Column(name = "max_price")
    private int maxPrice;

    @Column(name = "beds")
    private int beds;

    @Column(name = "baths")
    private int baths;

    @Column(name = "min_area")
    private double minArea;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "preference_user_id_fkey"))
    private User user;

    @Column(name = "zipcodes")
    @OneToMany(mappedBy = "preference", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zipcode> zipcodes;

}
