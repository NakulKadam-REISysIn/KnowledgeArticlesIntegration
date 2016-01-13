package com.translations.globallink.connect.sf.model.vendor.dto;

public class SFConnectionConfig {

	
	private String password;
	private String consumerKey;
	private String consumerSecret;
	private String url;
	private String user;
	private String queueName;
	   

	    public String getUrl() {
		return url;
	    }

	    public void setUrl(String url) {
		this.url = url;
	    }

	    public String getUser() {
		return user;
	    }

	    public void setUser(String user) {
		this.user = user;
	    }
	  
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
		
	/**
	 * @return the consumerKey
	 */
	public String getConsumerKey() {
		return consumerKey;
	}
	/**
	 * @param consumerKey the consumerKey to set
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	/**
	 * @return the consumerSecret
	 */
	public String getConsumerSecret() {
		return consumerSecret;
	}
	/**
	 * @param consumerSecret the consumerSecret to set
	 */
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
}
