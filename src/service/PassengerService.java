package service;

import model.PassengerEvent;
import model.Ticket;
import algorithms.StringAlgorithms;
import java.util.*;

/**
 * Service managing passenger event records and tickets in MetroMind.
 * Supplies real streaming data for Randomized QuickSort, Reservoir Sampling,
 * Blelloch Parallel Scan, and Parallel Reduce.
 */
public class PassengerService {
    private final Map<String, PassengerEvent> eventMap = new LinkedHashMap<>();
    private final Map<String, Ticket> ticketMap = new LinkedHashMap<>();

    public boolean addPassengerEvent(PassengerEvent event) {
        if (event == null || event.getId() == null || event.getId().trim().isEmpty()) {
            return false;
        }
        if (eventMap.containsKey(event.getId())) {
            return false;
        }
        eventMap.put(event.getId(), event);
        return true;
    }

    public List<PassengerEvent> getAllEvents() {
        return new ArrayList<>(eventMap.values());
    }

    public PassengerEvent getEventById(String id) {
        if (id == null) return null;
        return eventMap.get(id);
    }

    public boolean addTicket(Ticket ticket) {
        if (ticket == null || ticket.getId() == null || ticket.getId().trim().isEmpty()) {
            return false;
        }
        if (ticketMap.containsKey(ticket.getId())) {
            return false;
        }
        ticketMap.put(ticket.getId(), ticket);
        return true;
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(ticketMap.values());
    }

    public Ticket getTicketById(String id) {
        if (id == null) return null;
        return ticketMap.get(id);
    }

    /**
     * Search tickets matching a pattern using Rabin-Karp rolling hash.
     */
    public List<Ticket> searchTicketsWithRabinKarp(String pattern) {
        List<Ticket> matches = new ArrayList<>();
        if (pattern == null || pattern.isEmpty()) return matches;

        for (Ticket t : ticketMap.values()) {
            String combined = t.getId() + " " + t.getPassengerRef() + " " + t.getServiceId();
            StringAlgorithms.RabinKarpResult res = StringAlgorithms.rabinKarpSearch(combined, pattern);
            if (!res.matchIndices.isEmpty()) {
                matches.add(t);
            }
        }
        return matches;
    }

    public int[] getPassengerCountsArray() {
        List<PassengerEvent> list = getAllEvents();
        int[] counts = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            counts[i] = list.get(i).getPassengerCount();
        }
        return counts;
    }

    public int getEventCount() {
        return eventMap.size();
    }

    public int getTicketCount() {
        return ticketMap.size();
    }

    public void clear() {
        eventMap.clear();
        ticketMap.clear();
    }
}
