package com.wx.demo.DB;

import com.wx.demo.tools.Settings;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;

public class ConnectionPool {
    private static Logger logger = Logger.getLogger(ConnectionPool.class);
    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    private static String dbUrl = Settings.getSet().mysqlUrl; // 数据 URL
    private static String dbUsername = Settings.getSet().mysqlUserName; // 数据库用户名
    private static String dbPassword = Settings.getSet().mysqlPwd; // 数据库用户密码

    private int initialConnections = 1; // 连接池的初始大小
    private int incrementalConnections = 1;// 连接池自动增加的大小
    private int maxConnections = 10; // 连接池最大的大小
    private Vector connections = null; // 存放连接池中数据库连接的向量 , 初始时为 null

    private static ConnectionPool instance = null;

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
            try {
                instance.createPool();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public synchronized void createPool() throws Exception {
        if (connections != null) {
            return;
        }
        Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
        DriverManager.registerDriver(driver);
        connections = new Vector();
        createConnections(this.initialConnections);
    }

    private void createConnections(int numConnections) throws SQLException {
        for (int x = 0; x < numConnections; x++) {
            if (this.maxConnections > 0 && this.connections.size() >= this.maxConnections) {
                break;
            }
            try {
                connections.addElement(new PooledConnection(newConnection()));
            } catch (SQLException e) {
                logger.info(" 创建数据库连接失败！ " + e.getMessage());
                throw new SQLException();
            }
        }
    }

    private Connection newConnection() throws SQLException {
        // 创建一个数据库连接
        Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        if (connections.size() == 0) {
            DatabaseMetaData metaData = conn.getMetaData();
            int driverMaxConnections = metaData.getMaxConnections();
            if (driverMaxConnections > 0 && this.maxConnections > driverMaxConnections) {
                this.maxConnections = driverMaxConnections;
            }
        }
        return conn;
    }

    public synchronized Connection getConnection() throws SQLException {
        // 确保连接池己被创建
        if (connections == null) {
            return null;
        }
        Connection conn = getFreeConnection(); // 获得一个可用的数据库连接
        while (conn == null) {
            wait(250);
            conn = getFreeConnection();
        }
        return conn;
    }

    private Connection getFreeConnection() throws SQLException {
        Connection conn = findFreeConnection();
        if (conn == null) {
            createConnections(incrementalConnections);
            conn = findFreeConnection();
            if (conn == null) {
                return null;
            }
        }
        return conn;
    }

    private Connection findFreeConnection() throws SQLException {
        Connection conn = null;
        PooledConnection pConn = null;
        // 获得连接池向量中所有的对象
        Enumeration enumerate = connections.elements();
        // 遍历所有的对象，看是否有可用的连接
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (!pConn.isBusy()) {
                conn = pConn.getConnection();
                pConn.setBusy(true);
                // 测试此连接是否可用
                if (!testConnection(conn)) {
                    try {
                        conn = newConnection();
                    } catch (SQLException e) {
                        logger.info(" 创建数据库连接失败！ " + e.getMessage());
                        return null;
                    }
                    pConn.setConnection(conn);
                }
                break; // 己经找到一个可用的连接，退出
            }
        }
        return conn;// 返回找到到的可用连接
    }

    private boolean testConnection(Connection conn) {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            closeConnection(conn);
            return false;
        }
        return true;
    }

    public void returnConnection(Connection conn) {
        if (connections == null) {
            System.out.println(" 连接池不存在，无法返回此连接到连接池中 !");
            return;
        }
        PooledConnection pConn = null;
        Enumeration enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (conn == pConn.getConnection()) {
                pConn.setBusy(false);
                break;
            }
        }
    }

    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
        }
    }

    public synchronized void refreshConnections() throws SQLException {
        // 确保连接池己创新存在
        if (connections == null) {
            System.out.println(" 连接池不存在，无法刷新 !");
            return;
        }
        PooledConnection pConn = null;
        Enumeration enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            // 获得一个连接对象
            pConn = (PooledConnection) enumerate.nextElement();
            // 如果对象忙则等 5 秒 ,5 秒后直接刷新
            if (pConn.isBusy()) {
                wait(5000); // 等 5 秒
            }
            // 关闭此连接，用一个新的连接代替它。
            closeConnection(pConn.getConnection());
            pConn.setConnection(newConnection());
            pConn.setBusy(false);
        }
    }

    public synchronized void closeConnectionPool() throws SQLException {
        if (connections == null) {
            System.out.println(" 连接池不存在，无法关闭 !");
            return;
        }
        PooledConnection pConn = null;
        Enumeration enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (pConn.isBusy()) {
                wait(5000); // 等 5 秒
            }
            closeConnection(pConn.getConnection());
            connections.removeElement(pConn);
        }
        connections = null;
    }

    private void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(" 关闭数据库连接出错： " + e.getMessage());
        }
    }
}
