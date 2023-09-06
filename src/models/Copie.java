package models;

public class Copie {
private String statut;
private int livreId;

    public Copie(String statut, int livreId) {
        this.statut = statut;
        this.livreId = livreId;
    }
    public Copie()
    {}

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
}
