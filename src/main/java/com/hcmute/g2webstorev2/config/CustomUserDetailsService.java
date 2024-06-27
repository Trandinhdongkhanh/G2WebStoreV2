package com.hcmute.g2webstorev2.config;

import com.hcmute.g2webstorev2.repository.AdminRepo;
import com.hcmute.g2webstorev2.repository.CustomerRepo;
import com.hcmute.g2webstorev2.repository.SellerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.hcmute.g2webstorev2.enums.AppRole.*;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepo customerRepo;
    private final AdminRepo adminRepo;
    private final SellerRepo sellerRepo;

    @Override
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {
        //For example: "ADMIN:userA" will be split into 2 parts "ADMIN" and "userA"
        if (value.contains(":")) {
            String[] result = value.split(":");
            String role = result[0];
            String email = result[1];

            if (role.equals(CUSTOMER.name())) {
                return customerRepo.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));
            }
            if (role.equals(ADMIN.name())) {
                return adminRepo.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));
            }
            return sellerRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));
        }
        throw new IllegalStateException("Error in CustomUserDetailsService class: value must contain ':' prefix");
    }
}
