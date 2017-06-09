package com.actualize.mortgage;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.actualize.mortgage.services.impl.FileService;
/**
 * This class initiates the current application 
 * @author sboragala
 *
 */
@SpringBootApplication
public class TransformXServiceUcdConvertTemplateApplication extends SpringBootServletInitializer{

    //java -jar target/ROOT.war --textMappingFile="C:\Users\rsudula\Desktop\KotiTextTemplateMap.xml" --server.port=9090
   // @Autowired
    private FileService fileService;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TransformXServiceUcdConvertTemplateApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TransformXServiceUcdConvertTemplateApplication.class, args);
    }

    /*@Override
    public void run(String... args) throws FileNotFoundException {
            System.out.println(this.fileService.getFilename());
            System.out.println(this.fileService.getTextMappingFile());
    }
*/
}
