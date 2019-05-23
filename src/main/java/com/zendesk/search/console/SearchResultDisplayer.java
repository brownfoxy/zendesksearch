package com.zendesk.search.console;

import com.zendesk.search.service.SearchResult;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.List;

/**
 * Created by phanindra on 23/05/19.
 */
public class SearchResultDisplayer {

    private TextTerminal terminal;

    SearchResultDisplayer(TextIO textIO) {
        this.terminal = textIO.getTextTerminal();
    }

    void printResult(SearchResult searchResult) {
        if (searchResult.getTotalItems() == 0L) {
            terminal.println("No data found!");
            return;
        }
        List<Document> items = searchResult.getItems();
        terminal.println(String.format("Displaying %d of %d items", items.size(), searchResult.getTotalItems()));
        terminal.println("**********************RESULT START***********************************");
        terminal.println();
        for (Document item : items) {
            List<IndexableField> fields = item.getFields();
            for (IndexableField field : fields) {
                terminal.println(String.format("%-20s %s", field.name(), field.stringValue()));
                ;
            }
        }
        terminal.println();
        terminal.println("**********************RESULT END***********************************");


    }
}
