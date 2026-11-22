package io.github.yogeshsthapak.hotelbooking.api.controller;

import io.github.yogeshsthapak.hotelbooking.api.dto.CreateOwnerRequest;
import io.github.yogeshsthapak.hotelbooking.api.dto.PropertyResponse;
import io.github.yogeshsthapak.hotelbooking.domain.model.Owner;
import io.github.yogeshsthapak.hotelbooking.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final PropertyService propertyService;

    public OwnerController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Owner registerOwner(@Valid @RequestBody CreateOwnerRequest request) {
        return propertyService.registerOwner(request.name(), request.email());
    }

    @GetMapping("/{ownerId}")
    public Owner getOwner(@PathVariable String ownerId) {
        return propertyService.getOwner(ownerId);
    }

    @GetMapping("/{ownerId}/properties")
    public List<PropertyResponse> getOwnerProperties(@PathVariable String ownerId) {
        return propertyService.getPropertiesByOwner(ownerId).stream()
                .map(PropertyResponse::from)
                .toList();
    }
}
