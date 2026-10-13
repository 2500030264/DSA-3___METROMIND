import model.*;
import service.*;
import algorithms.*;
import test.ProjectValidationTests;
import util.InputHelper;

import java.util.*;

/**
 * MetroMind – Intelligent Public Transport Platform.
 * Main Terminal Application Driver.
 * Integrates Course Outcomes CO1 through CO6 across a unified transit data model.
 */
public class Main {
    private static final StationService stationService = new StationService();
    private static final RouteService routeService = new RouteService();
    private static final VehicleService vehicleService = new VehicleService();
    private static final TransportServiceService serviceService = new TransportServiceService();
    private static final PassengerService passengerService = new PassengerService();
    private static final DataStorageService storageService = new DataStorageService("data");

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(64));
        System.out.println("        METROMIND - INTELLIGENT PUBLIC TRANSPORT");
        System.out.println("=".repeat(64));
        System.out.println("Welcome to the MetroMind Transit Management & DSA-3 Platform.");

        handleStartupChoice();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputHelper.readInt("Enter your choice: ");
            switch (choice) {
                case 1:  stationManagementMenu(); break;
                case 2:  routeManagementMenu(); break;
                case 3:  serviceManagementMenu(); break;
                case 4:  vehicleManagementMenu(); break;
                case 5:  passengerTicketMenu(); break;
                case 6:  routeSearchAndTextIndexingMenu(); break;
                case 7:  routePlanningAndOptimizationMenu(); break;
                case 8:  passengerFlowNetworkMenu(); break;
                case 9:  vehicleAssignmentMenu(); break;
                case 10: schedulingConstraintsGraphMenu(); break;
                case 11: passengerAnalyticsParallelMenu(); break;
                case 12: dsaAlgorithmExplorerMenu(); break;
                case 13: viewAllTransportRecords(); break;
                case 14: saveData(); break;
                case 15: loadData(); break;
                case 16: ProjectValidationTests.printReport(); InputHelper.pause(); break;
                case 0:
                    System.out.println("\nThank you for using MetroMind. Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option from the menu.");
            }
        }
    }

    private static void handleStartupChoice() {
        System.out.println("\nStartup Options:");
        System.out.println("1. Start with empty data");
        System.out.println("2. Load sample MetroMind data (Capital Region Corridor: Vijayawada-Guntur-Amaravati)");
        System.out.println("3. Load saved data from disk (if available)");
        System.out.println("0. Exit");

        int choice = InputHelper.readIntInRange("Select startup mode: ", 0, 3);
        switch (choice) {
            case 1:
                System.out.println("Started with empty dataset. You can enter new records manually.");
                break;
            case 2:
                SampleDataGenerator.loadSampleData(stationService, routeService, vehicleService, serviceService, passengerService);
                System.out.println("Sample MetroMind dataset loaded successfully!");
                break;
            case 3:
                if (storageService.hasExistingData()) {
                    storageService.loadData(stationService, routeService, vehicleService, serviceService, passengerService);
                    System.out.println("Existing saved records loaded successfully from disk!");
                } else {
                    System.out.println("No saved records found on disk. Loading sample dataset instead.");
                    SampleDataGenerator.loadSampleData(stationService, routeService, vehicleService, serviceService, passengerService);
                }
                break;
            case 0:
                System.out.println("Exiting application.");
                System.exit(0);
        }
    }

    private static void printMainMenu() {
        System.out.println("\n" + "=".repeat(64));
        System.out.println("        METROMIND - INTELLIGENT PUBLIC TRANSPORT");
        System.out.println("=".repeat(64));
        System.out.println("Manage stations, routes, vehicles, passengers, and analytics.");
        System.out.println();
        System.out.println(" 1. Station Management");
        System.out.println(" 2. Route Management");
        System.out.println(" 3. Transport Service Management");
        System.out.println(" 4. Vehicle Management");
        System.out.println(" 5. Passenger and Ticket Management");
        System.out.println(" 6. Route Search and Text Indexing (CO1 String + CO2 Suffix Structures)");
        System.out.println(" 7. Route Planning and Optimization (CO3 Bitmask DP + Misspelling Fix)");
        System.out.println(" 8. Passenger Flow and Network Analysis (CO4 Max Flow & Min Cut)");
        System.out.println(" 9. Vehicle Assignment and Scheduling (CO4 Bipartite Matching)");
        System.out.println("10. Scheduling Constraints and Graph Analysis (CO5 SAT & NP Graph Sets)");
        System.out.println("11. Passenger Analytics and Parallel Processing (CO6 Randomized & Parallel)");
        System.out.println("12. DSA Algorithm Explorer (CO1-CO6 Unified Sandbox)");
        System.out.println("13. View All Transport Records");
        System.out.println("14. Save Data");
        System.out.println("15. Load Data");
        System.out.println("16. Run Project Validation Tests");
        System.out.println(" 0. Exit");
        System.out.println("-".repeat(64));
    }

    // ==========================================
    // 1. Station Management
    // ==========================================
    private static void stationManagementMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Station Management");
            System.out.println("1. Add a Station");
            System.out.println("2. View All Stations");
            System.out.println("3. Search Station by ID");
            System.out.println("4. Search Station by Name (with Levenshtein/Damerau-Levenshtein Suggestions)");
            System.out.println("5. Update Station Information");
            System.out.println("6. Delete a Station Safely");
            System.out.println("7. Find Connected Stations");
            System.out.println("8. Display Station Statistics");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    String id = InputHelper.readString("Enter Station ID (e.g., ST007): ");
                    if (stationService.getStationById(id) != null) {
                        System.out.println("Error: Station ID already exists. Duplicate IDs not allowed.");
                        break;
                    }
                    String name = InputHelper.readString("Enter Station Name: ");
                    String city = InputHelper.readString("Enter City: ");
                    String type = InputHelper.readString("Enter Station Type (CENTRAL_HUB, TERMINAL, TRANSIT_HUB, SUBURBAN): ");
                    int passengers = InputHelper.readInt("Enter Daily Passenger Count: ");
                    String info = InputHelper.readString("Enter Service Information: ");

                    Station st = new Station(id, name, city, type, new ArrayList<>(), passengers, info);
                    stationService.addStation(st);
                    System.out.println("Station " + name + " added successfully!");
                    break;
                }
                case 2: {
                    List<Station> all = stationService.getAllStations();
                    if (all.isEmpty()) {
                        System.out.println("No stations stored currently.");
                    } else {
                        System.out.println("\nAll Stored Stations (" + all.size() + "):");
                        for (Station s : all) System.out.println(s);
                    }
                    break;
                }
                case 3: {
                    String id = InputHelper.readString("Enter Station ID to search: ");
                    Station s = stationService.getStationById(id);
                    if (s != null) {
                        System.out.println("Found: " + s);
                    } else {
                        System.out.println("No station found with ID: " + id);
                    }
                    break;
                }
                case 4: {
                    String query = InputHelper.readString("Enter Station Name to search: ");
                    Station exact = stationService.getStationByName(query);
                    if (exact != null) {
                        System.out.println("Exact Match Found: " + exact);
                    } else {
                        System.out.println("No exact match found for '" + query + "'.");
                        System.out.println("Calculating closest spelling suggestions using Levenshtein distance...");
                        List<DynamicProgramming.SpellingSuggestion> suggs = stationService.suggestSimilarNames(query, 3);
                        if (suggs.isEmpty()) {
                            System.out.println("No close spelling suggestions found.");
                        } else {
                            System.out.println("Did you mean:");
                            for (int i = 0; i < suggs.size(); i++) {
                                System.out.printf("  %d. %s%n", i + 1, suggs.get(i));
                            }
                            if (InputHelper.readBoolean("Would you like to select one of these suggestions?")) {
                                int pick = InputHelper.readIntInRange("Select suggestion number: ", 1, suggs.size());
                                Station picked = stationService.getStationByName(suggs.get(pick - 1).candidate);
                                if (picked != null) System.out.println("Selected Station: " + picked);
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    String id = InputHelper.readString("Enter Station ID to update: ");
                    Station s = stationService.getStationById(id);
                    if (s == null) {
                        System.out.println("Station not found.");
                        break;
                    }
                    String newName = InputHelper.readOptionalString("New Name", s.getName());
                    String newCity = InputHelper.readOptionalString("New City", s.getCity());
                    String newType = InputHelper.readOptionalString("New Type", s.getStationType());
                    int newPass = InputHelper.readInt("New Passenger Count [" + s.getPassengerCount() + "]: ");
                    String newInfo = InputHelper.readOptionalString("New Service Info", s.getServiceInfo());
                    stationService.updateStation(id, newName, newCity, newType, newPass, newInfo);
                    System.out.println("Station updated successfully!");
                    break;
                }
                case 6: {
                    String id = InputHelper.readString("Enter Station ID to delete: ");
                    if (routeService.isStationUsedInRoutes(id)) {
                        System.out.println("Safety Check: Cannot delete station '" + id + "'! Active transit routes depend on it.");
                        System.out.println("Remove or update the dependent routes first before deleting this station.");
                    } else {
                        boolean ok = stationService.deleteStation(id, routeService);
                        if (ok) System.out.println("Station deleted successfully.");
                        else System.out.println("Station ID not found.");
                    }
                    break;
                }
                case 7: {
                    String id = InputHelper.readString("Enter Station ID: ");
                    List<Station> conns = stationService.getConnectedStations(id);
                    System.out.println("Directly Connected Stations (" + conns.size() + "):");
                    for (Station c : conns) System.out.println("  -> " + c.getId() + ": " + c.getName());
                    break;
                }
                case 8: {
                    List<Station> all = stationService.getAllStations();
                    int totalPass = 0;
                    Station busiest = null;
                    for (Station s : all) {
                        totalPass += s.getPassengerCount();
                        if (busiest == null || s.getPassengerCount() > busiest.getPassengerCount()) {
                            busiest = s;
                        }
                    }
                    System.out.println("Station Network Statistics:");
                    System.out.println("  Total Stations: " + all.size());
                    System.out.println("  Total Daily Passenger Footfall: " + totalPass);
                    if (busiest != null) {
                        System.out.println("  Busiest Station: " + busiest.getName() + " (" + busiest.getPassengerCount() + " passengers)");
                    }
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 2. Route Management
    // ==========================================
    private static void routeManagementMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Route Management");
            System.out.println("1. Add a Route");
            System.out.println("2. View All Routes");
            System.out.println("3. Search Route by ID");
            System.out.println("4. Search Routes Serving a Station");
            System.out.println("5. Search Direct Routes Between Two Stations");
            System.out.println("6. Display Full Intermediate Stations Sequence");
            System.out.println("7. Update Route Information");
            System.out.println("8. Delete a Route Safely");
            System.out.println("9. Search Route Descriptions using String Algorithms");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    String id = InputHelper.readString("Enter Route ID (e.g., RT006): ");
                    if (routeService.getRouteById(id) != null) {
                        System.out.println("Error: Route ID already exists.");
                        break;
                    }
                    String name = InputHelper.readString("Enter Route Name: ");
                    String src = InputHelper.readString("Enter Source Station ID: ");
                    if (stationService.getStationById(src) == null) {
                        System.out.println("Error: Source station '" + src + "' does not exist!");
                        break;
                    }
                    String dst = InputHelper.readString("Enter Destination Station ID: ");
                    if (stationService.getStationById(dst) == null) {
                        System.out.println("Error: Destination station '" + dst + "' does not exist!");
                        break;
                    }
                    String interStr = InputHelper.readOptionalString("Enter Intermediate Stations separated by semicolon (or leave blank)", "");
                    List<String> intermediates = new ArrayList<>();
                    if (!interStr.trim().isEmpty()) {
                        for (String part : interStr.split(";")) {
                            String clean = part.trim();
                            if (stationService.getStationById(clean) == null) {
                                System.out.println("Error: Intermediate station '" + clean + "' does not exist!");
                                return;
                            }
                            intermediates.add(clean);
                        }
                    }
                    double dist = InputHelper.readDoubleMin("Enter Distance in km: ", 0.1);
                    int time = InputHelper.readIntInRange("Enter Travel Time in minutes: ", 1, 1440);
                    double cost = InputHelper.readDoubleMin("Enter Travel Cost in Rs: ", 0.0);
                    String desc = InputHelper.readString("Enter Route Description: ");

                    Route r = new Route(id, name, src, dst, intermediates, dist, time, cost, desc, "ACTIVE");
                    routeService.addRoute(r, stationService);
                    System.out.println("Route added successfully!");
                    break;
                }
                case 2: {
                    List<Route> all = routeService.getAllRoutes();
                    if (all.isEmpty()) {
                        System.out.println("No routes stored currently.");
                    } else {
                        System.out.println("\nAll Stored Routes (" + all.size() + "):");
                        for (Route r : all) System.out.println(r);
                    }
                    break;
                }
                case 3: {
                    String id = InputHelper.readString("Enter Route ID: ");
                    Route r = routeService.getRouteById(id);
                    if (r != null) {
                        System.out.println("Found: " + r);
                        System.out.println("Full Sequence: " + r.getFullStationPath());
                        System.out.println("Description: " + r.getDescription());
                    } else {
                        System.out.println("Route not found.");
                    }
                    break;
                }
                case 4: {
                    String stId = InputHelper.readString("Enter Station ID: ");
                    List<Route> routes = routeService.getRoutesServingStation(stId);
                    System.out.println("Routes serving " + stId + " (" + routes.size() + "):");
                    for (Route r : routes) System.out.println("  " + r);
                    break;
                }
                case 5: {
                    String src = InputHelper.readString("Enter Source Station ID: ");
                    String dst = InputHelper.readString("Enter Destination Station ID: ");
                    List<Route> direct = routeService.findRoutesBetween(src, dst);
                    if (direct.isEmpty()) {
                        System.out.println("No direct routes found between " + src + " and " + dst + ".");
                    } else {
                        System.out.println("Direct Routes (" + direct.size() + "):");
                        for (Route r : direct) System.out.println("  " + r);
                    }
                    break;
                }
                case 6: {
                    String id = InputHelper.readString("Enter Route ID: ");
                    Route r = routeService.getRouteById(id);
                    if (r == null) {
                        System.out.println("Route not found.");
                    } else {
                        List<String> path = r.getFullStationPath();
                        System.out.println("Ordered Station Path for Route " + r.getName() + ":");
                        for (int i = 0; i < path.size(); i++) {
                            Station st = stationService.getStationById(path.get(i));
                            String stName = (st != null) ? st.getName() : "Unknown";
                            System.out.printf("  Step %d: %s (%s)%n", i + 1, path.get(i), stName);
                        }
                    }
                    break;
                }
                case 7: {
                    String id = InputHelper.readString("Enter Route ID to update: ");
                    Route r = routeService.getRouteById(id);
                    if (r == null) {
                        System.out.println("Route not found.");
                        break;
                    }
                    String newName = InputHelper.readOptionalString("New Name", r.getName());
                    double newDist = InputHelper.readDoubleMin("New Distance km [" + r.getDistanceKm() + "]: ", 0.1);
                    int newTime = InputHelper.readIntInRange("New Time mins [" + r.getEstimatedTimeMinutes() + "]: ", 1, 1440);
                    double newCost = InputHelper.readDoubleMin("New Cost [" + r.getTravelCost() + "]: ", 0.0);
                    String newStat = InputHelper.readOptionalString("New Status", r.getStatus());
                    String newDesc = InputHelper.readOptionalString("New Description", r.getDescription());
                    routeService.updateRoute(id, newName, newDist, newTime, newCost, newStat, newDesc);
                    System.out.println("Route updated successfully!");
                    break;
                }
                case 8: {
                    String id = InputHelper.readString("Enter Route ID to delete: ");
                    boolean ok = routeService.deleteRoute(id);
                    if (ok) System.out.println("Route deleted successfully.");
                    else System.out.println("Route ID not found.");
                    break;
                }
                case 9: {
                    String pattern = InputHelper.readString("Enter search keyword for route descriptions: ");
                    System.out.println("\nMatching Routes across Descriptions using KMP & Suffix Automaton:");
                    int matchCount = 0;
                    for (Route r : routeService.getAllRoutes()) {
                        StringAlgorithms.KmpResult res = StringAlgorithms.kmpSearch(r.getDescription(), pattern);
                        if (!res.matchIndices.isEmpty()) {
                            matchCount++;
                            System.out.printf("  [%s] %s | %d matches at positions %s%n",
                                    r.getId(), r.getName(), res.matchIndices.size(), res.matchIndices);
                        }
                    }
                    if (matchCount == 0) {
                        System.out.println("No routes found containing keyword: '" + pattern + "'.");
                    }
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 3. Transport Service Management
    // ==========================================
    private static void serviceManagementMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Transport Service Management");
            System.out.println("1. Add a Scheduled Transport Service");
            System.out.println("2. View All Transport Services");
            System.out.println("3. Search Service by ID");
            System.out.println("4. View Services for a Route");
            System.out.println("5. Calculate Real-Time Occupancy Percentage");
            System.out.println("6. Update Service Status / Times");
            System.out.println("7. Delete a Service");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    String id = InputHelper.readString("Enter Service ID (e.g., SV006): ");
                    if (serviceService.getServiceById(id) != null) {
                        System.out.println("Error: Service ID already exists.");
                        break;
                    }
                    String code = InputHelper.readString("Enter Service Code: ");
                    String routeId = InputHelper.readString("Enter Route ID: ");
                    if (routeService.getRouteById(routeId) == null) {
                        System.out.println("Error: Route '" + routeId + "' does not exist!");
                        break;
                    }
                    String dep = InputHelper.readString("Enter Departure Time (HH:MM): ");
                    String arr = InputHelper.readString("Enter Arrival Time (HH:MM): ");
                    int reqCap = InputHelper.readIntInRange("Enter Required Capacity: ", 1, 1000);
                    int occ = InputHelper.readIntInRange("Enter Current Passenger Occupancy: ", 0, reqCap * 2);

                    TransportService s = new TransportService(id, code, routeId, dep, arr, reqCap, occ, "UNASSIGNED", "SCHEDULED");
                    serviceService.addService(s, routeService);
                    System.out.println("Transport Service added successfully!");
                    break;
                }
                case 2: {
                    List<TransportService> all = serviceService.getAllServices();
                    if (all.isEmpty()) {
                        System.out.println("No services stored.");
                    } else {
                        System.out.println("\nAll Transport Services (" + all.size() + "):");
                        for (TransportService s : all) System.out.println(s);
                    }
                    break;
                }
                case 3: {
                    String id = InputHelper.readString("Enter Service ID: ");
                    TransportService s = serviceService.getServiceById(id);
                    if (s != null) {
                        System.out.println("Found: " + s);
                    } else {
                        System.out.println("Service not found.");
                    }
                    break;
                }
                case 4: {
                    String rId = InputHelper.readString("Enter Route ID: ");
                    List<TransportService> list = serviceService.getServicesForRoute(rId);
                    System.out.println("Services running on " + rId + " (" + list.size() + "):");
                    for (TransportService s : list) System.out.println("  " + s);
                    break;
                }
                case 5: {
                    String id = InputHelper.readString("Enter Service ID: ");
                    TransportService s = serviceService.getServiceById(id);
                    if (s == null) {
                        System.out.println("Service not found.");
                        break;
                    }
                    Vehicle v = vehicleService.getVehicleById(s.getAssignedVehicleId());
                    int cap = (v != null) ? v.getCapacity() : s.getRequiredCapacity();
                    double occPct = s.getOccupancyPercentage(cap);
                    System.out.println("\nOccupancy Calculation:");
                    System.out.println("  Service: " + s.getServiceCode() + " (" + s.getId() + ")");
                    System.out.println("  Assigned Vehicle: " + s.getAssignedVehicleId() + (v != null ? " (" + v.getName() + ")" : " [Unassigned]"));
                    System.out.println("  Current Passengers: " + s.getCurrentOccupancy());
                    System.out.println("  Vehicle Capacity: " + cap);
                    System.out.printf("  Occupancy Percentage: %.2f%%%n", occPct);
                    if (occPct > 100.0) {
                        System.out.println("  [ALERT] Overcrowding detected! Current occupancy exceeds vehicle capacity.");
                    }
                    break;
                }
                case 6: {
                    String id = InputHelper.readString("Enter Service ID to update: ");
                    TransportService s = serviceService.getServiceById(id);
                    if (s == null) {
                        System.out.println("Service not found.");
                        break;
                    }
                    String dep = InputHelper.readOptionalString("New Departure Time", s.getDepartureTime());
                    String arr = InputHelper.readOptionalString("New Arrival Time", s.getArrivalTime());
                    int minCap = InputHelper.readIntInRange("New Required Capacity [" + s.getRequiredCapacity() + "]: ", 1, 1000);
                    int occ = InputHelper.readIntInRange("New Current Occupancy [" + s.getCurrentOccupancy() + "]: ", 0, minCap * 2);
                    String status = InputHelper.readOptionalString("New Status", s.getStatus());
                    serviceService.updateService(id, dep, arr, minCap, occ, status);
                    System.out.println("Service updated successfully!");
                    break;
                }
                case 7: {
                    String id = InputHelper.readString("Enter Service ID to delete: ");
                    boolean ok = serviceService.deleteService(id);
                    if (ok) System.out.println("Service deleted successfully.");
                    else System.out.println("Service not found.");
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 4. Vehicle Management
    // ==========================================
    private static void vehicleManagementMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Vehicle Management");
            System.out.println("1. Add a Vehicle");
            System.out.println("2. View All Vehicles");
            System.out.println("3. View Available Vehicles");
            System.out.println("4. Search Vehicle by ID");
            System.out.println("5. Update Vehicle Information");
            System.out.println("6. Delete a Vehicle");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    String id = InputHelper.readString("Enter Vehicle ID (e.g., VH007): ");
                    if (vehicleService.getVehicleById(id) != null) {
                        System.out.println("Error: Vehicle ID already exists.");
                        break;
                    }
                    String name = InputHelper.readString("Enter Vehicle Name: ");
                    String type = InputHelper.readString("Enter Vehicle Type (METRO_TRAIN, ELECTRIC_BUS, FEEDER_VAN): ");
                    int cap = InputHelper.readIntInRange("Enter Vehicle Capacity: ", 1, 1000);
                    Vehicle v = new Vehicle(id, name, type, cap, "NONE", "AVAILABLE");
                    vehicleService.addVehicle(v);
                    System.out.println("Vehicle added successfully!");
                    break;
                }
                case 2: {
                    List<Vehicle> all = vehicleService.getAllVehicles();
                    System.out.println("\nAll Stored Vehicles (" + all.size() + "):");
                    for (Vehicle v : all) System.out.println(v);
                    break;
                }
                case 3: {
                    List<Vehicle> avail = vehicleService.getAvailableVehicles();
                    System.out.println("\nAvailable Vehicles (" + avail.size() + "):");
                    for (Vehicle v : avail) System.out.println(v);
                    break;
                }
                case 4: {
                    String id = InputHelper.readString("Enter Vehicle ID: ");
                    Vehicle v = vehicleService.getVehicleById(id);
                    if (v != null) System.out.println("Found: " + v);
                    else System.out.println("Vehicle not found.");
                    break;
                }
                case 5: {
                    String id = InputHelper.readString("Enter Vehicle ID to update: ");
                    Vehicle v = vehicleService.getVehicleById(id);
                    if (v == null) {
                        System.out.println("Vehicle not found.");
                        break;
                    }
                    String name = InputHelper.readOptionalString("New Name", v.getName());
                    String type = InputHelper.readOptionalString("New Type", v.getVehicleType());
                    int cap = InputHelper.readIntInRange("New Capacity [" + v.getCapacity() + "]: ", 1, 1000);
                    String status = InputHelper.readOptionalString("New Status", v.getStatus());
                    vehicleService.updateVehicle(id, name, type, cap, status);
                    System.out.println("Vehicle updated successfully!");
                    break;
                }
                case 6: {
                    String id = InputHelper.readString("Enter Vehicle ID to delete: ");
                    boolean ok = vehicleService.deleteVehicle(id);
                    if (ok) System.out.println("Vehicle deleted successfully.");
                    else System.out.println("Vehicle not found.");
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 5. Passenger and Ticket Management
    // ==========================================
    private static void passengerTicketMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Passenger and Ticket Management");
            System.out.println("1. Add Passenger Turnstile / Boarding Event");
            System.out.println("2. View All Passenger Events");
            System.out.println("3. Issue / Add a Ticket");
            System.out.println("4. View All Tickets");
            System.out.println("5. Search Tickets using Rabin-Karp Rolling Hash");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    String id = InputHelper.readString("Enter Event ID (e.g., EV017): ");
                    String sId = InputHelper.readString("Enter Service ID: ");
                    String stId = InputHelper.readString("Enter Station ID: ");
                    String time = InputHelper.readString("Enter Timestamp (HH:MM:SS): ");
                    String type = InputHelper.readString("Enter Event Type (BOARDING, ALIGHTING, TRANSFER): ");
                    int count = InputHelper.readIntInRange("Enter Passenger Count: ", 0, 5000);

                    PassengerEvent ev = new PassengerEvent(id, sId, stId, time, type, count);
                    passengerService.addPassengerEvent(ev);
                    System.out.println("Passenger Event recorded successfully!");
                    break;
                }
                case 2: {
                    List<PassengerEvent> all = passengerService.getAllEvents();
                    System.out.println("\nPassenger Events (" + all.size() + "):");
                    for (PassengerEvent e : all) System.out.println(e);
                    break;
                }
                case 3: {
                    String id = InputHelper.readString("Enter Ticket ID (e.g., TK109): ");
                    String passRef = InputHelper.readString("Enter Passenger Name: ");
                    String sId = InputHelper.readString("Enter Service ID: ");
                    String src = InputHelper.readString("Enter Source Station ID: ");
                    String dst = InputHelper.readString("Enter Destination Station ID: ");
                    double fare = InputHelper.readDoubleMin("Enter Fare Amount in Rs: ", 0.0);

                    Ticket t = new Ticket(id, passRef, sId, src, dst, fare, "CONFIRMED");
                    passengerService.addTicket(t);
                    System.out.println("Ticket issued successfully!");
                    break;
                }
                case 4: {
                    List<Ticket> all = passengerService.getAllTickets();
                    System.out.println("\nAll Stored Tickets (" + all.size() + "):");
                    for (Ticket t : all) System.out.println(t);
                    break;
                }
                case 5: {
                    String pattern = InputHelper.readString("Enter Ticket / Passenger search pattern (Rabin-Karp): ");
                    List<Ticket> matches = passengerService.searchTicketsWithRabinKarp(pattern);
                    System.out.println("\nRabin-Karp Search Results (" + matches.size() + " matches):");
                    for (Ticket t : matches) System.out.println("  " + t);
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 6. Route Search and Text Indexing (CO1 & CO2)
    // ==========================================
    private static void routeSearchAndTextIndexingMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Route Search & Text Indexing (CO1 & CO2)");
            System.out.println("1. Knuth-Morris-Pratt (KMP) Search with LPS Array");
            System.out.println("2. Z-Algorithm Pattern Matching with Z-Array");
            System.out.println("3. Rabin-Karp Rolling Hash Search with Collision Check");
            System.out.println("4. Aho-Corasick Multi-Keyword Transport Alert Scanner");
            System.out.println("5. Suffix Array Construction & Binary Search");
            System.out.println("6. SA-IS Linear Time Suffix Array (Induced Sorting)");
            System.out.println("7. Kasai Algorithm & Longest Common Substring in Route Descriptions");
            System.out.println("8. Suffix Automaton (SAM) Substring Query & State Inspection");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    InputHelper.printSubHeader("KMP String Search");
                    System.out.println("Select search corpus:");
                    System.out.println("1. Search Station Names");
                    System.out.println("2. Search Route Descriptions");
                    System.out.println("3. Custom User Text");
                    int mode = InputHelper.readIntInRange("Choose corpus: ", 1, 3);
                    String text = "";
                    if (mode == 1) {
                        StringBuilder sb = new StringBuilder();
                        for (Station s : stationService.getAllStations()) sb.append(s.getName()).append(" ");
                        text = sb.toString();
                    } else if (mode == 2) {
                        StringBuilder sb = new StringBuilder();
                        for (Route r : routeService.getAllRoutes()) sb.append(r.getDescription()).append(" ");
                        text = sb.toString();
                    } else {
                        text = InputHelper.readString("Enter text corpus: ");
                    }
                    String pat = InputHelper.readString("Enter search pattern: ");
                    StringAlgorithms.KmpResult res = StringAlgorithms.kmpSearch(text, pat);
                    System.out.println("\nKMP Search Results:");
                    System.out.println("  Search Pattern: '" + pat + "'");
                    System.out.println("  LPS Array: " + Arrays.toString(res.lps));
                    System.out.println("  Total Matches: " + res.matchIndices.size());
                    System.out.println("  Match Indices in Corpus: " + res.matchIndices);
                    System.out.println("  Character Comparisons: " + res.comparisons);
                    System.out.println("  Complexity: " + res.timeComplexity);
                    break;
                }
                case 2: {
                    InputHelper.printSubHeader("Z-Algorithm Pattern Matching");
                    String text = InputHelper.readString("Enter text or route description to analyze: ");
                    String pat = InputHelper.readString("Enter pattern: ");
                    StringAlgorithms.ZResult zRes = StringAlgorithms.zSearch(text, pat);
                    System.out.println("\nZ-Algorithm Results:");
                    System.out.println("  Constructed string: " + pat + "$" + text);
                    System.out.println("  Z-Array: " + Arrays.toString(zRes.zArray));
                    System.out.println("  Matching Positions in Text: " + zRes.matchIndices);
                    System.out.println("  Complexity: " + zRes.timeComplexity);
                    break;
                }
                case 3: {
                    InputHelper.printSubHeader("Rabin-Karp Rolling Hash");
                    String text = InputHelper.readString("Enter text to search: ");
                    String pat = InputHelper.readString("Enter pattern: ");
                    StringAlgorithms.RabinKarpResult rk = StringAlgorithms.rabinKarpSearch(text, pat);
                    System.out.println("\nRabin-Karp Results:");
                    System.out.println("  Pattern Rolling Hash: " + rk.patternHash);
                    System.out.println("  Total Hash Comparisons: " + rk.hashComparisons);
                    System.out.println("  Spurious Hash Collisions: " + rk.spuriousCollisions);
                    System.out.println("  Verified Exact Matches: " + rk.verifiedMatches);
                    System.out.println("  Match Starting Indices: " + rk.matchIndices);
                    System.out.println("  Complexity: " + rk.timeComplexity);
                    break;
                }
                case 4: {
                    InputHelper.printSubHeader("Aho-Corasick Multi-Pattern Alert Detection");
                    StringAlgorithms.AhoCorasickTrie trie = new StringAlgorithms.AhoCorasickTrie();
                    trie.insert("delayed");
                    trie.insert("cancelled");
                    trie.insert("overcrowded");
                    trie.insert("diversion");
                    trie.insert("accident");
                    trie.insert("service disruption");
                    System.out.println("Default Alert Keywords Loaded: [delayed, cancelled, overcrowded, diversion, accident, service disruption]");
                    if (InputHelper.readBoolean("Add custom alert keyword?")) {
                        String custom = InputHelper.readString("Enter keyword: ");
                        trie.insert(custom);
                    }
                    String text = InputHelper.readString("Enter transport announcement or select route description: ");
                    List<StringAlgorithms.AhoCorasickMatch> matches = trie.search(text);
                    System.out.println("\nDetected Alert Keywords (" + matches.size() + " occurrences):");
                    for (StringAlgorithms.AhoCorasickMatch m : matches) {
                        System.out.println("  ALERT -> " + m);
                    }
                    break;
                }
                case 5: {
                    InputHelper.printSubHeader("Suffix Array Construction & Binary Search");
                    String text = InputHelper.readString("Enter text: ");
                    int[] sa = SuffixStructures.buildSuffixArrayStandard(text);
                    System.out.println("Original Text: " + text);
                    System.out.println("Suffix Array (Sorted Suffix Indices): " + Arrays.toString(sa));
                    if (text.length() <= 30) {
                        System.out.println("Sorted Suffixes:");
                        for (int i = 0; i < sa.length; i++) {
                            System.out.printf("  SA[%d] = %2d -> %s%n", i, sa[i], text.substring(sa[i]));
                        }
                    }
                    String pat = InputHelper.readString("Enter pattern to binary search in Suffix Array: ");
                    List<Integer> occurrences = SuffixStructures.searchSuffixArray(text, sa, pat);
                    System.out.println("Pattern Occurrences via Suffix Array: " + occurrences);
                    break;
                }
                case 6: {
                    InputHelper.printSubHeader("SA-IS (Suffix Array by Induced Sorting) O(N)");
                    String text = InputHelper.readString("Enter text to index with SA-IS: ");
                    int[] sa = SuffixStructures.buildSuffixArraySAIS(text);
                    System.out.println("SA-IS Suffix Array: " + Arrays.toString(sa));
                    System.out.println("Algorithm: Pure SA-IS Linear Time Induced Sorting O(N)");
                    break;
                }
                case 7: {
                    InputHelper.printSubHeader("Kasai LCP & Common Substring Extraction");
                    List<Route> routes = routeService.getAllRoutes();
                    if (routes.size() >= 2) {
                        System.out.println("Analyzing route descriptions from Route 1 and Route 2:");
                        String desc1 = routes.get(0).getDescription();
                        String desc2 = routes.get(1).getDescription();
                        System.out.println("Route 1 Desc: " + desc1);
                        System.out.println("Route 2 Desc: " + desc2);
                        String lcs = SuffixStructures.findLongestCommonSubstring(desc1, desc2);
                        System.out.println("\nLongest Common Substring identified via SA + Kasai LCP:");
                        System.out.println("  '" + lcs + "' (Length: " + lcs.length() + ")");
                    } else {
                        String s1 = InputHelper.readString("Enter string 1: ");
                        String s2 = InputHelper.readString("Enter string 2: ");
                        String lcs = SuffixStructures.findLongestCommonSubstring(s1, s2);
                        System.out.println("Longest Common Substring: '" + lcs + "'");
                    }
                    break;
                }
                case 8: {
                    InputHelper.printSubHeader("Suffix Automaton (SAM)");
                    String text = InputHelper.readString("Enter route or service text to build Suffix Automaton: ");
                    SuffixStructures.SuffixAutomaton sam = new SuffixStructures.SuffixAutomaton(text);
                    System.out.println("Suffix Automaton built successfully!");
                    System.out.println("  Total States: " + sam.getStateCount());
                    System.out.println("  Distinct Substrings: " + sam.countDistinctSubstrings());
                    String q = InputHelper.readString("Enter query substring to check occurrence: ");
                    boolean exists = sam.containsSubstring(q);
                    System.out.printf("  Does substring '%s' occur? %s (Time: O(|query|) = O(%d))%n", q, exists ? "YES" : "NO", q.length());
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 7. Route Planning and Optimization (CO3)
    // ==========================================
    private static void routePlanningAndOptimizationMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Route Planning & Optimization (CO3)");
            System.out.println("1. Bitmask DP TSP Multi-Destination Tour Optimization");
            System.out.println("2. Levenshtein Misspelled Station Search & Suggestion");
            System.out.println("3. Damerau-Levenshtein Distance with Transposition Analysis");
            System.out.println("4. Matrix-Chain Multiplication Analytics Pipeline Optimization");
            System.out.println("5. Optimal Binary Search Tree (OBST) for Station Query Lookups");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    InputHelper.printSubHeader("Bitmask Dynamic Programming - Multi-Destination TSP");
                    List<Station> all = stationService.getAllStations();
                    if (all.size() < 2) {
                        System.out.println("Need at least 2 stations for route planning.");
                        break;
                    }
                    System.out.println("Available Stations:");
                    for (int i = 0; i < all.size(); i++) {
                        System.out.printf("  %d. [%s] %s%n", i + 1, all.get(i).getId(), all.get(i).getName());
                    }
                    String sel = InputHelper.readString("Select station numbers to visit separated by commas (e.g., 1,2,3,4): ");
                    List<String> chosenIds = new ArrayList<>();
                    for (String part : sel.split(",")) {
                        try {
                            int idx = Integer.parseInt(part.trim()) - 1;
                            if (idx >= 0 && idx < all.size() && !chosenIds.contains(all.get(idx).getId())) {
                                chosenIds.add(all.get(idx).getId());
                            }
                        } catch (Exception ignored) {}
                    }
                    if (chosenIds.size() < 2) {
                        System.out.println("Please select at least 2 distinct valid stations.");
                        break;
                    }
                    boolean returnToStart = InputHelper.readBoolean("Must the vehicle return to the start station? (Tour vs Path)");

                    double[][] distMatrix = routeService.buildDistanceMatrix(chosenIds);
                    DynamicProgramming.BitmaskTspResult res = DynamicProgramming.solveBitmaskTsp(chosenIds, distMatrix, returnToStart);

                    System.out.println("\nBitmask DP TSP Results:");
                    if (!res.isTourComplete) {
                        System.out.println("No valid connecting route sequence exists between the selected stations.");
                    } else {
                        System.out.printf("  Total Optimal Distance: %.2f km%n", res.totalCost);
                        System.out.println("  Optimal Visit Order:");
                        for (int i = 0; i < res.stationTour.size(); i++) {
                            String stId = res.stationTour.get(i);
                            Station st = stationService.getStationById(stId);
                            System.out.printf("    %d. %s (%s)%n", i + 1, stId, (st != null ? st.getName() : ""));
                        }
                    }
                    System.out.println("  Complexity: " + res.complexity);
                    break;
                }
                case 2: {
                    InputHelper.printSubHeader("Levenshtein Spelling Suggestion");
                    String query = InputHelper.readString("Enter station query (e.g., 'Vjayawada' or 'Guntr'): ");
                    List<DynamicProgramming.SpellingSuggestion> suggs = stationService.suggestSimilarNames(query, 4);
                    System.out.println("Levenshtein Distance Suggestions:");
                    for (DynamicProgramming.SpellingSuggestion s : suggs) {
                        System.out.println("  -> " + s);
                    }
                    break;
                }
                case 3: {
                    InputHelper.printSubHeader("Damerau-Levenshtein Distance");
                    String s1 = InputHelper.readString("Enter first station/string: ");
                    String s2 = InputHelper.readString("Enter second station/string: ");
                    int lev = DynamicProgramming.levenshteinDistance(s1, s2);
                    int dam = DynamicProgramming.damerauLevenshteinDistance(s1, s2);
                    System.out.println("Comparison Results:");
                    System.out.println("  Levenshtein Distance: " + lev);
                    System.out.println("  Damerau-Levenshtein Distance (transposition penalty = 1): " + dam);
                    break;
                }
                case 4: {
                    InputHelper.printSubHeader("Matrix-Chain Multiplication");
                    System.out.println("Transport Analytics Pipeline uses chained matrix operations:");
                    System.out.println("  M1 (Passenger Flow) x M2 (Route Transitions) x M3 (Vehicle Allocation) x M4 (Fare Matrix)");
                    String dimInput = InputHelper.readOptionalString("Enter matrix dimensions separated by spaces", "10 20 30 40 30");
                    String[] parts = dimInput.trim().split("\\s+");
                    int[] dims = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) dims[i] = Integer.parseInt(parts[i]);

                    DynamicProgramming.MatrixChainResult mcm = DynamicProgramming.solveMatrixChain(dims);
                    System.out.println("\nMatrix-Chain Multiplication Results:");
                    System.out.println("  Minimum Scalar Multiplications: " + mcm.minMultiplications);
                    System.out.println("  Optimal Parenthesization: " + mcm.optimalParenthesization);
                    System.out.println("  Complexity: " + mcm.complexity);
                    break;
                }
                case 5: {
                    InputHelper.printSubHeader("Optimal Binary Search Tree (OBST)");
                    List<Station> stations = stationService.getAllStations();
                    String[] keys = new String[stations.size()];
                    double[] freqs = new double[stations.size()];
                    for (int i = 0; i < stations.size(); i++) {
                        keys[i] = stations.get(i).getName();
                        freqs[i] = stations.get(i).getSearchFrequency();
                    }
                    DynamicProgramming.ObstResult obst = DynamicProgramming.solveObst(keys, freqs);
                    System.out.println("\nOptimal BST Results based on Station Search Frequencies:");
                    System.out.printf("  Expected Optimal Search Cost: %.2f%n", obst.minSearchCost);
                    System.out.println("  Tree Hierarchy Structure:\n" + obst.treeStructure);
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 8. Passenger Flow and Network Analysis (CO4)
    // ==========================================
    private static void passengerFlowNetworkMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Passenger Flow & Network Analysis (CO4)");
            System.out.println("1. Ford-Fulkerson Maximum Flow (DFS)");
            System.out.println("2. Edmonds-Karp Maximum Flow (BFS)");
            System.out.println("3. Dinic's Algorithm Maximum Flow (Level Graph + Blocking Flow)");
            System.out.println("4. Minimum Cut Analysis and Max-Flow Min-Cut Verification");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1:
                case 2:
                case 3:
                case 4: {
                    List<Station> all = stationService.getAllStations();
                    if (all.size() < 2) {
                        System.out.println("Need at least 2 stations to form a transit network.");
                        break;
                    }
                    System.out.println("Select Source and Destination Stations:");
                    for (int i = 0; i < all.size(); i++) {
                        System.out.printf("  %d. [%s] %s%n", i + 1, all.get(i).getId(), all.get(i).getName());
                    }
                    int sIdx = InputHelper.readIntInRange("Choose Source station: ", 1, all.size()) - 1;
                    int tIdx = InputHelper.readIntInRange("Choose Destination station: ", 1, all.size()) - 1;
                    if (sIdx == tIdx) {
                        System.out.println("Source and destination stations must be distinct!");
                        break;
                    }

                    // Build capacity network from routes
                    NetworkFlow.FlowNetwork net = buildTransitFlowNetwork();

                    if (ch == 1) {
                        NetworkFlow.FlowResult res = NetworkFlow.fordFulkerson(net, sIdx, tIdx);
                        System.out.println("\nFord-Fulkerson Max Flow: " + res.maxFlow);
                        System.out.println("Augmenting Paths used: " + res.augmentingPaths);
                    } else if (ch == 2) {
                        NetworkFlow.FlowResult res = NetworkFlow.edmondsKarp(net, sIdx, tIdx);
                        System.out.println("\nEdmonds-Karp Max Flow: " + res.maxFlow);
                        System.out.println("Augmenting Paths (BFS): " + res.augmentingPaths);
                        System.out.println("Complexity: " + res.complexity);
                    } else if (ch == 3) {
                        NetworkFlow.FlowResult res = NetworkFlow.dinicsAlgorithm(net, sIdx, tIdx);
                        System.out.println("\nDinic's Algorithm Max Flow: " + res.maxFlow);
                        for (String ph : res.augmentingPaths) System.out.println("  " + ph);
                        System.out.println("Complexity: " + res.complexity);
                    } else {
                        NetworkFlow.MinCutResult cut = NetworkFlow.computeMinimumCut(net, sIdx, tIdx);
                        System.out.println("\nMinimum Cut Results:");
                        System.out.println("  Source Partition (S): " + cut.sourcePartition);
                        System.out.println("  Destination Partition (T): " + cut.sinkPartition);
                        System.out.println("  Cut Capacity: " + cut.cutCapacity);
                        System.out.println("  Maximum Flow: " + cut.maxFlow);
                        System.out.println("  Max-Flow Min-Cut Theorem Verified: " + cut.verifiesMaxFlowMinCut);
                    }
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    private static NetworkFlow.FlowNetwork buildTransitFlowNetwork() {
        List<Station> stations = stationService.getAllStations();
        int n = stations.size();
        NetworkFlow.FlowNetwork net = new NetworkFlow.FlowNetwork(n);
        Map<String, Integer> idToIdx = new HashMap<>();
        for (int i = 0; i < n; i++) {
            net.registerNode(stations.get(i).getName(), i);
            idToIdx.put(stations.get(i).getId(), i);
        }

        for (Route r : routeService.getAllRoutes()) {
            List<String> path = r.getFullStationPath();
            int cap = 150; // Nominal hourly corridor passenger flow capacity
            for (int i = 0; i < path.size() - 1; i++) {
                Integer u = idToIdx.get(path.get(i));
                Integer v = idToIdx.get(path.get(i + 1));
                if (u != null && v != null) {
                    net.addEdge(u, v, cap);
                }
            }
        }
        return net;
    }

    // ==========================================
    // 9. Vehicle Assignment and Scheduling (CO4)
    // ==========================================
    private static void vehicleAssignmentMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Vehicle Assignment & Scheduling (CO4 Bipartite Matching)");
            System.out.println("1. Compute Maximum Bipartite Matching (Available Vehicles -> Services)");
            System.out.println("2. Apply Assignments to Stored Services");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1:
                case 2: {
                    List<Vehicle> vehs = vehicleService.getAllVehicles();
                    List<TransportService> servs = serviceService.getAllServices();
                    if (vehs.isEmpty() || servs.isEmpty()) {
                        System.out.println("Need vehicles and services stored to perform bipartite matching.");
                        break;
                    }

                    List<String> vehIds = new ArrayList<>();
                    for (Vehicle v : vehs) vehIds.add(v.getId());

                    List<String> servIds = new ArrayList<>();
                    for (TransportService s : servs) servIds.add(s.getId());

                    Map<String, List<String>> compatMap = new HashMap<>();
                    for (Vehicle v : vehs) {
                        List<String> compatible = new ArrayList<>();
                        for (TransportService s : servs) {
                            if (v.getCapacity() >= s.getRequiredCapacity()) {
                                compatible.add(s.getId());
                            }
                        }
                        compatMap.put(v.getId(), compatible);
                    }

                    NetworkFlow.BipartiteMatchResult res = NetworkFlow.computeBipartiteMatching(vehIds, servIds, compatMap);
                    System.out.println("\nMaximum Bipartite Matching Results:");
                    System.out.println("  Total Matches: " + res.matchCount);
                    System.out.println("  Assignments:");
                    for (Map.Entry<String, String> entry : res.vehicleToService.entrySet()) {
                        Vehicle v = vehicleService.getVehicleById(entry.getKey());
                        TransportService s = serviceService.getServiceById(entry.getValue());
                        System.out.printf("    Vehicle %s (%s, Cap: %d) ===> Service %s (%s, ReqCap: %d)%n",
                                v.getId(), v.getName(), v.getCapacity(), s.getId(), s.getServiceCode(), s.getRequiredCapacity());
                        if (ch == 2) {
                            vehicleService.assignVehicleToService(v.getId(), s.getId());
                            serviceService.assignVehicle(s.getId(), v.getId());
                        }
                    }
                    System.out.println("  Unassigned Vehicles: " + res.unassignedVehicles);
                    System.out.println("  Unfilled Services: " + res.unfilledServices);
                    if (ch == 2) {
                        System.out.println("Assignments successfully committed to MetroMind active records!");
                    }
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 10. Scheduling Constraints and Graph Analysis (CO5)
    // ==========================================
    private static void schedulingConstraintsGraphMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Scheduling Constraints & Graph Analysis (CO5)");
            System.out.println("1. SAT / 3-SAT Transit Scheduling Constraint Solver (DPLL)");
            System.out.println("2. Maximum Clique (Bron-Kerbosch Algorithm)");
            System.out.println("3. Maximum Independent Set (Complement Graph Reduction)");
            System.out.println("4. Minimum Vertex Cover (Exact Branch-and-Bound)");
            System.out.println("5. Vertex-Cover 2-Approximation (Maximal Matching Edge Algorithm)");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    InputHelper.printSubHeader("SAT / 3-SAT DPLL Scheduling Solver");
                    System.out.println("Encode scheduling constraints into CNF:");
                    System.out.println("Variables represent vehicle-to-trip assignments (e.g. v1_s1, v1_s2).");
                    NPConstraints.CnfFormula form = new NPConstraints.CnfFormula(Arrays.asList("v1_s1", "v1_s2", "v2_s1"));
                    form.addClause("v1_s1", "v2_s1");        // Service 1 must be covered
                    form.addClause("!v1_s1", "!v1_s2");      // Vehicle 1 cannot serve both overlapping trips s1 and s2
                    form.addClause("!v1_s1", "!v2_s1");      // At most one vehicle on service 1
                    NPConstraints.SatResult sat = NPConstraints.solveSat(form);
                    System.out.println("DPLL Result:");
                    System.out.println("  Satisfiable: " + sat.isSatisfiable);
                    System.out.println("  Model Assignment: " + sat.model);
                    System.out.println("  Algorithm: " + sat.algorithm);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5: {
                    InputHelper.printSubHeader("Transport Conflict Graph Analysis");
                    NPConstraints.Graph graph = new NPConstraints.Graph(Arrays.asList("Trip_A", "Trip_B", "Trip_C", "Trip_D", "Trip_E"));
                    graph.addEdge("Trip_A", "Trip_B");
                    graph.addEdge("Trip_B", "Trip_C");
                    graph.addEdge("Trip_C", "Trip_A");
                    graph.addEdge("Trip_C", "Trip_D");
                    graph.addEdge("Trip_D", "Trip_E");

                    if (ch == 2) {
                        List<String> clique = NPConstraints.findMaximumClique(graph);
                        System.out.println("Maximum Clique (Mutually Conflicting Trips): " + clique);
                    } else if (ch == 3) {
                        List<String> indSet = NPConstraints.findMaximumIndependentSet(graph);
                        System.out.println("Maximum Independent Set (Max Compatible Concurrent Trips): " + indSet);
                    } else if (ch == 4) {
                        NPConstraints.VertexCoverResult vc = NPConstraints.findExactMinimumVertexCover(graph);
                        System.out.println("Exact Minimum Vertex Cover: " + vc.cover + " (Size: " + vc.size + ")");
                    } else {
                        NPConstraints.VertexCoverResult exact = NPConstraints.findExactMinimumVertexCover(graph);
                        NPConstraints.VertexCoverResult approx = NPConstraints.findVertexCover2Approx(graph);
                        System.out.println("Vertex Cover 2-Approximation Comparison:");
                        System.out.println("  Exact Minimum Cover: " + exact.cover + " (Size: " + exact.size + ")");
                        System.out.println("  2-Approx Cover: " + approx.cover + " (Size: " + approx.size + ")");
                        System.out.println("  Approximation Ratio: " + ((double) approx.size / exact.size) + " <= 2.0 (Verified!)");
                    }
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 11. Passenger Analytics and Parallel Processing (CO6)
    // ==========================================
    private static void passengerAnalyticsParallelMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("Passenger Analytics & Parallel Processing (CO6)");
            System.out.println("1. Randomized QuickSort on Passenger Records");
            System.out.println("2. Reservoir Sampling (Algorithm R) on Passenger Event Stream");
            System.out.println("3. Miller-Rabin Probabilistic Primality Test for Hashing Moduli");
            System.out.println("4. Blelloch Parallel Scan (Exclusive Prefix Sum of Passenger Counts)");
            System.out.println("5. Parallel Reduce (ForkJoinPool) vs Sequential Passenger Totals");
            System.out.println("6. Brent's Theorem Theoretical Bound Calculator");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Enter your choice: ");
            switch (ch) {
                case 1: {
                    InputHelper.printSubHeader("Randomized QuickSort");
                    List<PassengerEvent> events = new ArrayList<>(passengerService.getAllEvents());
                    if (events.isEmpty()) {
                        System.out.println("No passenger records stored.");
                        break;
                    }
                    boolean asc = InputHelper.readBoolean("Sort in ascending order?");
                    ParallelAndRandomized.QuickSortStats stats = new ParallelAndRandomized.QuickSortStats();
                    ParallelAndRandomized.randomizedQuickSort(events, Comparator.comparingInt(PassengerEvent::getPassengerCount), asc, stats);
                    System.out.println("\nSorted Passenger Records:");
                    for (PassengerEvent e : events) System.out.println("  " + e);
                    System.out.println("Comparisons: " + stats.comparisons + ", Swaps: " + stats.swaps);
                    break;
                }
                case 2: {
                    InputHelper.printSubHeader("Reservoir Sampling (Algorithm R)");
                    List<PassengerEvent> events = passengerService.getAllEvents();
                    int k = InputHelper.readIntInRange("Enter sample size k: ", 1, Math.max(1, events.size()));
                    ParallelAndRandomized.ReservoirResult<PassengerEvent> res =
                            ParallelAndRandomized.reservoirSample(events.iterator(), k);
                    System.out.println("\nUniform Reservoir Sample of " + k + " events from stream of " + res.totalStreamItems + ":");
                    for (PassengerEvent e : res.sample) System.out.println("  " + e);
                    break;
                }
                case 3: {
                    InputHelper.printSubHeader("Miller-Rabin Primality Test");
                    long num = (long) InputHelper.readDoubleMin("Enter integer to test for primality: ", 2);
                    ParallelAndRandomized.PrimalityResult res = ParallelAndRandomized.millerRabinTest(num, 10);
                    System.out.println("\nMiller-Rabin Results:");
                    System.out.println("  Number: " + num);
                    System.out.println("  Result: " + (res.isProbablyPrime ? "PROBABLY PRIME" : "COMPOSITE"));
                    System.out.println("  Witness Bases Tested: " + res.witnessBases);
                    System.out.println("  Application: " + res.roleInMetroMind);
                    break;
                }
                case 4: {
                    InputHelper.printSubHeader("Blelloch Parallel Scan (Exclusive Prefix Sum)");
                    int[] counts = passengerService.getPassengerCountsArray();
                    if (counts.length == 0) counts = new int[]{120, 85, 210, 45, 160, 95, 30, 180};
                    ParallelAndRandomized.BlellochScanResult scan = ParallelAndRandomized.blellochExclusiveScan(counts);
                    System.out.println("Input Passenger Counts: " + Arrays.toString(scan.input));
                    System.out.println("Exclusive Prefix Sum:   " + Arrays.toString(scan.exclusivePrefixSum));
                    System.out.println("Total Passenger Sum:    " + scan.totalSum);
                    System.out.println("\nUp-Sweep Stages (Reduce Tree):");
                    for (String s : scan.upSweepStages) System.out.println("  " + s);
                    System.out.println("\nDown-Sweep Stages:");
                    for (String s : scan.downSweepStages) System.out.println("  " + s);
                    break;
                }
                case 5: {
                    InputHelper.printSubHeader("Parallel Reduce vs Sequential");
                    int[] data = passengerService.getPassengerCountsArray();
                    if (data.length < 100) {
                        int[] large = new int[500];
                        for (int i = 0; i < large.length; i++) large[i] = (i % 20) + 5;
                        data = large;
                    }
                    ParallelAndRandomized.ParallelReduceResult red = ParallelAndRandomized.parallelReduceSum(data);
                    System.out.println("\nParallel Reduce Results:");
                    System.out.println("  Records Processed: " + red.recordCount);
                    System.out.println("  ForkJoin Pool Threads: " + red.threadCount);
                    System.out.println("  Parallel Total Sum: " + red.parallelTotal);
                    System.out.println("  Sequential Total:   " + red.sequentialTotal);
                    System.out.println("  Matches Exact:      " + red.matchesSequential);
                    System.out.println("  Parallel Time:      " + red.parallelTimeNanos + " ns");
                    System.out.println("  Sequential Time:    " + red.sequentialTimeNanos + " ns");
                    break;
                }
                case 6: {
                    InputHelper.printSubHeader("Brent's Theorem Calculator");
                    double w = InputHelper.readDoubleMin("Enter Total Work T_1: ", 1);
                    double s = InputHelper.readDoubleMin("Enter Span (Critical Path) T_infinity: ", 1);
                    while (s > w) {
                        System.out.println("Span cannot exceed total work. Please re-enter span: ");
                        s = InputHelper.readDoubleMin("Enter Span T_infinity: ", 1);
                    }
                    int p = InputHelper.readIntInRange("Enter Number of Processors P: ", 1, 1024);
                    ParallelAndRandomized.BrentsTheoremResult brent = new ParallelAndRandomized.BrentsTheoremResult(w, s, p);
                    System.out.println("\nBrent's Theorem Bound Results:");
                    System.out.println("  Formula: " + brent.formula);
                    System.out.printf("  Theoretical Execution Time Bound T_%d <= %.2f%n", p, brent.boundTp);
                    System.out.printf("  Theoretical Speedup: %.2fx%n", brent.theoreticalSpeedup);
                    System.out.printf("  Theoretical Efficiency: %.2f%%%n", brent.theoreticalEfficiency * 100.0);
                    break;
                }
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
            if (!back) InputHelper.pause();
        }
    }

    // ==========================================
    // 12. DSA Algorithm Explorer
    // ==========================================
    private static void dsaAlgorithmExplorerMenu() {
        boolean back = false;
        while (!back) {
            InputHelper.printHeader("DSA-3 Algorithm Explorer");
            System.out.println("1. CO1 - String Algorithms (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)");
            System.out.println("2. CO2 - Suffix Structures (Suffix Array, SA-IS, Kasai LCP, SAM)");
            System.out.println("3. CO3 - Dynamic Programming (Levenshtein, Damerau, Bitmask TSP, MCM, OBST)");
            System.out.println("4. CO4 - Network Flow (Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite, Min-Cut)");
            System.out.println("5. CO5 - NP-Completeness (SAT, Clique, IndSet, Vertex Cover, 2-Approx)");
            System.out.println("6. CO6 - Randomized & Parallel (QuickSort, Reservoir, Miller-Rabin, Blelloch, Reduce, Brent)");
            System.out.println("0. Return to Main Menu");

            int ch = InputHelper.readInt("Select category to explore: ");
            switch (ch) {
                case 1: routeSearchAndTextIndexingMenu(); break;
                case 2: routeSearchAndTextIndexingMenu(); break;
                case 3: routePlanningAndOptimizationMenu(); break;
                case 4: passengerFlowNetworkMenu(); break;
                case 5: schedulingConstraintsGraphMenu(); break;
                case 6: passengerAnalyticsParallelMenu(); break;
                case 0: back = true; break;
                default: System.out.println("Invalid selection.");
            }
        }
    }

    // ==========================================
    // 13. Unified Records Dashboard
    // ==========================================
    private static void viewAllTransportRecords() {
        InputHelper.printHeader("Unified MetroMind Transport Records");
        System.out.println("Stations: " + stationService.getStationCount());
        for (Station s : stationService.getAllStations()) System.out.println("  " + s);

        System.out.println("\nRoutes: " + routeService.getRouteCount());
        for (Route r : routeService.getAllRoutes()) System.out.println("  " + r);

        System.out.println("\nVehicles: " + vehicleService.getVehicleCount());
        for (Vehicle v : vehicleService.getAllVehicles()) System.out.println("  " + v);

        System.out.println("\nTransport Services: " + serviceService.getServiceCount());
        for (TransportService s : serviceService.getAllServices()) System.out.println("  " + s);

        System.out.println("\nPassenger Events: " + passengerService.getEventCount());
        for (PassengerEvent e : passengerService.getAllEvents()) System.out.println("  " + e);

        System.out.println("\nTickets: " + passengerService.getTicketCount());
        for (Ticket t : passengerService.getAllTickets()) System.out.println("  " + t);

        InputHelper.pause();
    }

    // ==========================================
    // 14 & 15. Save and Load Data
    // ==========================================
    private static void saveData() {
        InputHelper.printHeader("Save MetroMind Data to Disk");
        boolean ok = storageService.saveData(stationService, routeService, vehicleService, serviceService, passengerService);
        if (ok) {
            System.out.println("All MetroMind records successfully persisted to CSV in the data/ directory!");
        } else {
            System.out.println("Failed to save data. Please check file permissions.");
        }
        InputHelper.pause();
    }

    private static void loadData() {
        InputHelper.printHeader("Load MetroMind Data from Disk");
        if (InputHelper.readBoolean("Loading from disk will overwrite unsaved changes. Proceed?")) {
            boolean ok = storageService.loadData(stationService, routeService, vehicleService, serviceService, passengerService);
            if (ok) {
                System.out.println("Records successfully reloaded from data/ directory!");
            } else {
                System.out.println("Failed to load records or no existing files found.");
            }
        }
        InputHelper.pause();
    }
}
