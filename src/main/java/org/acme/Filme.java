package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Filme extends PanacheEntity {
    public String titulo;
    public String sinopse;
    public int anoLancamento;
    public double nota;
    public int idadeIndicativa;

    public Filme() {
    }

    public Filme(String titulo, String sinopse, int anoLancamento, double nota, int idadeIndicativa) {
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.anoLancamento = anoLancamento;
        this.nota = nota;
        this.idadeIndicativa = idadeIndicativa;
    }
}