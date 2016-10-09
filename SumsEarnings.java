package com.seb.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SumsEarnings {
	
	private Map<String, Float> currencyRateMapInUse = Parameters.CURRENCY_RATE_MAP_2016_02;
	private Map<String, Float> withholdingTaxInUse = Parameters.WITHHOLDING_TAX_MAP_2016_02;
	private Map<String, Float> beginningBalanceInUse = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SumsEarnings sumsEarnings = new SumsEarnings();
		sumsEarnings.run();
	}
	

	  public void run() {

		String pathToEarningFiles = "/Users/alfre/Documents/iOs - Apple/AppStorePayment/2016_02/";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "	";
		boolean isFirstLine = true;
		String[] header = null;
		String fileName = "";
		String countryCode = "";
		String currencyCode = "";
		Map<String, Float> tmpAppEarningMap = new HashMap<String, Float>();
		List<EarningElement> earningElementList = new ArrayList<EarningElement>();  
		
		//get file list
		File folder = new File(pathToEarningFiles);
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	fileName = listOfFiles[i].getName();
	        if(fileName.endsWith(".txt") && !fileName.endsWith("Earnings_Report.txt") && !fileName.contains("Currency_Rate")){
	        	//get file identifier
	        	countryCode = fileName.substring(fileName.length()-6, fileName.length()-4);
	        	
	        	System.out.println("File " + listOfFiles[i].getName());
	        	tmpAppEarningMap = new HashMap<String, Float>();
	        	isFirstLine = true;
	        	try {
	        		br = new BufferedReader(new FileReader(pathToEarningFiles+listOfFiles[i].getName()));
	        		while ((line = br.readLine()) != null) {
	        			if(isFirstLine){
	        				header = line.split(cvsSplitBy);
	        			} else {
	        				String[] lineSplit = line.split(cvsSplitBy);
	        				if(lineSplit.length==header.length){
	        					currencyCode = lineSplit[8];
	        					if(!tmpAppEarningMap.containsKey(lineSplit[4])){
	        						tmpAppEarningMap.put(lineSplit[4], Float.parseFloat(lineSplit[7]));
	        					} else {
	        						Float floatSum = tmpAppEarningMap.get(lineSplit[4])+Float.parseFloat(lineSplit[7]);
	        						tmpAppEarningMap.put(lineSplit[4], floatSum);
	        					}
	        					System.out.println("App [name= " + lineSplit[4] 
	                                    + " , earning=" + lineSplit[7] + "]");
	        					System.out.println("Sum of " + lineSplit[4] + " = "+tmpAppEarningMap.get(lineSplit[4]));
	        				}
	        			}
	        			isFirstLine = false;

	        		}
	        		
	        		System.out.println("\n");
	        		System.out.println(currencyCode);
	        		float totalFloat = (float) 0.0;
	        		for(String key : tmpAppEarningMap.keySet()){
        			   totalFloat=totalFloat+tmpAppEarningMap.get(key);
        			}
	        		
	        		//Add beginning balance if any
	        		if (beginningBalanceInUse != null && beginningBalanceInUse.containsKey(currencyCode)) {
	        			System.out.println("BEGINNING BALANCE != 0");
        				System.out.println("Total + beginning balance = "+totalFloat+" - "+beginningBalanceInUse.get(currencyCode)+" = ");
        				totalFloat=totalFloat+beginningBalanceInUse.get(currencyCode);
        				System.out.println(totalFloat);
					}
	        		
	        		//Substract withholding tax to the total if any
        			if (withholdingTaxInUse != null && withholdingTaxInUse.containsKey(currencyCode)) {
        				System.out.println("WITHOLDING TAX != 0");
        				System.out.println("Total - withholding tax = "+totalFloat+" - "+withholdingTaxInUse.get(currencyCode)+" = ");
        				totalFloat=totalFloat-withholdingTaxInUse.get(currencyCode);
        				System.out.println(totalFloat);
					}
        			
	        		EarningElement earningElement = new EarningElement(countryCode, currencyCode, tmpAppEarningMap, totalFloat);
	        		if(currencyRateMapInUse.get(currencyCode)!=null){
	        			if (currencyCode.equalsIgnoreCase("USD")) {
							if (!countryCode.equalsIgnoreCase("US")) {
								//Special case of USD - RoW (Rest of the World)
								earningElement.setCurrencyRate(currencyRateMapInUse.get("USD-ROW"));
							} else {
								earningElement.setCurrencyRate(currencyRateMapInUse.get("USD"));
							}
						} else {
							earningElement.setCurrencyRate(currencyRateMapInUse.get(currencyCode));
						}
	        		}
	        		earningElementList.add(earningElement);
	        		System.out.println("Total : "+totalFloat);
	        		System.out.println("\n");

        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();
        		} finally {
        			if (br != null) {
        				try {
        					br.close();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        		}
	        }
	      }
	    }

		System.out.println("===========================================================================");
		System.out.println("\n");
		
		//ALL Recap + get total per app
		Map<String, Float> TotalEarningInUserCurrencyMap = new LinkedHashMap<String, Float>();
//		float tmpTotalCheck = (float) 0.0;
//		float tmpTotalCheckPerApp = (float) 0.0;
		for (EarningElement earningElement : earningElementList) {
			float totalInCurrency = earningElement.getTotalEarning()*earningElement.getCurrencyRate();
			BigDecimal roundUp = new BigDecimal(totalInCurrency).setScale(2, RoundingMode.HALF_UP);
			earningElement.setTotalEarningInUserCurrency(roundUp.floatValue());
			
			System.out.println(earningElement.toString());
//			tmpTotalCheck = tmpTotalCheck + earningElement.getTotalEarningInUserCurrency();
//			System.out.println("Total addition : "+tmpTotalCheck);
			int nbOfEntry = earningElement.getEarningPerApp().size();
			for (Entry<String, Float> entry : earningElement.getEarningPerApp().entrySet()){
				float d = entry.getValue()*earningElement.getCurrencyRate();
				BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
				d = bd.floatValue();
				if(!TotalEarningInUserCurrencyMap.containsKey(entry.getKey())){
					TotalEarningInUserCurrencyMap.put(entry.getKey(), d);
					System.out.println(entry.getKey()+" - "+d);
				} else {
					float floatSum = TotalEarningInUserCurrencyMap.get(entry.getKey())+d;
					BigDecimal bdBis = new BigDecimal(floatSum).setScale(2, RoundingMode.HALF_UP);
					floatSum = bdBis.floatValue();
					//Withholding case
					if (withholdingTaxInUse != null && withholdingTaxInUse.containsKey(earningElement.getCurrencyCode())) {
        				System.out.println("WITHOLDING TAX divided on Apps");
        				float currencyRate = currencyRateMapInUse.get(earningElement.getCurrencyCode());
        				System.out.println("Total app - (withholding tax / nbOfApp * currencyRate) = "+floatSum+" - "+withholdingTaxInUse.get(earningElement.getCurrencyCode())+" / nbOfApp * currencyRate = ");
        				floatSum=floatSum-(withholdingTaxInUse.get(earningElement.getCurrencyCode())/nbOfEntry)*currencyRate;
        				System.out.println(floatSum);
					}
					//Beginning balance
					if (beginningBalanceInUse != null && beginningBalanceInUse.containsKey(earningElement.getCurrencyCode())) {
        				System.out.println("Beginning balance divided on Apps");
        				float currencyRate = currencyRateMapInUse.get(earningElement.getCurrencyCode());
        				System.out.println("Total app - (beginning balance / nbOfApp * currencyRate) = "+floatSum+" - "+withholdingTaxInUse.get(earningElement.getCurrencyCode())+" / nbOfApp * currencyRate = ");
        				floatSum=floatSum+(beginningBalanceInUse.get(earningElement.getCurrencyCode())/nbOfEntry)*currencyRate;
        				System.out.println(floatSum);
					}
					TotalEarningInUserCurrencyMap.put(entry.getKey(), floatSum);
					System.out.println(entry.getKey()+" - "+floatSum);
					System.out.println("\n");
				}
//				tmpTotalCheckPerApp = tmpTotalCheckPerApp + d;
			}
//			System.out.println("Total using app : "+tmpTotalCheckPerApp);
//			System.out.println("\n");
		}
		
		//Billing per App
		System.out.println("TOTAL PER APP :");
		float totalOfTotalInUserCurrency = (float) 0.0;
		for (Entry<String, Float> entry : TotalEarningInUserCurrencyMap.entrySet()){
			System.out.println(entry.getKey()+" -> "+entry.getValue()+" "+Parameters.USER_CURRENCY_CODE);
			totalOfTotalInUserCurrency = totalOfTotalInUserCurrency + entry.getValue();
		}
		System.out.println("TOTAL OF ALL :"+totalOfTotalInUserCurrency+" "+Parameters.USER_CURRENCY_CODE);
		
		
		//Test
//		float totalOfTotalInUserCurrency2 = (float) 0.0;
//		for (EarningElement earningElement : earningElementList) {
//			totalOfTotalInUserCurrency2 = totalOfTotalInUserCurrency2 + earningElement.getTotalEarningInUserCurrency();
//		}
//		System.out.println("TOTAL OF ALL (BIS) :"+totalOfTotalInUserCurrency2+" "+Parameters.USER_CURRENCY_CODE+" (addition of each country's total, without splitting into apps)");
		
	  }

}
