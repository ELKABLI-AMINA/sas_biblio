package services;
import Connection.DBConnection;

import java.sql.Connection;
import java.sql.Date;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;


public class CopieService {

    private static Connection con  = DBConnection.createDBConnection();
    public void insertCopies(int bookId, int quantity ) {
        String query = "INSERT INTO copie (statut, livre_id ) VALUES (?, ? )";
        try {
            PreparedStatement pstm = con.prepareStatement(query);

            for (int i = 0; i < quantity; i++) {
                pstm.setString(1, "Disponible");
                pstm.setInt(2, bookId);
                pstm.addBatch(); // pour ajouter plusieurs instructions SQL à un lot
            }

            int[] batchResults = pstm.executeBatch(); //est utilisée pour exécuter toutes les instructions SQL ajoutées précédemment au lot (batch)

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

        String query = "UPDATE copie SET statut = 'emprunté' WHERE livre_id IN (SELECT id FROM livre WHERE isbn = ?)  AND statut = 'disponible' limit 1";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);

            int cnt = pstm.executeUpdate();

            if (cnt != 0) {
                System.out.println("Statut mis à jour avec succès : disponible -> emprunté.");
            } else {
                System.out.println("Aucune copie disponible correspondante trouvée ou le statut est déjà 'emprunté'.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getStatutByISBNDisponible(String isbn) {
        String query = "SELECT copie.statut as copie_statut, copie.id as copie_id  \n" +
                "FROM copie\n" +
                "INNER JOIN livre ON copie.livre_id = livre.id\n" +
                "WHERE isbn = ? AND copie.statut = 'disponible';\n";


        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);
            ResultSet result = pstm.executeQuery();

            while(result.next()){

                return result.getInt("copie_id");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }
    public String getStatutByISBNEmprunté(String isbn) {
        String query = "SELECT copie.statut as copie_statut " +
            "FROM copie " +
            "INNER JOIN livre ON copie.livre_id = livre.id " +
            "WHERE isbn = ? AND copie.statut = 'emprunté'";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);
            ResultSet result = pstm.executeQuery();

            while (result.next()) {
                return result.getString("copie_statut");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Si aucun résultat n'est trouvé, retournez null ou une valeur par défaut
        return null;
    }

    public void insererCopieEmprunteur(int copieId, int emprunteurId ) {
        String query = "INSERT INTO copie_emprunteur (copie_id, emprunteur_id, date_emprunt, date_retour) " +
                "VALUES (?, ?, SYSDATE(), SYSDATE() + INTERVAL 3 DAY)";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setInt(1, copieId);
            pstm.setInt(2, emprunteurId);




            int rowCount = pstm.executeUpdate();

            if (rowCount > 0) {
                System.out.println("L'entrée dans la table copie_emprunteur a été insérée avec succès.");
            } else {
                System.out.println("Échec de l'insertion dans la table copie_emprunteur.");
            }

            pstm.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
   }
    public int getNombreCopiesByISBNEmprunteurAndDate(String isbn, int emprunteurId, java.sql.Date date_emprunt) {
        int nombreCopies = 0;

        String query = "SELECT COUNT(*) AS nombreCopies FROM copie_emprunteur " +
                "INNER JOIN emprunteur ON copie_emprunteur.emprunteur_id = emprunteur.id " +
                "INNER JOIN copie ON copie_emprunteur.copie_id = copie.id " +
                "INNER JOIN livre ON copie.livre_id = livre.id " +
                "WHERE livre.isbn = ? AND emprunteur.id = ? AND copie_emprunteur.date_emprunt = ?";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);
            pstm.setInt(2, emprunteurId);
            pstm.setDate(3, date_emprunt);

            ResultSet resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                nombreCopies = resultSet.getInt("nombreCopies");
            }

            resultSet.close();
            pstm.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return nombreCopies;
    }




}
