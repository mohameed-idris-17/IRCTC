package org.TrainTicketBooking.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.TrainTicketBooking.Entities.Ticket;
import org.TrainTicketBooking.Entities.Train;
import org.TrainTicketBooking.Entities.User;
import org.TrainTicketBooking.util.userServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserBookingService {

    private User user;

    private List<User> userList;
    private static final String USER_PATH = "D:\\eclipse\\workspaceE\\IRCTC\\app\\src\\main\\java\\org\\TrainTicketBooking\\localDB\\users.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserBookingService(User user1) throws IOException {
        this.userList = loadUsers();
        if (!loginUser(user1)) {
            throw new IOException("Login failed: Invalid username or password");
        }
    }

    public UserBookingService() throws IOException {
        this.userList = loadUsers();
    }

    public List<User> loadUsers() throws IOException {
        File users = new File(USER_PATH);
        return objectMapper.readValue(users, new TypeReference<List<User>>() {
        });
    }

    public Boolean loginUser(User loginAttempt) {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(loginAttempt.getName()) && userServiceUtil.checkPassword(loginAttempt.getPassword(), user1.getHashPassword());
        }).findFirst();
        if (foundUser.isPresent()) {
            this.user = foundUser.get();
            return true;
        }
        return false;
    }

    public Boolean signUpUser(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_PATH);
        objectMapper.writeValue(usersFile, userList); // serialization
    }

    public void fetchBooking() {
        user.printTickets();
    }

    public Boolean cancelBooking(String ticketId) throws IOException {
        for (int i = 0; i < user.getTicketBooking().size(); i++) {
            if (user.getTicketBooking().get(i).getTicketId().equals(ticketId)) {
                user.getTicketBooking().remove(i);
                
                // Update user in the list
                for (int j = 0; j < userList.size(); j++) {
                    if (userList.get(j).getUserId().equals(user.getUserId())) {
                        userList.set(j, user);
                        break;
                    }
                }
                
                saveUserListToFile();
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public List<Train> getTrains(String sour, String dest) throws IOException {
        TrainService trainService = new TrainService();
        List<Train> trainServices = trainService.searchTrains(sour, dest);
        return trainServices;
    }

    public List<List<Integer>> fetchSeats(Train train) {
        List<List<Integer>> seatDisplay = new ArrayList<>();
        List<List<Boolean>> seats = train.getSeats();
        for (List<Boolean> row : seats) {
            List<Integer> rowDisplay = new ArrayList<>();
            for (Boolean seat : row) {
                rowDisplay.add(seat ? 1 : 0);
            }
            seatDisplay.add(rowDisplay);
        }
        return seatDisplay;
    }

    public Boolean bookTrainSeat(Train train, int row, int col, String source, String destination) throws IOException {
        // Validate row and column
        if (row < 0 || row >= train.getSeats().size()) {
            return false;
        }
        if (col < 0 || col >= train.getSeats().get(row).size()) {
            return false;
        }
        
        // Check if seat is available
        if (train.getSeats().get(row).get(col)) {
            return false; // Seat already booked
        }
        
        // Book the seat
        train.getSeats().get(row).set(col, true);
        
        // Create a ticket
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID().toString());
        ticket.setUserId(user.getUserId());
        ticket.setSource(source);
        ticket.setDestination(destination);
        ticket.setDateTravel(java.time.LocalDateTime.now().toString());
        ticket.setTrain(train);
        
        // Add ticket to user's bookings
        user.getTicketBooking().add(ticket);
        
        // Update user in the list
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(user.getUserId())) {
                userList.set(i, user);
                break;
            }
        }
        
        // Save to file
        saveUserListToFile();
        
        return true;
    }

    public User getUser() {
        return user;
    }

}
