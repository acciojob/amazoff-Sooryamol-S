package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository() {
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
    }

    public void saveOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
        partnerToOrderMap.put(partnerId, new HashSet<>());
    }

    public void saveOrderPartnerMap(String orderId, String partnerId) {
        if (orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)) {
            partnerToOrderMap.get(partnerId).add(orderId);
            orderToPartnerMap.put(orderId, partnerId);
        }
    }

    public Order findOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId) {
        if (partnerToOrderMap.containsKey(partnerId)) {
            return partnerToOrderMap.get(partnerId).size();
        }
        return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId) {
        if (partnerToOrderMap.containsKey(partnerId)) {
            return new ArrayList<>(partnerToOrderMap.get(partnerId));
        }
        return new ArrayList<>();
    }

    public List<String> findAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId) {
        if (partnerMap.containsKey(partnerId)) {
            // Unassign all orders from this partner
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            for (String orderId : orders) {
                orderToPartnerMap.remove(orderId);
            }
            partnerToOrderMap.remove(partnerId);
            partnerMap.remove(partnerId);
        }
    }

    public void deleteOrder(String orderId) {
        if (orderMap.containsKey(orderId)) {
            // Unassign this order from its partner
            if (orderToPartnerMap.containsKey(orderId)) {
                String partnerId = orderToPartnerMap.get(orderId);
                partnerToOrderMap.get(partnerId).remove(orderId);
                orderToPartnerMap.remove(orderId);
            }
            orderMap.remove(orderId);
        }
    }

    public Integer findCountOfUnassignedOrders() {
        return orderMap.size() - orderToPartnerMap.size();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId) {
        int givenTime = convertTimeToInt(timeString);
        if (partnerToOrderMap.containsKey(partnerId)) {
            int count = 0;
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                Order order = orderMap.get(orderId);
                if (order.getDeliveryTime() > givenTime) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId) {
        if (partnerToOrderMap.containsKey(partnerId)) {
            int latestTime = 0;
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                Order order = orderMap.get(orderId);
                if (order.getDeliveryTime() > latestTime) {
                    latestTime = order.getDeliveryTime();
                }
            }
            return convertTimeToString(latestTime);
        }
        return null;
    }

    private int convertTimeToInt(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }

    private String convertTimeToString(int time) {
        int hours = time / 60;
        int minutes = time % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}
