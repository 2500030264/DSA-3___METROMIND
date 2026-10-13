package service;

import model.Vehicle;
import java.util.*;

/**
 * Service managing transit vehicles in MetroMind.
 * Handles capacity verification, availability status, and service assignments.
 */
public class VehicleService {
    private final Map<String, Vehicle> vehicleMap = new LinkedHashMap<>();

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null || vehicle.getId().trim().isEmpty()) {
            return false;
        }
        if (vehicleMap.containsKey(vehicle.getId())) {
            return false;
        }
        vehicleMap.put(vehicle.getId(), vehicle);
        return true;
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicleMap.values());
    }

    public Vehicle getVehicleById(String id) {
        if (id == null) return null;
        return vehicleMap.get(id);
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> available = new ArrayList<>();
        for (Vehicle v : vehicleMap.values()) {
            if (v.isAvailable()) {
                available.add(v);
            }
        }
        return available;
    }

    public boolean updateVehicle(String id, String newName, String newType, int newCapacity, String newStatus) {
        Vehicle v = vehicleMap.get(id);
        if (v == null) return false;
        if (newName != null && !newName.trim().isEmpty()) v.setName(newName);
        if (newType != null && !newType.trim().isEmpty()) v.setVehicleType(newType);
        if (newCapacity > 0) v.setCapacity(newCapacity);
        if (newStatus != null) v.setStatus(newStatus);
        return true;
    }

    public boolean assignVehicleToService(String vehicleId, String serviceId) {
        Vehicle v = vehicleMap.get(vehicleId);
        if (v == null) return false;
        v.setAssignedServiceId(serviceId);
        v.setStatus("ASSIGNED");
        return true;
    }

    public boolean releaseVehicle(String vehicleId) {
        Vehicle v = vehicleMap.get(vehicleId);
        if (v == null) return false;
        v.setAssignedServiceId("NONE");
        v.setStatus("AVAILABLE");
        return true;
    }

    public boolean deleteVehicle(String id) {
        return vehicleMap.remove(id) != null;
    }

    public int getVehicleCount() {
        return vehicleMap.size();
    }

    public void clear() {
        vehicleMap.clear();
    }
}
