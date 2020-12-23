package parser;

import database.PostgreConnect;
import handlers.Country;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pojo.RawData;

public class ElementParse {
    static int handleCategory(String category) {

        int category_id = PostgreConnect.checkCategory(category);
        if (category_id == 0) {
            int id = PostgreConnect.insertCategory(category);
            return id;
        } else {
         return category_id;
        }
    }
    static int handleSubCategory(RawData rawData, int category_id) {

        int sub_category_id = PostgreConnect.checkSubCategory(rawData.getHeading());
        if (sub_category_id == 0) {
           return PostgreConnect.insertSubCategory(rawData, category_id);
        } else {
            return sub_category_id;
        }
    }
    static void handleElements(Element element, RawData rawData, int category_id) {

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
                rawData.setSource(element.getTextContent());
                break;
            case "SPECIAL-DATA-GROUP":
                subCategory_id = ElementParse.handleSubCategory(rawData,category_id);
                Country.handleSpecialDataGroup(element.getChildNodes(),subCategory_id);
                break;
            case "COUNTRY-STATS-GROUP":
                subCategory_id = ElementParse.handleSubCategory(rawData,category_id);
                Country.handleCountryStats((element.getElementsByTagName("COUNTRY-STATS")),subCategory_id);
                break;
            default:
                break;
        }
    }
    static void parseFiles(Document doc) {

        doc.getDocumentElement().normalize();
        Node entryNode = doc.getDocumentElement();

        RawData rawData = new RawData();

        Element entryElement = (Element) entryNode;

        int category_id = 0, subCategory_id;

        if (entryElement.hasAttribute("SECT-TYPE")) {
            String category = entryElement.getAttribute("SECT-TYPE");
            category_id = ElementParse.handleCategory(category);
        }

        NodeList nodeList = entryNode.getChildNodes();
        for (int i=0; i<nodeList.getLength();i++) {
            Node nNode = nodeList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)nNode;
                ElementParse.handleElements(element, rawData, category_id);
            }
        }
    }
}
