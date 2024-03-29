package WorldBuilder;


import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;

public class GalaxyViewer {

    private static final String dbName = "Galaxy";

    public static Connection connect(String dbName) {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:"+dbName+".db";
            // create a connection to the database
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static ArrayList<String> getSectorNames () {
        ArrayList<String> names = new ArrayList<>();
        try {
            String sql = "SELECT Name FROM Sectors";
            Connection conn = connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException throwables) {
            //throwables.printStackTrace();
            names.add("ERROR");
        }
        return names;
    }

    public static ArrayList<String> getSystemNames (String sectorName) {
        ArrayList<String> names = new ArrayList<>();
        try {
            String sql = "SELECT Name FROM Systems WHERE Sector = '" + sectorName + "'";
            Connection conn = connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            names.add("ERROR");
        }
        return names;
    }

    public static ArrayList<String> getNames (GalaxyDataBaseItem item) {
        ArrayList<String> names = new ArrayList<>();
        try {
            String sql = "SELECT Name FROM " + item.getTableNames();
            Connection conn = connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            names.add("ERROR");
        }
        return names;
    }

    public static ArrayList<Sector> getSectors () {
        ArrayList<Sector> items = new ArrayList<>();
        ArrayList<String> names = getSectorNames();
        for (String name : names) {
            try {
                Sector temp = new Sector();
                GalaxyDataBase.readEntry(temp, name);
                items.add(temp);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return items;
    }

    public static ArrayList<GalaxyDataBaseItem> getItems (GalaxyDataBaseItem item) {
        ArrayList<GalaxyDataBaseItem> items = new ArrayList<>();
        ArrayList<String> names = getNames(item);
        for (String name : names) {
            try {
                GalaxyDataBaseItem temp = item.getClass().getConstructor().newInstance();
                GalaxyDataBase.readEntry(temp, name);
                items.add(temp);
            } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException throwables) {
                throwables.printStackTrace();
            }
        }
        return items;
    }


}
