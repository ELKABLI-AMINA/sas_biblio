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



    public void displayAvailableBooks() {
        // Effectuez une requête SQL pour récupérer tous les livres disponibles
        String query = "SELECT titre, auteur, statut FROM livre INNER JOIN copie ON livre.id = copie.livre_id ";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet result = pstm.executeQuery();

            System.out.println("Liste des livres disponibles :");
            System.out.println("-------------------------------------------------------");
            System.out.println("Titre\t\tAuteur\t\tStatut");
            System.out.println("-------------------------------------------------------");

            while (result.next()) {
                String titre = result.getString("titre");
                String auteur = result.getString("auteur");
                String statut = result.getString("statut");

                System.out.format("%s\t\t%s\t\t%s\n", titre, auteur, statut);
            }

            System.out.println("-------------------------------------------------------");
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

        Book existingBook = checkIfExists(isbn);

        if (existingBook != null) {
            String statut = daoCopie.getStatutByISBN(isbn);
            if ("disponible".equalsIgnoreCase(statut)) {
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
                daoCopie.updateStatutToEmprunte(  isbn);

                System.out.println("L'emprunt a réussi !");
            } else {
                System.out.println("Désolé, ce livre n'est pas disponible pour l'emprunt.");
            }
        } else {
            System.out.println("Ce livre n'existe pas dans la bibliothèque.");
        }
    }

    public void retournerLivre() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez le numéro ISBN du livre que vous souhaitez retourner :");
        String isbn = scanner.next();

        Book existingBook = checkIfExists(isbn);

        if (existingBook != null) {
            // Vérifiez si le livre est emprunté
            String statut = daoCopie.getStatutByISBN(isbn);

            if ("emprunté".equals(statut)) {
                // Mettez à jour le statut du livre dans la table "copie" en le rendant disponible
                daoCopie.updateStatutToDisponible(isbn);

                // Supprimez les informations d'emprunteur et de date d'emprunt de la base de données (vous devrez implémenter cette méthode)
                daoCopie.supprimerEmprunteurEtDate(isbn);

                System.out.println("Le livre a été retourné avec succès !");
            } else {
                System.out.println("Désolé, ce livre n'est pas emprunté.");
            }
        } else {
            System.out.println("Ce livre n'existe pas dans la bibliothèque.");
        }
    }
    public void displayBorrowedBooks() {
        String query =
                      "SELECT livre.titre, livre.auteur, emprunteur.nom, copie_emprunteur.date_emprunt " +
                "FROM livre " +
                "INNER JOIN copie ON livre.id = copie.livre_id " +
                "INNER JOIN copie_emprunteur ON copie.id = copie_emprunteur.copie_id " +
                "INNER JOIN emprunteur ON copie_emprunteur.emprunteur_id = emprunteur.id " +
                "WHERE copie.statut = 'emprunté'";


        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet result = pstm.executeQuery();

            System.out.println("Liste des livres empruntés :");
            System.out.println("------------------------------------------------------------");
            System.out.println("Titre\t\tAuteur\t\tStatut\t\tEmprunteur\t\tNuméro de membre\t\tEmail\t\tDate d'emprunt");
            System.out.println("------------------------------------------------------------");

            while (result.next()) {
                String titre = result.getString("titre");
                String auteur = result.getString("auteur");
                String statut = result.getString("statut");
                String nomEmprunteur = result.getString("nom");
                String numMembre = result.getString("num_de_membre");
                String email = result.getString("email");
                String dateEmprunt = result.getString("date_emprunt");

                System.out.format("%s\t\t%s\t\t%s\t\t%s\t\t%s\t\t%s\t\t%s\n", titre, auteur, statut, nomEmprunteur, numMembre, email, dateEmprunt);
            }
            System.out.println("------------------------------------------------------------");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void generateLibraryReport() {
        int totalBooks = getTotalBooks();
        int availableBooks = getAvailableBooks();
        int borrowedBooks = getBorrowedBooks();
        int lostBooks = getLostBooks();

        System.out.println("===== Rapport de la bibliothèque =====");
        System.out.println("Nombre total de livres : " + totalBooks);
        System.out.println("Nombre de livres disponibles : " + availableBooks);
        System.out.println("Nombre de livres empruntés : " + borrowedBooks);
        System.out.println("Nombre de livres perdus : " + lostBooks);
    }

      private int getTotalBooks() {
        String query = "SELECT COUNT(*) AS total FROM livre";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {
                return result.getInt("total");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }


      private int getAvailableBooks() {
        String query = "SELECT COUNT(*) AS available FROM copie WHERE statut = 'disponible'";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {
                return result.getInt("available");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }


      private int getBorrowedBooks() {
        String query = "SELECT COUNT(*) AS borrowed FROM copie WHERE statut = 'emprunté'";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {
                return result.getInt("borrowed");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    private int getLostBooks() {

        return 0;
    }





}




