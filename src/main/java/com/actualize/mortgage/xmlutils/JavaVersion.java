package com.actualize.mortgage.xmlutils;


/**
 * Class <code>JavaVersion </code> execute as main class which specify the java versions
 * 
 */
public class JavaVersion {
    private static String[] elements = System.getProperty("java.version").split("\\.|_|-b");

    public static final int MAJOR = Integer.parseInt(elements[1]);
    public static final int MINOR = Integer.parseInt(elements[2]);
    public static final int UPDATE = elements.length > 3 ? Integer.parseInt(elements[3]) : 0;
    public static final String BUILD = elements.length > 4 ? elements[4] : "";
    
    private JavaVersion() {}
}
