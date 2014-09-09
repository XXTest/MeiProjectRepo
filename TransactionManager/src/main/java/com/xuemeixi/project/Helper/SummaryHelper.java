package com.xuemeixi.project.Helper;

import java.text.SimpleDateFormat;
import java.util.*;

import com.xuemeixi.project.domain.Transaction;
import com.xuemeixi.project.domain.Transactiondata;

public class SummaryHelper 
{
	public static void calculate(List<Transaction> ts, ArrayList<Transaction> td, ArrayList<Long> dailyAverage, Transaction tt, ArrayList<Long> totalAverage)
	{
		if(ts == null || ts.isEmpty())
		{
			return;
		}
		
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
		String dateKey = "";
		
		ArrayList<Date> da = new ArrayList<Date>(1024);
		Hashtable<String, Transaction> htable = new Hashtable<String, Transaction>(1024);
		
		long num = 0;
		long quantity =0;
		double revenue = 0;
		double cost = 0;
		
		for(Transaction t : ts)
		{
			// step 1: find same date record in ArrayList<Date>
			boolean hasFound = false;
			int rindex = -1;
			int result = 0;
			
			for(int j = 0; j < da.size(); j++)
			{
				result = t.getOrderDate().compareTo(da.get(j));
				
				if(result == 0) // find match case
				{
					rindex = j;
					hasFound = true;
					break;
				}
				
				if(result < 0) // no match and does not need to go further
				{
					rindex = j;
					hasFound = false;
					break;
				}
			}
			
			if(rindex == -1) // new date
			{
				da.add(t.getOrderDate());				
				dateKey = df.format(t.getOrderDate());				
				Transaction dateRecord = new Transaction(1, t.getOrderDate(), t.getOrderQuantity(), t.getPrice(), t.getCost());			
				htable.put(dateKey, dateRecord);
			}
			else
			{
				if(hasFound == true)
				{
					dateKey = df.format(t.getOrderDate());
					
					Transaction tv = htable.get(dateKey);
					
					num = tv.getTransactionId(); num++; 
					tv.setTransactionId(num);
					
					quantity = t.getOrderQuantity() + tv.getOrderQuantity(); 
					tv.setOrderQuantity(quantity);
					
					revenue = t.getPrice() + tv.getPrice();
					tv.setPrice(revenue);
					
					cost = t.getCost() + tv.getCost();
					tv.setCost(cost);
				}
				else
				{
					da.add(rindex, t.getOrderDate());				
					dateKey = df.format(t.getOrderDate());				
					Transaction dateRecord = new Transaction(1, t.getOrderDate(), t.getOrderQuantity(), t.getPrice(), t.getCost());			
					htable.put(dateKey, dateRecord);
				}
			}
			
		}
		
		num = 0;
		quantity =0;
		revenue = 0;
		cost = 0;
		
		double average = 0;
		long ct = 0;
		long dq = 0;
		
		for(int k = 0; k < da.size(); k++)
		{
			//Note: here the TransactionId field is used to store the number of transactions on the date
			dateKey = df.format(da.get(k));		
			Transaction tv = htable.get(dateKey);
			
			Transaction tn = new Transaction(tv.getTransactionId(), tv.getOrderDate(), tv.getOrderQuantity(), tv.getPrice(), tv.getCost());
			td.add(tn);
			
			average = (double) tn.getOrderQuantity(); // daily total quantity
			ct = tn.getTransactionId(); // daily total number of transactions
			dq = Math.round(average/ct);
			dailyAverage.add(new Long(dq));
			
			num = num + tn.getTransactionId();
			quantity = quantity + tn.getOrderQuantity();
			revenue = revenue + tn.getPrice();
			cost = cost + tn.getCost();
		}
		
		tt.setTransactionId(num);
		tt.setOrderQuantity(quantity);
		tt.setPrice(revenue);
		tt.setCost(cost);
		
		average = (double) tt.getOrderQuantity();
		ct = tt.getTransactionId();
		dq = Math.round(average/ct);
		totalAverage.add(new Long(dq));
	}
}
