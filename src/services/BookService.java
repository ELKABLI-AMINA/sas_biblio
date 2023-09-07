package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Connection.DBConnection;
import com.mysql.cj.jdbc.CallableStatementWrapper;
import models.Book;
import models.Emprunteur;
import services.EmprunteurService;
import services.CopieService;


public class BookService {
    CopieService daoCopie = new CopieService();
    EmprunteurService daoEmprunteur = new EmprunteurService();

    private static Connection con = DBConnection.createDBConnection();

    public int createBook(Book book) {
        String query = "INSERT INTO livre (titre, auteur, isbn) VALUES (?, ?, ?)";
        int bookId = -1; // Initialize with an invalid ID

        try {
            PreparedStatement pstm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, book.getTitre());
            pstm.setString(2, book.getAuteur());
            pstm.setString(3, book.getIsbn());

            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Book Inserted Successfully");
                // Retrieve the generated book ID
                ResultSet generatedKeys = pstm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bookId = generatedKeys.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bookId;
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

    public void updateBook(String isbn, String titre, String auteur, String newIsbn) {
        String query = "update livre set titre =?, auteur =? where isbn=?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, titre);
            pstm.setString(2, auteur);
            pstm.setString(3, isbn);

            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Book Details updated successfuly ");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static List<Book> rechercherLivres(String searchQuery) {
        List<Book> resultats = new ArrayList<>();

        String query = "SELECT * FROM livre WHERE titre LIKE ? OR auteur LIKE ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, "%" + searchQuery + "%");
            pstm.setString(2, "%" + searchQuery + "%");

            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Book livre = new Book();
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setIsbn(rs.getString("isbn"));
                resultats.add(livre);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultats;
    }

    public void deleteBook(String isbn) {
        String query = "DELETE from livre where isbn=? ";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, isbn);
            int cnt = stmt.executeUpdate();
            if (cnt != 0) {
                System.out.println("Book Deleted Succefully");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Book checkIfExists(String isbn) {
        String query = "SELECT * FROM livre WHERE isbn = ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, isbn);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {
                // Si un résultat est trouvé, créez un objet Book et remplissez-le avec les détails du livre
                Book existingBook = new Book();
                existingBook.setId(result.getInt("id"));
                existingBook.setTitre(result.getString("titre"));
                existingBook.setAuteur(result.getString("auteur"));
                existingBook.setIsbn(result.getString("isbn"));
                return existingBook;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Si aucun résultat n'est trouvé, retournez null
        return null;
    }


    public void emprunterLivre() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez le numéro ISBN du livre que vous souhaitez emprunter :");
        String isbn = scanner.next();

        // Vérifiez si le livre existe
        Book existingBook = checkIfExists(isbn);

        if (existingBook != null) {
            // Vérifiez si le livre est disponible
            String statut = daoCopie.getStatutByISBN(isbn);
            System.out.println(statut);
            if ("disponible".equalsIgnoreCase(statut)) {
                // Demandez les informations de l'emprunteur
                System.out.println("Entrez le nom de l'emprunteur :");
                String nom = scanner.next();
                System.out.println("Entrez le numéro de membre de l'emprunteur :");
                String numMembre = scanner.next();
                System.out.println("Entrez l'email de l'emprunteur :");
                String email = scanner.next();

                // Créez l'emprunteur et récupérez son ID
                Emprunteur emprunteur = new Emprunteur(nom, numMembre, email);
                emprunteur = daoEmprunteur.createBorrower(emprunteur);

                // Enregistrez la date d'emprunt (vous devrez définir la date actuelle)
                String dateEmprunt = "2023-09-15"; // Remplacez par la vraie date d'emprunt

                // Mettez à jour le statut du livre dans la table "copie" et enregistrez la date d'emprunt
                daoCopie.updateStatutToEmprunte( isbn);

                System.out.println("L'emprunt a réussi !");
            } else {
                System.out.println("Désolé, ce livre n'est pas disponible pour l'emprunt.");
            }
        } else {
            System.out.println("Ce livre n'existe pas dans la bibliothèque.");
        }
    }
}




