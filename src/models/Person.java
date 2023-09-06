package models;

public class Person {
    private String nom;
    private String email;

    public Person(String nom , String email) {
        this.nom = nom;
        this.email = email;
    }
    public Person(){}

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
