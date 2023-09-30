package cc.yuerblog;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MySQLTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        ClassPool classPool = ClassPool.getDefault();
        className = className.replace("/", ".");

        if (className.equals("com.mysql.cj.jdbc.NonRegisteringDriver")) {
            try {
                CtClass driverClass = classPool.get(className);
                CtMethod connectMethod = driverClass.getDeclaredMethod("connect");
                connectMethod.insertBefore(
                        "cc.yuerblog.MySQLManager.ConnectParams connectParams=cc.yuerblog.MySQLManager.get().modifyConnectParams($1,$2);$1=connectParams.url;$2=connectParams.info;");
                connectMethod.insertAfter("{if($_!=null) {$_=cc.yuerblog.MySQLManager.get().addConnection($_);}}"); // 记录JDBC连接
                return driverClass.toBytecode();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (className.equals("com.mysql.cj.jdbc.ConnectionImpl")) {
            try {
                CtClass driverClass = classPool.get(className);
                CtMethod connectMethod = driverClass.getDeclaredMethod("close");
                connectMethod.insertAfter("{cc.yuerblog.MySQLManager.get().removeConnection($0);}"); // 遗忘JDBC连接
                connectMethod.addCatch("{cc.yuerblog.MySQLManager.get().removeConnection($0);throw $e;}",classPool.get("java.sql.SQLException"));   // 异常也保证回收
                return driverClass.toBytecode();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

}
