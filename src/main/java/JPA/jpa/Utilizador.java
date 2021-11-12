package JPA.jpa;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class Utilizador implements Serializable
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

    public Utilizador(){}

    public Utilizador(String name, String password, String address, int phone, String email){
        this.name= name;
        this.password=password;
        this.address= address;
        this.phone=phone;
        this.email=email;
        this.session=true;
        this.wallet=0;
    }

    public String getEmail() {
        return email;
    }

    public void balanceWallet(double value) {
        this.wallet=this.wallet + value;
    }

    public boolean checkWallet(double value) {
        return this.wallet + value >= 0;
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

    public double getWallet() {
        return wallet;
    }

    public Long getId() {
        return id;
    }

    public String getHashedPassword() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            String hash = String.format("%X", ByteBuffer.wrap(digest).getLong()); //Hex.encodeHexString(digest);//.toLowerCase();
            return hash;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            // return not hashed password
            return password;
        }
    }
    @Override
    public String toString() {
        return "User: name "+ this.name + "email "+ this.email;
    }

}
