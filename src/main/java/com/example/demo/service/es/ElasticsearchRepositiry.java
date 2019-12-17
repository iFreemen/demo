package com.example.demo.service.es;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @ClassName ElasticsearchRepositiry
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/17 11:00
 * @Version V1.0
 **/
@NoRepositoryBean
public interface ElasticsearchRepositiry<T,ID extends Serializable> extends ElasticsearchCrudRepository<T,ID> {
    <S extends T> S index(S entity);

    Iterable<T> search(QueryBuilder queryBuilder);

    Page<T> search(QueryBuilder queryBuilder, Pageable pageable);

    Page<T> search(SearchQuery searchQuery);

    Page<T> searchSimilar(T entity, String[] fields, Pageable pageable);

    void refresh();

    Class<T> getEntityClass();
}
