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

    public Long count(User user) {
        if (user == null) {
            return getMt().count(new Query(), this.type);
        } else {
            return getMt().count(Query.query(getUserScopeCriteria(user)), this.type);
        }
    }

    public Long count(Query query, User user) {
        if (user == null) {
            return getMt().count(query, this.type);
        } else {
            return getMt().count(query.addCriteria(getUserScopeCriteria(user)),
                    this.type);
        }
    }

    public T findById(String id, User user) {
        if (user == null) {
            return getMt().findById(id, this.type);
        } else {
            return getMt().findOne(Query.query(Criteria.where("id").is(id))
                    .addCriteria(getUserScopeCriteria(user)), this.type);
        }
    }

    public List<T> find(User user) {
        if (user == null) {
            return getMt().findAll(this.type);
        } else {
            return getMt().find(Query.query(getUserScopeCriteria(user)), this.type);
        }
    }

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

    public List<T> find(Query query, User user) {
        if (user == null) {
            return getMt().find(query, this.type);
        } else {
            return getMt().find(query.addCriteria(getUserScopeCriteria(user)),
                    this.type);
        }
    }

    public void remove(Query query) {
        getMt().remove(query, this.type);
    }

    public void insert(List<T> entities) {
        getMt().insert(entities, this.type);
    }

    public void insert(T entity) {
        getMt().insert(entity);
    }

    public void save(T entity) {
        getMt().save(entity);
    }

    public void remove(T entity) {
        getMt().remove(entity);
    }

    public void updateMulti(Query query, Update update) {
        getMt().updateMulti(query, update, this.type);
    }

    public void upsert(String id, Update update) {
        getMt().upsert(new Query(Criteria.where("id").is(id)), update, this.type);
    }

    protected Criteria getUserScopeCriteria(User user) {
        return new Criteria();
    }

    public T findAndUpdate(String id, Update update) {
        return getMt().findAndModify(new Query(Criteria.where("id").is(id)), update, option, this.type);
    }
}
