package org.TrainTicketBooking.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket {
    @JsonProperty("ticket_id")
    private String ticketId;
    @JsonProperty("user_id")
    private String  userId;
    private String source;
    private String destination;
    @JsonProperty("date_of_travel")
    private String  dateTravel;
    private Train train;

    public String getTicketId() {
        return ticketId;
    }
    public String getTicketInfo(){
        return String.format("TicketID : %s belongs to User %s from %s to %s on %s",ticketId,userId,source,destination,dateTravel);
    }
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSource() {
        return source;

    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getDateTravel() {
        return dateTravel;
    }
    public void setDateTravel(String dateTravel) {
        this.dateTravel = dateTravel;
    }
    public Train getTrain() {
        return train;
    }
    public void setTrain(Train train) {
        this.train = train;

    }

}
