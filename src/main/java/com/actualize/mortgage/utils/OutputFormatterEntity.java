package com.actualize.mortgage.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.actualize.mortgage.mappingmodels.ConversionError;
/**
 * This ENUM class defines the various formats of populating and modifying the strings and values 
 * according to the requirement
 * @author sboragala
 *
 */
public enum OutputFormatterEntity {

    AMOUNT,                 // Converts a string into a number with two decimal places
    BOOLEAN,                // Converts 'Yes', 'No', 'True', 'False' in any caps format to 'true' or 'false'
    DATE,                   // Converts date format "mm/dd/yyyy" to MISMO format, e.g. yyyy-mm-dd
    DATETIME,               // Converts date format TODO to MISMO format, e.g. yyyy-mm-ddThh:mm:ssZ
    INTEGER,                // Converts a string into an integer (without a decimal)
    NUMBER,                 // Converts a string into a number (with a optional decimal)
    TELEPHONE,              // Converts a string into a MISMO telephone number
    RATE;                   // Converts a string into a number with four decimal places

//	private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ofPattern("[MM/dd/yy][MM/dd/yyyy]");
	private static final ArrayList<DateTimeFormatter> DATE_INPUT_FORMATTERS = initializeDateInputFormats();
    private static final DateTimeFormatter DATETIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yy");    // TODO
    
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;                  // 2015-11-02
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;        // 2015-11-02T17:00:00
    public static final NumberFormat AMOUNT_FORMAT = initializeAmountFormat();
    public static final NumberFormat RATE_FORMAT = initializeRateFormat();
    
    private static NumberFormat initializeAmountFormat() {
    	NumberFormat numberFormat = NumberFormat.getInstance();
    	numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    	numberFormat.setGroupingUsed(false);
    	numberFormat.setMinimumFractionDigits(2);
    	numberFormat.setMaximumFractionDigits(2);
    	return numberFormat;
    }

    private static NumberFormat initializeRateFormat() {
    	NumberFormat numberFormat = NumberFormat.getInstance();
    	numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    	numberFormat.setGroupingUsed(false);
    	numberFormat.setMinimumFractionDigits(4);
    	numberFormat.setMaximumFractionDigits(4);
    	return numberFormat;
    }
    
    private static ArrayList<DateTimeFormatter> initializeDateInputFormats() {
    	ArrayList<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
    	formatters.add(DateTimeFormatter.ofPattern("MM/dd/yy"));
    	formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    	formatters.add(DateTimeFormatter.ofPattern("yyyy-dd-MM"));
    	return formatters;
    }
    
    public static String ToMismoDate(LocalDate date) {
    	return DATE_FORMAT.format(date);
    }
    
    public static String ToMismoDateTime(LocalDateTime dateTime) {
    	return DATE_TIME_FORMAT.format(dateTime) + 'Z';
    }

    public ConversionError formatString(String inStr) {
        ConversionError conversionError = new ConversionError();
        switch (this) {
	        case AMOUNT:
	        	String amount = inStr.replaceAll("\\$|\\,", "");
	        	try {
	                conversionError.setInputValue(AMOUNT_FORMAT.format(Double.valueOf(amount)));
	            } catch (Exception e) {
	                conversionError.setErrorCode(e.getMessage());
	                conversionError.setErrorMsg("Can't convert '" + inStr + "' to amount");
	            }
	            break;
	        case BOOLEAN:
	            if ("Yes".equalsIgnoreCase(inStr) || "True".equalsIgnoreCase(inStr) || "T".equals(inStr))
	            	conversionError.setInputValue("true");
	            else if ("No".equalsIgnoreCase(inStr) || "False".equalsIgnoreCase(inStr) || "F".equals(inStr))
	            	conversionError.setInputValue("false");
	            else {
	                conversionError.setErrorCode("Boolean conversion error");
	                conversionError.setErrorMsg("Can't convert '" + inStr + "' to boolean");
	            }
	            break;
            case DATE:
                try {
                	LocalDate date = getDate(inStr);
                    conversionError.setInputValue(ToMismoDate(date));
                } catch (Exception e) {
                    conversionError.setErrorCode(e.getMessage());
                    conversionError.setErrorMsg("Can't convert '" + inStr + "' to date");
                }
                break;
            case DATETIME:
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(inStr, DATETIME_INPUT_FORMAT);
                    conversionError.setInputValue(ToMismoDateTime(dateTime));
                } catch (Exception e) {
                    conversionError.setErrorCode(e.getMessage());
                    conversionError.setErrorMsg("Can't convert '" + inStr + "' to datetime");
                }
                break;
            case INTEGER:
            	String integer = inStr.replaceAll("\\$|\\,", "");
                try {
                	Integer.valueOf(integer);
                    conversionError.setInputValue(integer);
                } catch (Exception e) {
                    conversionError.setErrorCode(e.getMessage());
                    conversionError.setErrorMsg("Can't convert '" + inStr + "' to number");
                }
                break;
            case NUMBER:
            	String number = inStr.replaceAll("\\$|\\,|%", "");
                try {
                	Double.valueOf(number);
                    conversionError.setInputValue(number);
                } catch (Exception e) {
                    conversionError.setErrorCode(e.getMessage());
                    conversionError.setErrorMsg("Can't convert '" + inStr + "' to number");
                }
                break;
            case TELEPHONE:
            	String phone = inStr.replaceAll("\\(|\\)|-|/|\\s", "");
                try {
                	Long.valueOf(phone);
                    conversionError.setInputValue(phone);
                } catch (Exception e) {
                    conversionError.setErrorCode(e.getMessage());
                    conversionError.setErrorMsg("Can't convert '" + inStr + "' to number");
                }
                break;
	        case RATE:
	        	String rate = inStr.replaceAll("\\,|%", "");
	        	try {
	                conversionError.setInputValue(RATE_FORMAT.format(Double.valueOf(rate)));
	            } catch (Exception e) {
	                conversionError.setErrorCode(e.getMessage());
	                conversionError.setErrorMsg("Can't convert '" + inStr + "' to amount");
	            }
	            break;
            default:
                break;
        }
        return conversionError;
    }

	public LocalDate getDate(String str) throws Exception {
		Exception last = null;
		for (DateTimeFormatter formatter : DATE_INPUT_FORMATTERS) {
			try {
				return LocalDate.parse(str, formatter);
			} catch (Exception e) {
				last = e;
			}
		}
		throw last;
	}
}
