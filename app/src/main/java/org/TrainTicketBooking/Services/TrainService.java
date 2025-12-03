package org.TrainTicketBooking.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.TrainTicketBooking.Entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrainService {

    private List<Train> trainList;

    public static String Train_Path = "D:\\eclipse\\workspaceE\\IRCTC\\app\\src\\main\\java\\org\\TrainTicketBooking\\localDB\\trains.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrainService() throws IOException {
        loadTrains();
    }

    public void loadTrains() throws IOException {
        File train = new File(Train_Path);
        trainList = objectMapper.readValue(train,new TypeReference<List<Train>>() {});
    }

    public List<Train> searchTrains(String sour, String dest){
        return trainList.stream().filter(train -> validTrain(train,sour,dest)).toList();
    }
    public boolean validTrain(Train train,String sour,String dest){
        List<String> stations = train.getStations();
        int sourIndex = stations.indexOf(sour);
        int destIndex = stations.indexOf(dest);
        return (sourIndex != -1 && destIndex != -1 && sourIndex < destIndex);
    }

}
