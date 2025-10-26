package com.matchme.controller;

import com.matchme.dto.LocationDto;
import com.matchme.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<LocationDto> search(@RequestParam String query) {
        return locationService.search(query);
    }
}
