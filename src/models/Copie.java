package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class Copie {


    private String statut;
    private int livreId;
    private Date date_retour;

    private ArrayList<Book> books;

    public Copie(String statut, int livreId , Date date_retour) {
        this.statut = statut;
        this.livreId = livreId;
        this.date_retour= date_retour;

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

    public Date getDate_retour() {
        return date_retour;
    }

    public void setDate_retour(Date date_retour) {
        this.date_retour = date_retour;
    }
}
