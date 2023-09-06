package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilisateur extends Person {

    private String password;

    public Utilisateur(String nom, String email, String password) {
        super(nom, email);
        this.password = password;
    }

    public Utilisateur() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();

        if (rs != null) {
            utilisateur.setNom(rs.getString("nom")); // Update with the correct column name
            utilisateur.setEmail(rs.getString("email"));
            utilisateur.setPassword(rs.getString("password"));
        }
        return utilisateur;
    }


}
