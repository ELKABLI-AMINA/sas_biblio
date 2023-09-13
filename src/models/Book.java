package models;

public class Book {

    private int id;
    private String titre;
    private String auteur;
    private String isbn;
    private int étagère_de_placard;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book(String titre, String auteur, String isbn , int étagère_de_placard) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.étagère_de_placard=étagère_de_placard;

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

    public int getÉtagère_de_placard() {
        return étagère_de_placard;
    }

    public void setÉtagère_de_placard(int étagère_de_placard) {
        this.étagère_de_placard = étagère_de_placard;
    }
}
