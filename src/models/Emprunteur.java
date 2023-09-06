package models;

public class Emprunteur extends  Person{
    private String num_de_membre;
    private String date_emprunt;

    public Emprunteur(String nom, String email, String num_de_membre) {
        super(nom, email);
        this.num_de_membre = num_de_membre;
    }

    public Emprunteur() {}

    public String getNum_de_membre() {
        return num_de_membre;
    }

    public void setNum_de_membre(String num_de_membre) {
        this.num_de_membre = num_de_membre;
    }

    public String getDate_emprunt() {
        return date_emprunt;
    }

    public void setDate_emprunt(String date_emprunt) {
        this.date_emprunt = date_emprunt;
    }
}
