package com.actualize.transformx.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileService {

    @Value("${filename:MortgageCadenceMap.xml}")
    private String filename;
    
    @Value("${textMappingFile:TextTemplateMap.xml}")
    private String textMappingFile;

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the textMappingFile
     */
    public String getTextMappingFile() {
        return textMappingFile;
    }
}