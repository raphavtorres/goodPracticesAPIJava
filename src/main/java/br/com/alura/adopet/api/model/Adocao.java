package br.com.alura.adopet.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;



// CONVENTION OVER CONFIGURATION (revisar na aula)
// siga a convenção para não ter que configurar

@Entity  // entidade da JPA
@Table(name = "adocoes")
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")  -- não preciso pq a convenção já diz que o nome da coluna == nome atributo
    private Long id;

    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY)  // Relacionamentos "...ToOne" por padrão são FetchType.EAGER
//    @JsonBackReference("tutor_adocoes")  -- só era necessário quando estavamos passando JPAs no controller
    private Tutor tutor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
//    @JsonManagedReference("adocao_pets") -- só era necessário quando estavamos passando JPAs no controller
    private Pet pet;

    private String motivo;

    @Enumerated(EnumType.STRING)
    private StatusAdocao status;

//    @Column(name = "justificativa_status") -- também está seguindo convenção
    private String justificativaStatus;

    public Adocao(Tutor tutor, Pet pet, String motivo) {
        this.tutor = tutor;
        this.pet = pet;
        this.motivo = motivo;
        this.status = StatusAdocao.AGUARDANDO_AVALIACAO;
        this.data = LocalDateTime.now();
    }

    public Adocao() {}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adocao adocao = (Adocao) o;
        return Objects.equals(id, adocao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public Pet getPet() {
        return pet;
    }

    public String getMotivo() {
        return motivo;
    }

    public StatusAdocao getStatus() {
        return status;
    }

    public String getJustificativaStatus() {
        return justificativaStatus;
    }

    public void marcarComoAprovado() {
        this.status = StatusAdocao.APROVADO;
    }

    public void marcarComoReprovada(String justificativa) {
        this.status = StatusAdocao.REPROVADO;
        this.justificativaStatus = justificativa;
    }
}
