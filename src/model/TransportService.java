package model;

/**
 * Represents a scheduled transit trip/service running on a designated route.
 */
public class TransportService {
    private String id;
    private String serviceCode;
    private String routeId;
    private String departureTime;
    private String arrivalTime;
    private int requiredCapacity;
    private int currentOccupancy;
    private String assignedVehicleId;
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    public TransportService(String id, String serviceCode, String routeId, String departureTime,
                            String arrivalTime, int requiredCapacity, int currentOccupancy,
                            String assignedVehicleId, String status) {
        this.id = id;
        this.serviceCode = serviceCode != null ? serviceCode : id;
        this.routeId = routeId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.requiredCapacity = Math.max(1, requiredCapacity);
        this.currentOccupancy = Math.max(0, currentOccupancy);
        this.assignedVehicleId = assignedVehicleId != null ? assignedVehicleId : "UNASSIGNED";
        this.status = status != null ? status : "SCHEDULED";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getRequiredCapacity() {
        return requiredCapacity;
    }

    public void setRequiredCapacity(int requiredCapacity) {
        this.requiredCapacity = Math.max(1, requiredCapacity);
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = Math.max(0, currentOccupancy);
    }

    public String getAssignedVehicleId() {
        return assignedVehicleId;
    }

    public void setAssignedVehicleId(String assignedVehicleId) {
        this.assignedVehicleId = assignedVehicleId != null ? assignedVehicleId : "UNASSIGNED";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Safe occupancy percentage calculation using specified capacity.
     */
    public double getOccupancyPercentage(int vehicleCapacity) {
        if (vehicleCapacity <= 0) return 0.0;
        return ((double) currentOccupancy / vehicleCapacity) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("[%s] Code: %-8s | Route: %-6s | Time: %s-%s | MinCap: %-4d | Occ: %-4d | Veh: %-7s | Status: %s",
                id, serviceCode, routeId, departureTime, arrivalTime, requiredCapacity, currentOccupancy, assignedVehicleId, status);
    }
}
