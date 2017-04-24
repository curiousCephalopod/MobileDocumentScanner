/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shtools;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed
 */
public class newUserGen {
    
    InputStream stream = newUserGen.class.getResourceAsStream("/database.properties");
    
    String newUser = "INSERT INTO Login VALUES (?,?,?,?)";
    PreparedStatement addUserS = null;
    
    SecureRandom random = new SecureRandom();
    
    String password;

    String salt;
    
    public newUserGen()
    {
        try {
            com.mycompany.academigyraeg.SimpleDataSource.init(stream);
        } catch (IOException ex) {
            Logger.getLogger(newUserGen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(newUserGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       
    
    
    public void addNewUser(String username, String password, String email)
    {
        salt = new BigInteger(130, random).toString(32);

        this.password = password;

        try (Connection conn = SimpleDataSource.getConnection()){
            addUserS = conn.prepareStatement(newUser);
            addUserS.setString(1, password);
            addUserS.setString(2, salt);
            addUserS.setString(3, username);
            addUserS.setString(4, email);
            
            addUserS.executeUpdate();
        }
        catch(SQLException exception){
            System.out.println("SQL error(new user)");
        }
    }
}

