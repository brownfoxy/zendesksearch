package com.zendesk.search.console;

import com.zendesk.search.service.SearchResult;
import com.zendesk.search.service.SearchResultItem;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        List<SearchResultItem> items = searchResult.getItems();
        terminal.println();
        terminal.println(String.format("Displaying %d of %d items", items.size(), searchResult.getTotalItems()));
        terminal.println();
        int size = items.size();
        for (int i=0; i<size; i++) {
            terminal.println("==================================================================");
            terminal.println(String.format("Item %d/%d", i+1, size));
            terminal.println("==================================================================");
            List<IndexableField> fields = items.get(i).getDocument().getFields();
            for (IndexableField field : fields) {
                terminal.println(String.format("%-20s %s", field.name(), field.stringValue()));
            }
            printRelatedResult(items.get(i));
        }
        terminal.println();
        terminal.println("==================================================================");
    }

    private void printRelatedResult(SearchResultItem item) {
        Map<String, SearchResult> itemRelatedEntities = item.getRelatedEntities();
            Set<Map.Entry<String, SearchResult>> entrySet = itemRelatedEntities.entrySet();
            for (Map.Entry<String, SearchResult> entry : entrySet) {
                terminal.println();
                String key = entry.getKey();
                SearchResult value = entry.getValue();
                terminal.println(String.format("Displaying %d of %d related %s", value.getItems().size(), value.getTotalItems(), key));
                terminal.println();
                List<SearchResultItem> valueItems = value.getItems();
                for (int i=0; i<valueItems.size(); i++) {
                    terminal.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    terminal.println(String.format("%s %d/%d", key, i+1, valueItems.size()));
                    terminal.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    terminal.println();
                    Document document = valueItems.get(i).getDocument();
                    List<IndexableField> fields = document.getFields();
                    for (IndexableField field : fields) {
                        terminal.println(String.format("%-20s %s", field.name(), field.stringValue()));
                    }
                    terminal.println();
                }
            }
    }

    public String showEntitiesSelection(Map<String, Integer> entities) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Select ");
        int size = entities.keySet().size();
        int counter=0;
        for (String entity: entities.keySet()) {
            counter++;
            String capitalizeFirstCharacter = capitalizeFirstCharacter(entity);
            stringBuffer.append(String.format("%d) %s", entities.get(entity), capitalizeFirstCharacter));
            if (counter<size) {
                stringBuffer.append(String.format(" or "));
            }
        }
        return stringBuffer.toString();

    }

    private String capitalizeFirstCharacter(String entity) {
        return entity.substring(0, 1).toUpperCase() + entity.substring(1);
    }

    public void showFields(List<String> fieldsInsideEntity) {
        terminal.println();
        for(String field: fieldsInsideEntity) {
            terminal.println(field);
        }
    }
}
