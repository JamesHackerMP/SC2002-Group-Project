package entity.interfaces.project;

import entity.Flat;
import java.util.List;

public interface ProjectDetails {
    String getName();
    String getNeighborhood();
    int getTwoRoomUnits();
    int getTwoRoomPrice();
    int getThreeRoomUnits();
    int getThreeRoomPrice();
    List<Flat> getFlats();
    void setName(String name);
    void setNeighborhood(String neighborhood);
    void setTwoRoomUnits(int twoRoomUnits);
    void setTwoRoomPrice(int twoRoomPrice);
    void setThreeRoomUnits(int threeRoomUnits);
    void setThreeRoomPrice(int threeRoomPrice);
}