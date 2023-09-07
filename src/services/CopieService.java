package services;
import Connection.DBConnection;
import models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CopieService {
    private static Connection con  = DBConnection.createDBConnection();
    public void insertCopies(int bookId, int quantity) {
        String query = "INSERT INTO copie (statut, livre_id) VALUES (?, ?)";
        try {
            PreparedStatement pstm = con.prepareStatement(query);

            // Set the initial status for all copies
            for (int i = 0; i < quantity; i++) {
                pstm.setString(1, "Disponible");
                pstm.setInt(2, bookId);
                pstm.addBatch();
            }

            // Execute the batch insert
            int[] batchResults = pstm.executeBatch();

            // Check the results to ensure all inserts were successful
            for (int result : batchResults) {
                if (result <= 0) {
                    System.err.println("Error inserting copies.");
                    return;
                }
            }

            System.out.println(quantity + " copies inserted successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void updateStatutToEmprunte(String isbn) {
//        String query = "UPDATE copie SET statut = 'emprunté' WHERE livre_id IN (SELECT id FROM livre WHERE isbn = ?)";
        String query = "UPDATE copie SET statut = 'emprunté' WHERE id = ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);

            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Statut mis à jour avec succès : disponible -> emprunté.");
            } else {
                System.out.println("Aucune copie disponible correspondante trouvée.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public String getStatutByISBN(String isbn) {
        String query = "SELECT statut FROM copie INNER JOIN livre ON livre_id=livre.id WHERE isbn = ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {

                return result.getString("statut");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Si aucun résultat n'est trouvé, retournez null ou une valeur par défaut
        return null;
    }



}
