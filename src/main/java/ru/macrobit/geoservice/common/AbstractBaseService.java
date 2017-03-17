package ru.macrobit.geoservice.common;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * Created by [david] on 08.10.16.
 */
public abstract class AbstractBaseService<T> {
    protected Class<T> type;
    private FindAndModifyOptions option = new FindAndModifyOptions().returnNew(true);


    public AbstractBaseService(Class<T> type) {
        this.type = type;
    }

    protected abstract MongoOperations getMt();

    /**
     * Count documents in collection by user scope
     *
     * @param user
     * @return document count
     */
    public Long count(User user) {
        if (user == null) {
            return getMt().count(new Query(), this.type);
        } else {
            return getMt().count(Query.query(getUserScopeCriteria(user)), this.type);
        }
    }

    /**
     * @param query
     * @param user
     * @return document count by query
     */
    public Long count(Query query, User user) {
        if (user == null) {
            return getMt().count(query, this.type);
        } else {
            return getMt().count(query.addCriteria(getUserScopeCriteria(user)),
                    this.type);
        }
    }

    /**
     * Find document by Id
     *
     * @param id
     * @param user
     * @return T extends Entity
     */
    public T findById(String id, User user) {
        if (user == null) {
            return getMt().findById(id, this.type);
        } else {
            return getMt().findOne(Query.query(Criteria.where("id").is(id))
                    .addCriteria(getUserScopeCriteria(user)), this.type);
        }
    }

    /**
     * Find by user scope
     *
     * @param user
     * @return List of entities
     */
    public List<T> find(User user) {
        if (user == null) {
            return getMt().findAll(this.type);
        } else {
            return getMt().find(Query.query(getUserScopeCriteria(user)), this.type);
        }
    }

    /**
     * Find by user scope with skip and limit
     *
     * @param user
     * @param skip
     * @param limit
     * @param sort
     * @return
     */
    public List<T> find(User user, Integer skip, Integer limit, Sort sort) {
        Query query = null;
        if (user == null) {
            query = new Query();
        } else {
            query = Query.query(getUserScopeCriteria(user));
        }
        if (skip != null && limit != null && skip >= 0 && limit > 0) {
            query = query.skip(skip).limit(limit);
        }
        if (sort != null) {
            query = query.with(sort);
        }

        return getMt().find(query, this.type);
    }

    /**
     * Find by user scope and query with skip and limit
     *
     * @param query
     * @param user
     * @param skip
     * @param limit
     * @param sort
     * @return
     */
    public List<T> find(Query query, User user, Integer skip, Integer limit,
                        Sort sort) {
        if (user != null) {
            query = query.addCriteria(getUserScopeCriteria(user));
        }
        if (skip != null && limit != null && skip >= 0 && limit > 0) {
            query = query.skip(skip).limit(limit);
        }
        if (sort != null) {
            query = query.with(sort);
        }

        return getMt().find(query, this.type);
    }

    /**
     * @param query
     * @param user
     * @return
     */
    public List<T> find(Query query, User user) {
        if (user == null) {
            return getMt().find(query, this.type);
        } else {
            return getMt().find(query.addCriteria(getUserScopeCriteria(user)),
                    this.type);
        }
    }

    /**
     * Delete multi documents by query
     *
     * @param query
     */
    public void remove(Query query) {
        getMt().remove(query, this.type);
    }

    /**
     * Batch insert
     *
     * @param entities
     */
    public void insert(List<T> entities) {
        getMt().insert(entities, this.type);
    }

    /**
     * Insert single entity
     *
     * @param entity
     */
    public void insert(T entity) {
        getMt().insert(entity);
    }

    /**
     * Replace entity
     *
     * @param entity
     */
    public void save(T entity) {
        getMt().save(entity);
    }

    /**
     * Delete entity
     *
     * @param entity
     */
    public void remove(T entity) {
        getMt().remove(entity);
    }

    /**
     * Multi update by query
     *
     * @param query
     * @param update
     */
    public void updateMulti(Query query, Update update) {
        getMt().updateMulti(query, update, this.type);
    }

    /**
     * Atomic update document or insert if not exists
     *
     * @param id
     * @param update
     */
    public void upsert(String id, Update update) {
        getMt().upsert(new Query(Criteria.where("id").is(id)), update, this.type);
    }

    /**
     * User scope criteria
     *
     * @param user
     * @return criteria
     */
    protected Criteria getUserScopeCriteria(User user) {
        return new Criteria();
    }

    /**
     * Atomic update document and return just updated
     *
     * @param id
     * @param update
     * @return
     */
    public T findAndUpdate(String id, Update update) {
        return getMt().findAndModify(new Query(Criteria.where("id").is(id)), update, option, this.type);
    }
}
