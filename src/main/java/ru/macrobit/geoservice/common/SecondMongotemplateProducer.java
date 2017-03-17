package ru.macrobit.geoservice.common;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 12.06.16.
 */
@Startup
@Singleton
public class SecondMongotemplateProducer {
    private static final Logger log = LoggerFactory.getLogger(SecondMongotemplateProducer.class);
    private MongoTemplate mt;
    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        List<MongoCredential> mongoCredentialList = new ArrayList<>();
        mongoCredentialList.add(MongoCredential
                .createCredential("taxi", "taxi", "Q4862513".toCharArray()));
        try {
            mongoClient = new MongoClient(new ServerAddress("db"), mongoCredentialList);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mt = new MongoTemplate(mongoClient, "taxi");
        log.warn("mt == null {}", mt == null);
    }

    public MongoTemplate getMt() {
        return mt;
    }

    public Mongo getMongo() {
        return this.mongoClient;
    }
}
