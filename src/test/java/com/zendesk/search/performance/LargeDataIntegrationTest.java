package com.zendesk.search.performance;

import com.zendesk.search.console.SearchResultDisplayer;
import com.zendesk.search.index.LuceneIndexWriter;
import com.zendesk.search.parse.JsonDataParser;
import com.zendesk.search.model.SearchQuery;
import com.zendesk.search.model.SearchResult;
import com.zendesk.search.service.ZendeskSearchServiceImpl;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.beryx.textio.TextIO;
import org.beryx.textio.mock.MockTextTerminal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by phanindra on 28/05/19.
 */
public class LargeDataIntegrationTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File users;

    @Before
    public void setUp11000Users() throws IOException {
        users = temporaryFolder.newFile("users.json");

        JSONObject obj = new JSONObject();
        obj.put("_id", 1);
        obj.put("url", "http://initech.zendesk.com/api/v2/users/75.json");
        obj.put("external_id", "0db0c1da-8901-4dc3-a469-fe4b500d0fca");
        obj.put("name", "Catalina Simpson");
        obj.put("alias", "Miss Rosanna");
        obj.put("created_at", "2016-06-07T09:18:00 -10:00");
        obj.put("active", false);
        obj.put("verified", true);
        obj.put("shared", true);
        obj.put("locale", "zh-CN");
        obj.put("timezone", "US Minor Outlying Islands");
        obj.put("last_login_at", "2012-10-15T12:36:41 -11:00");
        obj.put("email", "rosannasimpson@flotonic.com");
        obj.put("phone", "8615-883-099");
        obj.put("signature", "Don't Worry Be Happy!");
        obj.put("organization_id", 119);
        obj.put("role", "agent");
        obj.put("suspended", true);

        JSONArray list = new JSONArray();
        list.add("tag 1");
        list.add("tag 2");
        list.add("tag 3");
        list.add("tag 4");
        list.add("tag 5");


        obj.put("tags", list);

        try (FileWriter file = new FileWriter(users, true)) {
            file.write("[");
            file.write(obj.toJSONString());
            for (int i = 2; i <= 11000; i++) {
                file.write(",");
                obj.replace("_id", 1, i);
                file.write(obj.toJSONString());
            }
            file.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLargeData() throws IOException {
        JsonDataParser dataParser = new JsonDataParser(users.getParentFile().getAbsolutePath());
        File index = temporaryFolder.newFolder("test-index");
        LuceneIndexWriter writer = new LuceneIndexWriter(index.getAbsolutePath(), dataParser);
        writer.createIndex();

        Directory directory = FSDirectory.open(index.toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        ZendeskSearchServiceImpl zendeskSearchService = new ZendeskSearchServiceImpl(indexSearcher);

        SearchResult result = zendeskSearchService.searchForFullValueMatching(new SearchQuery("alias", "Miss Rosanna", "users"));
        assertEquals(11000, result.getTotalItems());
        assertEquals("Miss Rosanna", result.getItems().get(0).getDocument().get("alias"));

        // verify message about large no.of items matched

        MockTextTerminal terminal = new MockTextTerminal();
        TextIO textIO = new TextIO(terminal);
        SearchResultDisplayer resultDisplayer = new SearchResultDisplayer(textIO);
        resultDisplayer.printResult(result);

        assertTrue(terminal.getOutput().contains("Displaying 10 of 11000 items"));



    }

}
