package model;

/**
 * Represents a passenger event (boarding, alighting, turnstile tap) at a station.
 * Used for randomized algorithms (Reservoir Sampling, QuickSort) and parallel stream analytics (Blelloch Scan, Parallel Reduce).
 */
public class PassengerEvent {
    private String id;
    private String serviceId;
    private String stationId;
    private String timestamp;
    private String eventType; // BOARDING, ALIGHTING, TRANSFER
    private int passengerCount;

    public PassengerEvent(String id, String serviceId, String stationId, String timestamp,
                          String eventType, int passengerCount) {
        this.id = id;
        this.serviceId = serviceId;
        this.stationId = stationId;
        this.timestamp = timestamp != null ? timestamp : "12:00:00";
        this.eventType = eventType != null ? eventType : "BOARDING";
        this.passengerCount = Math.max(0, passengerCount);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = Math.max(0, passengerCount);
    }

    @Override
    public String toString() {
        return String.format("[%s] Time: %s | Service: %-6s | Station: %-6s | Type: %-9s | Passengers: %-4d",
                id, timestamp, serviceId, stationId, eventType, passengerCount);
    }
}
