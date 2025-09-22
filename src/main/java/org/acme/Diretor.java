package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Diretor extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    public String nome;

    @Past(message = "A data de nascimento deve ser no passado")
    public LocalDate nascimento;

    @NotBlank(message = "A nacionalidade é obrigatória")
    @Size(max = 80)
    public String nacionalidade;

    @Size(max = 500, message = "A biografia não pode ultrapassar 500 caracteres")
    public String biografia;

    // One-to-Many: um diretor pode ter vários filmes
    @OneToMany(mappedBy = "diretor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Filme> filmes = new ArrayList<>();

    public Diretor() {}

    public Diretor(Long id, String nome, LocalDate nascimento, String nacionalidade, String biografia) {
        this.id = id;
        this.nome = nome;
        this.nascimento = nascimento;
        this.nacionalidade = nacionalidade;
        this.biografia = biografia;
    }
}
