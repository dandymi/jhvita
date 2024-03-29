package it.maraschi.jhvita.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.maraschi.jhvita.domain.Cliente;
import it.maraschi.jhvita.repository.ClienteRepository;
import it.maraschi.jhvita.repository.search.ClienteSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cliente}.
 */
@Service
@Transactional
public class ClienteService {

    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;

    private final ClienteSearchRepository clienteSearchRepository;

    public ClienteService(ClienteRepository clienteRepository, ClienteSearchRepository clienteSearchRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteSearchRepository = clienteSearchRepository;
    }

    /**
     * Save a cliente.
     *
     * @param cliente the entity to save.
     * @return the persisted entity.
     */
    public Cliente save(Cliente cliente) {
        log.debug("Request to save Cliente : {}", cliente);
        Cliente result = clienteRepository.save(cliente);
        clienteSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a cliente.
     *
     * @param cliente the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Cliente> partialUpdate(Cliente cliente) {
        log.debug("Request to partially update Cliente : {}", cliente);

        return clienteRepository
            .findById(cliente.getId())
            .map(existingCliente -> {
                if (cliente.getCodCliente() != null) {
                    existingCliente.setCodCliente(cliente.getCodCliente());
                }
                if (cliente.getRagioneSociale() != null) {
                    existingCliente.setRagioneSociale(cliente.getRagioneSociale());
                }
                if (cliente.getPartitaIVA() != null) {
                    existingCliente.setPartitaIVA(cliente.getPartitaIVA());
                }

                return existingCliente;
            })
            .map(clienteRepository::save)
            .map(savedCliente -> {
                clienteSearchRepository.save(savedCliente);

                return savedCliente;
            });
    }

    /**
     * Get all the clientes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        log.debug("Request to get all Clientes");
        return clienteRepository.findAll(pageable);
    }

    /**
     * Get one cliente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> findOne(Long id) {
        log.debug("Request to get Cliente : {}", id);
        return clienteRepository.findById(id);
    }

    /**
     * Delete the cliente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Cliente : {}", id);
        clienteRepository.deleteById(id);
        clienteSearchRepository.deleteById(id);
    }

    /**
     * Search for the cliente corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Cliente> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Clientes for query {}", query);
        return clienteSearchRepository.search(query, pageable);
    }
}
