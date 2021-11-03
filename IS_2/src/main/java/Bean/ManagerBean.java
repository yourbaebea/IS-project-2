package Bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Classes.*;



public class ManagerBean
{

    public static final Scanner sc = new Scanner(System.in);
    private boolean leave=false;
    
	public ManagerBean()
	{
        // ENUNCIADO
        //To create manager accounts the system should use a script, e.g., written in JPA.

        //connections here
		//idk whats going here but i would say this is where we initiate a new user.session
	}
	
	private boolean connect()
	{
		try
		{
			//funcions to connect to context or wtv
            return true;
		}catch(Exception e) {
			e.printStackTrace();
            return false;
		}
	}
	                                                                                                                                                                                                                                                              
	
	public static void main(String[] args)
	{

        ManagerBean manager = new ManagerBean();
        if(manager.connect()){

            while(!manager.leave){
                System.out.println("Manager Interface, no login needed ? \n1-Create Trip\n2-Delete Trip\n3-List top 5 users\n4-List trips by interval\n5-List trips on certain day\n-1 Exit\n\nSelect Option:");

			    int option = Integer.parseInt(sc.nextLine());
                List<Trip> trips;
                
                switch (option) {
                    case 1: //create trip
                        System.out.println("origin:");
                        String origin = sc.nextLine();
                        System.out.println("destination:");
                        String destination = sc.nextLine();
                        System.out.println("Day: dd/MM/yyyy");
                        String day = sc.nextLine();
                        System.out.println("Time: hh:mm");
                        String time = sc.nextLine();
                        System.out.println("price:");
                        String price = sc.nextLine();
                        System.out.println("capacity:");
                        String capacity = sc.nextLine();
                        
                        //no query needed we can have multiple trips at the same time place etc etc

                        DateFormat sourceFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
                        String dateAsString = time+" "+day;
                        Date date;
                        int c;
                        double p;
                        try {
                            date = sourceFormat.parse(dateAsString);
                        } catch (ParseException e) {
                            System.out.println("Date format is not valid, trip is not being created");
                            break;
                        }
                        
                        try {
                            c= Integer.parseInt(capacity);
                            
                        } catch (Exception e) {
                            System.out.println("capacity is not valid, trip is not being created");
                            break;
                        }

                        if(c<=0){
                            System.out.println("capacity is not valid, trip is not being created");
                            break;
                        }

                        try {
                            p= Double.parseDouble(price);
                            
                        } catch (Exception e) {
                            System.out.println("capacity is not valid, trip is not being created");
                            break;
                        }

                        if(p<0){
                            System.out.println("price is not valid, trip is not being created");
                            break;
                        }

                        Trip trip= new Trip(origin, destination, date, p, c);

                        //update query to the trips table
                    case 2:
                        
                        //new query
                        //Select * from trips"
                        //check if date > current time
                        trips= new ArrayList<>();
                        //this should be the return of the query
                        
                        manager.listTrips(trips);

                        System.out.println("List all users in trip by id: (-1 to leave)");
                        String response = sc.nextLine();
                        int r;
                        try {
                            r= Integer.parseInt(response);
                            
                        } catch (Exception e) {
                            System.out.println("not valid, leaving");
                            break;
                        }

                        if(r==-1 || r>=trips.size()){
                            System.out.println("leaving...");
                            break;
                        }

                        int trip_id= trips.get(r).getId();
                        

                        manager.listUsers(trip_id);

                        break;


                        
                    default:
                        break;
                }


            }
        }
            
    }


    public void listUsers(int trip_id){
        
        //new query
        //Select buyer_id from tickets where trip_id=:trip_id
        //Select * from users where id=:buyers //this buyers is the list of buyer_ids
        
        //List<User> u = q.getResultList();
        List<Utilizador> u= new ArrayList<>();

        if (u.size()==0){
            System.out.println("There are no current users in this trip");
            return;
        }

        System.out.println("List of users in this trip");
        for(int i=0; i< u.size(); i++){
            System.out.println( i + " "+ u.get(i).toString());
        }

    }

    public void topUsers(){
        
        //new query
        
        /*

        SELECT buyer_id , COUNT(buyer_id)
        FROM
        tickets
        GROUP BY buyer_id
        SORT DESC
        */

        //Select * from users where id=:buyers //this buyers is the list of buyer_ids
        
        //List<User> u = q.getResultList();
        List<Utilizador> u= new ArrayList<>();
        int count= 1; // count of the buyer id

        if (u.size()==0){
            System.out.println("There are no users with trips");
            return;
        }

        System.out.println("Top 5 List of users");
        for(int i=0; i< 5 || i< u.size(); i++){
            System.out.println( (i+1) + ": "+ count + " tickets, "+ u.get(i).toString());
        }

    }

	public void listTrips(List<Trip> trips){
        if (trips.size()==0){
            System.out.println("There are no upcoming trips");
            return;
        }

        System.out.println("List of Upcoming trips");
        for(int i=0; i< trips.size(); i++){
            System.out.println( i+ " "+ trips.get(i).toString());
        }
    }

}
