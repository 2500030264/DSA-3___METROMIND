package service;

import model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service managing CSV persistence for MetroMind.
 * Saves and loads stations, routes, vehicles, transport services,
 * passenger events, and tickets to local CSV files.
 */
public class DataStorageService {
    private final String dataDir;

    public DataStorageService(String dataDir) {
        this.dataDir = dataDir != null ? dataDir : "data";
        File dir = new File(this.dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public boolean saveData(StationService stationService, RouteService routeService,
                            VehicleService vehicleService, TransportServiceService serviceService,
                            PassengerService passengerService) {
        try {
            saveStations(stationService.getAllStations());
            saveRoutes(routeService.getAllRoutes());
            saveVehicles(vehicleService.getAllVehicles());
            saveServices(serviceService.getAllServices());
            saveEvents(passengerService.getAllEvents());
            saveTickets(passengerService.getAllTickets());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            return false;
        }
    }

    public boolean loadData(StationService stationService, RouteService routeService,
                            VehicleService vehicleService, TransportServiceService serviceService,
                            PassengerService passengerService) {
        try {
            File sFile = new File(dataDir, "stations.csv");
            if (!sFile.exists()) return false;

            stationService.clear();
            routeService.clear();
            vehicleService.clear();
            serviceService.clear();
            passengerService.clear();

            loadStations(stationService);
            loadRoutes(routeService, stationService);
            loadVehicles(vehicleService);
            loadServices(serviceService, routeService);
            loadEvents(passengerService);
            loadTickets(passengerService);
            return true;
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            return false;
        }
    }

    // --- Station CSV ---
    private void saveStations(List<Station> stations) throws IOException {
        File file = new File(dataDir, "stations.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("id,name,city,stationType,connectedStationIds,passengerCount,serviceInfo");
            for (Station s : stations) {
                String conns = String.join(";", s.getConnectedStationIds());
                pw.printf("%s,%s,%s,%s,%s,%d,%s%n",
                        escape(s.getId()), escape(s.getName()), escape(s.getCity()),
                        escape(s.getStationType()), escape(conns), s.getPassengerCount(), escape(s.getServiceInfo()));
            }
        }
    }

    private void loadStations(StationService service) throws IOException {
        File file = new File(dataDir, "stations.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    List<String> conns = new ArrayList<>();
                    if (!parts[4].trim().isEmpty()) {
                        conns.addAll(Arrays.asList(parts[4].split(";")));
                    }
                    int passengers = parseIntSafe(parts[5], 0);
                    Station st = new Station(parts[0], parts[1], parts[2], parts[3], conns, passengers, parts[6]);
                    service.addStation(st);
                }
            }
        }
    }

    // --- Route CSV ---
    private void saveRoutes(List<Route> routes) throws IOException {
        File file = new File(dataDir, "routes.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("id,name,sourceStationId,destinationStationId,intermediateStationIds,distanceKm,estimatedTimeMinutes,travelCost,description,status");
            for (Route r : routes) {
                String inter = String.join(";", r.getIntermediateStationIds());
                pw.printf("%s,%s,%s,%s,%s,%.2f,%d,%.2f,%s,%s%n",
                        escape(r.getId()), escape(r.getName()), escape(r.getSourceStationId()),
                        escape(r.getDestinationStationId()), escape(inter), r.getDistanceKm(),
                        r.getEstimatedTimeMinutes(), r.getTravelCost(), escape(r.getDescription()), escape(r.getStatus()));
            }
        }
    }

