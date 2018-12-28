package com.farsunset.cim.client.model;

public class IdentityInfo {
	public String PublicKey;
	public String Key;
	public int UserID;
	public String CompanyCode;
	public String Device = "PHONE";

	public String getPublicKey() {
		return PublicKey;
	}

	public void setPublicKey(String publicKey) {
		PublicKey = publicKey;
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userId) {
		UserID = userId;
	}

	public String getCompanyCode() {
		return CompanyCode;
	}

	public void setCompanyCode(String companyCode) {
		CompanyCode = companyCode;
	}
}
