package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a transit route connecting stations.
 * Contains ordered stations, distance, travel time, cost, description, and status.
 */
public class Route {
    private String id;
    private String name;
    private String sourceStationId;
    private String destinationStationId;
    private List<String> intermediateStationIds;
    private double distanceKm;
    private int estimatedTimeMinutes;
    private double travelCost;
    private String description;
    private String status; // "ACTIVE", "UNDER_MAINTENANCE", "SUSPENDED"

    public Route(String id, String name, String sourceStationId, String destinationStationId,
                 List<String> intermediateStationIds, double distanceKm, int estimatedTimeMinutes,
                 double travelCost, String description, String status) {
        this.id = id;
        this.name = name;
        this.sourceStationId = sourceStationId;
        this.destinationStationId = destinationStationId;
        this.intermediateStationIds = intermediateStationIds != null ? new ArrayList<>(intermediateStationIds) : new ArrayList<>();
        this.distanceKm = Math.max(0.1, distanceKm);
        this.estimatedTimeMinutes = Math.max(1, estimatedTimeMinutes);
        this.travelCost = Math.max(0.0, travelCost);
        this.description = description != null ? description : "";
        this.status = status != null ? status : "ACTIVE";
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

    public List<String> getIntermediateStationIds() {
        return Collections.unmodifiableList(intermediateStationIds);
    }

    public void setIntermediateStationIds(List<String> intermediateStationIds) {
        this.intermediateStationIds = intermediateStationIds != null ? new ArrayList<>(intermediateStationIds) : new ArrayList<>();
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = Math.max(0.1, distanceKm);
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = Math.max(1, estimatedTimeMinutes);
    }

    public double getTravelCost() {
        return travelCost;
    }

    public void setTravelCost(double travelCost) {
        this.travelCost = Math.max(0.0, travelCost);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns full ordered station sequence: source -> intermediates -> destination
     */
    public List<String> getFullStationPath() {
        List<String> path = new ArrayList<>();
        path.add(sourceStationId);
        path.addAll(intermediateStationIds);
        path.add(destinationStationId);
        return path;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-28s | %s -> %s | Dist: %-5.1f km | Time: %-3d min | Cost: Rs. %-5.1f | Status: %s",
                id, name, sourceStationId, destinationStationId, distanceKm, estimatedTimeMinutes, travelCost, status);
    }
}
