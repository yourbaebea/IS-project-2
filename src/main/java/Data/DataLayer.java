package Data;

import javax.persistence.*;

import Classes.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.System.*;

/**
 * Session Bean implementation class Bean
 */
//@Stateless
//@LocalBean
public class DataLayer{

	private EntityManagerFactory emf;
    private EntityManager em;
    public DataLayer() {
        super();
        try {
            emf = Persistence.createEntityManagerFactory("Bus");
            em = emf.createEntityManager();
        }catch(Exception e){
           System.out.println(e);
        }
    }

    /*------------------------------------------------USER FUNCTIONS--------------------------------------------------------------*/

    //checks if email is already in, if it isnt we add the user
    public Utilizador registerUser(Utilizador user) {
        
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u WHERE u.email=:email", Utilizador.class)
                .setParameter("email",user.getEmail());
        List<Utilizador> u= q.getResultList();
        if(u.size()==0){
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
        else{
            return null;
        }
    }

    //we need to convert hash to string, password is not yet saved in hash
    public Utilizador loginUser(String email, String password){
        try{
            TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u WHERE u.email=:email", Utilizador.class)
                .setParameter("email",email);
            Utilizador u= q.getSingleResult();
            if(u.getPassword().equals(password)) return u; //HERE
            return null;
        } catch(NoResultException e) {
            return null;
        }
    }

    public void incrementWallet(Utilizador user ,Double value){
        em.getTransaction().begin();
        user.balanceWallet(value);
        em.getTransaction().commit();

    }

    public void changeData(Utilizador user, String change , Integer option){
        em.getTransaction().begin();
        switch(option){
            case 1: user.setName(change); break;
            case 2: user.setPassword(change); break;
            case 3: user.setEmail(change); break;
            case 4: user.setAddress(change); break;
            case 5: user.setPhone(Integer.parseInt(change)); break;
        }
        em.getTransaction().commit();
    }

    //List trips by date
    public List<Trip> listTrips(Date start , Date end){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.time>=:start AND t.time<:end ORDER BY t.time", Trip.class)
        .setParameter("start",start)
        .setParameter("end",end);

        return q.getResultList();
    }

    // list all future trips of the user
    public List<Trip> listUserTrips(Long user_id) {

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t INNER JOIN Ticket t1 ON t.id = t1.trip.id WHERE t1.buyer_id =:user_id AND t.time > CURRENT_DATE ORDER BY t.time", Trip.class)
                .setParameter("user_id", user_id);

        return q.getResultList();
    }

    //list all future trips
    public List<Trip> listAvailableTrips(String origin , String destination){

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.destination=:destination AND t.origin =:origin AND t.time > current_date AND (t.capacity - t.occupancy> 0) ORDER BY t.time", Trip.class)
                .setParameter("destination",destination)
                .setParameter("origin",origin);

        return q.getResultList();

    }

    //create a new ticket
    public String createTicket(Trip trip, Utilizador user){        
        if(user.checkWallet( -trip.getPrice())){

            if(trip.buyTicket(user.getId())){
                user.balanceWallet(-trip.getPrice());
                em.persist(user);
                em.persist(trip); // im not sure here tho
                return "Ticket was purchased!";
            }
            return "Ticket was not purchased, you already have a ticket for this trip...";
            
        }
        return  "Wallet problems," + user.getWallet() +" is not enought to purchase this trip, please transfer some money to your account and try again";
    }

    //returns the ticket value and deletes ticket from db
    public String returnTicket(Trip trip, Utilizador user){

        TypedQuery<Ticket> q = em.createQuery("SELECT DISTINCT t1 FROM Ticket t1 WHERE t1.buyer_id =:user_id AND t1.trip.id=:trip_id", Ticket.class)
                .setParameter("user_id", user.getId())
                .setParameter("trip_id", trip.getId());
        
        Ticket t;
        try{
            t=  q.getSingleResult();
        }
        catch(NoResultException e){
            return "Ticket was not return, this actually should never happen";
        }
        em.getTransaction().begin();
        em.remove(t);
        user.balanceWallet(trip.getPrice());
        trip.returnTicket(user.getId());

        em.getTransaction().commit();

        return "Ticket was returned!";

    }

    /*------------------------------------------------MANAGER FUNCTIONS--------------------------------------------------------------*/

    //create a new trip
    public String createTrip(Trip trip){
        em.getTransaction().begin();
        em.persist(trip);
        em.getTransaction().commit();
        return "New Trip created!";
    }

    //List all trips
    public List<Trip> listAllTrips(){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.time> CURRENT_DATE ORDER BY t.time", Trip.class);
        return q.getResultList();
    }

    //List 
    public List<Trip> listDailyTrips(Date start){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE  t.time::DATE AS Date = CAST( :day AS Date ) ORDER BY t.time", Trip.class)
        .setParameter("day", start);
        return q.getResultList();
    }
    
    //Delete trip, still missing email stuff IM REALLY NOT SURE HERE HOW TO DELETE ALL OF THIS
    public String deleteTrip(Trip trip){
        em.getTransaction().begin();
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u INNER JOIN Ticket t1 ON u.id = t1.buyer_id WHERE t1.trip.id =:trip_id", Utilizador.class)
        .setParameter("trip_id",trip.getId());

        List<Utilizador> u= q.getResultList();

        if(u.size()==0) return "Trip was deleted, there were no tickets sold so it was only removed from the table";
        
        for(Utilizador user: u){
            incrementWallet(user, trip.getPrice());
            //whatever email means should be here
        }

        Query q2 = em.createQuery("DELETE FROM Ticket t1 WHERE t1=:trip_id")
            .setParameter("trip_id",trip.getId());

        em.remove(trip);
        em.getTransaction().commit();

        return "Trip was deleted, all the purchases were returned and emails sent!";
    
    }

    //List top users
    public String listTopUsers(){
        Query q = em.createQuery("SELECT u.name, COUNT(t1.buyer_id) as c FROM Ticket t1 INNER JOIN Utilizador u ON t1.buyer_id = u.id GROUP BY t1.buyer_id ORDER by c DESC LIMIT 5");
        String s="";
        try {
            List<Object[]> list = q.getResultList();

            String name;
            int count, i=1;

            for (Object[] obj : list) {
                name = (String) obj[0];
                count = (int) obj[1];

                if(count > 0){
                    s= s + i + "\tName: "+ name + "\t\tNumber of tickets: " + count + "\n";
                    i++;
                }
                else break;
            }
        } catch (Exception e) {
            return "Error returning the top users in the app";
        }

        if (s.equals("")) s= "No users bought tickets yet! Please try this again when there are more purchases";
        return s;
    }

    //List users in trip
    public List<Utilizador> getUsers(Trip trip){
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u INNER JOIN Ticket t1 ON u.id = t1.buyer_id WHERE t1.trip.id =:trip_id ORDER BY u.name", Utilizador.class)
        .setParameter("trip_id",trip.getId());
        return q.getResultList();
    }

    public String getRevenue(Date yesterday){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE  t.time::DATE AS Date= CAST( :day AS Date ) SORT BY t.time", Trip.class)
            .setParameter("day", yesterday);
        
        List<Trip> trips= q.getResultList();
        Double count_money=0.0;
        int count_people=0;
        for(Trip t: trips){
            count_money += t.getOccupancy() * t.getPrice();
            count_people += t.getOccupancy();
        }

        return "Earned "+ count_money + " euros from " + trips.size() + " trips, with a total of "+ count_people + " tickets bought!";
    }


}
    