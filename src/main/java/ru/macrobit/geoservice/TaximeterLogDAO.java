package ru.macrobit.geoservice;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.macrobit.geoservice.common.AbstractBaseService;
import ru.macrobit.geoservice.pojo.LogEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by [david] on 08.10.16.
 */
@ApplicationScoped
public class TaximeterLogDAO extends AbstractBaseService<LogEntry> {
    private static final Logger log = LoggerFactory.getLogger(TaximeterLogDAO.class);
    @Inject
    protected MongoOperations mt;
    @Inject
    private Mongo mongo;

    public List<LogEntry> getLogs(String orderId, Integer limit, boolean build) {
        Criteria criteria = new Criteria("orderId").is(orderId);
        if (!build) {
            criteria.and("build").ne(true);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "timestamp"));
        if (limit != null && limit > 0) {
            query.limit(limit);
        }
        return find(query, null);
    }

    public void bulkInsert(List<LogEntry> logs, String orderId) {
        if (logs.isEmpty())
            return;
        try {
            BulkWriteOperation bulkWriteOperation = mongo.getDB("taxi").getCollection("taximeterlog").initializeUnorderedBulkOperation();
            logs.stream().forEach(log -> {
                DBObject dbObject = new BasicDBObject();
                dbObject.put("lat", log.getLat());
                dbObject.put("lon", log.getLon());
                dbObject.put("timestamp", log.getTimestamp());
                dbObject.put("src", log.getSrc());
                dbObject.put("error", log.getError());
                dbObject.put("orderId", orderId);
                bulkWriteOperation.insert(dbObject);
            });
            bulkWriteOperation.execute();
        } catch (Exception e) {
            log.error(orderId);
        }

    }

    public TaximeterLogDAO() {
        super(LogEntry.class);
    }

    @Override
    protected MongoOperations getMt() {
        return mt;
    }
}
