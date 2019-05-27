package com.zendesk.search.service;

import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phanindra on 23/05/19.
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
