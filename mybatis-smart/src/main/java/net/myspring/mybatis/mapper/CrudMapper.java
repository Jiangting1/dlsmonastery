package net.myspring.mybatis.mapper;

import net.myspring.mybatis.form.BaseForm;
import net.myspring.mybatis.provider.CrudProvider;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author liuj
 */
public interface CrudMapper<T, ID extends Serializable> extends BaseMapper<T, ID> {

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity
     * @return the saved result
     */
    @InsertProvider(type=CrudProvider.class, method = "save")
    @Options(useGeneratedKeys = true)
    <S extends T> int save(S entity);


    @InsertProvider(type=CrudProvider.class, method = "save")
    @Options(useGeneratedKeys = true)
    <S extends BaseForm<T>> int saveForm(S entity);

    /**
     * Saves all given entities.
     *
     * @param entities
     * @return the saved result
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    @InsertProvider(type=CrudProvider.class, method = "batchSave")
    @Options(useGeneratedKeys = true)
    <S extends T> int batchSave(Iterable<S> entities);

    @InsertProvider(type=CrudProvider.class, method = "batchSave")
    @Options(useGeneratedKeys = true)
    <S extends BaseForm<T>> int batchSaveForm(Iterable<S> entities);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    @SelectProvider(type=CrudProvider.class, method = "findOne")
    T findOne(ID id);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    @SelectProvider(type=CrudProvider.class, method = "exists")
    Boolean exists(ID id);

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    @SelectProvider(type=CrudProvider.class, method = "findAll")
    List<T> findAll();

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids
     * @return
     */
    @SelectProvider(type=CrudProvider.class, method = "findByIds")
    List<T> findByIds(Iterable<ID> ids);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities
     */
    @SelectProvider(type=CrudProvider.class, method = "count")
    Long count();

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    @DeleteProvider(type=CrudProvider.class, method = "deleteById")
    int deleteById(ID id);

    /**
     * Delete all entities  with the given IDs.
     *
     * @param ids
     * @return
     */
    @DeleteProvider(type=CrudProvider.class, method = "deleteByIds")
    int deleteByIds(Iterable<ID> ids);


    /**
     * Deletes a given entity.
     *
     * @param entity
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    @DeleteProvider(type=CrudProvider.class, method = "deleteByEntity")
    int deleteByEntity(T entity);


    /**
     * Deletes the given entities.
     *
     * @param entities
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    @DeleteProvider(type=CrudProvider.class, method = "batchDelete")
    int batchDelete(Iterable<? extends T> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    @DeleteProvider(type=CrudProvider.class, method = "deleteAll")
    int deleteAll();

    /**
     * Updates a given entity
     *
     * @param entity
     */
    @UpdateProvider(type=CrudProvider.class, method = "update")
    <S extends T> int update(S entity);

    @UpdateProvider(type=CrudProvider.class, method = "update")
    <S extends BaseForm<T>> int updateForm(S entity);

}
