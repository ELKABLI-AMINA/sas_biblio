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
    public Emprunteur emprunteurExisteDeja(String nom, String num_de_membre, String email) {
        String query = "SELECT id  FROM emprunteur WHERE nom = ? AND num_de_membre = ? AND email = ?";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, nom);
            pstm.setString(2, num_de_membre);
            pstm.setString(3, email);

            ResultSet resultSet = pstm.executeQuery();
            while(resultSet.next()){
                 Emprunteur emprunteur = new Emprunteur();
                emprunteur.setId(resultSet.getInt("id"));
                return emprunteur;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public int getEmprunteurIdByInfo(String nom, String num_de_membre, String email) {
        int emprunteurId = -1; // Initialiser à -1 par défaut (en cas de non-existence)

        String query = "SELECT id FROM emprunteur WHERE nom = ? AND num_de_membre = ? AND email = ?";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, nom);
            pstm.setString(2, num_de_membre);
            pstm.setString(3, email);

            ResultSet resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                emprunteurId = resultSet.getInt("id");
            }

            resultSet.close();
            pstm.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return emprunteurId;
    }
    public void supprimerEmprunteurEtDate(String isbn) {
        String query = "DELETE FROM copie_emprunteur WHERE copie_id IN (SELECT id FROM copie WHERE livre_id IN (SELECT id FROM livre WHERE isbn = ?))";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);

            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Informations d'emprunteur et de date d'emprunt supprimées avec succès.");
            } else {
                System.out.println("Aucune copie correspondante trouvée.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }















}
