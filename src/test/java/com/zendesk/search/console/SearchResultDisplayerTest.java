package com.zendesk.search.console;

import com.zendesk.search.model.SearchResult;
import com.zendesk.search.model.SearchResultItem;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.beryx.textio.TextIO;
import org.beryx.textio.mock.MockTextTerminal;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by phanindra on 23/05/19.
 */
public class SearchResultDisplayerTest {

    @Test
    public void testPrintResult() {
        MockTextTerminal terminal = new MockTextTerminal();
        TextIO textIO = new TextIO(terminal);
        SearchResultDisplayer resultDisplayer = new SearchResultDisplayer(textIO);
        SearchResult result = new SearchResult();
        result.setTotalItems(1);
        Document document = new Document();
        document.add(new StringField("_id", "1", Field.Store.YES));
        result.addItem(new SearchResultItem(document));
        resultDisplayer.printResult(result);
        assertTrue(terminal.getOutput().contains("Displaying 1 of 1 items"));
    }


    @Test
    public void testPrintResultEmpty() {
        MockTextTerminal terminal = new MockTextTerminal();
        TextIO textIO = new TextIO(terminal);
        SearchResultDisplayer resultDisplayer = new SearchResultDisplayer(textIO);
        SearchResult result = new SearchResult();
        result.setTotalItems(0);
        resultDisplayer.printResult(result);
        assertTrue(terminal.getOutput().contains("No data found!"));
    }
}
