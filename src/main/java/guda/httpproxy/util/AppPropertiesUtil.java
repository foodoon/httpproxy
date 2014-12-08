package guda.httpproxy.util;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * Created by well on 2014/12/8.
 */
public class AppPropertiesUtil {

    private static Properties properties = new Properties();

    static{
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("app.properties");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getAppProperties(String key){
        return (String)properties.get(key);
    }

}
