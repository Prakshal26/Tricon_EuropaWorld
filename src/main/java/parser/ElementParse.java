package parser;

import database.PostgreConnect;
import handlers.Country;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pojo.RawData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElementParse {
    static int handleCategory(String category, HashMap<String,Integer> categoryMap ) {

        if (categoryMap.containsKey(category)) {
            return categoryMap.get(category);
        } else {
            int id = PostgreConnect.insertCategory(category);
            categoryMap.put(category,id);
            return id;
        }
    }
    static int handleSubCategory(HashMap<String,Integer> subCategoryMap,RawData rawData, int category_id) {

        if (subCategoryMap.containsKey(rawData.getHeading())) {
            return subCategoryMap.get(rawData.getHeading());
        } else {
            int id = PostgreConnect.insertSubCategory(rawData,category_id);
            subCategoryMap.put(rawData.getHeading(),id);
            return id;
        }
    }
    static void handleSubCategoryTable(Element element, RawData rawData, int category_id, HashMap<String,Integer> subCategoryMap, HashMap<String,Integer> countryMap) {

        String tagName = element.getTagName();
        int subCategory_id;
        switch (tagName) {

            case "HEADING":
                String subCategory = element.getTextContent();
                rawData.setHeading(subCategory);
                break;
            case "UNITS":
                rawData.setUnits(element.getTextContent());
                break;
            case "SOURCES":
                NodeList nodeList = element.getChildNodes();
                Node node = nodeList.item(1);
                Element sourceInfo = (Element) node;
                rawData.setSource(sourceInfo.getTextContent());
                break;
            default:
                break;
        }
    }
    static void parseFiles(Document doc, HashMap<String, Integer> categoryMap,HashMap<String, Integer> subCategoryMap, HashMap<String, Integer>  countryMap) {

        doc.getDocumentElement().normalize();
        Node entryNode = doc.getDocumentElement();

        RawData rawData = new RawData();

        Element entryElement = (Element) entryNode;

        int category_id = 0, subCategory_id = 0;

        //Handle Category Table

        if (entryElement.hasAttribute("SECT-TYPE")) {
            String category = entryElement.getAttribute("SECT-TYPE");
            category_id = ElementParse.handleCategory(category, categoryMap);
        }

        //Handle SubCategoryTable
        NodeList nodeList = entryNode.getChildNodes();
        for (int i=0; i<nodeList.getLength();i++) {
            Node nNode = nodeList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)nNode;
                ElementParse.handleSubCategoryTable(element, rawData, category_id, subCategoryMap, countryMap);
            }
        }
        if (category_id != 0) {
            subCategory_id = ElementParse.handleSubCategory(subCategoryMap,rawData,category_id);
        }


        //Handle Country Table
        if (subCategory_id != 0) {
            for (int i=0; i<nodeList.getLength();i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)nNode;
                    if (element.getTagName().equalsIgnoreCase("SPECIAL-DATA-GROUP")) {
                        Country.handleSpecialDataGroup(element.getChildNodes(),subCategory_id, countryMap);
                    }
                    if (element.getTagName().equalsIgnoreCase("COUNTRY-STATS-GROUP")) {
                        Country.handleCountryStats((element.getElementsByTagName("COUNTRY-STATS")),subCategory_id, countryMap);
                    }
                }
            }
        }
    }
}
