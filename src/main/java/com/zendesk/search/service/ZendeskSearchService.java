package com.zendesk.search.service;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by phanindra on 23/05/19.
 */
public interface ZendeskSearchService {

    /**
     * Finds items whose field value matches exactly
     * @param query
     * @return
     * @throws IOException
     */
    SearchResult searchForFullValueMatching(SearchQuery query) throws IOException;

    /**
     * Gets different entities in the provided data
     * @return
     */
    List<String> findEntities() throws IOException;

    /**
     * Given a document, find other related documents whose type is not <code>entityNameLowercase</code>
     * @param doc
     * @param entityNameLowercase
     * @return
     * @throws IOException
     */
    HashMap<String, SearchResult> findRelatedResult(Document doc, String entityNameLowercase) throws IOException;

    /**
     * Returns list of fields to search inside an entity
     * @param entity
     * @return
     */
    List<String> findFieldsInsideEntity(String entity) throws IOException;
}
