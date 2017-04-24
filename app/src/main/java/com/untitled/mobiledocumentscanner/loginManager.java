/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shtools;

import com.mycompany.academigyraeg.SimpleDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Ed
 */
public class loginManager {
    String username;
    String password;
    
    PreparedStatement loginCheckS = null;
    PreparedStatement getSaltS = null;
    
    String passCheck = "SELECT userID FROM Login WHERE username = ? AND password = ?";
    
    String getSalt = "SELECT salt FROM Login WHERE username = ?";
    
    ResultSet rs;
    public loginManager(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    public boolean login()
    {
        try (Connection conn = SimpleDataSource.getConnection()){
            loginCheckS = conn.prepareStatement(passCheck);
            loginCheckS.setString(1, username);
            loginCheckS.setString(2, password);
            rs = loginCheckS.executeQuery();
            
            if(!rs.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch(SQLException exception){
            System.out.println("SQL error(login)");
            return false;
        }
        
    }
    
    public void hashPass()
    {
        try (Connection conn = SimpleDataSource.getConnection()){
            getSaltS = conn.prepareStatement(getSalt);
            getSaltS.setString(1, username);
            rs = getSaltS.executeQuery();
            String salt = rs.getString("salt");
            
           
        }
        catch(SQLException exception){
            System.out.println("SQL error(login)");
        }
    }
}
