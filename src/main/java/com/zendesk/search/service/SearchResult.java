package com.zendesk.search.service;

import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phanindra on 23/05/19.
 */
public class SearchResult {

    private List<Document> items = new ArrayList<>();
    private long totalItems;

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public List<Document> getItems() {
        return items;
    }

    public void addItem(Document item) {
        items.add(item);
    }


}
