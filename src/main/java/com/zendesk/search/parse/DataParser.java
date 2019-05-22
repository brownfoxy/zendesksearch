package com.zendesk.search.parse;

import org.apache.lucene.document.Document;

import java.io.IOException;

/**
 * Created by phanindra on 22/05/19.
 */
public interface DataParser {
    Iterable<Document> parse() throws IOException;
}
