package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Connection.DBConnection;
import models.Book;
import models.Emprunteur;
import java.sql.Timestamp;
import java.util.Date;

public class BookService {

    EmprunteurService daoEmprunteur = new EmprunteurService();

    private static Connection con = DBConnection.createDBConnection();

    public int createBook(Book book) {
        String query = "INSERT INTO livre (titre, auteur, isbn  , étagère_de_placard) VALUES (?, ?, ?, ?)";
        int bookId = -1;

        try {
            PreparedStatement pstm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);


            pstm.setString(1, book.getTitre());
            pstm.setString(2, book.getAuteur());
            pstm.setString(3, book.getIsbn());
            pstm.setInt(4, book.getÉtagère_de_placard());
            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Book Inserted Successfully");
                ResultSet generatedKeys = pstm.getGeneratedKeys();// pour récupérer l'id
                if (generatedKeys.next()) {
                    bookId = generatedKeys.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bookId;
    }


    public List<Book> displayAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        String query = "SELECT titre, auteur, statut FROM livre INNER JOIN copie ON livre.id = copie.livre_id  where statut= 'disponible' ";

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
        return availableBooks;
    }


    public void updateBook(String isbn, String titre, String auteur, String newIsbn) {
        CopieService daoCopie = new CopieService();
        BookService daoBook = new BookService();

        String query = "UPDATE livre SET titre=?, auteur=?, isbn=? WHERE isbn=?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, titre);
            pstm.setString(2, auteur);
            pstm.setString(3, newIsbn);
            pstm.setString(4, isbn);

            int cnt = pstm.executeUpdate();
            if (cnt != 0) {
                System.out.println("Book Details updated successfully");
//                if (quantityToAdd > 0) {
//
//                    daoCopie.insertCopies(bookId,
//                            quantityToAdd);
//                }
//
//                if (quantityToRemove > 0) {
//                    daoCopie.supprimerCopies(bookId, quantityToRemove);
//                }
            } else {
                System.out.println("No book found with the provided ISBN.");
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

        String queryCheckCopies = "SELECT COUNT(*) FROM copie WHERE livre_id IN (SELECT id FROM livre WHERE isbn = ?) AND statut= 'emprunte' ";

        try {
            PreparedStatement stmtCheckCopies = con.prepareStatement(queryCheckCopies);
            stmtCheckCopies.setString(1, isbn);
            ResultSet resultSet = stmtCheckCopies.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("Le livre ne peut pas être supprimé car il y a des copies empruntées.");
                return;
            }


            String queryDeleteCopies = "DELETE FROM copie WHERE livre_id IN (SELECT id FROM livre WHERE isbn = ?)";
            String queryDeleteBook = "DELETE FROM livre WHERE isbn = ?";


            PreparedStatement stmtDeleteCopies = con.prepareStatement(queryDeleteCopies);
            stmtDeleteCopies.setString(1, isbn);
            int copiesDeleted = stmtDeleteCopies.executeUpdate();

            PreparedStatement stmtDeleteBook = con.prepareStatement(queryDeleteBook);
            stmtDeleteBook.setString(1, isbn);
            int bookDeleted = stmtDeleteBook.executeUpdate();

            if (bookDeleted > 0) {
                System.out.println("Le livre et les copies associées ont été supprimés avec succès.");
            } else {
                System.out.println("Aucun livre trouvé avec l'ISBN fourni.");
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


        return null;
    }


    public void emprunterLivre() {
        CopieService daoCopie = new CopieService();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez le numéro ISBN du livre que vous souhaitez emprunter :");
        String isbn = scanner.next();

        Book existingBook = checkIfExists(isbn);

        if (existingBook != null) {
            int copieId = daoCopie.getStatutByISBNDisponible(isbn);
            if (copieId != 0) {
                System.out.println("Entrez le nom de l'emprunteur :");
                String nom = scanner.next();
                System.out.println("Entrez le numéro de membre de l'emprunteur :");
                String num_de_membre = scanner.next();
                System.out.println("Entrez l'email de l'emprunteur :");
                String email = scanner.next();
                Emprunteur emprunteur = daoEmprunteur.emprunteurExisteDeja(nom, num_de_membre, email);
                if (emprunteur != null) {
                     daoCopie.insererCopieEmprunteur(copieId, emprunteur.getId());
                    daoCopie.updateStatutToEmprunte(isbn);
                } else {
                    emprunteur = new Emprunteur(nom, num_de_membre, email);
                    emprunteur = daoEmprunteur.createBorrower(emprunteur);
                    int emprunteurId = daoEmprunteur.getEmprunteurIdByInfo(nom, num_de_membre, email);
                    Timestamp dateEmprunt = new Timestamp(new Date().getTime());
                    daoCopie.insererCopieEmprunteur(copieId, emprunteurId);
                    System.out.println("L'emprunt a réussi !");
                }
            } else {
                System.out.println("Désolé, ce livre n'est pas disponible pour l'emprunt.");
            }
        } else {
            System.out.println("Ce livre n'existe pas dans la bibliothèque.");
        }
    }
    public void displayBorrowedBooks() {
        String query =
                "SELECT livre.titre, livre.auteur, emprunteur.nom , emprunteur.num_de_membre, emprunteur.email, copie_emprunteur.date_emprunt " +
                        "FROM copie_emprunteur " +
                        "JOIN copie ON copie_emprunteur.copie_id = copie.id " +
                        "JOIN emprunteur ON copie_emprunteur.emprunteur_id = emprunteur.id " +
                        "JOIN livre ON copie.livre_id = livre.id " +
                        "WHERE copie.statut = 'emprunté'";


        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                String titreLivre = resultSet.getString("titre");
                String auteurLivre = resultSet.getString("auteur");
                String nomEmprunteur = resultSet.getString("nom");
                String numMembre = resultSet.getString("num_de_membre");
                String emailEmprunteur = resultSet.getString("email");
                String dateEmprunt = resultSet.getString("date_emprunt");

                System.out.println("Titre du livre : " + titreLivre);
                System.out.println("Auteur du livre : " + auteurLivre);
                System.out.println("Nom de l'emprunteur : " + nomEmprunteur);
                System.out.println("Numéro de membre de l'emprunteur : " + numMembre);
                System.out.println("Email de l'emprunteur : " + emailEmprunteur);
                System.out.println("Date d'emprunt : " + dateEmprunt);
                System.out.println("------------------------");
            }


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

    public void retournerLivre() {
        CopieService daoCopie = new CopieService();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez le numéro ISBN du livre que vous souhaitez retourner :");
        String isbn = scanner.next();

        String statut = daoCopie.getStatutByISBNEmprunté(isbn);

        if ("emprunté".equals(statut)) {
            daoCopie.updateStatutToDisponible(isbn);

            daoEmprunteur.supprimerEmprunteurEtDate(isbn);

            System.out.println("Le livre a été retourné avec succès !");
        } else {
            System.out.println("Désolé, ce livre n'est pas emprunté.");
        }

    }


}

