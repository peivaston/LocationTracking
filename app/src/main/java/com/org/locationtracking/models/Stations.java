package com.org.locationtracking.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Stations
{
    //station detail
    private String stationName;
    private String longitude;
    private String latitude;
    private long date;

    public Stations(String stationName, String latitude, String longitude, long date)
    {
        this.stationName = stationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public Stations()
    {
    }
}
