package service;

import model.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Generates realistic sample data for the MetroMind platform.
 * Features a real capital-region transit network (Vijayawada - Guntur - Amaravati corridor)
 * with complete stations, routes, vehicles, services, passenger events, and tickets.
 */
public class SampleDataGenerator {

    public static void loadSampleData(StationService stationService, RouteService routeService,
                                      VehicleService vehicleService, TransportServiceService serviceService,
                                      PassengerService passengerService) {
        // Clear existing data
        stationService.clear();
        routeService.clear();
        vehicleService.clear();
        serviceService.clear();
        passengerService.clear();

        // 1. Create Stations
        Station s1 = new Station("ST001", "Vijayawada Central", "Vijayawada", "CENTRAL_HUB",
                Arrays.asList("ST002", "ST003", "ST006"), 12500, "24x7 Transit Hub with Multi-modal Interchange");
        Station s2 = new Station("ST002", "Guntur Terminal", "Guntur", "TERMINAL",
                Arrays.asList("ST001", "ST004", "ST005"), 9800, "Major Intercity Bus & Metro Terminal");
        Station s3 = new Station("ST003", "Mangalagiri Smart Station", "Mangalagiri", "TRANSIT_HUB",
                Arrays.asList("ST001", "ST002", "ST004"), 4500, "Smart AI-monitored Junction");
        Station s4 = new Station("ST004", "Amaravati Secretariat", "Amaravati", "GOV_HUB",
                Arrays.asList("ST002", "ST003"), 7200, "Capital Government Complex Terminal");
        Station s5 = new Station("ST005", "Tenali Junction", "Tenali", "SUBURBAN",
                Arrays.asList("ST002"), 3400, "Suburban Rail & Feeder Bus Station");
        Station s6 = new Station("ST006", "Gannavaram Airport", "Gannavaram", "AIRPORT_LINK",
                Arrays.asList("ST001"), 2900, "Airport Metro Station with Automated Baggage Check-in");

        stationService.addStation(s1);
        stationService.addStation(s2);
        stationService.addStation(s3);
        stationService.addStation(s4);
        stationService.addStation(s5);
        stationService.addStation(s6);

        // 2. Create Routes
        Route r1 = new Route("RT001", "Capital Metro Express", "ST001", "ST004",
                Collections.singletonList("ST003"), 34.5, 45, 50.0,
                "Capital Metro Express connects Vijayawada Central to Amaravati Secretariat via Mangalagiri Smart Station. High frequency electric rapid transit.", "ACTIVE");

        Route r2 = new Route("RT002", "Krishna Delta Rapid Corridor", "ST001", "ST002",
                Collections.singletonList("ST003"), 38.0, 50, 60.0,
                "Krishna Delta Rapid Corridor provides intercity transit between Vijayawada and Guntur. Potential passenger overcrowding during evening hours.", "ACTIVE");

        Route r3 = new Route("RT003", "Amaravati-Guntur Connector", "ST004", "ST002",
                Collections.emptyList(), 28.5, 35, 40.0,
                "Direct express transit between Amaravati Secretariat and Guntur Terminal. Service disruption warning during monsoon road repair.", "ACTIVE");

        Route r4 = new Route("RT004", "Tenali Suburban Shuttle", "ST002", "ST005",
                Collections.emptyList(), 25.0, 30, 25.0,
                "Tenali Suburban feeder shuttle connecting Guntur Terminal with Tenali Junction. High reliability timetable.", "ACTIVE");

        Route r5 = new Route("RT005", "Airport Metro Express", "ST001", "ST006",
                Collections.emptyList(), 21.0, 25, 45.0,
                "Dedicated airport link transit from Vijayawada Central to Gannavaram Airport with automated luggage transit.", "ACTIVE");

        routeService.addRoute(r1, stationService);
        routeService.addRoute(r2, stationService);
        routeService.addRoute(r3, stationService);
        routeService.addRoute(r4, stationService);
        routeService.addRoute(r5, stationService);

        // 3. Create Vehicles
        Vehicle v1 = new Vehicle("VH001", "Metro Train Alpha 01", "METRO_TRAIN", 300, "NONE", "AVAILABLE");
        Vehicle v2 = new Vehicle("VH002", "Metro Train Alpha 02", "METRO_TRAIN", 300, "NONE", "AVAILABLE");
        Vehicle v3 = new Vehicle("VH003", "Electric Bus Beta 101", "ELECTRIC_BUS", 65, "NONE", "AVAILABLE");
        Vehicle v4 = new Vehicle("VH004", "Electric Bus Beta 102", "ELECTRIC_BUS", 65, "NONE", "AVAILABLE");
        Vehicle v5 = new Vehicle("VH005", "Feeder Van Gamma 201", "FEEDER_VAN", 25, "NONE", "AVAILABLE");
        Vehicle v6 = new Vehicle("VH006", "Airport Shuttle 301", "ELECTRIC_BUS", 50, "NONE", "AVAILABLE");

        vehicleService.addVehicle(v1);
        vehicleService.addVehicle(v2);
        vehicleService.addVehicle(v3);
        vehicleService.addVehicle(v4);
        vehicleService.addVehicle(v5);
        vehicleService.addVehicle(v6);

        // 4. Create Transport Services
        TransportService sv1 = new TransportService("SV001", "CAP-01", "RT001", "07:00", "07:45", 250, 240, "UNASSIGNED", "SCHEDULED");
        TransportService sv2 = new TransportService("SV002", "DEL-02", "RT002", "07:30", "08:20", 250, 210, "UNASSIGNED", "SCHEDULED");
        TransportService sv3 = new TransportService("SV003", "SEC-03", "RT003", "08:00", "08:35", 50, 48, "UNASSIGNED", "SCHEDULED");
        TransportService sv4 = new TransportService("SV004", "TEN-04", "RT004", "08:15", "08:45", 20, 18, "UNASSIGNED", "SCHEDULED");
        TransportService sv5 = new TransportService("SV005", "AIR-05", "RT005", "08:45", "09:10", 45, 42, "UNASSIGNED", "SCHEDULED");

        serviceService.addService(sv1, routeService);
        serviceService.addService(sv2, routeService);
        serviceService.addService(sv3, routeService);
        serviceService.addService(sv4, routeService);
        serviceService.addService(sv5, routeService);

        // 5. Create Passenger Events (Streaming and parallel analytics)
        int[] sampleCounts = {120, 85, 210, 45, 160, 95, 30, 180, 240, 110, 75, 130, 195, 88, 150, 220};
        String[] sampleTypes = {"BOARDING", "ALIGHTING", "BOARDING", "BOARDING", "ALIGHTING", "BOARDING", "ALIGHTING", "BOARDING",
                                "BOARDING", "ALIGHTING", "BOARDING", "BOARDING", "ALIGHTING", "BOARDING", "ALIGHTING", "BOARDING"};
        String[] stations = {"ST001", "ST002", "ST003", "ST004", "ST005", "ST006", "ST001", "ST002", "ST003", "ST004", "ST001", "ST002", "ST003", "ST004", "ST005", "ST006"};

        for (int i = 0; i < sampleCounts.length; i++) {
            String eventId = String.format("EV%03d", i + 1);
            String serviceId = String.format("SV%03d", (i % 5) + 1);
            String time = String.format("%02d:%02d:00", 7 + (i / 4), (i * 15) % 60);
            PassengerEvent ev = new PassengerEvent(eventId, serviceId, stations[i], time, sampleTypes[i], sampleCounts[i]);
            passengerService.addPassengerEvent(ev);
        }

        // 6. Create Tickets
        passengerService.addTicket(new Ticket("TK101", "Aarav Sharma", "SV001", "ST001", "ST004", 50.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK102", "Priya Varma", "SV001", "ST001", "ST003", 25.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK103", "Rajesh Rao", "SV002", "ST001", "ST002", 60.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK104", "Sneha Reddy", "SV002", "ST003", "ST002", 35.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK105", "Kiran Kumar", "SV003", "ST004", "ST002", 40.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK106", "Deepa Nair", "SV004", "ST002", "ST005", 25.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK107", "Vikram Patel", "SV005", "ST001", "ST006", 45.0, "CONFIRMED"));
        passengerService.addTicket(new Ticket("TK108", "Ananya Das", "SV005", "ST001", "ST006", 45.0, "CONFIRMED"));
    }
}
