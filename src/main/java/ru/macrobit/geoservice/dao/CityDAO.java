package ru.macrobit.geoservice.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import ru.macrobit.geoservice.common.AbstractBaseService;
import ru.macrobit.geoservice.common.SecondMongotemplateProducer;
import ru.macrobit.geoservice.search.pojo.City;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by david on 12.02.17.
 */
@Singleton
@Startup
public class CityDAO extends AbstractBaseService<City> {

    @EJB
    private SecondMongotemplateProducer mongotemplateProducer;

    private Map<String, City> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        this.cache = find(null).stream().collect(Collectors.toMap(City::getId, Function.identity()));
    }

    public Map<String, City> getCache() {
        return this.cache;
    }

    public CityDAO() {
        super(City.class);
    }

    @Override
    protected MongoOperations getMt() {
        return mongotemplateProducer.getMt();
    }
}
