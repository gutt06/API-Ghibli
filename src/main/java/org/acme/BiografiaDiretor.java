package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class BiografiaDiretor extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Size(max = 2000, message = "A biografia não pode ultrapassar 2000 caracteres")
    @Column(length = 2000)
    public String textoCompleto;

    @Size(max = 100, message = "O resumo não pode ultrapassar 100 caracteres")
    public String resumo;

    public String premiosRecebidos;

    // One-to-One: uma biografia pertence a um diretor
    @OneToOne(mappedBy = "biografia", fetch = FetchType.LAZY)
    @JsonIgnore
    public Diretor diretor;

    public BiografiaDiretor() {}

    public BiografiaDiretor(String textoCompleto, String resumo, String premiosRecebidos) {
        this.textoCompleto = textoCompleto;
        this.resumo = resumo;
        this.premiosRecebidos = premiosRecebidos;
    }
}