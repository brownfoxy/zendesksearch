package com.zendesk.search.index;

import com.zendesk.search.parse.DataParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by phanindra on 22/05/19.
 */
public class LuceneIndexWriter {


    private String indexPath = "";

    private DataParser dataParser;

    private IndexWriter indexWriter = null;

    LuceneIndexWriter(String indexPath, DataParser dataParser) {
        this.indexPath = indexPath;
        this.dataParser = dataParser;
    }

    void createIndex() {

        // open
        indexWriter = openIndex();

        // add docs
        Iterable<Document> documentIterable = dataParser.parse();
        for (Document doc : documentIterable) {
            try {
                this.indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " + ex.getMessage());
            }
        }

        //close

        finish();
    }


    private IndexWriter openIndex() {
        try {
            Directory dir = FSDirectory.open(new File(indexPath).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            //Always overwrite the directory
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            return new IndexWriter(dir, iwc);

        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
            return null;
        }
    }

    /**
     * Write the document to the index and close it
     */
    private void finish() {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }


}