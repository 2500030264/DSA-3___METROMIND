package model;

/**
 * Represents a transit ticket issued to a passenger.
 * Used for ticket identifier pattern searching (Rabin-Karp) and revenue analytics.
 */
public class Ticket {
    private String id;
    private String passengerRef;
    private String serviceId;
    private String sourceStationId;
    private String destinationStationId;
    private double fareAmount;
    private String status; // CONFIRMED, COMPLETED, CANCELLED

    public Ticket(String id, String passengerRef, String serviceId, String sourceStationId,
                  String destinationStationId, double fareAmount, String status) {
        this.id = id;
        this.passengerRef = passengerRef != null ? passengerRef : "ANONYMOUS";
        this.serviceId = serviceId;
        this.sourceStationId = sourceStationId;
        this.destinationStationId = destinationStationId;
        this.fareAmount = Math.max(0.0, fareAmount);
        this.status = status != null ? status : "CONFIRMED";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerRef() {
        return passengerRef;
    }

    public void setPassengerRef(String passengerRef) {
        this.passengerRef = passengerRef;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSourceStationId() {
        return sourceStationId;
    }

    public void setSourceStationId(String sourceStationId) {
        this.sourceStationId = sourceStationId;
    }

    public String getDestinationStationId() {
        return destinationStationId;
    }

    public void setDestinationStationId(String destinationStationId) {
        this.destinationStationId = destinationStationId;
    }

    public double getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = Math.max(0.0, fareAmount);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%s] Passenger: %-15s | Service: %-6s | %s -> %s | Fare: Rs. %-5.1f | Status: %s",
                id, passengerRef, serviceId, sourceStationId, destinationStationId, fareAmount, status);
    }
}
