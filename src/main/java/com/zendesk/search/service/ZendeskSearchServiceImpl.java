package com.zendesk.search.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phanindra on 23/05/19.
 */
public class ZendeskSearchServiceImpl implements ZendeskSearchService {

    IndexSearcher indexSearcher;

    public ZendeskSearchServiceImpl(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    @Override
    public SearchResult searchForFullValueMatching(String fieldName, String fieldValue) throws IOException {
        SearchResult result = new SearchResult();
        List<Document> documents = new ArrayList<>();
        Term t = new Term(fieldName, fieldValue);
        Query query = new TermQuery(t);
        TopDocs topDocs = indexSearcher.search(query, 10);
        TotalHits totalHits = topDocs.totalHits;

        result.setTotalItems(totalHits.value);

        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            result.addItem(doc);
        }
        return result;
    }
}
