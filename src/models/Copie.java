package models;

import java.util.ArrayList;

public class Copie {


    private String statut;
    private int livreId;

    private ArrayList<Book> books;

    public Copie(String statut, int livreId ) {
        this.statut = statut;
        this.livreId = livreId;
        this.initBook();
    }

    public Copie() {
        this.initBook();
    }

    private void initBook() {
        this.books = new ArrayList<Book>();
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getLivreId() {
        return livreId;
    }

    public void setLivreId(int livreId) {
        this.livreId = livreId;
    }


    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }
}
