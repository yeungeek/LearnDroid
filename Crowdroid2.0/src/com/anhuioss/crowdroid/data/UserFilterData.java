package com.anhuioss.crowdroid.data;

public class UserFilterData {

	private String service;

	private String userName;

	// -----------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// -----------------------------------------------------------------------
	public UserFilterData() {

	}

	// -----------------------------------------------------------------------
	/**
	 * Set service
	 * 
	 * @param String
	 *            service
	 */
	// -----------------------------------------------------------------------
	public void setService(String service) {
		this.service = service;
	}

	// -----------------------------------------------------------------------
	/**
	 * Set user name
	 * 
	 * @param String
	 *            userName
	 */
	// -----------------------------------------------------------------------
	public void setUserName(String userName) {
		this.userName = userName;
	}

	// -----------------------------------------------------------------------
	/**
	 * Get service
	 * 
	 * @return String service
	 */
	// -----------------------------------------------------------------------
	public String getService() {
		return service;
	}

	// -----------------------------------------------------------------------
	/**
	 * Get user name
	 * 
	 * @return String userName
	 */
	// -----------------------------------------------------------------------
	public String getUserName() {
		return userName;
	}

	// -----------------------------------------------------------------------
	/**
	 * Get user filter xml
	 * 
	 * @return String xml
	 */
	// -----------------------------------------------------------------------
	public String getUserFilterXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<service>").append(service).append("</service>")
				.append("<user_name>").append(userName).append("</user_name>");
		return buffer.toString();
	}
}
