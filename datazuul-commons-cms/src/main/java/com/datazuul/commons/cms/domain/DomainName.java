package com.datazuul.commons.cms.domain;

public class DomainName {
	private String fullyQualifiedDomainName = null;

	public DomainName(String fullyQualifiedDomainName) {
		super();
		this.fullyQualifiedDomainName = fullyQualifiedDomainName;
	}

	public String getFullyQualifiedDomainName() {
		return fullyQualifiedDomainName;
	}

	public String getTopLevelDomain() {
		if (this.fullyQualifiedDomainName == null) {
			return null;
		}
		return getFullyQualifiedDomainName().substring(
				getFullyQualifiedDomainName().lastIndexOf(".") + 1);
	}
}
