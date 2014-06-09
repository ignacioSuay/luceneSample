package com.ignaciosuay.writer;



import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.List;
import java.util.Set;


/**
 * Created by suay on 5/13/14.
 */
public class LuceneIndexWriter {

    String indexPath = "";

    String jsonFilePath = "";

    IndexWriter indexWriter = null;

    public LuceneIndexWriter(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }

    public void createIndex(){
        openIndex();
        JSONArray jsonObjects = parseJSONFile();
        addDocuments(jsonObjects);
        finish();
    }

    public boolean openIndex(){
        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48, analyzer);

            //Always overwrite the directory
            iwc.setOpenMode(OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, iwc);

            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());

        }
        return false;

    }

    /**
     * Add documents to the index
     */
    public void addDocuments(JSONArray jsonObjects){
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            Document doc = new Document();
            for(String field : (Set<String>) object.keySet()){
                Class type = object.get(field).getClass();
                if(type.equals(String.class)){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
                }else if(type.equals(Long.class)){
                    doc.add(new LongField(field, (long)object.get(field), Field.Store.YES));
                }else if(type.equals(Double.class)){
                    doc.add(new DoubleField(field, (double)object.get(field), Field.Store.YES));
                }else if(type.equals(Boolean.class)){
                    doc.add(new StringField(field, object.get(field).toString(), Field.Store.YES));
                }
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
    }

    /**
     * Write the document to the index and close it
     */
    public void finish(){
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }

    /**
     * Parse a Json file. The file path should be included in the constructor
     */
    public JSONArray parseJSONFile(){

        //Get the JSON file, in this case is in ~/resources/test.json
        InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
        Reader reader = new InputStreamReader(jsonFile);

        //Parse the json file using simple-json library
        Object fileObjects= JSONValue.parse(reader);
        JSONArray arrayObjects=(JSONArray)fileObjects;

        return arrayObjects;

    }


}