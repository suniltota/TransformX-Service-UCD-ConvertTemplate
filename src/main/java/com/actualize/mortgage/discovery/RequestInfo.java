package com.actualize.mortgage.discovery;

import java.io.Serializable;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * 
 * @author sboragala
 *
 */
public class RequestInfo implements Serializable{
	
	private static final long serialVersionUID = 483055912156082766L;
	
	private final String name;
    private final String consumes;
    private final String custom;
    private final String headers;
    private final String methods;
    private final String params;
    private final String patterns;
    private final String produces;

    public RequestInfo(RequestMappingInfo rmi) {
	    name = rmi.getName();
	    consumes = rmi.getConsumesCondition().toString();
	    custom = rmi.getCustomCondition()==null ? null : rmi.getCustomCondition().toString();
	    headers = rmi.getHeadersCondition().toString();
	    methods = rmi.getMethodsCondition().toString();
	    params = rmi.getParamsCondition().toString();
	    patterns = rmi.getPatternsCondition().toString();
	    produces = rmi.getProducesCondition().toString();
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the consumes
	 */
	public String getConsumes() {
		return consumes;
	}

	/**
	 * @return the custom
	 */
	public String getCustom() {
		return custom;
	}

	/**
	 * @return the headers
	 */
	public String getHeaders() {
		return headers;
	}

	/**
	 * @return the methods
	 */
	public String getMethods() {
		return methods;
	}

	/**
	 * @return the params
	 */
	public String getParams() {
		return params;
	}

	/**
	 * @return the patterns
	 */
	public String getPatterns() {
		return patterns;
	}

	/**
	 * @return the produces
	 */
	public String getProduces() {
		return produces;
	}

    
}
