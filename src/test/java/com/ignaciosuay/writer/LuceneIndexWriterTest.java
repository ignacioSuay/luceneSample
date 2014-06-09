package com.ignaciosuay.writer;


import org.junit.Test;

/**
 * Created by suay on 5/13/14.
 */
public class LuceneIndexWriterTest {

    @Test
    public void testLuceneConfig(){
        try {
            LuceneIndexWriter lw = new LuceneIndexWriter("indexDir", "/test.json");
            lw.createIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
