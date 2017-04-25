/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.untitled.mobiledocumentscanner;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Ed
 */
public class imageSubmitter {
    File image;
    FileInputStream inputStream;
    byte[] fileContent;
    
    PreparedStatement uploadImageS = null;
    PreparedStatement IDCheckS = null;
    PreparedStatement currentPageS = null;
    PreparedStatement updatePageS = null;
    PreparedStatement updateAccessS = null;

    String IDCheck = "SELECT * FROM ImageStore WHERE imageID = ?;";

    String uploadImage = "INSERT INTO ImageStore(imageID, image) VALUES((?, ?));";

    String checkCurrentPage = "SELECT noPages FROM Document WHERE docID = ?";
    String updatePageNo = "UPDATE Document SET noPages = ? WHERE docID = ?";

    String accessUpdate = "INSERT INTO ImageStore VALUES(?, ?, ?, ?);";
    
    
    int userID;
    String password = "";
    SecureRandom random = new SecureRandom();
    int imageID;
    String imageKey;
    String tags;
    int ID;
    ResultSet rs;
    int docID;

    
    PreparedStatement uploader;
    public imageSubmitter(String filePath, int userID, String password, String tags, int docID)
    {
        imageKey = new BigInteger(130, random).toString(32);
        this.tags = tags;
        this.docID = docID;
        
        this.password = password;
        this.userID = userID;
        try
        {
            
            image = new File("E:\\documents\\pictures\\profilepic1.jpg");
            inputStream = new FileInputStream(image);
            //fileContent = Files.readAllBytes(image.toPath());
        }
        catch(IOException exception)
        {
            
        }
    }
    
    public boolean uploadImage()
    {
        try{
            //generate new imageID
            //check if ID is free
            //if yes

            Key key = new SecretKeySpec(imageKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] imageOut = cipher.doFinal(fileContent);
            //add imageOut byte[] to end of statement with setByte[]()
            //submit image
            
            

        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    
    /**
     * CHECK THIS THRICE, FOR THE LOVE OF GOD
     * @return 
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean simpleUploadImage()
    {

        try (Connection conn = DataSource.getConnection()) {
            
            
            
            IDCheckS = conn.prepareStatement(IDCheck);
            //gen initial ID
            ID = random.nextInt(999999999);
            IDCheckS.setInt(1,ID);
            rs = IDCheckS.executeQuery();
            Boolean isFree = false;
            //check if ID already exists
            while(!isFree)
            {
                //if it exists, regen key and try again
                if(rs.next())
                {
                    ID = random.nextInt(999999999);
                    IDCheckS.setInt(1,ID);
                    ResultSet rs = IDCheckS.executeQuery();
                }
                else
                {
                    isFree = true;
                }
            }
            //upload iamge
            uploadImageS = conn.prepareStatement(uploadImage);
            uploadImageS.setInt(1,ID);
            uploadImageS.setBlob(2, inputStream);
            uploadImageS.executeQuery();
            //get current pageNo
            currentPageS = conn.prepareStatement(checkCurrentPage);
            currentPageS.setInt(1, docID);
            rs = currentPageS.executeQuery();
            int page = rs.getInt("noPages")+1;
            //update access table
            updateAccessS = conn.prepareStatement(accessUpdate);
            updateAccessS.setInt(1, docID);
            updateAccessS.setInt(2, ID);
            updateAccessS.setString(3, "");
            updateAccessS.setInt(4, page);
            updateAccessS.executeUpdate();
            //update doc pageNo
            updatePageS = conn.prepareStatement(updatePageNo);
            updatePageS.setInt(1, page);
            updatePageS.executeUpdate();
        }
        catch(SQLException exception){
            System.out.println("SQL error(new user)");
        }
        return false;
    }
    
    
    
    
    
    
    
    public byte[] returnString()
    {
        return fileContent;
    }
    /*
    public byte[] extractBytes (String ImageName) throws IOException {
        //USED FROM STACK OVERFLOW
        // open image
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

        return ( data.getData() );
   }*/
    
    public byte[] encryptImage(String userKey)
    {
        byte[] a = new byte[5];
        return a;
    }
}