    private void loadRoutes(RouteService routeService, StationService stationService) throws IOException {
        File file = new File(dataDir, "routes.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 10) {
                    List<String> inter = new ArrayList<>();
                    if (!parts[4].trim().isEmpty()) {
                        inter.addAll(Arrays.asList(parts[4].split(";")));
                    }
                    double dist = parseDoubleSafe(parts[5], 10.0);
                    int time = parseIntSafe(parts[6], 15);
                    double cost = parseDoubleSafe(parts[7], 20.0);
                    Route r = new Route(parts[0], parts[1], parts[2], parts[3], inter, dist, time, cost, parts[8], parts[9]);
                    routeService.addRoute(r, stationService);
                }
            }
        }
    }

    // --- Vehicle CSV ---
    private void saveVehicles(List<Vehicle> vehicles) throws IOException {
        File file = new File(dataDir, "vehicles.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("id,name,vehicleType,capacity,assignedServiceId,status");
            for (Vehicle v : vehicles) {
                pw.printf("%s,%s,%s,%d,%s,%s%n",
                        escape(v.getId()), escape(v.getName()), escape(v.getVehicleType()),
                        v.getCapacity(), escape(v.getAssignedServiceId()), escape(v.getStatus()));
            }
        }
    }

    private void loadVehicles(VehicleService service) throws IOException {
        File file = new File(dataDir, "vehicles.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    int cap = parseIntSafe(parts[3], 50);
                    Vehicle v = new Vehicle(parts[0], parts[1], parts[2], cap, parts[4], parts[5]);
                    service.addVehicle(v);
                }
            }
        }
    }

    // --- Service CSV ---
    private void saveServices(List<TransportService> services) throws IOException {
        File file = new File(dataDir, "services.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("id,serviceCode,routeId,departureTime,arrivalTime,requiredCapacity,currentOccupancy,assignedVehicleId,status");
            for (TransportService s : services) {
                pw.printf("%s,%s,%s,%s,%s,%d,%d,%s,%s%n",
                        escape(s.getId()), escape(s.getServiceCode()), escape(s.getRouteId()),
                        escape(s.getDepartureTime()), escape(s.getArrivalTime()), s.getRequiredCapacity(),
                        s.getCurrentOccupancy(), escape(s.getAssignedVehicleId()), escape(s.getStatus()));
            }
        }
    }

    private void loadServices(TransportServiceService serviceService, RouteService routeService) throws IOException {
        File file = new File(dataDir, "services.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 9) {
                    int reqCap = parseIntSafe(parts[5], 50);
                    int occ = parseIntSafe(parts[6], 0);
                    TransportService s = new TransportService(parts[0], parts[1], parts[2], parts[3], parts[4], reqCap, occ, parts[7], parts[8]);
                    serviceService.addService(s, routeService);
                }
            }
        }
    }

    // --- Passenger Events CSV ---
    private void saveEvents(List<PassengerEvent> events) throws IOException {
        File file = new File(dataDir, "passenger_events.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("id,serviceId,stationId,timestamp,eventType,passengerCount");
            for (PassengerEvent e : events) {
                pw.printf("%s,%s,%s,%s,%s,%d%n",
                        escape(e.getId()), escape(e.getServiceId()), escape(e.getStationId()),
                        escape(e.getTimestamp()), escape(e.getEventType()), e.getPassengerCount());
            }
        }
    }

    private void loadEvents(PassengerService service) throws IOException {
        File file = new File(dataDir, "passenger_events.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    int count = parseIntSafe(parts[5], 0);
                    PassengerEvent ev = new PassengerEvent(parts[0], parts[1], parts[2], parts[3], parts[4], count);
                    service.addPassengerEvent(ev);
                }
            }
        }
    }

    // --- Ticket CSV ---
    private void saveTickets(List<Ticket> tickets) throws IOException {
        File file = new File(dataDir, "tickets.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("id,passengerRef,serviceId,sourceStationId,destinationStationId,fareAmount,status");
            for (Ticket t : tickets) {
                pw.printf("%s,%s,%s,%s,%s,%.2f,%s%n",
                        escape(t.getId()), escape(t.getPassengerRef()), escape(t.getServiceId()),
                        escape(t.getSourceStationId()), escape(t.getDestinationStationId()),
                        t.getFareAmount(), escape(t.getStatus()));
            }
        }
    }

    private void loadTickets(PassengerService service) throws IOException {
        File file = new File(dataDir, "tickets.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    double fare = parseDoubleSafe(parts[5], 20.0);
                    Ticket t = new Ticket(parts[0], parts[1], parts[2], parts[3], parts[4], fare, parts[6]);
                    service.addTicket(t);
                }
            }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", ";");
    }

    private int parseIntSafe(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private double parseDoubleSafe(String s, double def) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public boolean hasExistingData() {
        File f = new File(dataDir, "stations.csv");
        return f.exists() && f.length() > 0;
    }
}
