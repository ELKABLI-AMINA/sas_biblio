package services;
import Connection.DBConnection;
import models.Book;
import models.Emprunteur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmprunteurService {
    private static Connection con  = DBConnection.createDBConnection();
    public Emprunteur createBorrower(Emprunteur emprunteur) {
        String query = "INSERT INTO emprunteur (nom, num_de_membre, email) VALUES (?, ?, ?)";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, emprunteur.getNom());
            pstm.setString(2, emprunteur.getNum_de_membre());
            pstm.setString(3, emprunteur.getEmail());
            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Borrow Inserted Successfully");
            }
            return emprunteur;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }



}
