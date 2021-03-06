package com.zendesk.search.index;

import com.zendesk.search.parse.DataParser;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by phanindra on 22/05/19.
 */
public class LuceneIndexWriter {

    private final static Logger logger = Logger.getLogger(LuceneIndexWriter.class);


    private String indexPath = "";

    private DataParser dataParser;

    private IndexWriter indexWriter = null;

    public LuceneIndexWriter(String indexPath, DataParser dataParser) {
        this.indexPath = indexPath;
        this.dataParser = dataParser;
    }

    public void createIndex() throws IOException {

        // open
        indexWriter = openIndex();

        // Facet on fileName field to get entities available
        FacetsConfig config = new FacetsConfig();
        config.setIndexFieldName("fileName", "facet_fileName");

        // add docs
        Iterable<Document> documentIterable = dataParser.parse();
        for (Document doc : documentIterable) {
            try {
                this.indexWriter.addDocument(config.build(doc));
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " + ex.getMessage());
            }
        }

        //close

        finish();
    }


    private IndexWriter openIndex() {
        Path path = new File(indexPath).toPath();
        logger.info("opening the index writer at the path: " + path.toFile().getAbsolutePath() );
        try {
            Directory dir = FSDirectory.open(path);
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