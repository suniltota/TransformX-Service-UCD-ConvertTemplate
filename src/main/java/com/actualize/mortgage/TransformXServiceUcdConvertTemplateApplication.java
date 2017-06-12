package com.actualize.mortgage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * This class initiates the current application 
 * @author sboragala
 *
 */
@SpringBootApplication
public class TransformXServiceUcdConvertTemplateApplication {

    //java -jar target/ROOT.war --textMappingFile="C:\Users\rsudula\Desktop\KotiTextTemplateMap.xml" --server.port=9090
   // @Autowired
//    private FileService fileService;


    public static void main(String[] args) {
        SpringApplication.run(TransformXServiceUcdConvertTemplateApplication.class, args);
    }

  /*  @Override
    public void run(String... args) throws FileNotFoundException {
            System.out.println(this.fileService.getFilename());
            System.out.println(this.fileService.getTextMappingFile());
    }*/
}
