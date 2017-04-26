package com.actualize.transformx.discovery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
/**
 * 
 * @author sboragala
 *
 */
public class RequestSearchResults implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7668739044341566321L;
	
	private final String pattern;
    private final ArrayList<RequestInfo> results;

    public RequestSearchResults(Set<RequestMappingInfo> rmiset, String pattern) {
    	this.pattern = pattern;
    	results = new ArrayList<RequestInfo>();
    	for (RequestMappingInfo rmi : rmiset)
    		for (String rmipattern : rmi.getPatternsCondition().getPatterns())
	    		if (rmipattern.matches(pattern)) {
		    		results.add(new RequestInfo(rmi));
		    		break;
	    		}
    }

    public String getPattern() {
        return pattern;
    }

    public ArrayList<RequestInfo> getResults() {
        return results;
    }
}
