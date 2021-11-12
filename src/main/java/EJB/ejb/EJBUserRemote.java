package EJB.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import JPA.jpa.Trip;
import JPA.jpa.Utilizador;
import JPA.jpa.Manager;
import JPA.jpa.Trip;

@Remote
public interface EJBUserRemote {

	public void connect();
    public Utilizador registerUser(Utilizador user);
    public Utilizador loginUser(String email, String password);
    public void incrementWallet(Utilizador user ,Double value);
    public boolean changeData(Utilizador user, String change , Integer option);
    public List<Trip> listTrips(Date start , Date end);
    public List<Trip> listUserTrips(Long user_id);
    public List<Trip> listAvailableTrips(String origin , String destination);
	public int createTicket(Trip trip, Utilizador user);
    public String returnTicket(Trip trip, Utilizador user);
	
}

