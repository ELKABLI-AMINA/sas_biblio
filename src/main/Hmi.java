package main;

import models.Book;
import models.Emprunteur;
import models.Utilisateur;
import services.BookService;
import services.EmprunteurService;
import services.UtilisateurService;

import java.util.Scanner;

public class Hmi {
    public static void start() {
        EmprunteurService daoEmprunteur = new EmprunteurService();
        BookService daoBook = new BookService();
        UtilisateurService dao = new UtilisateurService();
        System.out.println("Welcome to Library management application");
        Scanner reader = new Scanner(System.in);
        boolean isLoggedIn = false;
        Utilisateur currentUser = null;

        do {
            System.out.println("Choose an option:\n");

            if (!isLoggedIn) {
                System.out.println("1. Register");
                System.out.println("2. Login");
            } else {
                System.out.println("3. Manage Library");
                System.out.println("4. Logout");
            }

            int choice = reader.nextInt();
            reader.nextLine(); // Lire la ligne vide après nextInt()

            switch (choice) {
                case 1:
                    // ... (ajoutez la logique d'inscription ici)
                    break;
                case 2:
                    System.out.println("Enter Email:");
                    String loginEmail = reader.nextLine();
                    System.out.println("Enter password:");
                    String loginPassword = reader.nextLine();

                    Utilisateur loginSuccessful = dao.loginUser(loginEmail, loginPassword);
                    if (loginSuccessful != null) {
                        System.out.println("Hello " + loginSuccessful.getNom());
                        isLoggedIn = true;
                        currentUser = loginSuccessful;
                    } else {
                        System.out.println("Login failed. Please check your email and password.");
                    }
                    break;
                case 3:
                    if (isLoggedIn) {
                        do {
                            System.out.println("Manage Library");
                            System.out.println("1. Add a Book");
                            System.out.println("2. display the list of available books");
                            System.out.println("3. Add a Borrower");
                            System.out.println("4. borrow a book");
                            System.out.println("5. return a book");
                            System.out.println("9. display the list of borrowed books");
                            System.out.println("6. delete a book ");
                            System.out.println("8. update a book");

                            int libraryChoice = reader.nextInt();
                            reader.nextLine();

                            switch (libraryChoice) {
                                case 1:
                                    Book book = new Book();

                                    System.out.println("Enter title : ");
                                    String titre = reader.next();
                                    System.out.println("Enter Auteur : ");
                                    String auteur = reader.next();
                                    System.out.println("Enter isbn : ");
                                    String isbn = reader.next();
                                    book.setTitre(titre);
                                    book.setAuteur(auteur);
                                    book.setIsbn(isbn);
                                    daoBook.createBook(book);
                                    break;
                                case 2:
                                    daoBook.showAllBooks();
                                    break;
                                case 3:
                                    Emprunteur emprunteur = new Emprunteur();

                                    System.out.println("Enter name : ");
                                    String nom = reader.next();
                                    System.out.println("Enter Membership_number : ");
                                    String num_de_membre = reader.next();
                                    System.out.println("Enter email : ");
                                    String email = reader.next();
                                    emprunteur.setNom(nom);
                                    emprunteur.setNum_de_membre(num_de_membre);
                                    emprunteur.setEmail(email);
                                    daoEmprunteur.createBorrower(emprunteur);


                                    break;
                                case 4:
                                    // Logique pour afficher les livres empruntés
                                    break;

                                case 8:
                                    System.out.println("Enter isbn to update the details");
                                    String oldIsbn = reader.next(); // Sauvegarde de l'ancien ISBN
                                    System.out.println("Enter the new title");
                                    titre = reader.next();
                                    System.out.println("Enter the new auteur");
                                    auteur = reader.next();
                                    System.out.println("Enter the new isbn");
                                    String newIsbn = reader.next(); // Saisie du nouveau ISBN
                                    daoBook.updateBook(oldIsbn, titre, auteur, newIsbn);
                                    break;

                                case 6:
                                    System.out.println("Enter isbn to delete the details");
                                    isbn = reader.next();
                                    daoBook.deleteBook(isbn);
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please select a valid library option.");
                                    break;
                            }
                        } while (true);
                    } else {
                        System.out.println("Invalid choice. Please select a valid option.");
                    }
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    isLoggedIn = false;
                    currentUser = null;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    break;
            }
        } while (true);
    }
}
