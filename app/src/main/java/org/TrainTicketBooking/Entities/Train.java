package org.TrainTicketBooking.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {
     @JsonProperty("train_id")
     private String trainId;

     @JsonProperty("train_no")
     private String trainNo;

     private List<List<Boolean>> seats;

     @JsonProperty("station_times")
     private Map<String, String> trainStationTime;

     private List<String >  stations;

     public String getTrainId() {
         return trainId;
     }
     public void setTrainId(String trainId) {
         this.trainId = trainId;
     }
     public String getTrainNo() {
         return trainNo;
     }
     public void setTrainNo(String trainNo) {
         this.trainNo = trainNo;
     }
     public List<List<Boolean>> getSeats() {
         return seats;
     }
     public void setSeats(List<List<Boolean>> seats) {
         this.seats = seats;

     }
     public Map<String, String> getStationTimes() {
         return trainStationTime;
     }
     public void setStationTimes(Map<String, String> trainStationTime) {
         this.trainStationTime = trainStationTime;
     }
     public List<String> getStations() {
         return stations;
     }
     public void setStations(List<String> stations) {
         this.stations = stations;
     }
     public String getStationTime(String station) {
         return trainStationTime != null ? trainStationTime.get(station) : null;
     }
     public void setStationTime(String station, String time) {
         if (trainStationTime != null) {
             this.trainStationTime.put(station, time);
         }
     }
     public String getTrainInfo() {
         return String.format("Train Id: %s Train No: %s", trainId, trainNo);
     }
}
