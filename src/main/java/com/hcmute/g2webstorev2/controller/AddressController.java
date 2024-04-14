package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AddressRequest;
import com.hcmute.g2webstorev2.dto.response.AddressResponse;
import com.hcmute.g2webstorev2.service.AddressService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AddressResponse>> getAddressesByCustomer() {
        return ResponseEntity.ok(addressService.getAddressesByCustomer());
    }

    @PutMapping("/me/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> updateAddressByCustomer(
            @RequestBody @Valid AddressRequest body,
            @PathVariable("addressId")
            @NotNull(message = "Address ID cannot be null")
            @Min(value = 1, message = "Address ID must be equals or greater than 1") Integer id) {
        addressService.updateAddressByCustomer(body, id);
        return ResponseEntity.ok("Address updated successfully");
    }

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressResponse> addAddressByCustomer(@Valid @RequestBody AddressRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.addAddressByCustomer(body));
    }

    @DeleteMapping("/me/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> delAddressByCustomer(
            @PathVariable("addressId")
            @NotNull(message = "Address ID cannot be null")
            @Min(value = 1, message = "Address ID must be equals or greater than 1") Integer id) {
        addressService.delAddressByCustomer(id);
        return ResponseEntity.ok("Address with ID = " + id + " deleted successfully");
    }
}
