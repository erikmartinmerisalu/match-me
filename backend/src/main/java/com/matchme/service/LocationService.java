package com.matchme.service;

import com.matchme.entity.Location;
import com.matchme.dto.LocationDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final List<Location> locations = new ArrayList<>();

    @PostConstruct
    public void loadCsv() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getResourceAsStream("/World_Cities_Location_table.csv"))
                ))) {

            locations.addAll(reader.lines()
                    .map(line -> line.replace("\"", ""))
                    .map(line -> line.split(";"))
                    .filter(parts -> parts.length >= 6)
                    .map(parts -> new Location(
                            Long.parseLong(parts[0]),
                            parts[1],
                            parts[2],
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            Double.parseDouble(parts[5])
                    ))
                    .collect(Collectors.toList()));

            System.out.println("Location CSV Loaded " + locations.size() + " locations from CSV");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locations csv file", e);
        }
    }

    public List<LocationDto> search(String query) {
        if (query == null || query.isBlank()) return Collections.emptyList();

        String q = query.toLowerCase();

        return locations.stream()
                .filter(loc -> loc.getCountry().toLowerCase().contains(q)
                        || loc.getCity().toLowerCase().contains(q))
                .limit(5)
                .map(loc -> new LocationDto(
                        loc.getId(),
                        loc.getCountry(),
                        loc.getCity(),
                        loc.getLatitude(),
                        loc.getLongitude(),
                        loc.getElevation()
                ))
                .collect(Collectors.toList());
    }
}
