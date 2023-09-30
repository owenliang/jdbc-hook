package cc.yuerblog;

import javassist.ClassPool;
import javassist.CtClass;

public class Main {
    public static void main(String[] args) {
        ClassPool classPool=ClassPool.getDefault();
        CtClass demoCls=classPool.makeClass("cc.yuerblog.Demo");
        System.out.println(demoCls.getName());
    }
}