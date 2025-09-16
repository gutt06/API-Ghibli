package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Diretor extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String nome;
    public String nascimento;
    public String nacionalidade;
    public String biografia;
    public String filme;

    public Diretor() {
    }

    public Diretor(Long id, String nome, String nascimento, String nacionalidade, String biografia, String filme) {
        this.id = id;
        this.nome = nome;
        this.nascimento = nascimento;
        this.nacionalidade = nacionalidade;
        this.biografia = biografia;
        this.filme = filme;
    }
}
