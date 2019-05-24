package com.zendesk.search.parse;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by phanindra on 22/05/19.
 */
public class JsonDataParser implements DataParser {
    private final static Logger logger = Logger.getLogger(JsonDataParser.class);

    private String jsonDataPath = "";

    public JsonDataParser(String jsonDataPath) {
        this.jsonDataPath = jsonDataPath;
    }

    /**
     * Parse a Json file.
     */
    @Override
    public Iterable<Document> parse() throws IOException {
        logger.info("Parsing json file(s) at " + jsonDataPath);

        List<Document> documents = new ArrayList<>();
        File dataFolder = new File(jsonDataPath);
        if (dataFolder.exists()) {
            File[] jsonFiles = dataFolder.listFiles();
            for (File jsonFile : jsonFiles) {
                int indexOfDot = jsonFile.getName().indexOf(".");
                String fileName = jsonFile.getName().substring(0, indexOfDot);
                try (InputStream fileInputStream = new FileInputStream(jsonFile)) {
                    Reader readerJson = new InputStreamReader(fileInputStream);
                    //Parse the json file using simple-json library
                    Object fileObjects = JSONValue.parse(readerJson);
                    JSONArray arrayObjects = (JSONArray) fileObjects;

                    for (JSONObject object : (List<JSONObject>) arrayObjects) {
                        Document doc = new Document();
                        doc.add(new SortedSetDocValuesFacetField("fileName", fileName.toLowerCase()));
                        doc.add(new StringField("fileName", fileName.toLowerCase(), Field.Store.NO));
                        for (String field : (Set<String>) object.keySet()) {
                            doc.add(new StringField(field, String.valueOf(object.get(field)), Field.Store.YES));
                        }
                        documents.add(doc);
                    }
                }

            }

        } else {
            throw new IOException("There is no data exists at "+ jsonDataPath);
        }
        return documents;
    }
}
