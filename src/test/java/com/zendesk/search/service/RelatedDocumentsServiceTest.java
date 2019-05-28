package com.zendesk.search.service;

import com.zendesk.search.model.SearchResult;
import com.zendesk.search.model.SearchResultItem;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.FacetsConfig;
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
 * Created by phanindra on 27/05/19.
 */
public class RelatedDocumentsServiceTest {
    private Directory inMemory;
    private ZendeskSearchService searchService;

    @Before
    public void createInMemoryIndex() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        FacetsConfig config = new FacetsConfig();
        config.setIndexFieldName("fileName", "facet_fileName");

        try {
            inMemory = new ByteBuffersDirectory();
            IndexWriter indexWriter = new IndexWriter(inMemory, indexWriterConfig);

            Document organization = new Document();
            organization.add(new StringField("fileName", "organizations", Field.Store.NO));
            organization.add(new StringField("_id", "o1", Field.Store.YES));
            organization.add(new StringField("name", "Enthaze", Field.Store.YES));

            Document user1 = new Document();
            user1.add(new StringField("fileName", "users", Field.Store.NO));
            user1.add(new StringField("_id", "u1", Field.Store.YES));
            user1.add(new StringField("name", "James", Field.Store.YES));
            user1.add(new StringField("organization_id", "o1", Field.Store.YES));

            Document user2 = new Document();
            user2.add(new StringField("fileName", "users", Field.Store.NO));
            user2.add(new StringField("_id", "u2", Field.Store.YES));
            user2.add(new StringField("name", "Francisca", Field.Store.YES));
            user2.add(new StringField("organization_id", "o1", Field.Store.YES));

            Document ticket = new Document();
            ticket.add(new StringField("fileName", "tickets", Field.Store.NO));
            ticket.add(new StringField("_id", "t1", Field.Store.YES));
            ticket.add(new StringField("subject", "A Catastrophe in Korea (North)", Field.Store.YES));
            ticket.add(new StringField("organization_id", "o1", Field.Store.YES));
            ticket.add(new StringField("submitter_id", "u1", Field.Store.YES));
            ticket.add(new StringField("assignee_id", "u2", Field.Store.YES));

            indexWriter.addDocument(config.build(organization));
            indexWriter.addDocument(config.build(user1));
            indexWriter.addDocument(config.build(user2));
            indexWriter.addDocument(config.build(ticket));


            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // before you can search
        createSearcher();
    }

    @Test
    public void testRelatedDocuments() throws IOException {
        Document organization = new Document();
        organization.add(new StringField("_id", "o1", Field.Store.NO));
        HashMap<String, SearchResult> relatedResult = searchService.findRelatedResult(organization, "organizations");
        assertTrue(relatedResult.size()==2);
        assertTrue(relatedResult.containsKey("tickets"));
        assertTrue(relatedResult.containsKey("users"));

        SearchResult users = relatedResult.get("users");
        assertEquals(2, users.getTotalItems());
        List<SearchResultItem> usersItems = users.getItems();
        List<String> userIds = usersItems.stream().map(sri -> sri.getDocument()).map(document -> document.get("_id")).collect(Collectors.toList());
        assertTrue(userIds.contains("u1"));
        assertTrue(userIds.contains("u2"));


        SearchResult tickets = relatedResult.get("tickets");
        assertEquals(1, tickets.getTotalItems());
        Document ticketDoc = tickets.getItems().get(0).getDocument();
        assertTrue(ticketDoc.get("_id").equals("t1"));
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
