package com.seb.tool;

import java.util.Map;
import java.util.Map.Entry;

public class EarningElement {
	
	private String countryCode;
	
	private String currencyCode;
	
	private float currencyRate;
	
	private Map<String, Float> earningPerApp;
	
	private float totalEarning;
	
	private float totalEarningInUserCurrency;
	
	public EarningElement(String countryCode, String currencyCode, Map<String, Float> earningPerApp,
			float totalEarning) {
		super();
		this.countryCode = countryCode;
		this.currencyCode = currencyCode;
		this.earningPerApp = earningPerApp;
		this.totalEarning = totalEarning;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public float getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(float currencyRate) {
		this.currencyRate = currencyRate;
	}

	public Map<String, Float> getEarningPerApp() {
		return earningPerApp;
	}

	public void setEarningPerApp(Map<String, Float> earningPerApp) {
		this.earningPerApp = earningPerApp;
	}

	public float getTotalEarning() {
		return totalEarning;
	}

	public void setTotalEarning(float totalEarning) {
		this.totalEarning = totalEarning;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public float getTotalEarningInUserCurrency() {
		return totalEarningInUserCurrency;
	}

	public void setTotalEarningInUserCurrency(float totalEarningInUserCurrency) {
		this.totalEarningInUserCurrency = totalEarningInUserCurrency;
	}

	@Override
	public String toString() {
		String recap = "Country Code : "+this.countryCode+"\n";
		recap = recap + "Currency : "+this.currencyCode+"\n";
		for (Entry<String, Float> entry : this.earningPerApp.entrySet())
		{
		    recap = recap + entry.getKey() + " -> " + entry.getValue()+" "+this.currencyCode+"\n";
		}
		recap = recap + "Total earning : "+this.totalEarning+" "+this.currencyCode+"\n";
		if (this.currencyRate!=0) {
			recap = recap + "Currency rate : "+this.currencyRate+"\n";
			for (Entry<String, Float> entry : this.earningPerApp.entrySet())
			{
			    recap = recap + entry.getKey() + " -> " + entry.getValue()*this.currencyRate+" "+Parameters.USER_CURRENCY_CODE+"\n";
			}
			recap = recap + "Total earning : "+this.totalEarningInUserCurrency+" "+Parameters.USER_CURRENCY_CODE+"\n";
		}
		return recap;
	}
	
}
