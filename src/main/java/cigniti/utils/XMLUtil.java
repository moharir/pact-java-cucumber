package cigniti.utils;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileReader;

public class XMLUtil {

    static StringBuilder sb = new StringBuilder();
    /**
     * To read the XML file
     * This method returns the content in the file as String
     */
    public static String readXMLFile(String path){
        String sCurrentLine;
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Assert.fail("IO Exception : " + e.getMessage());
        }
        return sb.toString();
    }

}
