package com.roshan.realtor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "street", length = 100)
    private String street;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "zipcode")
    @Size(min=5, max=5, message = "ZIP code must only contain 5 characters")
    @Pattern(regexp = "\\d{5}", message = "ZIP code must contain only digits")
    private String zipcode;

    @Column(precision = 10, scale = 6, nullable = true)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6, nullable = true)
    private BigDecimal longitude;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", foreignKey = @ForeignKey(name = "addresses_house_id_fkey"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private House house;
}
