package com.zendesk.search.console;

import com.zendesk.search.index.LuceneIndexWriter;
import com.zendesk.search.parse.JsonDataParser;
import com.zendesk.search.service.SearchQuery;
import com.zendesk.search.service.SearchResult;
import com.zendesk.search.service.ZendeskSearchService;
import com.zendesk.search.service.ZendeskSearchServiceImpl;
import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by phanindra on 22/05/19.
 */
public class CommandLineInterface {
    private final static Logger logger = Logger.getLogger(CommandLineInterface.class);
    final String indexDir = "indexDir1";
    final String jsonDataPath = "./data";

    private ZendeskSearchService zendeskSearchService;
    private SearchResultDisplayer searchResultDisplayer;
    private TextIO textIO;
    private Map<String, Integer> entityMap = new LinkedHashMap<>();
    private static final String NUMBER_FORMAT_ERROR_MESSAGE = "Expected an integer value!";

    public CommandLineInterface() {
        textIO = TextIoFactory.getTextIO();
        searchResultDisplayer = new SearchResultDisplayer(textIO);
    }

    public void run() throws IOException, URISyntaxException {

        indexData();

        prepareSearch();

        TextTerminal<?> textTerminal = textIO.getTextTerminal();
        textTerminal.println("Welcome to Zendesk Search");
        textTerminal.println("Type 'quit' at any time, Press 'enter' to continue");
        textTerminal.println();
        showOptions(textTerminal);

        while (true) {
            String selected = textIO.newStringInputReader()
                    .read();
            if (selected.trim().equalsIgnoreCase("quit")) {
                respondToQuitCommand(textTerminal);
            }
            int intOption;
            try {
                intOption = Integer.parseInt(selected);
            } catch (NumberFormatException nfe) {
                textTerminal.println(NUMBER_FORMAT_ERROR_MESSAGE);
                continue;
            }
            if (intOption == 1) {
                String entityNumber;
                String selectedEntity = null;
                String entitySelectionMessage = getEntitySelectionMessage();
                while (selectedEntity == null) {
                    entityNumber = textIO.newStringInputReader().read(entitySelectionMessage);
                    if (entityNumber.equalsIgnoreCase("quit")) {
                        respondToQuitCommand(textTerminal);
                    }
                    try {
                        Integer valueOf = Integer.valueOf(entityNumber);
                        for (Map.Entry<String, Integer> entry : entityMap.entrySet()) {
                            if (entry.getValue() == valueOf) {
                                selectedEntity = entry.getKey();
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        textTerminal.println(NUMBER_FORMAT_ERROR_MESSAGE);
                        continue;
                    }
                    if (selectedEntity == null) {
                        textTerminal.println("Invalid input!");
                    }
                }

                String searchTerm = textIO.newStringInputReader().read("Enter a search term");
                String searchValue = textIO.newStringInputReader().read("Enter a search value");
                SearchQuery searchQuery = new SearchQuery(searchTerm, searchValue, selectedEntity);
                SearchResult result = zendeskSearchService.searchForFullValueMatching(searchQuery);
                logger.debug("found " + result.getTotalItems() + " documents");
                searchResultDisplayer.printResult(result);
                showOptions(textTerminal);
            } else if (intOption == 2) {
                textTerminal.println(String.format("User selected %d", intOption));
            } else {
                textTerminal.println("Invalid input!");
            }
        }
    }

    private void respondToQuitCommand(TextTerminal<?> textTerminal) {
        textTerminal.println("exiting from the app...");
        System.exit(1);
    }

    private String getEntitySelectionMessage() throws IOException {
        List<String> entities = zendeskSearchService.findEntities();

        for (int i = 0; i < entities.size(); i++) {
            entityMap.put(entities.get(i), i + 1);
        }
        return searchResultDisplayer.showEntitiesSelection(entityMap);
    }

    private void showOptions(TextTerminal<?> textTerminal) {
        textTerminal.println();
        textTerminal.println("Select Search Options:");
        textTerminal.print(Arrays.asList("* Press 1 to search Zendesk", "* Press 2 to view list of searchable fields", "* Press 'quit' to exit"));
        textTerminal.println();
        textTerminal.println();
    }

    private void prepareSearch() throws URISyntaxException, IOException {
        Directory directory = FSDirectory.open(new File(indexDir).toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        zendeskSearchService = new ZendeskSearchServiceImpl(indexSearcher);
    }

    private void indexData() {

        ClassLoader classLoader = getClass().getClassLoader();
        URL jsonData = classLoader.getResource(jsonDataPath);

        logger.info("Creating index at: " + indexDir);
        try {
            JsonDataParser jsonDataParser = new JsonDataParser(jsonData.getPath());
            LuceneIndexWriter indexWriter = new LuceneIndexWriter(indexDir, jsonDataParser);
            indexWriter.createIndex();
            logger.info("Successfully created index");
        } catch (Exception e) {
            logger.error("Error creating index", e);
            System.exit(1);
        }

    }
}
