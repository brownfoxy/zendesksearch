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
import org.apache.log4j.Logger;

/**
 * Created by phanindra on 22/05/19.
 */
public class LuceneIndexWriter {

    private final static Logger logger = Logger.getLogger(LuceneIndexWriter.class);


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
        logger.info("opening the index writer at the path" + indexPath);
        try {
            Directory dir = FSDirectory.open(new File(indexPath).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            //Always overwrite the directory
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            return new IndexWriter(dir, iwc);

        } catch (Exception e) {
            logger.error("opening the index writer at the path: " + indexPath, e);
            return null;
        }
    }

    /**
     * Write the document to the index and close it
     */
    private void finish() {
        logger.info("closing the index writer");
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            logger.error("We had a problem closing the index: " + ex.getMessage());
        }
    }


}