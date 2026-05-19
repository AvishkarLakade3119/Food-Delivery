package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.Pattern;

/**
 * DTO for Opening Hours information in Restaurant requests
 */
public class OpeningHoursDto {
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String monday;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String tuesday;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String wednesday;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String thursday;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String friday;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String saturday;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$|^CLOSED$", 
             message = "Opening hours must be in format HH:MM-HH:MM or CLOSED")
    private String sunday;
    
    // Default constructor
    public OpeningHoursDto() {}
    
    // Constructor with all fields
    public OpeningHoursDto(String monday, String tuesday, String wednesday, String thursday, 
                          String friday, String saturday, String sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }
    
    // Getters and Setters
    public String getMonday() {
        return monday;
    }
    
    public void setMonday(String monday) {
        this.monday = monday;
    }
    
    public String getTuesday() {
        return tuesday;
    }
    
    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }
    
    public String getWednesday() {
        return wednesday;
    }
    
    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }
    
    public String getThursday() {
        return thursday;
    }
    
    public void setThursday(String thursday) {
        this.thursday = thursday;
    }
    
    public String getFriday() {
        return friday;
    }
    
    public void setFriday(String friday) {
        this.friday = friday;
    }
    
    public String getSaturday() {
        return saturday;
    }
    
    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }
    
    public String getSunday() {
        return sunday;
    }
    
    public void setSunday(String sunday) {
        this.sunday = sunday;
    }
    
    /**
     * Converts the opening hours to a formatted string
     * @return Formatted opening hours string
     */
    public String toFormattedString() {
        return String.format("Mon: %s, Tue: %s, Wed: %s, Thu: %s, Fri: %s, Sat: %s, Sun: %s",
            monday != null ? monday : "CLOSED",
            tuesday != null ? tuesday : "CLOSED",
            wednesday != null ? wednesday : "CLOSED",
            thursday != null ? thursday : "CLOSED",
            friday != null ? friday : "CLOSED",
            saturday != null ? saturday : "CLOSED",
            sunday != null ? sunday : "CLOSED");
    }
    
    @Override
    public String toString() {
        return "OpeningHoursDto{" +
                "monday='" + monday + '\'' +
                ", tuesday='" + tuesday + '\'' +
                ", wednesday='" + wednesday + '\'' +
                ", thursday='" + thursday + '\'' +
                ", friday='" + friday + '\'' +
                ", saturday='" + saturday + '\'' +
                ", sunday='" + sunday + '\'' +
                '}';
    }
}