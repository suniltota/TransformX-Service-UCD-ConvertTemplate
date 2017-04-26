package com.actualize.transformx;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

import com.actualize.transformx.services.impl.FileService;
/**
 * This class initiates the current application 
 * @author sboragala
 *
 */
@SpringBootApplication(scanBasePackages = "com.actualize.transformx")
@ImportResource("classpath:config.xml")
public class TransformXServiceUcdConvertTemplateApplication extends SpringBootServletInitializer implements CommandLineRunner{

    //java -jar target/ROOT.war --textMappingFile="C:\Users\rsudula\Desktop\KotiTextTemplateMap.xml" --server.port=9090
    @Autowired
    private FileService fileService;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TransformXServiceUcdConvertTemplateApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TransformXServiceUcdConvertTemplateApplication.class, args);
    }

    @Override
    public void run(String... args) throws FileNotFoundException {
            System.out.println(this.fileService.getFilename());
            System.out.println(this.fileService.getTextMappingFile());
    }

}
