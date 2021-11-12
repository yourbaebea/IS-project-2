package JPA.jpa;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;

import EJB.ejb.EJBManager;
import EJB.ejb.EJBManagerRemote;


public class ManagerInterface
{

    public static final Scanner sc = new Scanner(System.in);
    private boolean loggedin = false;
    private boolean leave=false;
    public EJBManager mb=null;
    public Manager user;

	public ManagerInterface()
	{
        //connections here
		//idk whats going here but i would say this is where we initiate a new user.session
	}
	
	boolean connect()
	{
		try
		{
            mb= new EJBManager();
            mb.connect();
            //mb =(EJBManagerRemote)context.lookup();
            return true;
		}catch(Exception e) {
			e.printStackTrace();
            return false;
		}
	}


    //Fazer com Scheduler
    public void dailySummary(){
        float total=0;
        List<Trip> trips =  mb.getRevenue();
        for(Trip a : trips){
            List <Ticket> tickets = a.getTickets();
            total+= tickets.size()*a.getPrice();
        }
        System.out.println("Revenues of today : "+total);
    }

	public static void main(String[] args)
	{

        ManagerInterface manager = new ManagerInterface();
        if(manager.connect()){

            while(!manager.leave){
                System.out.println("Welcome, please\n1-Login\n-1- Exit\n\nSelect Option:");
                int option = Integer.parseInt(sc.nextLine());
                if(option!=1){
                    System.out.println("exiting the manager interface");
                    return;
                }

                System.out.println("email:");
                String email = sc.nextLine();
                System.out.println("Password:");
                String password = sc.nextLine();

                manager.user = manager.mb.loginManager(email, password);
                if(manager.user!=null){
                    manager.loggedin=true;
                    manager.menu(manager);
                }
                else{
                    System.out.println("error in login");
                }
            }
        }
    }


    public void menu(ManagerInterface manager) {
        while(this.loggedin){
            System.out.println("\n\nManager Interface\n1-Create Trip\n2-Delete Trip\n3-List top 5 users\n4-List trips by interval\n5-List trips on certain day\n6-get revenue of the day, this should be automatic but rn it isnt\n-1 Exit\n\nSelect Option:");

			    int option = Integer.parseInt(sc.nextLine());
                List<Trip> trips;
                SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
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

                        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String dateAsString = day +" "+ time;
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

                        System.out.println(manager.mb.createTrip(new Trip(origin, destination, date, p, c)));
                        break;

                    case 2: //Delete trip
                        trips= manager.mb.listAllTrips();
                        
                        manager.listTrips(trips);

                        System.out.println("Select Trip to delete with id (-1 to leave purchase):");
                        String id = sc.nextLine();
                        int trip_id;
                        try{
                            trip_id= Integer.parseInt(id);  
                        }
                        catch(NumberFormatException ex){
                            System.out.println("No valid id");
                            return;
                        }
                        if(trip_id==-1) return;
                        if(trip_id>trips.size()){
                            System.out.println("No valid id");
                            return;
                        }

                        System.out.println("Processing purchase....");
                        System.out.println(manager.mb.deleteTrip(trips.get(trip_id)));
                        break;



                    case 3: //List top 5 people
                        
                        List<Object[]> list= manager.mb.listTopUsers();
                        if(list==null){
                            System.out.println("There are no users\n");
                            break;
                        }
                        else {
                            System.out.println("Top 5 users:");

                            for (int i = 0; i <= list.size()-1; i++) {
                                long count = (long) list.get(i)[1];
                                if (count == 0) {
                                    if (i == 1) System.out.println("No users have tickets");
                                    break;
                                }

                                System.out.println((i+1) + "\tName: " + list.get(i)[0] + "\t\tNumber of tickets: " + count);
                            }
                        }
                        
                        break;
                    case 4: //list trips by interval
                        System.out.println("inicial date (dd/MM/yyyy HH:mm):");
                        String start = sc.nextLine();
                        System.out.println("final date (dd/MM/yyyy HH:mm):");
                        String end = sc.nextLine();
                        //convert to Date
                        try{
                            Date date_start = formatter.parse(start);
                            Date date_end = formatter.parse(end);
                            trips= manager.mb.listTrips(date_start,date_end);
                            if(trips.size()>0) {
                                manager.getTripUsers(trips);
                            }
                            else{
                                System.out.println("There are no trips on this day");
                            }

                        }catch(ParseException e){
                            System.out.println("Formato de data errado");
                        }
                        break;
                    case 5: //list trips on certain day
                        SimpleDateFormat formatter2=new SimpleDateFormat("dd/MM/yyyy");
                        System.out.println("Day to check trips:");
                        String d = sc.nextLine();
                        //convert to Date 
                        try{
                            Date date_start = formatter2.parse(d);

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date_start);
                            cal.add(Calendar.DATE, 1);

                            Date date_end = cal.getTime();

                            trips= manager.mb.listTrips(date_start,date_end);
                            if(trips.size()>0) {
                                manager.getTripUsers(trips);
                            }
                            else{
                                System.out.println("There are no trips on this day");
                            }

                        }catch(ParseException e){
                            System.out.println("Wrong format");
                        }
                        break;
                    default:
                        return;
                }


        }
    }

    public void getTripUsers(List<Trip> trips){
        //List Trips
        listTrips(trips);

        System.out.println("Get users in Trip with id (-1 to leave):");
        String id = sc.nextLine();
        int trip_id;
        try{
            trip_id= Integer.parseInt(id);  
        }
        catch(NumberFormatException ex){
            System.out.println("No valid id");
            return;
        }
        if(trip_id==-1) return;
        if(trip_id>trips.size()){
            System.out.println("No valid id");
            return;
        }

        List<Utilizador> users= mb.getUsers(trips.get(trip_id));

        if (users.size()==0){
            System.out.println("There are no current users in this trip");
            return;
        }

        System.out.println("List of Users");
        for(Utilizador u: users) System.out.println( u.getName());
        
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
