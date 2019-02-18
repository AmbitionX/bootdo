//package com.wx.DB;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//import java.sql.*;
//import java.util.HashMap;
//
//public class DBUtil {
//
//    public static JSONArray executeQuery(String sqlStr) {//查询记录,返回json
//        try {
//            Connection connection = ConnectionPool.getInstance().getConnection();
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(sqlStr);
//            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//            HashMap<String, String> columns = new HashMap<>();
//            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
//                columns.put(resultSetMetaData.getColumnName(i), resultSetMetaData.getColumnTypeName(i));
//            }
//            JSONArray jsonArray = new JSONArray();
//            while (resultSet.next()) {
//                JSONObject jsonObject = new JSONObject();
//                for (String key : columns.keySet()) {
//                    String type = columns.get(key);
//                    Object val;
//                    if (type.equals("TINYINT")) {
//                        val = resultSet.getInt(key);
//                    } else {
//                        val = resultSet.getObject(key);
//                    }
//                    jsonObject.put(key, val);
//                }
//                jsonArray.add(jsonObject);
//            }
//            resultSet.close();
//            statement.close();
//            ConnectionPool.getInstance().returnConnection(connection);
//            return jsonArray;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static int executeUpdate(String sqlStr) {//更新记录,传入sql语句
//        try {
//            Connection connection = ConnectionPool.getInstance().getConnection();
//            Statement statement = connection.createStatement();
//            int updateNum = statement.executeUpdate(sqlStr);
//            statement.close();
//            ConnectionPool.getInstance().returnConnection(connection);
//            return updateNum;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    public static void main(String[] args) throws SQLException {
////        String testsql = "select * from fy_wx_users";
//        String testsql = "select * from wx_users";
//        JSONArray resultSet = executeQuery(testsql);
//        int test = 1;
//    }
//}
