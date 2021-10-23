package Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class User implements Serializable
{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String password;
    private String address;
    private int phone;
    private String email;
    private double wallet;
	private boolean session;


    public User(){}

    public User(String name, String password, String address, int phone, String email){
        this.name= name;
        this.password=password;
        this.address= address;
        this.phone=phone;
        this.email=email;
        this.session=true;
        this.wallet=0;
    }

    public void balanceWallet(double value) {
        this.wallet=this.wallet + value;
    }

    public boolean checkWallet(double value) {
        if(this.wallet + value>=0) {
            return true;
        }
        return false;
    }


    public boolean login(String password){
        if(this.password==password){
            this.session=true;
            return true;
        }
        return false;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSession(boolean session) {
        this.session = session;
    }
    

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public double getWallet() {
        return wallet;
    }

}
