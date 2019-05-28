package com.zendesk.search.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phanindra on 23/05/19.
 */

/**
 * Represents a response to one search query
  */
public class SearchResult {

    private List<SearchResultItem> items = new ArrayList<>();
    private long totalItems;

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    public void addItem(SearchResultItem item) {
        items.add(item);
    }





}
