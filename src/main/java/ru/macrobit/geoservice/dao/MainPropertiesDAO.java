package ru.macrobit.geoservice.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import ru.macrobit.geoservice.common.AbstractBaseService;
import ru.macrobit.geoservice.common.SecondMongotemplateProducer;
import ru.macrobit.geoservice.pojo.MainProperties;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MainPropertiesDAO extends AbstractBaseService<MainProperties> {

    public MainPropertiesDAO() {
        super(MainProperties.class);
    }

    @EJB
    private SecondMongotemplateProducer mongotemplateProducer;

    @Override
    protected MongoOperations getMt() {
        return mongotemplateProducer.getMt();
    }
}
