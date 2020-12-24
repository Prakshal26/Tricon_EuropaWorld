package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import database.PostgreConnect;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Country {

    public static int handleCountry(String country, String countryAttribute, HashMap<String, Integer>  countryMap) {

        String tempAttribute = countryAttribute;

        if (countryMap.containsKey(countryAttribute)) {
            return countryMap.get(countryAttribute);
        } else {
            if (countryAttribute.equalsIgnoreCase("*****")) {
                countryAttribute = "";
            }
            int id = PostgreConnect.insertCountry(country, countryAttribute);
            countryMap.put(tempAttribute,id);
            return id;
        }
    }

    public static String jsonConvertor(HashMap<String,String> hashMap) {
        String jsonData = "";
        try {
            jsonData = new ObjectMapper().writeValueAsString(hashMap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    public static String handleValueList(NodeList valueList) {

        HashMap<String,String> hashMap = new LinkedHashMap<>();
        String key = "";

        for (int i=0; i<valueList.getLength();i++) {
            Node valueNode = valueList.item(i);
            if (valueNode.getNodeType()==Node.ELEMENT_NODE) {
                Element valueElement = (Element) valueNode;
                if (valueElement.hasAttribute("LABEL")) {
                    key = valueElement.getAttribute("LABEL");
                }
                if (valueElement.hasAttribute("YEAR")) {
                    key = valueElement.getAttribute("YEAR");
                }
                hashMap.put(key,valueElement.getTextContent());
            }
        }
        return Country.jsonConvertor(hashMap);
    }

    public static void handleCountryStats(NodeList countryStatsList, int subCategory_id, HashMap<String, Integer>  countryMap) {

        for (int i=0;i<countryStatsList.getLength();i++) {
            Node countryStatsNode = countryStatsList.item(i);
            Element countryStatsElement = (Element) countryStatsNode;
            String countryAttribute = countryStatsElement.getAttribute("ISO");
            NodeList nodeList = countryStatsElement.getElementsByTagName("*");

            int country_id = 0;
            String jsonData ="", jsonFootNote= "";

            for (int j=0; j<nodeList.getLength();j++) {
                Node node = nodeList.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (element.getTagName()=="NAME") {
                        //Store the Country name and get the id.
                        country_id = Country.handleCountry((element.getTextContent()), countryAttribute, countryMap);
                    }
                    if (element.getTagName()=="DATA-GROUP") {
                        //Loop through all the sub elements of data group and create a JSON object.
                       jsonData = Country.handleValueList(element.getChildNodes());
                    }
                    if (element.getTagName()=="FOOTNOTE") {
                        HashMap<String, String> footMap = new LinkedHashMap<>();
                        footMap.put(element.getAttribute("REF-SYMBOL"),element.getTextContent());
                        jsonFootNote = Country.jsonConvertor(footMap);
                    }
                }
            }
            PostgreConnect.insertData(jsonData,jsonFootNote, country_id, subCategory_id);
        }
    }

    public static void handleSpecialDataGroup(NodeList nodeList, int subCategory_id, HashMap<String, Integer>  countryMap) {

        int country_id =0;
        String jsonData = "";
        String jsonFootNote = "";
        for (int j=0; j<nodeList.getLength();j++) {
            Node node = nodeList.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getTagName()=="NAME") {
                    String countryAttribute = "*****";
                    //Special Data Group Means World Data. It does not have ISO code so for
                    //i am storing a dummy ISO code in local HashMap for World.
                    country_id = Country.handleCountry((element.getTextContent()), countryAttribute, countryMap);
                }
                if (element.getTagName()=="DATA-GROUP") {
                    //Loop through all the sub elements of data group and create a JSON object.
                   jsonData =  Country.handleValueList(element.getChildNodes());
                }
                if (element.getTagName()=="FOOTNOTE") {
                    HashMap<String, String> footMap = new LinkedHashMap<>();
                    footMap.put(element.getAttribute("ID"),element.getTextContent());
                    Country.jsonConvertor(footMap);
                }
            }
        }
        PostgreConnect.insertData(jsonData,jsonFootNote, country_id, subCategory_id);
    }
}
