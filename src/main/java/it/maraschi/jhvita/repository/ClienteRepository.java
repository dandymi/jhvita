package it.maraschi.jhvita.repository;

import it.maraschi.jhvita.domain.Cliente;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Cliente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    
}
