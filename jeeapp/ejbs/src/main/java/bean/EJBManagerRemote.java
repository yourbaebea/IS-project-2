package bean;

import java.util.Date;
import java.util.List;

import data.*;

public interface EJBManagerRemote {

    public void connect();
    public Manager registerManager(Manager manager);
    public Manager loginManager(String email, String password);
    public boolean createTrip(Trip trip);
    public List<Trip> listAllTrips();
    public List<Trip> listDailyTrips(Date start);
    public boolean deleteTrip(Trip trip);
    public void incrementWallet(Utilizador user ,Double value);
    public List<Trip> listTrips(Date start , Date end);
    public List<Object[]> listTopUsers();
    public List<Utilizador> getUsers(Trip trip);

}
