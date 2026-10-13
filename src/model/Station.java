package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a transit station in the MetroMind platform.
 * Supports connection tracking, passenger count, and search frequency analytics.
 */
public class Station {
    private String id;
    private String name;
    private String city;
    private String stationType; // e.g., "CENTRAL_HUB", "TERMINAL", "TRANSIT_HUB", "SUBURBAN"
    private List<String> connectedStationIds;
    private int passengerCount;
    private String serviceInfo;
    private int searchFrequency; // Used for Optimal BST & frequency analytics

    public Station(String id, String name, String city, String stationType, 
                   List<String> connectedStationIds, int passengerCount, String serviceInfo) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.stationType = stationType;
        this.connectedStationIds = connectedStationIds != null ? new ArrayList<>(connectedStationIds) : new ArrayList<>();
        this.passengerCount = Math.max(0, passengerCount);
        this.serviceInfo = serviceInfo != null ? serviceInfo : "";
        this.searchFrequency = 1;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    public List<String> getConnectedStationIds() {
        return Collections.unmodifiableList(connectedStationIds);
    }

    public void addConnectedStation(String stationId) {
        if (stationId != null && !stationId.trim().isEmpty() && !connectedStationIds.contains(stationId)) {
            connectedStationIds.add(stationId);
        }
    }

    public void removeConnectedStation(String stationId) {
        connectedStationIds.remove(stationId);
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = Math.max(0, passengerCount);
    }

    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public int getSearchFrequency() {
        return searchFrequency;
    }

    public void incrementSearchFrequency() {
        this.searchFrequency++;
    }

    public void setSearchFrequency(int searchFrequency) {
        this.searchFrequency = Math.max(1, searchFrequency);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-25s | City: %-12s | Type: %-12s | Passengers: %-5d | Conns: %s",
                id, name, city, stationType, passengerCount, connectedStationIds);
    }
}
