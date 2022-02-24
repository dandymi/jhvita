package it.maraschi.jhvita.web.rest;

import static it.maraschi.jhvita.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.maraschi.jhvita.IntegrationTest;
import it.maraschi.jhvita.domain.Cliente;
import it.maraschi.jhvita.repository.ClienteRepository;
import it.maraschi.jhvita.repository.search.ClienteSearchRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClienteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClienteResourceIT {

    private static final BigDecimal DEFAULT_COD_CLIENTE = new BigDecimal(1);
    private static final BigDecimal UPDATED_COD_CLIENTE = new BigDecimal(2);

    private static final String DEFAULT_RAGIONE_SOCIALE = "AAAAAAAAAA";
    private static final String UPDATED_RAGIONE_SOCIALE = "BBBBBBBBBB";

    private static final String DEFAULT_PARTITA_IVA = "AAAAAAAAAA";
    private static final String UPDATED_PARTITA_IVA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clientes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/clientes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * This repository is mocked in the it.maraschi.jhvita.repository.search test package.
     *
     * @see it.maraschi.jhvita.repository.search.ClienteSearchRepositoryMockConfiguration
     */
    @Autowired
    private ClienteSearchRepository mockClienteSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClienteMockMvc;

    private Cliente cliente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cliente createEntity(EntityManager em) {
        Cliente cliente = new Cliente()
            .codCliente(DEFAULT_COD_CLIENTE)
            .ragioneSociale(DEFAULT_RAGIONE_SOCIALE)
            .partitaIVA(DEFAULT_PARTITA_IVA);
        return cliente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cliente createUpdatedEntity(EntityManager em) {
        Cliente cliente = new Cliente()
            .codCliente(UPDATED_COD_CLIENTE)
            .ragioneSociale(UPDATED_RAGIONE_SOCIALE)
            .partitaIVA(UPDATED_PARTITA_IVA);
        return cliente;
    }

    @BeforeEach
    public void initTest() {
        cliente = createEntity(em);
    }

    @Test
    @Transactional
    void createCliente() throws Exception {
        int databaseSizeBeforeCreate = clienteRepository.findAll().size();
        // Create the Cliente
        restClienteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cliente)))
            .andExpect(status().isCreated());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeCreate + 1);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getCodCliente()).isEqualByComparingTo(DEFAULT_COD_CLIENTE);
        assertThat(testCliente.getRagioneSociale()).isEqualTo(DEFAULT_RAGIONE_SOCIALE);
        assertThat(testCliente.getPartitaIVA()).isEqualTo(DEFAULT_PARTITA_IVA);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(1)).save(testCliente);
    }

    @Test
    @Transactional
    void createClienteWithExistingId() throws Exception {
        // Create the Cliente with an existing ID
        cliente.setId(1L);

        int databaseSizeBeforeCreate = clienteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClienteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cliente)))
            .andExpect(status().isBadRequest());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeCreate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void checkRagioneSocialeIsRequired() throws Exception {
        int databaseSizeBeforeTest = clienteRepository.findAll().size();
        // set the field null
        cliente.setRagioneSociale(null);

        // Create the Cliente, which fails.

        restClienteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cliente)))
            .andExpect(status().isBadRequest());

        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientes() throws Exception {
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);

        // Get all the clienteList
        restClienteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cliente.getId().intValue())))
            .andExpect(jsonPath("$.[*].codCliente").value(hasItem(sameNumber(DEFAULT_COD_CLIENTE))))
            .andExpect(jsonPath("$.[*].ragioneSociale").value(hasItem(DEFAULT_RAGIONE_SOCIALE)))
            .andExpect(jsonPath("$.[*].partitaIVA").value(hasItem(DEFAULT_PARTITA_IVA)));
    }

    @Test
    @Transactional
    void getCliente() throws Exception {
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);

        // Get the cliente
        restClienteMockMvc
            .perform(get(ENTITY_API_URL_ID, cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cliente.getId().intValue()))
            .andExpect(jsonPath("$.codCliente").value(sameNumber(DEFAULT_COD_CLIENTE)))
            .andExpect(jsonPath("$.ragioneSociale").value(DEFAULT_RAGIONE_SOCIALE))
            .andExpect(jsonPath("$.partitaIVA").value(DEFAULT_PARTITA_IVA));
    }

    @Test
    @Transactional
    void getNonExistingCliente() throws Exception {
        // Get the cliente
        restClienteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCliente() throws Exception {
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);

        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();

        // Update the cliente
        Cliente updatedCliente = clienteRepository.findById(cliente.getId()).get();
        // Disconnect from session so that the updates on updatedCliente are not directly saved in db
        em.detach(updatedCliente);
        updatedCliente.codCliente(UPDATED_COD_CLIENTE).ragioneSociale(UPDATED_RAGIONE_SOCIALE).partitaIVA(UPDATED_PARTITA_IVA);

        restClienteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCliente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCliente))
            )
            .andExpect(status().isOk());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getCodCliente()).isEqualByComparingTo(UPDATED_COD_CLIENTE);
        assertThat(testCliente.getRagioneSociale()).isEqualTo(UPDATED_RAGIONE_SOCIALE);
        assertThat(testCliente.getPartitaIVA()).isEqualTo(UPDATED_PARTITA_IVA);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository).save(testCliente);
    }

    @Test
    @Transactional
    void putNonExistingCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();
        cliente.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClienteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cliente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cliente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void putWithIdMismatchCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cliente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cliente)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void partialUpdateClienteWithPatch() throws Exception {
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);

        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();

        // Update the cliente using partial update
        Cliente partialUpdatedCliente = new Cliente();
        partialUpdatedCliente.setId(cliente.getId());

        partialUpdatedCliente.partitaIVA(UPDATED_PARTITA_IVA);

        restClienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCliente.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCliente))
            )
            .andExpect(status().isOk());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getCodCliente()).isEqualByComparingTo(DEFAULT_COD_CLIENTE);
        assertThat(testCliente.getRagioneSociale()).isEqualTo(DEFAULT_RAGIONE_SOCIALE);
        assertThat(testCliente.getPartitaIVA()).isEqualTo(UPDATED_PARTITA_IVA);
    }

    @Test
    @Transactional
    void fullUpdateClienteWithPatch() throws Exception {
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);

        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();

        // Update the cliente using partial update
        Cliente partialUpdatedCliente = new Cliente();
        partialUpdatedCliente.setId(cliente.getId());

        partialUpdatedCliente.codCliente(UPDATED_COD_CLIENTE).ragioneSociale(UPDATED_RAGIONE_SOCIALE).partitaIVA(UPDATED_PARTITA_IVA);

        restClienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCliente.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCliente))
            )
            .andExpect(status().isOk());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getCodCliente()).isEqualByComparingTo(UPDATED_COD_CLIENTE);
        assertThat(testCliente.getRagioneSociale()).isEqualTo(UPDATED_RAGIONE_SOCIALE);
        assertThat(testCliente.getPartitaIVA()).isEqualTo(UPDATED_PARTITA_IVA);
    }

    @Test
    @Transactional
    void patchNonExistingCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();
        cliente.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cliente.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cliente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cliente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cliente)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(0)).save(cliente);
    }

    @Test
    @Transactional
    void deleteCliente() throws Exception {
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);

        int databaseSizeBeforeDelete = clienteRepository.findAll().size();

        // Delete the cliente
        restClienteMockMvc
            .perform(delete(ENTITY_API_URL_ID, cliente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cliente> clienteList = clienteRepository.findAll();
        assertThat(clienteList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Cliente in Elasticsearch
        verify(mockClienteSearchRepository, times(1)).deleteById(cliente.getId());
    }

    @Test
    @Transactional
    void searchCliente() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        clienteRepository.saveAndFlush(cliente);
        when(mockClienteSearchRepository.search("id:" + cliente.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(cliente), PageRequest.of(0, 1), 1));

        // Search the cliente
        restClienteMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cliente.getId().intValue())))
            .andExpect(jsonPath("$.[*].codCliente").value(hasItem(sameNumber(DEFAULT_COD_CLIENTE))))
            .andExpect(jsonPath("$.[*].ragioneSociale").value(hasItem(DEFAULT_RAGIONE_SOCIALE)))
            .andExpect(jsonPath("$.[*].partitaIVA").value(hasItem(DEFAULT_PARTITA_IVA)));
    }
}
