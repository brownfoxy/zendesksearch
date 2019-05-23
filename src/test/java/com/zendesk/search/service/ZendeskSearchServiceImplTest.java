package com.zendesk.search.service;

import com.zendesk.search.parse.DataParser;
import com.zendesk.search.parse.JsonDataParser;
import com.zendesk.search.service.SearchResult;
import com.zendesk.search.service.ZendeskSearchService;
import com.zendesk.search.service.ZendeskSearchServiceImpl;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by phanindra on 23/05/19.
 */
public class ZendeskSearchServiceImplTest {
    private Directory inMemory;

    @Before
    public void createInMemoryIndex() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        try {
            inMemory = new ByteBuffersDirectory();
            IndexWriter indexWriter = new IndexWriter(inMemory, indexWriterConfig);
            Document doc1 = new Document();
            doc1.add(new StringField("_id", "1", Field.Store.YES));
            doc1.add(new StringField("name", "foo", Field.Store.YES));
            indexWriter.addDocument(doc1);

            Document doc2 = new Document();
            doc1.add(new StringField("_id", "2", Field.Store.YES));
            doc1.add(new StringField("name", "foo bar", Field.Store.YES));
            indexWriter.addDocument(doc2);
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
            SearchResult result = zendeskSearchService.searchForFullValueMatching("name", "foo");
            assertEquals(1, result.getTotalItems());
            assertEquals("foo", result.getItems().get(0).get("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
