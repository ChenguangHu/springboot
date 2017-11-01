package com.geb;
import javax.servlet.MultipartConfigElement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
//启动注解事务管理
@EnableTransactionManagement
public class Application {
//    static{
//        try {
//            //初始化log4j
//            String log4jPath = Application.class.getClassLoader().getResource("").getPath() + "log4j.properties";
//            System.out.print("初始化Log4j：" + log4jPath);
//            PropertyConfigurator.configure(log4jPath);
//          
////            AutoMapper autoMapper = new AutoMapper();
////            autoMapper.domMappers();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

 @Bean  
    public MultipartConfigElement multipartConfigElement() {  
        MultipartConfigFactory factory = new MultipartConfigFactory();  
        //单个文件最大  
        factory.setMaxFileSize("8MB"); //KB,MB  
        /// 设置总上传数据总大小  
        factory.setMaxRequestSize("100MB");  
        return factory.createMultipartConfig();  
    }  
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
