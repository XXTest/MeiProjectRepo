package com.xuemeixi.project.domain;

import java.util.Date;


public class Transaction 
{
	public Transaction(long transactionId, Date orderDate, long orderQuantity,
			double price, double cost) 
	{
		TransactionId = transactionId;
		OrderDate = orderDate;
		OrderQuantity = orderQuantity;
		Price = price;
		Cost = cost;
	}
	
	public Transaction() {
		TransactionId = 0;
		OrderDate = new Date(0);
		OrderQuantity = 0;
		Price = 0;
		Cost = 0;
	}

	private long TransactionId;
	private Date OrderDate;
	private long OrderQuantity;
	private double Price;
	private double Cost;
	
	public long getTransactionId() {
		return TransactionId;
	}
	public void setTransactionId(long transactionId) {
		TransactionId = transactionId;
	}
	public Date getOrderDate() {
		return OrderDate;
	}
	public void setOrderDate(Date orderDate) {
		OrderDate = orderDate;
	}
	public long getOrderQuantity() {
		return OrderQuantity;
	}
	public void setOrderQuantity(long orderQuantity) {
		OrderQuantity = orderQuantity;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	public double getCost() {
		return Cost;
	}
	public void setCost(double cost) {
		Cost = cost;
	}
}
