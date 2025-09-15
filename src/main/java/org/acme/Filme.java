package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Filme extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Vamos gerar automaticamente no banco
    public Long id;
    public String titulo;
    public String sinopse;
    public int anoLancamento;
    public double nota;
    public int idadeIndicativa;

    public Filme() {
    }

    public Filme(Long id, String titulo, String sinopse, int anoLancamento, double nota, int idadeIndicativa) {
        this.id = id;
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.anoLancamento = anoLancamento;
        this.nota = nota;
        this.idadeIndicativa = idadeIndicativa;
    }
}