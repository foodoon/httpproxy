package guda.page.gen;

import guda.gen.DemoGen;
import guda.tools.mybatis.MybatisTools;

/**
 * Created by foodoon on 2014/7/28.
 */
public class AutoGen {

    private static String jdbcPath = "C:\\Users\\well\\.m2\\repository\\mysql\\mysql-connector-java\\5.1.9\\mysql-connector-java-5.1.9.jar";

    private static String jdbUrl = "jdbc:mysql://127.0.0.1:3306/page?useUnicode=true&characterEncoding=utf8";

    public static void main(String[] args){
        DemoGen demoGen = new DemoGen();
        demoGen.setJdbcPassword("");
        demoGen.setJdbcUsername("root");
        demoGen.setJdbcPath(jdbcPath);
        demoGen.setJdbUrl(jdbUrl);
        demoGen.setDriverClass("com.mysql.jdbc.Driver");
        String baseDir = (String)System.getProperties().get("user.dir");
        //demoGen.genDAOWithDir("css", "page", "guda",baseDir);
        //demoGen.genDaoXMLWithDir("css","page","guda",baseDir);
//        demoGen.genBiz("demo","well");
//        demoGen.genAction("demo","well");





    }
}
