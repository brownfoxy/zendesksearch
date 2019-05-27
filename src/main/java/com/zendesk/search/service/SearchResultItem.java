package com.zendesk.search.service;

import org.apache.lucene.document.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phanindra on 27/05/19.
 */
public class SearchResultItem {
    private Document document;
    private Map<String, SearchResult> relatedEntities = new HashMap();

    public SearchResultItem(Document document) {
        this.document = document;
    }

    public SearchResultItem() {

    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Map<String, SearchResult> getRelatedEntities() {
        return relatedEntities;
    }

    public void setRelatedEntities(Map<String, SearchResult> relatedEntities) {
        this.relatedEntities = relatedEntities;
    }


}
