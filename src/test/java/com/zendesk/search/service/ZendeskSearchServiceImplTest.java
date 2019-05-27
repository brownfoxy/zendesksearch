package com.zendesk.search.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by phanindra on 23/05/19.
 */
public class ZendeskSearchServiceImplTest {
    private Directory inMemory;
    private ZendeskSearchServiceImpl searchService;

    @Before
    public void createInMemoryIndex() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        FacetsConfig config = new FacetsConfig();
        config.setIndexFieldName("fileName", "facet_fileName");

        try {
            inMemory = new ByteBuffersDirectory();
            IndexWriter indexWriter = new IndexWriter(inMemory, indexWriterConfig);
            Document doc1 = new Document();
            doc1.add(new StringField("fileName", "test", Field.Store.YES));
            doc1.add(new StringField("_id", "1", Field.Store.YES));
            doc1.add(new StringField("name", "foo", Field.Store.YES));
            doc1.add(new SortedSetDocValuesFacetField("fileName", "users"));
            indexWriter.addDocument(config.build(doc1));

            Document doc2 = new Document();
            doc1.add(new StringField("fileName", "test", Field.Store.YES));
            doc2.add(new StringField("_id", "2", Field.Store.YES));
            doc2.add(new StringField("name", "foo bar", Field.Store.YES));
            doc2.add(new SortedSetDocValuesFacetField("fileName", "tickets"));
            indexWriter.addDocument(config.build(doc2));

            Document emptyValue = new Document();
            emptyValue.add(new StringField("fileName", "test", Field.Store.YES));
            emptyValue.add(new StringField("_id", "456", Field.Store.YES));
            emptyValue.add(new StringField("alias", "", Field.Store.YES));
            indexWriter.addDocument(config.build(emptyValue));

            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // before you can search
        createSearcher();
    }

    @Test
    public void testExactMatchQuery() throws IOException {
            searchService.getSettingsService().setRelatedEntitySettings(null);
            SearchQuery searchQuery = new SearchQuery("name", "foo", "test");
            SearchResult result = searchService.searchForFullValueMatching(searchQuery);
            assertEquals(1, result.getTotalItems());
            assertEquals("foo", result.getItems().get(0).getDocument().get("name"));
    }

    @Test
    public void testEmptyValue() throws IOException {
            searchService.getSettingsService().setRelatedEntitySettings(null);
            SearchQuery searchQuery = new SearchQuery("alias", "", "test");
            SearchResult result = searchService.searchForFullValueMatching(searchQuery);
            assertEquals(1, result.getTotalItems());
            assertEquals("456", result.getItems().get(0).getDocument().get("_id"));
    }

    @Test
    public void testFacetQuery() throws IOException {
            List<String> entities = searchService.findEntities();
            assertTrue(entities.contains("users"));
            assertTrue(entities.contains("tickets"));
    }


    public void createSearcher() {
        IndexReader indexReader = null;
        try {
            indexReader = DirectoryReader.open(inMemory);
            final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            searchService = new ZendeskSearchServiceImpl(indexSearcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
