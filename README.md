# jdbc-hook

JDBC的字节码增强，提供Hook扩展点给上层逻辑


## 测试代码

编写1个使用JDBC的项目，引入MYSQL驱动：

```
    <dependencies>
        <!-- https://mvnrepository.com/artifact/com.mysql/mysql-connector-j -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>
```

使用javaagent，可以看到运行期间JDBC的连接建立和销毁(包括GC)都被agent注入的代码所追踪：

```
package com.example;

import java.sql.Connection;
import java.sql.DriverManager;

// java -javaagent:C:/Users/owenliang/Documents/vscode/jdbc-hook/target/jdbc-hook-1.0-SNAPSHOT.jar -jar target/demo-1.0-SNAPSHOT.jar
public class Main {
    public static void main(String[] args) {
        while (true) {
            String url = "jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123";
            try {
                Connection conn = DriverManager.getConnection(url);
                conn.isValid(1);
                conn.close();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

输出如下，agent可以拦截修改JDBC连接地址、也会收集建立好的JDBC连接，这样就可以从上层对JDBC连接进行管控，比如：禁止建立JDBC连接，或者强制连向其他MySQL地址.

下面演示了封禁5秒,放开5秒的反复效果:

```
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
进入封禁状态
连接数量:0
removeConnection com.mysql.cj.jdbc.ConnectionImpl@52e6fdee
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@15ff3e9e
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@8dbdac1
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@3ce1e309
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@74e52303
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@3bb9a3ff
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@323b36e0
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@6356695f
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@1fa268de
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@a514af7
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@4fb61f4a
java.sql.SQLException: No suitable driver found for jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123
        at java.sql.DriverManager.getConnection(Unknown Source)
        at java.sql.DriverManager.getConnection(Unknown Source)
        at com.example.Main.main(Main.java:12)
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
恢复正常状态
连接数量:0
removeConnection com.mysql.cj.jdbc.ConnectionImpl@7b9a4292
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@6379eb
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@9f116cc
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection com.mysql.cj.jdbc.ConnectionImpl@5876a9af
```