package cc.yuerblog;

import java.sql.Connection;
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
        while (true) {
            mutex.lock();
            System.out.println("MySQLManager connHashMap size " + connHashMap.size());
            mutex.unlock();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    public ConnectParams modifyConnectParams(String url, Properties info) {
        System.out.println("modifyConnectParams " + url + " " + info);
        ConnectParams connectParams = new ConnectParams();
        connectParams.url = url;
        connectParams.info = info;
        return connectParams;
    }

    public Connection addConnection(Connection conn) {
        mutex.lock();
        connHashMap.put(conn, true);
        mutex.unlock();
        return conn;
    }

    public boolean removeConnection(Connection conn) {
        mutex.lock();
        boolean ret = connHashMap.remove(conn);
        mutex.unlock();
        System.out.println("removeConnection "+ret);
        return ret;
    }

}
