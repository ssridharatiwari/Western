package com.milk.milkcollectionapp.utils.apicalling;

public interface WebServiceListener {
	public void onWebServiceActionComplete(String result, String url);
	public void onWebServiceError(String result, String url);
}
