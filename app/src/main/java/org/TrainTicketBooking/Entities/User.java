package org.TrainTicketBooking.Entities;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
public class User {
    private String name;
    private String password;
    @JsonProperty("hashed_password")
    private String hashPassword;
    @JsonProperty("tickets_booked")
    private List<Ticket> TicketBooking;
    @JsonProperty("user_id")
    private String userId;
    public User(String name, String password,String hashPassword, String userId, List<Ticket> TicketBooking ){
        this.name = name;
        this.password = password;
        this.userId = userId;
        this.TicketBooking = TicketBooking;
        this.hashPassword = hashPassword;
    }

    public User() {}

    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
    public String getHashPassword() {
        return hashPassword;
    }
    public List<Ticket> getTicketBooking() {
        return TicketBooking;
    }
    public String getUserId() {
        return userId;
    }

    public void printTickets(){
        if (TicketBooking == null || TicketBooking.isEmpty()) {
            System.out.println("No tickets booked yet.");
            return;
        }
        System.out.println("Your bookings:");
        for(int i=0 ;i<TicketBooking.size();i++){
            System.out.println((i+1) + ". " + TicketBooking.get(i).getTicketInfo());
        }
    }


}
