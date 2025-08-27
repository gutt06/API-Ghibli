package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Movie extends PanacheEntity {
    public String titulo;
    public String autor;
    public int anoLancamento;
    public double nota;

    public Movie() {
    }

    public Movie(String autor, String titulo, int anoLancamento, double nota) {
        this.autor = autor;
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.nota = nota;
    }
}
