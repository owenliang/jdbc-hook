package cc.yuerblog;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String arg, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MySQLTransformer()); // MySQL Driver Inject.
    }
}