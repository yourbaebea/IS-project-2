package Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.validator.routines.EmailValidator;

import Classes.*;



public class UserInterface
{

    public static final Scanner sc = new Scanner(System.in);
    private boolean loggedin = false;
    private boolean leave=false;
    private User user=null;
    
	public UserInterface()
	{
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

        UserInterface client = new UserInterface();
        if(client.connect()){

            while(!client.leave){
                System.out.println("Welcome\n1-Register\n2-Login\n-1 Exit\n\nSelect Option:");

			    int option = Integer.parseInt(sc.nextLine());
                String email, password;
                int valid;
                switch(option){
                    case 1:
                        System.out.println("Name:");
                        String name = sc.nextLine();
                        System.out.println("email:");
                        email = sc.nextLine();
                        System.out.println("phone:");
                        String phone = sc.nextLine();
                        System.out.println("address:");
                        String address = sc.nextLine();
                        System.out.println("Password:");
                        password = sc.nextLine();
                
                        valid = checkAll(name, password, address, phone, email);
                
                        if(valid==1){
                            client.user= new User(name, password, address, Integer.parseInt(phone),email);
                            client.loggedin=true;
                        }
                        else{
                            System.out.println("error in creating new user: error code "+ valid);

                            /* if we need to debug we can just get the meaning of each value
                            -1 invalid input of vars
                            -2 invalid phone number
                            -3 invalid email format
                            -4 email is not unique
                            0 there is only one email already in the table, ONLY FOR EDIT USER
                            1 all good
                            */
                        }
                        break;

                    case 2:
                        System.out.println("email:");
                        email = sc.nextLine();
                        System.out.println("Password:");
                        password = sc.nextLine();
                
                        boolean check= login(email, password);
                        if(check){
                            menu();
                        }
                        else System.out.println("error in login");
                        
                        break;
                    case -1:
                        client.leave=true;
                        break;
                    default:
                        System.out.println("option was not valid, back to the menu");
                
                    }


            }
        }
            
    }

    public void menu(){
        while(this.loggedin){
            System.out.println("User"+ this.user.getName()+"\t\tMain Menu\n1-Purchase Ticket\n2-Return Ticket\n3-List all trips\n4-List all my tickets\n5-Transfer money into wallet\n6-other stuff here\n0 Logout\n-1 Delete account (and any info related to this account)\n\nSelect Option:");

            int option = Integer.parseInt(sc.nextLine());
            List<Trip> trips=new ArrayList<>();
            switch(option){
                case 1: //buy ticket
                    System.out.println("Select origin:");
                    String origin = sc.nextLine();
                    System.out.println("Select destination:");
                    String destination = sc.nextLine();

                    //new query
                    //Select * from trips t where t.origin=:origin and t.destination=:destination"
                    //check if date > current time
                    trips= new ArrayList<>();
                    //this should be the return of the query

                    purchaseTicket(trips);

                    break;
                case 2: //return ticket

                    //new query
                    //Select trip_id from tickets t where t.buyer_id=:id"
                    //get all trips with this trip_id
                    //:id is this.user.getId();
                    //check if date > current time
                    trips= new ArrayList<>();
                    //this should be the return of the query

                    returnTicket(trips);
                    break;
                case 3: //list all trips with interval
                    System.out.println("inicial date:");
                    String start = sc.nextLine();
                    System.out.println("final date:");
                    String end = sc.nextLine();
                    //convert to Date

                    //new query
                    //Select * from trips t where t.date>=:start and t.date<=:end"
                    trips= new ArrayList<>();
                    //this should be the return of the query
                    listTrips(trips);
                    break;
                case 4: //list all of users tickets
                    //new query
                    //Select trip_id from tickets t where t.buyer_id=:id"
                    //get all trips with this trip_id
                    //:id is this.user.getId();
                    //check if date > current time
                    trips= new ArrayList<>();
                    //this should be the return of the query
                    listTrips(trips);
                    break;
                case 5: //adding money to wallet
                    System.out.println("Quantity:");
                    String cash = sc.nextLine();
                    double value;
                    System.out.println("Processing...");
                    try {
                        value= Double.parseDouble(cash);
                        this.user.balanceWallet(value);
                        System.out.println("Current value inside wallet: "+ this.user.getWallet());
                    } catch (Exception e) {
                        System.out.println("Value was not valid");
                    }

                        
                case 0:
                    this.loggedin=false;
                    break;
                default:
                    System.out.println("option was not valid, back to the menu");
            
                }


        }

    }

    public void purchaseTicket(List<Trip> trips){
        
        listTrips(trips);

        System.out.println("Buy Trip with id (-1 to leave purchase):");
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

        double value= trips.get(trip_id).getPrice();

        if(this.user.checkWallet(-value)){
            System.out.println("Processing purchase....");

            if(trips.get(trip_id).createTicket(this.user.getId())){
                System.out.println("Ticket was purchased!");
                this.user.balanceWallet(-value);
            }
            else{
                System.out.println("Ticket was not purchased, please try other trip or check if you already have a ticket for this trip...");
                //either the trip is full or this user already has this ticket
            }
            
        }
        else{
            System.out.println("Wallet problems," + this.user.getWallet() +" is not enought to purchase this trip, please transfer some money to your account and try again");
        }
        
    }

    
    public void returnTicket(List<Trip> trips){
        
        listTrips(trips);

        System.out.println("Return Trip with id (-1 to leave):");
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

        double value= trips.get(trip_id).getPrice();

        System.out.println("Processing return....");

        if(trips.get(trip_id).returnTicket(this.user.getId())){
            System.out.println("Ticket was returned!");
            this.user.balanceWallet(value);
        }
        else{
            System.out.println("Ticket was not return, this actually should never happen");
            //idk what needs to go wrong to get here but if you did, gl ;))
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

    private static boolean login(String email, String password){
        //new query
        //Select * from users where email=:email
        //hashed password!!!!!!! we need to convert
        
        /*
        List<User> u = q.getResultList();
        User user= u.get(0);
        */
        User u= new User();

        if(password==u.getPassword()){
            this.loggedin=true;
            this.user= u;
            return true;
        }

        return false;

    }

    /**
     * 
     * @param name
     * @param password
     * @param address
     * @param phone
     * @param email
     * @return
     * -1 invalid input of vars
     * -2 invalid phone number
     * -3 invalid email format
     * -4 email is not unique
     * 0 there is only one email already in the table, ONLY FOR EDIT USER
     * 1 all good
     * 
     */
    private static int checkAll(String name, String password, String address, String phone, String email){

        if(name == "" || password=="" || address=="" || phone.length()!= 9|| email==""){
            return -1;
        }

        try{
            Integer.parseInt(phone);  
        }
        catch(NumberFormatException ex){
            return -2;
        }
       
        if(!EmailValidator.getInstance().isValid(email)) return -3;

        //new query, if email already exists return error
        //Select count(*) from users where email=:email
        int count=0;
        if(count==1) return 0;
        if(count>1) return -4;

        return 1;
    }



}
