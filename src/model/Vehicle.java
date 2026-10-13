package model;

/**
 * Represents a transit vehicle (Metro train, bus, electric feeder) in MetroMind.
 */
public class Vehicle {
    private String id;
    private String name;
    private String vehicleType; // METRO_TRAIN, ELECTRIC_BUS, FEEDER_VAN
    private int capacity;
    private String assignedServiceId;
    private String status; // AVAILABLE, ASSIGNED, UNDER_MAINTENANCE

    public Vehicle(String id, String name, String vehicleType, int capacity, 
                   String assignedServiceId, String status) {
        this.id = id;
        this.name = name;
        this.vehicleType = vehicleType;
        this.capacity = Math.max(1, capacity);
        this.assignedServiceId = assignedServiceId != null ? assignedServiceId : "NONE";
        this.status = status != null ? status : "AVAILABLE";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public String getAssignedServiceId() {
        return assignedServiceId;
    }

    public void setAssignedServiceId(String assignedServiceId) {
        this.assignedServiceId = assignedServiceId != null ? assignedServiceId : "NONE";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status) && ("NONE".equalsIgnoreCase(assignedServiceId) || assignedServiceId.isEmpty());
    }

    @Override
    public String toString() {
        return String.format("[%s] %-22s | Type: %-13s | Cap: %-4d | Assigned: %-8s | Status: %s",
                id, name, vehicleType, capacity, assignedServiceId, status);
    }
}
