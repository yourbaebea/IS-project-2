package data;

import javax.persistence.*;

@Entity
public class Manager extends Utilizador
{
    public Manager(){
        super();
    }

    public Manager(String name, String password, String address, int phone, String email){
        super(name, password, address,phone,email);
    }


    @Override
    public String toString() {
        return "Manager: name "+ super.getName() + "email "+ this.getEmail();
    }
}
