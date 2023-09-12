/**
 * 
 */
package com.core.datamodel.model.wrapper;

public class JSONNotificationWrapper {
	private Long id;
	private String timeWhen;
	private String message;
	private String medium;
	private Long targrtTypeId;
	private Long targetId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTimeWhen() {
		return timeWhen;
	}

	public void setTimeWhen(String timeWhen) {
		this.timeWhen = timeWhen;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public Long getTargrtTypeId() {
		return targrtTypeId;
	}

	public void setTargrtTypeId(Long targrtTypeId) {
		this.targrtTypeId = targrtTypeId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
}
