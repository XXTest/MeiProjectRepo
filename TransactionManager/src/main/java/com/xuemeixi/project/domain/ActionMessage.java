package com.xuemeixi.project.domain;

public class ActionMessage 
{
    private int StatusCode;
    private String Message;
    
    public ActionMessage() 
    {
		StatusCode = 0;
		Message = "";
	}
    
    
	public ActionMessage(int statusCode, String message) 
	{
		StatusCode = statusCode;
		Message = message;
	}


	public int getStatusCode() 
	{
		return StatusCode;
	}
	
	public void setStatusCode(int statusCode) {
		StatusCode = statusCode;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
     
}
