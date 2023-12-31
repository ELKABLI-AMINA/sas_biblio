package main;

import models.Book;
import models.Utilisateur;
import services.BookService;
import services.CopieService;
import services.EmprunteurService;
import services.UtilisateurService;

import java.util.Scanner;
import java.util.List;


public class Hmi {
    public static void start() {
        EmprunteurService daoEmprunteur = new EmprunteurService();
        BookService daoBook = new BookService();
        CopieService daoCopie = new CopieService();
        UtilisateurService dao = new UtilisateurService();
        System.out.println("----------Welcome to Library management application---------");
        Scanner reader = new Scanner(System.in);
        boolean isLoggedIn = false;
        Utilisateur currentUser = null;

        do {
            System.out.println("--------------Choose an option--------------:\n");

            if (!isLoggedIn) {
                System.out.println("1. Register");
                System.out.println("2. Login");
            } else {
                System.out.println("3. Manage Library");
                System.out.println("4. Logout");
            }

            int choice = reader.nextInt();
            reader.nextLine();

            switch (choice) {
                case 1:
                    Utilisateur utilisateur = new Utilisateur();
                    System.out.println("Enter name:");
                    String nom = reader.nextLine();
                    System.out.println("Enter Email:");
                    String email = reader.nextLine();
                    System.out.println("Enter password:");
                    String password = reader.nextLine();
                    utilisateur.setNom(nom);
                    utilisateur.setEmail(email);
                    utilisateur.setPassword(password);
                    dao.createUtilisateur(utilisateur);

                    break;
                case 2:
                    System.out.println("Enter Email:");
                    String loginEmail = reader.nextLine();
                    System.out.println("Enter password:");
                    String loginPassword = reader.nextLine();

                    Utilisateur loginSuccessful = dao.loginUser(loginEmail, loginPassword);
                    if (loginSuccessful != null) {
                        System.out.println("*_____________________________*");
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
                            System.out.println("2. Display available books");
                            System.out.println("3. Search  a book");
                            System.out.println("4. update  a book");
                            System.out.println("5. delete a book");
                            System.out.println("6. borrow a book ");
                            System.out.println("7. retourne a book");
                            System.out.println("8. display the list of borrowed books");
                            System.out.println("9. statistics");


                            int libraryChoice = reader.nextInt();
                            reader.nextLine();

                            switch (libraryChoice) {
                                case 1:
                                    System.out.println("Enter ISBN: ");
                                    String isbn = reader.next();
                                    Book existingBook = daoBook.checkIfExists(isbn);

                                    if (existingBook != null) {
                                        System.out.println("Le livre existe déjà avec ISBN : " + existingBook.getIsbn());
                                    } else {
                                        Book newBook = new Book();
                                        System.out.println("Enter title : ");
                                        String titre = reader.next();
                                        System.out.println("Enter Auteur : ");
                                        String auteur = reader.next();
                                        System.out.println("Enter quantity: ");
                                        int quantity = reader.nextInt();
                                        System.out.println("Enter closet_shelves");
                                        int étagère_de_placard = reader.nextInt();
                                        newBook.setTitre(titre);
                                        newBook.setAuteur(auteur);
                                        newBook.setIsbn(isbn);
                                        newBook.setÉtagère_de_placard(étagère_de_placard);
                                        int bookId = daoBook.createBook(newBook);
                                        daoCopie.insertCopies(bookId, quantity);
                                        System.out.println("Nouveau livre créé avec succès.");
                                    }
                                    break;

                                case 2:
                                 daoBook.displayAvailableBooks();

                                    break;
                                case 3:

                                    System.out.println("Enter a title or author to search for books:");
                                    String searchQuery = reader.next();
                                    List<Book> resultatRecherche = BookService.rechercherLivres(searchQuery);

                                    if (!resultatRecherche.isEmpty()) {
                                        System.out.println("Search Results:");
                                        for (Book bookSearch : resultatRecherche) {
                                            System.out.println("Titre: " + bookSearch.getTitre());
                                            System.out.println("Auteur: " + bookSearch.getAuteur());
                                            System.out.println("ISBN: " + bookSearch.getIsbn());
                                            System.out.println();
                                        }
                                    } else {
                                        System.out.println("No books found matching the search criteria.");
                                    }

                                    break;
                                case 4:
                                    System.out.println("Enter oldIsbn: ");
                                    String oldIsbn = reader.next();

                                    existingBook = daoBook.checkIfExists(oldIsbn);

                                    if (existingBook != null) {
                                        System.out.println("Le livre existe déjà avec ISBN : " + existingBook.getIsbn());
                                        System.out.println("Enter new title : ");
                                        String titre = reader.next();
                                        System.out.println("Enter new Auteur : ");
                                        String auteur = reader.next();
                                        System.out.println("Enter new closet_shelves");
                                        int étagère_de_placard = reader.nextInt();
                                        System.out.println("Enter newIsbn: ");
                                        String newIsbn = reader.next(); // Déclarez et initialisez newIsbn

                                        // Utilisez oldIsbn comme isbn pour mettre à jour le livre existant
                                        daoBook.updateBook(oldIsbn, titre, auteur, newIsbn, étagère_de_placard);
                                        System.out.println("Livre mis à jour avec succès.");
                                    } else {
                                        System.out.println("Le livre avec l'ISBN " + oldIsbn + " n'existe pas.");
                                    }
                                    break;



                                case 5:
                                    System.out.println("Enter isbn to delete the details");
                                    isbn = reader.next();
                                    daoBook.deleteBook(isbn);
                                    break;

                                case 6:
                                    daoBook.emprunterLivre();
                                    break;
                                case 7:
                                    daoBook.retournerLivre();
                                    break;
                                case 8:
                                    daoBook.displayBorrowedBooks();
                                    break;
                                case 9:
                                    daoBook.generateLibraryReport();
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



