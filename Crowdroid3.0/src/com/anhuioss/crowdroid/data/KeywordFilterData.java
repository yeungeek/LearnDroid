package com.anhuioss.crowdroid.data;

public class KeywordFilterData {
	
    private String keyword;
    
    
    //---------------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------------
    public KeywordFilterData() {
    	
    }
    
    
    //---------------------------------------------------------------------------
    /**
     * Set keyword
     */
    //---------------------------------------------------------------------------
    public void setKeyword(String keyword) {
    	this.keyword = keyword;
    }
    
    
    //---------------------------------------------------------------------------
    /**
     * Get keyword
     * @return String keyword
     */
    //---------------------------------------------------------------------------
    public String getKeyword() {
    	return keyword;
    }
    
    
    //---------------------------------------------------------------------------
    /**
     * Get keyword xml
     * @return String
     */
    //---------------------------------------------------------------------------
    public String getKeywordXml() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("<keyword>").append(keyword).append("</keyword>");
    	return buffer.toString();
    }
}
