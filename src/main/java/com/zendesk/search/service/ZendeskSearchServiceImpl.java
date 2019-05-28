package com.zendesk.search.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phanindra on 23/05/19.
 */
public class ZendeskSearchServiceImpl implements ZendeskSearchService {

    IndexSearcher indexSearcher;

    private SettingsService settingsService = new SettingsService();

    public ZendeskSearchServiceImpl(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    @Override
    public SearchResult searchForFullValueMatching(SearchQuery searchQuery) throws IOException {
        SearchResult result = new SearchResult();

        Term t1 = new Term(searchQuery.getSearchTerm(), searchQuery.getSearchValue());
        String entity = searchQuery.getEntity();
        String entityNameLowercased = entity.toLowerCase();
        Query query1 = new TermQuery(t1);
        BooleanClause booleanClause1 = new BooleanClause(query1, BooleanClause.Occur.MUST);

        Term t2 = new Term("fileName", entityNameLowercased);
        Query query2 = new TermQuery(t2);
        BooleanClause booleanClause2 = new BooleanClause(query2, BooleanClause.Occur.MUST);

        Query query = new BooleanQuery.Builder().add(booleanClause1).add(booleanClause2).build();

        // selected entity

        TopScoreDocCollector collector = TopScoreDocCollector.create(10, Integer.MAX_VALUE);
        indexSearcher.search(query, collector);
        TopDocs topDocs = collector.topDocs();
        int totalHits = collector.getTotalHits();

        result.setTotalItems(totalHits);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            SearchResultItem searchResultItem = new SearchResultItem();
            Document doc = indexSearcher.doc(scoreDoc.doc);
            searchResultItem.setDocument(doc);
            if (settingsService.getRelatedEntitySettings()!=null) {
                Map<String, SearchResult> searchRelatedEntities = findRelatedResult(doc, entityNameLowercased);
                searchResultItem.setRelatedEntities(searchRelatedEntities);
            }
            result.addItem(searchResultItem);
        }
        return result;
    }

    @Override
     public HashMap<String, SearchResult> findRelatedResult(Document doc, String entityNameLowercase) throws IOException {
        HashMap<String, SearchResult> relatedResult = new HashMap<>();


        Map<String, Map<String, String>> relatedEntitySettings = settingsService.getRelatedEntitySettings();
        Map<String, String> settings = relatedEntitySettings.get(entityNameLowercase);
        for(String e: settings.keySet()) {
            // search result
            SearchResult result = new SearchResult();
            String settingsSemicolonDelimited = settings.get(e);
            String[] sourceTarget = settingsSemicolonDelimited.split(";");
            String source = sourceTarget[0];
            String[] sourceFields = source.split(",");
            String target = sourceTarget[1];
            String[] targetFields = target.split(",");

            List<String> sourceValues = new ArrayList<>(2);
            for (String s: sourceFields) {
                sourceValues.add(doc.get(s));
            }

            // e.g fileName:ticket AND organisation_id=45
            // e.g fileName:ticket AND (submitter_id=45 OR assignee_id=45)
            // e.g fileName:organization AND (_id=Users(organization_id))
            Term fileName = new Term("fileName", e);
            TermQuery entityQuery = new TermQuery(fileName);

            BooleanClause entityClause = new BooleanClause(entityQuery, BooleanClause.Occur.MUST);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(entityClause);

            BooleanQuery.Builder fieldQueryBuilder = new BooleanQuery.Builder();

            for (String targetField : targetFields) {
                for (String sourceValue: sourceValues) {
                    Term relatedField = new Term(targetField, sourceValue);
                    TermQuery relatedFieldQuery = new TermQuery(relatedField);
                    BooleanClause fieldClause = new BooleanClause(relatedFieldQuery, BooleanClause.Occur.SHOULD);
                    fieldQueryBuilder.add(fieldClause);
                }
            }
            builder.add(new BooleanClause(fieldQueryBuilder.build(), BooleanClause.Occur.MUST));
            TopDocs topDocs = indexSearcher.search(builder.build(), 10);
            TotalHits totalHits = topDocs.totalHits;

            result.setTotalItems(totalHits.value);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                SearchResultItem searchResultItem = new SearchResultItem();
                searchResultItem.setDocument(document);
                result.addItem(searchResultItem);
            }
            relatedResult.put(e, result);
        }
        return  relatedResult;
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

    @Override
    public List<String> findFieldsInsideEntity(String entity) throws IOException {
        List<String> fields = new ArrayList<>();
            Term t = new Term("fileName", entity.toLowerCase());
            Query query = new TermQuery(t);
        TopDocs topDocs = indexSearcher.search(query, 1);
        Document doc = indexSearcher.doc(topDocs.scoreDocs[0].doc);
        for (IndexableField field : doc.getFields()) {
            String name = field.name();
            if (!name.equals("fileName")) {
                fields.add(name);
            }
        }
        return fields;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

}
