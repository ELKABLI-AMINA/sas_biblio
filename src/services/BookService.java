package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import Connection.DBConnection;
import models.Book;

public class BookService {
    private static Connection con  = DBConnection.createDBConnection();
    public void createBook(Book book) {
        String query = "INSERT INTO livre (titre, auteur, isbn) VALUES (?, ?, ?)";

        try {
                PreparedStatement pstm = con.prepareStatement(query);
                pstm.setString(1, book.getTitre());
                pstm.setString(2, book.getAuteur());
                pstm.setString(3, book.getIsbn());
                int cnt = pstm.executeUpdate();
                if (cnt != 0) {
                    System.out.println("Book Inserted Successfully");
                }
            }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showAllBooks() {
        String query = "SELECT * FROM livre";
        System.out.println("Book Details ");

        try {
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                System.out.format("%s\t%s\t%s\t%s\n",
                        result.getString(1), // Colonne 1 : id
                        result.getString(2), // Colonne 2 : titre
                        result.getString(3), // Colonne 3 : auteur
                        result.getString(4)  // Colonne 4 : isbn
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateBook(String isbn , String titre , String auteur, String newIsbn) {
        String query ="update livre set titre =?, auteur =? where isbn=?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, titre);
            pstm.setString(2, auteur);
            pstm.setString(3, isbn);

            int cnt = pstm.executeUpdate();
            if (cnt!=0){
                System.out.println("Book Details updated successfuly ");
            }


        }catch(Exception ex){
            ex.printStackTrace();
        }



    }
    public void deleteBook(String isbn) {
        String query = "DELETE from livre where isbn=? ";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, isbn);
            int cnt= stmt.executeUpdate();
            if (cnt!=0){
                System.out.println("Book Deleted Succefully");
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
