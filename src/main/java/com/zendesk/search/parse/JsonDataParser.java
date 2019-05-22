package com.zendesk.search.parse;

import com.zendesk.search.index.LuceneIndexWriter;
import org.apache.log4j.Logger;
import org.apache.lucene.document.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by phanindra on 22/05/19.
 */
public class JsonDataParser implements DataParser {
    private final static Logger logger = Logger.getLogger(JsonDataParser.class);

    private String jsonFilePath = "";

    public JsonDataParser(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    /**
     * Parse a Json file.
     */
    @Override
    public Iterable<Document> parse() {
        logger.info("Parsing json file at " + jsonFilePath);
        List<Document> documents = new ArrayList<>();
        InputStream jsonFile = getClass().getResourceAsStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        //Parse the json file using simple-json library
        Object fileObjects = JSONValue.parse(readerJson);
        JSONArray arrayObjects = (JSONArray) fileObjects;

        for (JSONObject object : (List<JSONObject>) arrayObjects) {
            Document doc = new Document();
            for (String field : (Set<String>) object.keySet()) {
                doc.add(new StringField(field, String.valueOf(object.get(field)), Field.Store.YES));
            }
            documents.add(doc);
        }

        return documents;
    }
}
