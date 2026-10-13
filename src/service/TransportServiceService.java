package service;

import model.TransportService;
import model.Vehicle;
import java.util.*;

/**
 * Service managing scheduled transit trips/services in MetroMind.
 * Computes exact occupancy percentages based on assigned vehicle capacity.
 */
public class TransportServiceService {
    private final Map<String, TransportService> serviceMap = new LinkedHashMap<>();

    public boolean addService(TransportService service, RouteService routeService) {
        if (service == null || service.getId() == null || service.getId().trim().isEmpty()) {
            return false;
        }
        if (serviceMap.containsKey(service.getId())) {
            return false;
        }
        if (routeService != null && routeService.getRouteById(service.getRouteId()) == null) {
            return false; // Route must exist
        }
        serviceMap.put(service.getId(), service);
        return true;
    }

    public List<TransportService> getAllServices() {
        return new ArrayList<>(serviceMap.values());
    }

    public TransportService getServiceById(String id) {
        if (id == null) return null;
        return serviceMap.get(id);
    }

    public List<TransportService> getServicesForRoute(String routeId) {
        List<TransportService> results = new ArrayList<>();
        if (routeId == null) return results;
        for (TransportService s : serviceMap.values()) {
            if (s.getRouteId().equalsIgnoreCase(routeId)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<TransportService> getUnassignedServices() {
        List<TransportService> results = new ArrayList<>();
        for (TransportService s : serviceMap.values()) {
            if ("UNASSIGNED".equalsIgnoreCase(s.getAssignedVehicleId()) || s.getAssignedVehicleId().isEmpty()) {
                results.add(s);
            }
        }
        return results;
    }

    public boolean updateService(String id, String depTime, String arrTime, int minCap, int currentOcc, String status) {
        TransportService s = serviceMap.get(id);
        if (s == null) return false;
        if (depTime != null && !depTime.trim().isEmpty()) s.setDepartureTime(depTime);
        if (arrTime != null && !arrTime.trim().isEmpty()) s.setArrivalTime(arrTime);
        if (minCap > 0) s.setRequiredCapacity(minCap);
        if (currentOcc >= 0) s.setCurrentOccupancy(currentOcc);
        if (status != null) s.setStatus(status);
        return true;
    }

    public boolean assignVehicle(String serviceId, String vehicleId) {
        TransportService s = serviceMap.get(serviceId);
        if (s == null) return false;
        s.setAssignedVehicleId(vehicleId);
        return true;
    }

    public boolean deleteService(String id) {
        return serviceMap.remove(id) != null;
    }

    public double calculateOccupancy(String serviceId, VehicleService vehicleService) {
        TransportService s = serviceMap.get(serviceId);
        if (s == null || vehicleService == null) return 0.0;
        Vehicle v = vehicleService.getVehicleById(s.getAssignedVehicleId());
        int cap = (v != null) ? v.getCapacity() : s.getRequiredCapacity();
        return s.getOccupancyPercentage(cap);
    }

    public int getServiceCount() {
        return serviceMap.size();
    }

    public void clear() {
        serviceMap.clear();
    }
}
