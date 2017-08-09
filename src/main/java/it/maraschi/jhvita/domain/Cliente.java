package it.maraschi.jhvita.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A Cliente.
 */
@Entity
@Table(name = "cliente")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "cliente")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_cliente", precision=10, scale=2)
    private BigDecimal codCliente;

    @NotNull
    @Size(min = 0, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "ragione_sociale", length = 100, nullable = false)
    private String ragioneSociale;

    @Column(name = "partita_iva")
    private String partitaIVA;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCodCliente() {
        return codCliente;
    }

    public Cliente codCliente(BigDecimal codCliente) {
        this.codCliente = codCliente;
        return this;
    }

    public void setCodCliente(BigDecimal codCliente) {
        this.codCliente = codCliente;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public Cliente ragioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
        return this;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getPartitaIVA() {
        return partitaIVA;
    }

    public Cliente partitaIVA(String partitaIVA) {
        this.partitaIVA = partitaIVA;
        return this;
    }

    public void setPartitaIVA(String partitaIVA) {
        this.partitaIVA = partitaIVA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cliente cliente = (Cliente) o;
        if (cliente.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cliente.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + getId() +
            ", codCliente='" + getCodCliente() + "'" +
            ", ragioneSociale='" + getRagioneSociale() + "'" +
            ", partitaIVA='" + getPartitaIVA() + "'" +
            "}";
    }
}
