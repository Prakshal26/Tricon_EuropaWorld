package parser;

import database.PostgreConnect;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    public static void main(String[] args) throws SQLException {

       // Connection connection = null;
        try {
            PostgreConnect.connect();

            HashMap<String,Integer> categoryMap = new LinkedHashMap<>();
            HashMap<String,Integer> subCategoryMap = new LinkedHashMap<>();
            HashMap<String,Integer> countryMap = new LinkedHashMap<>();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File dir = new File("C:\\Users\\lenovo\\IdeaProjects\\EuropaDbWork\\XML_Files\\Test");

            File [] files = dir.listFiles();
            int inserted_id = 0;
            int file_count =1;
            for(File file : files) {
                if(file.isFile() && file.getName().endsWith(".xml")) {
                    Document doc = dBuilder.parse(file);
                    System.out.println("Parsing file "+file_count);
                    file_count++;
                    ElementParse.parseFiles(doc, categoryMap, subCategoryMap, countryMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
