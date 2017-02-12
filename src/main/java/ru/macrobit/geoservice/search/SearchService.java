package ru.macrobit.geoservice.search;

import ru.macrobit.geoservice.search.pojo.SearchResult;

/**
 * Created by david on 12.02.17.
 */
public interface SearchService {

    SearchResult search(String pattern, String city, String disp, String filterCity) throws Exception;

}

