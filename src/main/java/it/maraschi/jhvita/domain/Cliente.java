package it.maraschi.jhvita.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Cliente.
 */
@Entity
@Table(name = "cliente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cliente")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cod_cliente", precision = 21, scale = 2)
    private BigDecimal codCliente;

    @NotNull
    @Size(min = 0, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "ragione_sociale", length = 100, nullable = false)
    private String ragioneSociale;

    @Column(name = "partita_iva")
    private String partitaIVA;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cliente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCodCliente() {
        return this.codCliente;
    }

    public Cliente codCliente(BigDecimal codCliente) {
        this.setCodCliente(codCliente);
        return this;
    }

    public void setCodCliente(BigDecimal codCliente) {
        this.codCliente = codCliente;
    }

    public String getRagioneSociale() {
        return this.ragioneSociale;
    }

    public Cliente ragioneSociale(String ragioneSociale) {
        this.setRagioneSociale(ragioneSociale);
        return this;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getPartitaIVA() {
        return this.partitaIVA;
    }

    public Cliente partitaIVA(String partitaIVA) {
        this.setPartitaIVA(partitaIVA);
        return this;
    }

    public void setPartitaIVA(String partitaIVA) {
        this.partitaIVA = partitaIVA;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        return id != null && id.equals(((Cliente) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + getId() +
            ", codCliente=" + getCodCliente() +
            ", ragioneSociale='" + getRagioneSociale() + "'" +
            ", partitaIVA='" + getPartitaIVA() + "'" +
            "}";
    }
}
