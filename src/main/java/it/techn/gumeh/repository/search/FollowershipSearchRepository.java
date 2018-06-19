package it.techn.gumeh.repository.search;

import it.techn.gumeh.domain.Followership;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Followership entity.
 */
public interface FollowershipSearchRepository extends ElasticsearchRepository<Followership, Long> {
}
