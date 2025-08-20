package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Book extends PanacheEntity {
    public String titulo;
    public String autor;
    public String editora;
    public int anoLancamento;
    public boolean estaDisponivel;

    public Book() {
    }

    public Book(String titulo, String autor, String editora, int anoLancamento, boolean estaDisponivel) {
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anoLancamento = anoLancamento;
        this.estaDisponivel = estaDisponivel;
    }
}
