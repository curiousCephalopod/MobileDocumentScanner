/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shtools;

import com.mycompany.academigyraeg.SimpleDataSource;
import java.io.File;
import java.security.Key;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Ed
 */
public class imageRetriever {
    String selection = "SELECT imageID, image, EncryptionKey FROM Storage"
            + "INNER JOIN ImageAccess ON ImageAccess.imageID = Storage.imageID"
            + "WHERE docID = ? ORDER BY pageNo";
    
    String selectionF = "SELECT imageID, image, EncryptionKey FROM Storage"
            + "INNER JOIN ImageAccess ON ImageAccess.imageID = Storage.imageID"
            + "WHERE docID = ? AND pageNo = 1";
    
    String docGet = "SELECT docID, docTitle, tag FROM DocumentAllocation"
            + "INNER JOIN TagApplication ON DocumentAllocation.docID = TagApplication.docID"
            + "INNER JOIN Tag ON TagApplication.tagID = Tag.tagID"
            + "WHERE userID = ?";
    File bufferImage;
    ResultSet imageStorage;
    String password;
    int userID;
    
    PreparedStatement selectionS = null;
    PreparedStatement docSelectionS = null;
    ResultSet drs;
    ResultSet irs;
    
    public imageRetriever(int userID, String password)
    {
        this.password = password;
        this.userID = userID;
        //get doc result set
        try (Connection conn = SimpleDataSource.getConnection()){
            
            
            docSelectionS = conn.prepareStatement(docGet);
            docSelectionS.setInt(1,userID);
            drs = docSelectionS.executeQuery();
            
        }
        catch(SQLException exception){
            System.out.println("SQL error(doc retrieval)");
        }
    }
    
    public ArrayList getDocs()
    {
        ArrayList out = new ArrayList();
        Document temp;
        int id;
        String title;
        Date date;
        int pages;
        byte[] cover;
        //import and store each set of document metadata
        try (Connection conn = SimpleDataSource.getConnection())
        {
            //while there are documents left to be processed
            while(!drs.isAfterLast())
            {
                id = drs.getInt("docID");
                title = drs.getString("docTitle");
                date = drs.getDate("dateCreated");
                pages = drs.getInt("noPages");
                //retrieve cover image
                selectionS = conn.prepareStatement(selectionF);
                selectionS.setInt(1,id);
                irs = selectionS.executeQuery();
                Blob b = irs.getBlob("image");
                cover = b.getBytes(1, (int)b.length());
                //create doc summary
                temp = new Document(id, title, date, pages, cover);
                //add temp file to output
                out.add(temp);
                //advance to next doc
                drs.next();
            }
        }
        catch(SQLException exception){
            System.out.println("SQL error(doc retrieval)");
        }
        return out;
    }
    
    public ArrayList retrieveImages(int docID)
    {
        ArrayList out = new ArrayList();
        byte[] temp;
        Blob tempBlob;
        
        
        try (Connection conn = SimpleDataSource.getConnection()){
            selectionS = conn.prepareStatement(selection);
            selectionS.setInt(1,docID);
            irs = selectionS.executeQuery();
            //while there are images in the document add them to output
            while(!irs.isAfterLast())
            {
                tempBlob = irs.getBlob("image");
                temp = tempBlob.getBytes(1, (int)tempBlob.length());
                out.add(temp);
                irs.next();
            }
        }
        catch(SQLException exception){
            System.out.println("SQL error(image retrieval)");
        }
        
        return out;
    }
    
    
    public void saveImagesLocally()
    {
        //for each image in imageStorage
        //  extract key from RS
        byte[] encKey = new byte[5];//to change
        //  decrypt key
        try
        {
            //key decryption
            Key keyKey = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keyKey);
            String imageKey = new String(cipher.doFinal(encKey));
            //image decryption
        }
        catch(Exception e)
        {
            
        }
    }
    
    public void basicSaveImagesLocally()
    {
        
        //set new image
        //extract image ID
        //save byte[] as new file
    }
}
