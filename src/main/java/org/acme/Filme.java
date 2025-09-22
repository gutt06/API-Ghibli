package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Filme extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "O título não pode ser vazio")
    @Size(min = 1, max = 200)
    public String titulo;

    @NotBlank(message = "A sinopse é obrigatória")
    @Size(max = 2000)
    public String sinopse;

    @Min(value = 1986, message = "Ano de lançamento inválido, o primeiro filme do estudio foi lancado em 1986")
    public int anoLancamento;

    @DecimalMin(value = "0.0", inclusive = true, message = "Nota mínima é 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Nota máxima é 10.0")
    public double nota;

    @Min(value = 0, message = "Idade indicativa não pode ser negativa")
    public int idadeIndicativa;

    // Many-to-One: vários filmes para um diretor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "diretor_id")
    public Diretor diretor;

    // Many-to-Many: filmes - gêneros
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "filme_genero",
            joinColumns = @JoinColumn(name = "filme_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    public Set<Genero> generos = new HashSet<>();

    public Filme() {}

    public Filme(Long id, String titulo, String sinopse, int anoLancamento, double nota, int idadeIndicativa) {
        this.id = id;
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.anoLancamento = anoLancamento;
        this.nota = nota;
        this.idadeIndicativa = idadeIndicativa;
    }
}