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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by phanindra on 23/05/19.
 */
public class ZendeskSearchServiceImplTest {
    private Directory inMemory;

    @Before
    public void createInMemoryIndex() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        FacetsConfig config = new FacetsConfig();
        config.setIndexFieldName("fileName", "facet_fileName");

        try {
            inMemory = new ByteBuffersDirectory();
            IndexWriter indexWriter = new IndexWriter(inMemory, indexWriterConfig);
            Document doc1 = new Document();
            doc1.add(new StringField("_id", "1", Field.Store.YES));
            doc1.add(new StringField("name", "foo", Field.Store.YES));
            doc1.add(new SortedSetDocValuesFacetField("fileName", "users"));
            indexWriter.addDocument(config.build(doc1));

            Document doc2 = new Document();
            doc2.add(new StringField("_id", "2", Field.Store.YES));
            doc2.add(new StringField("name", "foo bar", Field.Store.YES));
            doc2.add(new SortedSetDocValuesFacetField("fileName", "tickets"));
            indexWriter.addDocument(config.build(doc2));
            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExactMatchQuery() {
        IndexReader indexReader = null;
        try {
            indexReader = DirectoryReader.open(inMemory);
            final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            ZendeskSearchService zendeskSearchService = new ZendeskSearchServiceImpl(indexSearcher);
            SearchQuery searchQuery = new SearchQuery("name", "foo", null);
            SearchResult result = zendeskSearchService.searchForFullValueMatching(searchQuery);
            assertEquals(1, result.getTotalItems());
            assertEquals("foo", result.getItems().get(0).get("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFacetQuery() throws IOException {
        IndexReader indexReader = null;
        try {
            indexReader = DirectoryReader.open(inMemory);
            final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            ZendeskSearchService zendeskSearchService = new ZendeskSearchServiceImpl(indexSearcher);
            List<String> entities = zendeskSearchService.findEntities();
            assertTrue(entities.contains("users"));
            assertTrue(entities.contains("tickets"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
