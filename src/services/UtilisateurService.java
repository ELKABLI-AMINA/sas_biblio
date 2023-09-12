package services;

import Connection.DBConnection;
import models.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UtilisateurService {
    private static Connection con  = DBConnection.createDBConnection();
    public void createUtilisateur(Utilisateur utilisateur) {
        String query = "INSERT INTO utilisateur (nom, email, password) VALUES (?, ?, ?)";
        try {
            if (isManagerRegistered()) {
                System.out.println("A manager is already registered. You cannot register again.");
            } else {
                PreparedStatement pstm = con.prepareStatement(query);
                pstm.setString(1, utilisateur.getNom());
                pstm.setString(2, utilisateur.getEmail());
                pstm.setString(3, utilisateur.getPassword());
                int cnt = pstm.executeUpdate();
                if (cnt != 0) {
                    System.out.println("Manager Inserted Successfully");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isManagerRegistered() {
        try {

            String query = "SELECT COUNT(id) FROM utilisateur ";
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    public Utilisateur loginUser(String email, String password) {
        Utilisateur utilisateur = new Utilisateur();
        try {
            String query = "SELECT id, nom, email, password FROM utilisateur WHERE email = ? AND password = ?";
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, email);
            pstm.setString(2, password);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return utilisateur.map(rs);
            }
            else  return  null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return utilisateur;
    }

    public static void quitApplication() {
        System.out.println("Goodbye!");
        System.exit(0);
    }



}
