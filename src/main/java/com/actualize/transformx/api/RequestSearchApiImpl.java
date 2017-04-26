package com.actualize.transformx.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.actualize.transformx.discovery.RequestSearchResults;

/**
 * 
 * @author sboragala
 *
 */
@RestController
class RequestSearchApiImpl {

    private final RequestMappingHandlerMapping  handlerMapping;
    
    @Autowired
    public RequestSearchApiImpl(RequestMappingHandlerMapping handlerMapping) {
     this.handlerMapping = handlerMapping;
    }

    /**
     * 
     * @param pattern
     * @return
     */
    @RequestMapping(value = "/transformx/discovery", method = RequestMethod.GET, produces = "application/json")
    public RequestSearchResults showuris(@RequestParam(value="pattern", defaultValue=".*") String pattern) {
    	return new RequestSearchResults(handlerMapping.getHandlerMethods().keySet(), pattern);
    }
}