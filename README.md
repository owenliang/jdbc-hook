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
    public static void main(String[] args) throws Exception {
        while (true) {
            String url = "jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123";
            Connection conn = DriverManager.getConnection(url);
            conn.isValid(1);
            conn.close();
            Thread.sleep(1000);
        }
    }
}
```

输出如下，agent可以拦截修改JDBC连接地址、也会收集建立好的JDBC连接，这样就可以从上层对JDBC连接进行管控，比如：随时杀死所有的JDBC连接，或者下发新的JDBC连接地址

```
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
MySQLManager connHashMap size 0
removeConnection true
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection true
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection true
modifyConnectParams jdbc:mysql://10.0.0.235:3306/sbtest?user=root&password=baidu@123 {}
removeConnection true
```