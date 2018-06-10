package it.techn.gumeh.repository.search;

import it.techn.gumeh.domain.Resurce;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Resurce entity.
 */
public interface ResurceSearchRepository extends ElasticsearchRepository<Resurce, Long> {
}
