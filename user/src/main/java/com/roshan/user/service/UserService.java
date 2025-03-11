package com.roshan.user.service;

import com.roshan.user.dto.AuthResponse;
import com.roshan.user.dto.LoginRequestDTO;
import com.roshan.user.dto.PreferenceRequestDTO;
import com.roshan.user.dto.UserDTO;
import com.roshan.user.model.Preference;
import com.roshan.user.model.Zipcode;
import com.roshan.user.repository.PreferenceRepository;
import com.roshan.user.repository.UserRepository;
import com.roshan.user.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.roshan.user.model.User;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // Password encoder
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthResponse signUp(UserDTO userDTO) {
        // Check if user already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already in use!");
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use!");
        }


        // Create a new user
        User newUser = new User(
                UUID.randomUUID(),  // Generate unique ID
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                null,  // Phone (optional)
                userDTO.getUsername(),
                encoder.encode(userDTO.getPassword()), // Hash password
                null  // Address (optional)
        );

        // Save to DB
        newUser = userRepository.save(newUser);

        // Generate JWT token for immediate login after signup (optional)
        String jwtToken = jwtUtil.generateToken(newUser);

        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequestDTO loginRequestDTO) throws UsernameNotFoundException {
        // Fetch user by email or username
        Optional<User> userOptional = userRepository.findByEmail(loginRequestDTO.getIdentifier())
                .or(() -> userRepository.findByUsername(loginRequestDTO.getIdentifier()));

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials!");
        }

        User user = userOptional.get();

        // Validate password
        if (!encoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            log.error("Invalid credentials for user: {}", user);
            throw new RuntimeException("Invalid credentials!");
        }

        return new AuthResponse(jwtUtil.generateToken(user));
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // No authorities assigned
        );
    }

    public void addPreference(PreferenceRequestDTO preferenceRequestDTO) {

        // Extract the username from the security context
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // Retrieve the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));


        List<Zipcode> zipcodes = preferenceRequestDTO.getZipcodes().stream().map(Zipcode::new).toList();
        Preference preference = Preference.builder()
                .beds(preferenceRequestDTO.getBeds())
                .baths(preferenceRequestDTO.getBaths())
                .minArea(preferenceRequestDTO.getMinArea())
                .minPrice(preferenceRequestDTO.getMinPrice())
                .maxPrice(preferenceRequestDTO.getMaxPrice())
                .type(preferenceRequestDTO.getType())
                .city(preferenceRequestDTO.getCity())
                .state(preferenceRequestDTO.getState())
                .zipcodes(zipcodes)
                .user(user)
                .build();
        log.info("Preference created successfully");
        // Save to DB
        preferenceRepository.save(preference);
        log.info("Preference added successfully");
        return;
    }
}
