package models;

public class Book {

    private int id;
    private String titre;
    private String auteur;
    private String isbn;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book(String titre, String auteur, String isbn) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;

    }

    public Book() {

    }



    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


}
