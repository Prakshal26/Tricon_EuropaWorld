package database;

import pojo.RawData;

import java.sql.*;

public class PostgreConnect {

    static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/europa_data_parsing",
                            "postgres", "root");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

        }
        return connection;
    }

    public static int checkCategory(String category)  {

        int temp = -1;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from category where name ='" +category+"'");
            //preparedStatement.setString(1, category);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                temp = 0;
            } else {
               // resultSet.beforeFirst();
                temp = resultSet.getInt(1);
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }


    public static int checkSubCategory(String sub_Category)  {

        int temp = -1;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from sub_category where name ='" +sub_Category+"'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                temp = 0;
            } else {
                // resultSet.beforeFirst();
                temp = resultSet.getInt(1);
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static int insertCategory(String category) {

        int id = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT into category (name)" + "VALUES (?)",Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, category);
            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                id=rs.getInt(1);
            }
            return id;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id;
    }
    public static int insertSubCategory(RawData rawData, int category_id) {

        int id = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT into sub_category (name, cat_id, source, units)" + "VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);

            int i = 1;
            statement.setString(i++, rawData.getHeading());
            statement.setInt(i++, category_id);
            statement.setString(i++, rawData.getSource());
            statement.setString(i++, rawData.getUnits());
            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                id=rs.getInt(1);
            }
            return id;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id;
    }

    public static int checkCountry(String countryAttribute)  {

        int temp = -1;
        try {
            if (countryAttribute.contains("'")) {
               countryAttribute = countryAttribute.replaceAll("'","&rsquo;");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("select * from country where country_id ='" +countryAttribute+"'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                temp = 0;
            } else {
                // resultSet.beforeFirst();
                temp = resultSet.getInt(1);
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }
    public static int insertCountry(String country, String countryAttribute) {

        int id = 0;
        try {
            if (country.contains("'")) {
                country = country.replaceAll("'","&rsquo;");
            }
            PreparedStatement statement = connection.prepareStatement("INSERT into country (country_name, country_id)" + "VALUES (?,?)",Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, country);
            statement.setString(2, countryAttribute);
            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                id=rs.getInt(1);
            }
            return id;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id;
    }

    public static void insertData(String jsonData,String jsonFootNote, int country_id, int subCategory_id) {

        int id = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT into country_data (sub_Category_id, country_id, data, footnote)" + "VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);

            int i = 1;
            statement.setInt(i++,subCategory_id );
            statement.setInt(i++, country_id);
            statement.setString(i++, jsonData);
            statement.setString(i++, jsonFootNote);
            statement.execute();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
