package Bean;

import Data.DataLayer;
import Classes.Trip;
import Classes.Utilizador;
import org.apache.commons.validator.routines.EmailValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;



public class UserBean {

    public static final Scanner sc = new Scanner(System.in);
    private boolean loggedin = false;
    private boolean leave=false;
    private Utilizador user=null;
    public DataLayer bd=null;

	public UserBean()
	{
        //connections here
		//idk whats going here but i would say this is where we initiate a new user.session
	}
	
	private boolean connect()
	{
		try
		{
            bd = new DataLayer();
            //bean =(UserBeanRemote)context.lookup();
            return true;
		}catch(Exception e) {
			e.printStackTrace();
            return false;
		}
	}
	                                                                                                                                                                                                                                                              
	public static void main(String[] args)
	{

        UserBean client = new UserBean();
        if(client.connect()){

            while(!client.leave){
                System.out.println("Welcome\n1-Register\n2-Login\n-1 Exit\n\nSelect Option:");

			    int option = Integer.parseInt(sc.nextLine());
                String email, password;
                int valid;
                switch(option){
                    case 1: //register new user
                        System.out.println("Register\n");
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
                            client.user= client.bd.registerUser(new Utilizador(name, password, address, Integer.parseInt(phone),email));

                            if(client.user!=null){
                                System.out.println("Account created sucessfully, going to main menu!");
                                client.loggedin=true;
                                client.menu();
                            }
                            else System.out.println("Account was not created, email already in the system, please try to login!");
                            
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

                    case 2: //login
                        System.out.println("Login\n");
                        System.out.println("email:");
                        email = sc.nextLine();
                        System.out.println("Password:");
                        password = sc.nextLine();

                        client.user = client.bd.loginUser(email, password);
                        if(client.user!=null){
                            client.loggedin=true;
                            client.menu();
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

    public void menu() {
        while(this.loggedin){
            System.out.println("User :\t"+ this.user.getName()+"\t\tMain Menu\n1-Purchase Ticket\n2-Return Ticket\n3-List all trips\n4-List all my trips\n5-Transfer money into wallet\n6-Change personal information\n0 Logout\n-1 Delete account (and any info related to this account)\n\nSelect Option:");

            int option = Integer.parseInt(sc.nextLine());
            List<Trip> trips;
            switch(option){
                case 1: //buy ticket
                    System.out.println("Select origin:");
                    String origin = sc.nextLine();
                    System.out.println("Select destination:");
                    String destination = sc.nextLine();

                    //Trips with that origin and destination
                    trips= bd.listAvailableTrips(origin, destination);

                    purchaseTicket(trips);

                    break;
                case 2: //return ticket
                    trips= bd.listUserTrips(this.user.getId());
                    returnTicket(trips);
                    break;

                case 3: //list all trips with interval
                    System.out.println("inicial date (dd/MM/yyyy HH:mm):");
                    String start = sc.nextLine();
                    System.out.println("final date (dd/MM/yyyy HH:mm):");
                    String end = sc.nextLine();
                    //convert to Date
                    SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try{
                        Date data_inicio = formatter.parse(start);
                        Date data_fim = formatter.parse(end);
                        trips= bd.listTrips(data_inicio,data_fim);
                        listTrips(trips);

                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                    break;

                case 4: //list all of users trips

                    trips= bd.listUserTrips(this.user.getId());
                    listTrips(trips);
                    break;

                case 5: //adding money to wallet
                    System.out.println("Quantity:");
                    String cash = sc.nextLine();
                    double value;
                    System.out.println("Processing...");
                    try {
                        value= Double.parseDouble(cash);
                        bd.incrementWallet(this.user,value);
                        System.out.println("Current value inside wallet: "+ this.user.getWallet());
                    } catch (Exception e) {
                        System.out.println("Value was not valid");
                    }
                    break;
                case 6:
                    alterarDados();
                    break;
                case 0:
                    this.loggedin=false;
                    break;
                default:
                    System.out.println("option was not valid, back to the menu");
            
            }
        }
    }

    public void alterarDados(){
        System.out.println("\tEscolha o que alterar\t\n\n 1 - Nome\n 2 - Palavra-passe\n 3 - Email\n 4 - Morada\n 5 - Telefone\n -1 - Voltar\n");
        int a= Integer.parseInt(sc.nextLine());
        switch(a){
            case 1:
                System.out.println("Novo Nome\n");
                String nome = sc.nextLine();
                bd.changeData(this.user,nome,1);
                alterarDados();
                break;
            case 2:
                System.out.println("Nova Passe\n");
                String passe = sc.nextLine();
                bd.changeData(this.user,passe,2);
                alterarDados();
                break;
            case 3:
                System.out.println("Novo Email\n");
                String email = sc.nextLine();
                bd.changeData(this.user,email,3);
                alterarDados();
                break;
            case 4:
                System.out.println("Nova Morada\n");
                String morada = sc.nextLine();
                bd.changeData(this.user,morada,4);
                alterarDados();
                break;
            case 5:
                System.out.println("Novo Telefone\n");
                String telefone = sc.nextLine();
                bd.changeData(this.user,telefone,5);
                alterarDados();
                break;
            case -1:
                menu();
        }
    }

    //updated
    public void purchaseTicket(List<Trip> trips){

        //List Available Trips
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

        System.out.println("Processing purchase....");
        System.out.println(bd.createTicket(trips.get(trip_id),this.user));
        
    }

    //updated
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

        System.out.println("Processing return....");
        System.out.println(bd.returnTicket(trips.get(trip_id),this.user));        
        
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
