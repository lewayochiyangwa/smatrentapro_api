package com.smatpro.api.Property;

import com.smatpro.api.Helpers.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RestController
//@RequestMapping("/api/v1")

@RequiredArgsConstructor
@Slf4j  // to have something printed on the console
public class PropertyController {
    @Autowired
    PropertyRepo propertyRepo;
    PropertyController(PropertyRepo propertyRepo) {
        this.propertyRepo = propertyRepo;
    }
    @PostMapping("/property")
    @PreAuthorize("hasAuthority('admin:create')")
    public ApiResponse saveServer(@RequestBody @Valid Property property)throws Exception{

        property.setName(property.getName());
        property.setAddress(property.getAddress());
        propertyRepo.save(property);
        return ApiResponse.builder().status("success").code(200).message("Property saved successfully").data(property).build();
    }

    @GetMapping("/property")
    public ApiResponse getProperties() throws Exception {
        System.out.println("Fetching properties");

        Optional<List<Property>> propertiesOptional = Optional.ofNullable(propertyRepo.findAll());

        return ApiResponse.builder()
                .status("success")
                .code(200)
                .message("Properties fetched successfully")
                .data(propertiesOptional.orElse(Collections.emptyList()))
                .build();
    }
}
