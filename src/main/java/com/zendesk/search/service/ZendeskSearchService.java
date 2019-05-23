package com.zendesk.search.service;

import java.io.IOException;

/**
 * Created by phanindra on 23/05/19.
 */
public interface ZendeskSearchService {
    SearchResult searchForFullValueMatching(String fieldName, String fieldValue) throws IOException;
}
