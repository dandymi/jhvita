package it.maraschi.jhvita.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.maraschi.jhvita.domain.Cliente;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Cliente} entity.
 */
public interface ClienteSearchRepository extends ElasticsearchRepository<Cliente, Long>, ClienteSearchRepositoryInternal {}

interface ClienteSearchRepositoryInternal {
    Page<Cliente> search(String query, Pageable pageable);
}

class ClienteSearchRepositoryInternalImpl implements ClienteSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ClienteSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Cliente> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Cliente> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Cliente.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
