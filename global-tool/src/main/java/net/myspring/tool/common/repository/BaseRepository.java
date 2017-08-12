package net.myspring.tool.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

   Map<ID,T> findMap(Collection<ID> ids);

   T logicDelete(ID id);

   List<T> loginDeleteByIdList(List<ID> idList);
}
