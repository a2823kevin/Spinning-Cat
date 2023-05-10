package edu.proj;

import java.awt.Dimension;
import java.sql.*;
import java.util.ArrayList;

public class CatDB {
    Connection connection;

    public CatDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:../assets/cat.db");
    }

    public ArrayList<String> getTableNames() throws SQLException {
        ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
        ArrayList<String> result = new ArrayList<String>();
        while (rs.next()!=false) {
            String tableName = rs.getString("TABLE_NAME");
            if (tableName.length()>7) {
                if (tableName.substring(0, 7).equals("sqlite_")==false) {
                    result.add(tableName);
                }
            }
            else {
                result.add(tableName);
            }
        }

        return result;
    }

    public void initTables() throws SQLException {
        initComponentSizeTable();
        initModelTable();
        initMusicTable();
    }

    public void initComponentSizeTable() throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "DROP TABLE IF EXISTS component_size;" +
                     "CREATE TABLE component_size(" +
                     "name TEXT, " +
                     "width INT, " +
                     "height INT" +
                     ");";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void initModelTable() throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS model(" +
                     "name TEXT, " +
                     "obj_path TEXT, " +
                     "mtl_path TEXT" +
                     ");";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void initMusicTable() throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS music(" +
                     "name TEXT, " +
                     "snd_path TEXT" +
                     ");";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void insertComponentSize(String name, int width, int height) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(
        "INSERT INTO component_size (name, width, height) VALUES (?, ?, ?)"
        );
        pstmt.setString(1, name);
        pstmt.setInt(2, width);
        pstmt.setInt(3, height);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public Dimension getComponentSize(String name, float scalingRatio) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(
        "SELECT * FROM component_size WHERE name = ?"
        );
        pstmt.setString(1, name);

        int width = 0, height = 0;
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()!=false) {
            width = rs.getInt("width");
            height = rs.getInt("height");
        }

        return new Dimension((int)(width*scalingRatio), (int)(height*scalingRatio));
    }

    public ArrayList<String> getModelNames() throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT name FROM model";

        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<String> modelNames = new ArrayList<String>();
        while (rs.next()!=false) {
            modelNames.add(rs.getString("name"));
        }

        stmt.close();
        return modelNames;
    }

    public ArrayList<String> getMusicNames() throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT name FROM music";

        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<String> musicNames = new ArrayList<String>();
        while (rs.next()!=false) {
            musicNames.add(rs.getString("name"));
        }

        stmt.close();
        return musicNames;
    }

    public ArrayList<String> getModelPath(String name) throws SQLException {
        ArrayList<String> modelPath = new ArrayList<String>();

        if (name.equals("-")) {
            modelPath.add("-");
            modelPath.add("-");
            return modelPath;
        }

        PreparedStatement pstmt = connection.prepareStatement(
        "SELECT * FROM model WHERE name = ?"
        );
        pstmt.setString(1, name);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()!=false) {
            modelPath.add(rs.getString("obj_path"));
            modelPath.add(rs.getString("mtl_path"));
        }

        return modelPath;
    }

    public String getMusicPath(String name) throws SQLException {
        String musicPath = null;

        if (name.equals("-")) {
            musicPath = "-";
            return musicPath;
        }

        PreparedStatement pstmt = connection.prepareStatement(
        "SELECT * FROM music WHERE name = ?"
        );
        pstmt.setString(1, name);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()!=false) {
            musicPath = rs.getString("snd_path");
        }

        return musicPath;
    }

    public void exit() throws SQLException {
        connection.close();
    }
}
