package cc.yuerblog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySQLManager implements Runnable {
    static {
        instance = new MySQLManager();
    }

    static public class ConnectParams {
        public String url;
        public Properties info;
    }

    private static MySQLManager instance;

    private WeakHashMap<Connection, Boolean> connHashMap = new WeakHashMap<>();

    private Lock mutex = new ReentrantLock();

    private Boolean disable = false;

    public static MySQLManager get() {
        return instance;
    }

    public MySQLManager() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        // 测试JDBC管控(例如:禁写)
        while (true) {
            try {
                System.out.println("进入封禁状态");
                disableAllConnection();
                System.out.println("连接数量:" + connectionCount()); 

                Thread.sleep(5000);

                System.out.println("恢复正常状态");
                enableAllConnection();
                System.out.println("连接数量:" + connectionCount()); 

                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    int connectionCount() {
        mutex.lock();
        int size=connHashMap.size();
        mutex.unlock();
        return size;
    }

    public ConnectParams modifyConnectParams(String url, Properties info) {
        System.out.println("modifyConnectParams " + url + " " + info);
        // 支持修改url和info，管控JDBC连向指定数据库
        ConnectParams connectParams = new ConnectParams();
        connectParams.url = url;
        connectParams.info = info;
        return connectParams;
    }

    public Connection addConnection(Connection conn) {
        mutex.lock();
        if (disable) {  // 封禁状态,直接关闭
            mutex.unlock();
            try {
                conn.close();
            } catch (SQLException e) {
            }
            return null;
        }
        connHashMap.put(conn, true);
        mutex.unlock();
        return conn;
    }

    public void removeConnection(Connection conn) {
        mutex.lock();
        connHashMap.remove(conn);
        mutex.unlock();
        System.out.println("removeConnection "+conn);
    }

    public void killAllConnection() {
        List<Connection> conns = new ArrayList<>();
        mutex.lock();
        for (Connection conn : connHashMap.keySet()){
            conns.add(conn);
        }
        connHashMap.clear();
        mutex.unlock();

        for (Connection conn : conns) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    public void disableAllConnection() {
        // 下发封禁
        mutex.lock();
        disable = true;
        mutex.unlock();

        // 杀死存量连接
        killAllConnection();
    }

    public void enableAllConnection() {
        mutex.lock();
        disable = false;
        mutex.unlock();
    }
}
