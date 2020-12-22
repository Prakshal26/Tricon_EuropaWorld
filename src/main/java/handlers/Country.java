package handlers;

import database.PostgreConnect;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Country {

    public static int handleCountry(String country, String countryAttribute) {

        int country_id = PostgreConnect.checkCountry(countryAttribute);
        if (country_id == 0) {
            int id = PostgreConnect.insertCountry(country, countryAttribute);
            return id;
        } else {
            return country_id;
        }
    }

    public static void handleDataGroup(Element element, int country_id, int subCategory_id) {

    }

    public static void handleCountryStats(NodeList countryStatsList, int subCategory_id) {

        for (int i=0;i<countryStatsList.getLength();i++) {
            Node countryStatsNode = countryStatsList.item(i);
            Element countryStatsElement = (Element) countryStatsNode;
            String countryAttribute = countryStatsElement.getAttribute("ISO");
            NodeList nodeList = countryStatsElement.getElementsByTagName("*");

            int country_id = 0;

            for (int j=0; j<nodeList.getLength();j++) {
                Node node = nodeList.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (element.getTagName()=="NAME") {
                        //Store the Country name and get the id.
                        country_id = Country.handleCountry((element.getTextContent()), countryAttribute);
                    }
                    if (element.getTagName()=="DATA-GROUP") {
                        //Loop through all the sub elements of data group and create a JSON object.
                        Country.handleDataGroup(element, country_id, subCategory_id);

                    }
                }
            }
        }

    }
}
