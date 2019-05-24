package com.zendesk.search.index;

import com.zendesk.search.parse.DataParser;
import com.zendesk.search.parse.JsonDataParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 * Created by phanindra on 22/05/19.
 */
public class LuceneIndexWriterTest {

    static final String INDEX_PATH = "indexDir";
    static final String JSON_DATA_PATH = "./test-data";


    public void testWriteIndex() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL jsonData = classLoader.getResource(JSON_DATA_PATH);
        try {
            DataParser jsonDataParser = new JsonDataParser(jsonData.getPath());
            LuceneIndexWriter lw = new LuceneIndexWriter(INDEX_PATH, jsonDataParser);
            lw.createIndex();

            //Check the index has been created successfully
            Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
            IndexReader indexReader = DirectoryReader.open(indexDirectory);

            int numDocs = indexReader.numDocs();
            assertEquals(numDocs, 3);

            for (int i = 0; i < numDocs; i++) {
                Document document = indexReader.document(i);
                System.out.println("d=" + document);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testReadLucene() throws IOException, ParseException {

        testWriteIndex();

        Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Term t = new Term("name", "bob");
        Query query = new TermQuery(t);

        TopDocs topDocs = indexSearcher.search(query, 10);
        assertEquals(1, topDocs.totalHits.value);

    }

}
