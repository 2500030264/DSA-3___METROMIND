package service;

import model.Station;
import algorithms.DynamicProgramming;
import java.util.*;

/**
 * Service managing transit stations in MetroMind.
 * Integrates Levenshtein/Damerau-Levenshtein suggestions on misspelled lookups,
 * safe deletion checking, connectivity validation, and search frequency analytics.
 */
public class StationService {
    private final Map<String, Station> stationMap = new LinkedHashMap<>();

    public boolean addStation(Station station) {
        if (station == null || station.getId() == null || station.getId().trim().isEmpty()) {
            return false;
        }
        if (stationMap.containsKey(station.getId())) {
            return false;
        }
        stationMap.put(station.getId(), station);
        return true;
    }

    public List<Station> getAllStations() {
        return new ArrayList<>(stationMap.values());
    }

    public Station getStationById(String id) {
        if (id == null) return null;
        Station st = stationMap.get(id);
        if (st != null) {
            st.incrementSearchFrequency();
        }
        return st;
    }

    public Station getStationByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        for (Station s : stationMap.values()) {
            if (s.getName().equalsIgnoreCase(name.trim())) {
                s.incrementSearchFrequency();
                return s;
            }
        }
        return null;
    }

    public List<Station> searchStationsByNamePrefix(String query) {
        List<Station> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;
        String q = query.trim().toLowerCase();
        for (Station s : stationMap.values()) {
            if (s.getName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Calculates spelling suggestions for misspelled station names using Levenshtein distance.
     */
    public List<DynamicProgramming.SpellingSuggestion> suggestSimilarNames(String query, int maxDist) {
        List<String> names = new ArrayList<>();
        for (Station s : stationMap.values()) {
            names.add(s.getName());
        }
        return DynamicProgramming.suggestStationNames(query, names, maxDist);
    }

    public boolean updateStation(String id, String newName, String newCity, String newType, int newPassengerCount, String newServiceInfo) {
        Station st = stationMap.get(id);
        if (st == null) return false;
        if (newName != null && !newName.trim().isEmpty()) st.setName(newName);
        if (newCity != null && !newCity.trim().isEmpty()) st.setCity(newCity);
        if (newType != null && !newType.trim().isEmpty()) st.setStationType(newType);
        st.setPassengerCount(Math.max(0, newPassengerCount));
        if (newServiceInfo != null) st.setServiceInfo(newServiceInfo);
        return true;
    }

    /**
     * Safely deletes station after ensuring no route references it.
     */
    public boolean deleteStation(String id, RouteService routeService) {
        if (!stationMap.containsKey(id)) return false;
        // Verify route dependencies
        if (routeService != null && routeService.isStationUsedInRoutes(id)) {
            return false; // Cannot delete: active routes depend on this station
        }
        stationMap.remove(id);
        // Remove from connected lists of other stations
        for (Station s : stationMap.values()) {
            s.removeConnectedStation(id);
        }
        return true;
    }

    public List<Station> getConnectedStations(String id) {
        List<Station> connected = new ArrayList<>();
        Station source = stationMap.get(id);
        if (source != null) {
            for (String connId : source.getConnectedStationIds()) {
                Station target = stationMap.get(connId);
                if (target != null) connected.add(target);
            }
        }
        return connected;
    }

    public void connectStations(String id1, String id2) {
        Station s1 = stationMap.get(id1);
        Station s2 = stationMap.get(id2);
        if (s1 != null && s2 != null && !id1.equals(id2)) {
            s1.addConnectedStation(id2);
            s2.addConnectedStation(id1);
        }
    }

    public int getStationCount() {
        return stationMap.size();
    }

    public void clear() {
        stationMap.clear();
    }
}
