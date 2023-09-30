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
        className = className.replace("/", ".");
        if (!className.equals("com.mysql.cj.jdbc.NonRegisteringDriver")) {
            return null;
        }

        ClassPool classPool = ClassPool.getDefault();

        try {
            CtClass driverClass = classPool.get(className);
            CtMethod connectMethod = driverClass.getDeclaredMethod("connect");
            System.out.println(className);
            connectMethod.insertBefore("{System.out.println($1);$1=\"jdbc:mysql://127.0.0.1:3306/db01\";System.out.println($1);}");    // DEMO: JDBC URL篡改
            return driverClass.toBytecode();
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

}
