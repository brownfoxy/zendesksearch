package com.zendesk.search.model;

/**
 * Created by phanindra on 24/05/19.
 *
 */
// Represents a query issued to backend system.
public class SearchQuery {
    private String searchTerm;
    private String searchValue;
    private String entity;

    public SearchQuery(String searchTerm, String searchValue, String entity) {
        this.searchTerm = searchTerm;
        this.searchValue = searchValue;
        this.entity = entity;
    }


    public String getSearchTerm() {
        return searchTerm;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public String getEntity() {
        return entity;
    }
}
