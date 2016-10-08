package ru.macrobit.geoservice.common;

import com.mongodb.*;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by [david] on 08.10.16.
 */
public class MongoTemplateProducer extends AbstractMongoConfiguration {

    @Produces
    @ApplicationScoped
    public MongoOperations createMongoOperations() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }

    @Produces
    @ApplicationScoped
    public GridFsOperations createGridFsOperations() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Override
    protected String getDatabaseName() {
        return "taxi";
    }


    @Produces
    @ApplicationScoped
    @Override
    public Mongo mongo() throws Exception {
        List<MongoCredential> mongoCredentialList = new ArrayList<>();
        mongoCredentialList.add(MongoCredential
                .createCredential("taxi", "taxi", "Q4862513".toCharArray()));
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(300);
        MongoClientOptions options = builder.build();
        return new MongoClient(new ServerAddress("track"), mongoCredentialList, options);
    }
}
