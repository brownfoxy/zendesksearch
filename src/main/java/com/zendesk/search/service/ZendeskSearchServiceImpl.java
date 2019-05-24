package com.zendesk.search.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
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
    public SearchResult searchForFullValueMatching(SearchQuery searchQuery) throws IOException {
        SearchResult result = new SearchResult();
        Term t1 = new Term(searchQuery.getSearchTerm(), searchQuery.getSearchValue());
        String entity = searchQuery.getEntity();
        Query query1 = new TermQuery(t1);
        BooleanClause booleanClause1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
        Query query = new BooleanQuery.Builder().add(booleanClause1).build();
        if (entity != null) {
            Term t2 = new Term("fileName", entity.toLowerCase());
            Query query2 = new TermQuery(t2);
            BooleanClause booleanClause2 = new BooleanClause(query2, BooleanClause.Occur.MUST);
            query = new BooleanQuery.Builder().add(booleanClause1).add(booleanClause2).build();
        }
        TopDocs topDocs = indexSearcher.search(query, 10);
        TotalHits totalHits = topDocs.totalHits;

        result.setTotalItems(totalHits.value);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            result.addItem(doc);
        }
        return result;
    }

    @Override
    public List<String> findEntities() throws IOException {
        List<String> entities = new ArrayList<>();
        SortedSetDocValuesReaderState state =
                new DefaultSortedSetDocValuesReaderState(indexSearcher.getIndexReader(), "facet_fileName");
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(indexSearcher, new MatchAllDocsQuery(), 10, fc);
        Facets facets = new SortedSetDocValuesFacetCounts(state, fc);
        FacetResult result = facets.getTopChildren(10, "fileName");
        for (int i = 0; i < result.childCount; i++) {
            LabelAndValue lv = result.labelValues[i];
            entities.add(lv.label);
        }

        return entities;

    }
}
