package com.anhuioss.crowdroid.data;

public class TranslationData {

	private String userName;

	private String service;

	private String from;

	private String to;

	private String engine;

	// ------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// ------------------------------------------------------------------------------
	public TranslationData() {

	}

	// ------------------------------------------------------------------------------
	/**
	 * Set user name
	 * 
	 * @param String
	 *            userName
	 */
	// ------------------------------------------------------------------------------
	public void setUserName(String userName) {
		this.userName = userName;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Set service
	 * 
	 * @param String
	 *            service
	 */
	// ------------------------------------------------------------------------------
	public void setService(String service) {
		this.service = service;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Set from
	 * 
	 * @param String
	 *            from
	 */
	// ------------------------------------------------------------------------------
	public void setFrom(String from) {
		this.from = from;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Set to
	 * 
	 * @param String
	 *            to
	 */
	// ------------------------------------------------------------------------------
	public void setTo(String to) {
		this.to = to;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Get user name
	 * 
	 * @return String userName
	 */
	// ------------------------------------------------------------------------------
	public String getUserName() {
		return userName;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Get service
	 * 
	 * @return String service
	 */
	// ------------------------------------------------------------------------------
	public String getService() {
		return service;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Get from
	 * 
	 * @return String from
	 */
	// ------------------------------------------------------------------------------
	public String getFrom() {
		return from;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Get to
	 * 
	 * @return String to
	 */
	// ------------------------------------------------------------------------------
	public String getTo() {
		return to;
	}

	/**
	 * 获取翻译的engine
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * 设置翻译的engine
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Get translation xml
	 * 
	 * @return String xml
	 */
	// ------------------------------------------------------------------------------
	public String getTranslationXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<user_name>").append(userName).append("</user_name>")
				.append("<service>").append(service).append("</service>")
				.append("<from>").append(from).append("</from>").append("<to>")
				.append(to).append("</to>").append("<engine>").append(engine)
				.append("</engine>");
		return buffer.toString();
	}
}
