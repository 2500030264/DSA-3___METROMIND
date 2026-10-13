package service;

import model.Route;
import model.Station;
import algorithms.StringAlgorithms;
import algorithms.SuffixStructures;
import java.util.*;

/**
 * Service managing transit routes in MetroMind.
 * Supports station dependency verification, pathfinding across the network graph,
 * and text-indexing search on route descriptions.
 */
public class RouteService {
    private final Map<String, Route> routeMap = new LinkedHashMap<>();

    public boolean addRoute(Route route, StationService stationService) {
        if (route == null || route.getId() == null || route.getId().trim().isEmpty()) {
            return false;
        }
        if (routeMap.containsKey(route.getId())) {
            return false;
        }

        // Validate stations exist
        if (stationService != null) {
            if (stationService.getStationById(route.getSourceStationId()) == null) return false;
            if (stationService.getStationById(route.getDestinationStationId()) == null) return false;
            for (String inter : route.getIntermediateStationIds()) {
                if (stationService.getStationById(inter) == null) return false;
            }
        }

        routeMap.put(route.getId(), route);

        // Update station connections
        if (stationService != null) {
            List<String> path = route.getFullStationPath();
            for (int i = 0; i < path.size() - 1; i++) {
                stationService.connectStations(path.get(i), path.get(i + 1));
            }
        }
        return true;
    }

    public List<Route> getAllRoutes() {
        return new ArrayList<>(routeMap.values());
    }

    public Route getRouteById(String id) {
        if (id == null) return null;
        return routeMap.get(id);
    }

    public List<Route> getRoutesServingStation(String stationId) {
        List<Route> results = new ArrayList<>();
        if (stationId == null) return results;
        for (Route r : routeMap.values()) {
            if (r.getFullStationPath().contains(stationId)) {
                results.add(r);
            }
        }
        return results;
    }

    public List<Route> findRoutesBetween(String sourceId, String destId) {
        List<Route> direct = new ArrayList<>();
        for (Route r : routeMap.values()) {
            List<String> path = r.getFullStationPath();
            int sIdx = path.indexOf(sourceId);
            int dIdx = path.indexOf(destId);
            if (sIdx != -1 && dIdx != -1 && sIdx < dIdx) {
                direct.add(r);
            }
        }
        return direct;
    }

    public boolean isStationUsedInRoutes(String stationId) {
        for (Route r : routeMap.values()) {
            if (r.getFullStationPath().contains(stationId)) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteRoute(String id) {
        return routeMap.remove(id) != null;
    }

    public boolean updateRoute(String id, String newName, double newDistance, int newTime, double newCost, String newStatus, String newDesc) {
        Route r = routeMap.get(id);
        if (r == null) return false;
        if (newName != null && !newName.trim().isEmpty()) r.setName(newName);
        if (newDistance > 0) r.setDistanceKm(newDistance);
        if (newTime > 0) r.setEstimatedTimeMinutes(newTime);
        if (newCost >= 0) r.setTravelCost(newCost);
        if (newStatus != null) r.setStatus(newStatus);
        if (newDesc != null) r.setDescription(newDesc);
        return true;
    }

    /**
     * Builds an adjacency cost matrix between given stations for Bitmask DP TSP.
     * Uses Floyd-Warshall or direct edge weights across stored routes.
     */
    public double[][] buildDistanceMatrix(List<String> stationIds) {
        int k = stationIds.size();
        double[][] dist = new double[k][k];
        for (int i = 0; i < k; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            dist[i][i] = 0;
        }

        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < k; i++) indexMap.put(stationIds.get(i), i);

        // Add direct edge costs from routes
        for (Route r : routeMap.values()) {
            List<String> path = r.getFullStationPath();
            double segmentDist = r.getDistanceKm() / Math.max(1, (path.size() - 1));
            for (int i = 0; i < path.size() - 1; i++) {
                String u = path.get(i);
                String v = path.get(i + 1);
                Integer ui = indexMap.get(u);
                Integer vi = indexMap.get(v);
                if (ui != null && vi != null) {
                    dist[ui][vi] = Math.min(dist[ui][vi], segmentDist);
                    dist[vi][ui] = Math.min(dist[vi][ui], segmentDist);
                }
            }
        }

        // Run Floyd-Warshall across these stations to find shortest multi-hop paths
        for (int mid = 0; mid < k; mid++) {
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    if (dist[i][mid] < Double.POSITIVE_INFINITY && dist[mid][j] < Double.POSITIVE_INFINITY) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][mid] + dist[mid][j]);
                    }
                }
            }
        }

        return dist;
    }

    public int getRouteCount() {
        return routeMap.size();
    }

    public void clear() {
        routeMap.clear();
    }
}
