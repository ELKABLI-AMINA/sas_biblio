package models;

public class Emprunteur extends  Person{
    private int id;
    private String num_de_membre;


    public Emprunteur(String nom, String email, String num_de_membre) {
        super(nom, email);
        this.num_de_membre = num_de_membre;

    }

    public Emprunteur() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNum_de_membre() {
        return num_de_membre;
    }

    public void setNum_de_membre(String num_de_membre) {
        this.num_de_membre = num_de_membre;
    }


}
