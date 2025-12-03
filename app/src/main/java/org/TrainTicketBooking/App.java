package org.TrainTicketBooking;


import java.io.IOException;
import java.util.*;
import org.TrainTicketBooking.Entities.Train;
import org.TrainTicketBooking.Entities.User;
import org.TrainTicketBooking.Services.UserBookingService;
import org.TrainTicketBooking.util.userServiceUtil;

public class App {


    public static void main(String[] args) throws IOException {
        System.out.println("running Ticket Booking App");
        int option =0 ;
        Scanner sc = new Scanner(System.in);
        UserBookingService userBookingService = null;
        Train trainSelectedForBooking = null;
        try{
            userBookingService = new UserBookingService();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("something went wrong");
        }
        while(option!=7){
            System.out.println("Enter your option");
            System.out.println("1. sign up");
            System.out.println("2. login");
            System.out.println("3. Fetch Booking Details");
            System.out.println("4. Search Trains");
            System.out.println("5. Book my seat");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Exit");
            option = sc.nextInt();
            sc.nextLine(); // consume newline
            switch(option){
                case 1:
                    System.out.println("Enter your name");
                    String nameToSignUp  = sc.nextLine();
                    System.out.println("Enter your Password");
                    String passwordToSignUp = sc.nextLine();
                    if (nameToSignUp.trim().isEmpty() || passwordToSignUp.trim().isEmpty()) {
                        System.out.println("Name and password cannot be empty");
                        break;
                    }
                    User userToSignUp = new  User(nameToSignUp,passwordToSignUp, userServiceUtil.hashPassword(passwordToSignUp), UUID.randomUUID().toString(),new ArrayList<>());
                    if (userBookingService != null) {
                        Boolean signupSuccess = userBookingService.signUpUser(userToSignUp);
                        if (signupSuccess) {
                            System.out.println("Sign up successful! Please login to continue.");
                        } else {
                            System.out.println("Sign up failed. Please try again.");
                        }
                    } else {
                        System.out.println("Service unavailable");
                    }
                    break;
                case 2:
                    System.out.println("Enter your name");
                    String nametologin = sc.nextLine();
                    System.out.println("Enter your Password");
                    String passwordtologin = sc.nextLine();
                    if (nametologin.trim().isEmpty() || passwordtologin.trim().isEmpty()) {
                        System.out.println("Name and password cannot be empty");
                        break;
                    }
                    User userToLogin = new User(nametologin,passwordtologin, userServiceUtil.hashPassword(passwordtologin), UUID.randomUUID().toString(),new ArrayList<>());
                    try{
                        userBookingService = new UserBookingService(userToLogin);
                        System.out.println("Login successful! Welcome " + nametologin);
                    } catch (IOException e) {
                        System.out.println("Login failed: " + e.getMessage());
                    }
                    break;
                case 3:
                    if (userBookingService == null || userBookingService.getUser() == null) {
                        System.out.println("Please login first (option 2)");
                        break;
                    }
                    System.out.println("fetch booking details");
                    userBookingService.fetchBooking();
                    break;

                case 4:
                    if (userBookingService == null) {
                        System.out.println("Service unavailable");
                        break;
                    }
                    System.out.println("Type your source station");
                    String source = sc.next();
                    System.out.println("Type your destination station");
                    String dest = sc.next();
                    try {
                        List<Train> trains = userBookingService.getTrains(source, dest);
                        if (trains.isEmpty()) {
                            System.out.println("No trains found for the given route");
                            break;
                        }
                        int index = 1;
                        for (Train t: trains){
                            System.out.println(index+" Train id : "+t.getTrainId());
                            for (Map.Entry<String, String> entry: t.getStationTimes().entrySet()){
                                System.out.println("station "+entry.getKey()+" time: "+entry.getValue());
                            }
                            index++;
                        }
                        System.out.println("Select a train by typing 1,2,3...");
                        int trainIndex = sc.nextInt() - 1;
                        if (trainIndex >= 0 && trainIndex < trains.size()) {
                            trainSelectedForBooking = trains.get(trainIndex);
                            System.out.println("Train selected: " + trainSelectedForBooking.getTrainInfo());
                        } else {
                            System.out.println("Invalid train selection");
                        }
                    } catch (IOException e) {
                        System.out.println("Error searching trains: " + e.getMessage());
                    }
                    break;
                case 5:
                    if (userBookingService == null || userBookingService.getUser() == null) {
                        System.out.println("Please login first (option 2)");
                        break;
                    }
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please search and select a train first (option 4)");
                        break;
                    }
                    System.out.println("Select a seat out of these seats (0=available, 1=booked)");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    for (int i = 0; i < seats.size(); i++) {
                        System.out.print("Row " + i + ": ");
                        for (Integer val: seats.get(i)){
                            System.out.print(val+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row");
                    int row = sc.nextInt();
                    System.out.println("Enter the column");
                    int col = sc.nextInt();
                    System.out.println("Enter source station");
                    String bookingSource = sc.next();
                    System.out.println("Enter destination station");
                    String bookingDest = sc.next();
                    System.out.println("Booking your seat....");
                    try {
                        Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col, bookingSource, bookingDest);
                        if(booked.equals(Boolean.TRUE)){
                            System.out.println("Booked! Enjoy your journey");
                        }else{
                            System.out.println("Can't book this seat - it may be already booked or invalid");
                        }
                    } catch (IOException e) {
                        System.out.println("Booking failed: " + e.getMessage());
                    }
                    break;
                case 6:
                    if (userBookingService == null || userBookingService.getUser() == null) {
                        System.out.println("Please login first (option 2)");
                        break;
                    }
                    System.out.println("Enter ticket ID to cancel");
                    String ticketIdToCancel = sc.next();
                    try {
                        Boolean cancelled = userBookingService.cancelBooking(ticketIdToCancel);
                        if (cancelled) {
                            System.out.println("Booking cancelled successfully");
                        } else {
                            System.out.println("Ticket not found");
                        }
                    } catch (IOException e) {
                        System.out.println("Cancellation failed: " + e.getMessage());
                    }
                    break;
                default:
                    break;
            }

        }
        System.out.println("Thank you for using IRCTC Ticket Booking App!");
        sc.close();
    }
}
