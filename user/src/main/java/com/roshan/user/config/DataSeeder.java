package com.roshan.user.config;

import com.github.javafaker.Faker;
import com.roshan.user.model.Preference;
import com.roshan.user.model.User;
import com.roshan.user.model.Zipcode;
import com.roshan.user.repository.PreferenceRepository;
import com.roshan.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        // Only seed if the database is empty
        if (userRepository.count() == 0) {
            seedUsers();
        }
    }

    private void seedUsers() {
        List<User> users = new ArrayList<>();
        
        // Create 100 users
        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .email(faker.internet().emailAddress())
                    .phone(faker.phoneNumber().cellPhone())
                    .username(faker.name().username())
                    .password(passwordEncoder.encode("password123"))
                    .build();
            users.add(user);
        }
        
        // Save all users
        users = userRepository.saveAll(users);
        
        // Create 1000 preferences (10 preferences per user on average)
        List<Preference> preferences = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User randomUser = users.get(faker.random().nextInt(users.size()));
            
            // Generate 1-5 random zipcodes
            List<Zipcode> zipcodes = new ArrayList<>();
            int numZipcodes = faker.random().nextInt(1, 6);
            for (int j = 0; j < numZipcodes; j++) {
                Zipcode zipcode = new Zipcode(faker.address().zipCode());
                zipcodes.add(zipcode);
            }
            
            Preference preference = Preference.builder()
                    .id(UUID.randomUUID())
                    .minPrice(faker.random().nextInt(100000, 500000))
                    .maxPrice(faker.random().nextInt(500001, 2000000))
                    .beds(faker.random().nextInt(1, 7))
                    .baths(faker.random().nextInt(1, 6))
                    .minArea(faker.random().nextDouble() * 5000 + 1000)
                    .type(faker.options().option("CONDO", "MANUFACTURED", "MULTI_FAMILY", "TOWNHOUSE", "SINGLE_FAMILY", "LOT"))
                    .city(faker.address().city())
                    .state("CA")
                    .user(randomUser)
                    .zipcodes(zipcodes)
                    .build();
            
            // Set the preference reference in zipcodes
            zipcodes.forEach(zipcode -> zipcode.setPreference(preference));
            
            preferences.add(preference);
        }
        
        // Save all preferences
        preferenceRepository.saveAll(preferences);
        
        System.out.println("Data seeding completed:");
        System.out.println("- Created " + users.size() + " users");
        System.out.println("- Created " + preferences.size() + " preferences");
    }
} 