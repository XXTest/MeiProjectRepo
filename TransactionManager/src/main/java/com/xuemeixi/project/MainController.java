package com.xuemeixi.project;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
// import com.mongodb.MongoException;
import com.xuemeixi.project.domain.Transaction;
import com.xuemeixi.project.domain.ActionMessage;
import com.xuemeixi.project.domain.Transactiondata;
import com.xuemeixi.project.MongoDB.MongoAccess;
import com.xuemeixi.project.Helper.SummaryHelper;

@Controller
public class MainController 
{
	private static String BasicErrorMessage = "";
	
	private static AtomicLong seedNumber= new AtomicLong(1);
	
    private static MongoClient mongo;	
    private static String MongoDBName = "testdb";
    private static String MongoDBCollectionName = "transaction";

    static
	{	
    	long newSeedNumber = 0;
    	
		try
		{
   			mongo = new MongoClient("localhost", 27017);
   			
   			newSeedNumber = getSeed(); newSeedNumber++;
   			
   			newSeedNumber = seedNumber.getAndSet(newSeedNumber);
		}
		catch(Exception e)
		{
			mongo = null;
			BasicErrorMessage = "DB access operation error.";
		}
	}
      
    private MongoAccess mongoAccess = new MongoAccess(mongo,MongoDBName, MongoDBCollectionName);
    
    
	@RequestMapping(value="/", method=RequestMethod.GET)
    public String Greeting() 
	{
        return "greeting";        
    }
	
    @RequestMapping(value="/start", method=RequestMethod.GET)
    public String Greeting2() 
	{
        return "greeting";
    }
    
    @RequestMapping(value="/addtransaction",method=RequestMethod.GET)
    public String AddTransactionGet(Model model) 
	{
    	model.addAttribute("transactiondata", new Transactiondata());
        return "addtransaction";
    }
    
    @RequestMapping(value="/addtransaction", method=RequestMethod.POST)
    public String AddTransactionPost(@ModelAttribute Transactiondata transactiondata, Model model) 
	{
    	int result = 1;
    	String msg = "Empty transaction.";
    	
    	Date dt = new Date(0);
    	long quantity = 0;
    	double price = 0;
    	double cost = 0;
    	
    	if(transactiondata != null)
    	{
    		try
    		{
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    			dt = formatter.parse(transactiondata.getS1());
    			
    			quantity = Long.parseLong(transactiondata.getS2());
    			
    			price = Double.parseDouble(transactiondata.getS3());
    			
    			BigDecimal bd = new BigDecimal(price);
    			bd = bd.setScale(2, RoundingMode.HALF_UP);
    			price = bd.doubleValue();
    			
    			cost = Double.parseDouble(transactiondata.getS4());
    			BigDecimal bd2 = new BigDecimal(cost);
    			bd2 = bd2.setScale(2, RoundingMode.HALF_UP);
    			cost = bd2.doubleValue();
    		}
    		catch(Exception e)
    		{
    			result = -2;
    			msg = "Data conversion error.";
    			return "addtransaction";
    		}
    		
    		Transaction tran = new Transaction(seedNumber.getAndIncrement(), dt, quantity, price, cost);
	        result = this.insertOneTransaction(tran);
	        
	        msg = "Success";
	        if(result < 0)
	        {
	        	msg = "Failure";
	        }
    	}
        
    	ActionMessage actionMessage = new ActionMessage(result, msg);
    	model.addAttribute("transactiondata", transactiondata);
    	// model.addAttribute("actionmessage", actionMessage);
        //return "addtransaction";
    	return "greeting";
    }

    @RequestMapping(value="/load")
    public String LoadTransactionData(Model model) 
	{
		model.addAttribute("transactions", getAllTransactionData());
        return "load";
    }
    
    @RequestMapping(value="/summary")
    public String LoadSummary(Model model) 
	{
    	ArrayList<Transactiondata> daySummary = new ArrayList<Transactiondata>();
    	Transactiondata totalSummary = new Transactiondata();
    	
    	getSummary(daySummary, totalSummary);
		model.addAttribute("dailyrecords", daySummary);
		model.addAttribute("sumup", totalSummary);
        return "summary";
    }
    
    private void insertTestDocuments()
    {
    	long seq;
    	Date dt;
    	long quantity = 100;
    	double price = 1000;
    	double cost = 500;
    	
    	long m = 31536000000L;
    	
    	int result;
    	
    	List<Transaction> trans = new ArrayList<Transaction>();
    	
    	for(int i =0; i<5; i++)
    	{
    		seq = seedNumber.getAndIncrement();
    		dt = new Date(m*(i+1));
    		quantity = quantity + i*100;
    		price = price + i*1000;
    		cost = cost + i*500;
    		Transaction tr = new Transaction(seq, dt, quantity, price, cost);
    		trans.add(tr);
    	}
    	
    	result = insertAllTransactions(trans);
    }
	
	private List<Transaction> getTestTransactions()
	{
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		transactions.add(new Transaction(1, new Date(), 100, 500, 100.01));
		transactions.add(new Transaction(2, new Date(), 200, 1000, 200.25));
		transactions.add(new Transaction(3, new Date(), 300, 1500, 300.55));
		
		return transactions;
	}
	
	private int insertAllTransactions(List<Transaction> transactions)
	{
		if(mongo == null)
		{
			return -1;
		}
		
		if(transactions == null || transactions.isEmpty())
		{
			return 1;
		}
		
		if(mongo != null)
		{
			try
			{
				DB db = mongo.getDB("testdb");
				DBCollection table = db.getCollection("transaction");
				
				List<DBObject> docList = new ArrayList<DBObject>();
				
				for(Transaction transaction : transactions)
				{
					DBObject document = new BasicDBObject("TransactionId", transaction.getTransactionId())
					.append("OrderDate", transaction.getOrderDate())
					.append("OrderQuantity", transaction.getOrderQuantity())
					.append("Price", transaction.getPrice())
					.append("Cost", transaction.getCost());
					
					docList.add(document);
				}
				
				table.insert(docList);
			}
			catch(Exception e)
			{
				return -1;
			}
		}
		
		return 0;
	}
	
	private int insertOneTransaction(Transaction transaction)
	{
		if(mongo == null)
		{
			return -1;
		}
		
		if(transaction == null)
		{
			return 1;
		}
		
		int result = 0;
		if(mongo != null)
		{
			try
			{
				DB db = mongo.getDB("testdb");
				DBCollection table = db.getCollection("transaction");
				
				DBObject document = new BasicDBObject("TransactionId", transaction.getTransactionId())
										.append("OrderDate", transaction.getOrderDate())
										.append("OrderQuantity", transaction.getOrderQuantity())
										.append("Price", transaction.getPrice())
										.append("Cost", transaction.getCost());
				table.insert(document);
			}
			catch(Exception e)
			{
				return -1;
			}
		}
		
		return result;
	}
	
	private void getSummary(ArrayList<Transactiondata> daySummary, Transactiondata totalSummary)
	{
		List<Transaction> trans = mongoAccess.getAllTransactions();
		
		if(trans == null || trans.size() <1)
		{
			return;
		}
		
		ArrayList<Transaction> dailyResult = new ArrayList<Transaction>();
		ArrayList<Long> dailyAverage = new ArrayList<Long>();
		Transaction totalResult = new Transaction();
		ArrayList<Long> totalAverage = new ArrayList<Long>();
		
		SummaryHelper.calculate(trans, dailyResult, dailyAverage, totalResult, totalAverage);
		
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
		
		Transaction tv;
		
		long num;
		long quantity;
		long average;
		double revenue;
		double cost;
		
		long value = 0;
		
		String dateValue = "";
		String numValue = "";
		String quantityValue = "";
		String averageValue = "";
		String revenueValue = "";
		String costValue = "";
		
		for(int i = 0; i < dailyResult.size(); i++)
		{
			Transactiondata td = new Transactiondata();
			
			tv = dailyResult.get(i);
			
			dateValue = df.format(tv.getOrderDate());
			td.setS1(dateValue);
			
			numValue = Long.toString(tv.getTransactionId());
			td.setS2(numValue);
			
			quantityValue = Long.toString(tv.getOrderQuantity());
			td.setS3(quantityValue);
			
			averageValue = dailyAverage.get(i).toString();
			td.setS4(averageValue);
			
			revenue = tv.getPrice();
			BigDecimal bd = new BigDecimal(revenue);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			revenue = bd.doubleValue();
			revenueValue = "$" + Double.toString(revenue);
			td.setS5(revenueValue);
			
			cost = tv.getCost();
			BigDecimal bd2 = new BigDecimal(cost);
			bd2 = bd2.setScale(2, RoundingMode.HALF_UP);
			cost = bd2.doubleValue();
			costValue = "$" + Double.toString(cost);	
			td.setS6(costValue);
			
			daySummary.add(td);
		}
		

		//dateValue = df.format(totalResult.getOrderDate());
		//totalSummary.setS1(dateValue);
		
		numValue = Long.toString(totalResult.getTransactionId());
		totalSummary.setS2(numValue);
		
		quantityValue = Long.toString(totalResult.getOrderQuantity());
		totalSummary.setS3(quantityValue);
		
		averageValue = totalAverage.get(0).toString();
		totalSummary.setS4(averageValue);
		
		revenue = totalResult.getPrice();
		BigDecimal bdc = new BigDecimal(revenue);
		bdc = bdc.setScale(2, RoundingMode.HALF_UP);
		revenue = bdc.doubleValue();
		revenueValue = "$" + Double.toString(revenue);
		totalSummary.setS5(revenueValue);
		
		cost = totalResult.getCost();
		BigDecimal bdc2 = new BigDecimal(cost);
		bdc2 = bdc2.setScale(2, RoundingMode.HALF_UP);
		cost = bdc2.doubleValue();
		costValue = "$" + Double.toString(cost);
		totalSummary.setS6(costValue);
	}
	
	private List<Transactiondata> getAllTransactionData()
	{
		List<Transactiondata> data = new ArrayList<Transactiondata>();
		
		List<Transaction> trans = mongoAccess.getAllTransactions();
		
		String s1;
		String s2;
		String s3;
		String s4;
		String s5;
		
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			for(Transaction tran : trans)
			{
				s1 = Long.toString(tran.getTransactionId());
				s2 = df.format(tran.getOrderDate());
				s3 = Long.toString(tran.getOrderQuantity());
				s4 = "$" + Double.toString(tran.getPrice());
				s5 = "$" + Double.toString(tran.getCost());
				
				data.add(new Transactiondata(s1, s2, s3, s4, s5));
			}
		}
		catch(Exception e)
		{
			trans.clear();
		}
		
		return data;
	}

	private static long getSeed()
	{
		long newSeedNumber = 0;
		
		if(mongo != null)
		{
			try
			{
				DB db = mongo.getDB("testdb");
				DBCollection table = db.getCollection("transaction");
				
				List<DBObject> docs = table.find().toArray();
				
				for(DBObject doc : docs)
				{
					long transactionId = ((Number)doc.get("TransactionId")).longValue();
					
					if(transactionId > newSeedNumber) 
					{
						newSeedNumber = transactionId;
					}
				}
			}
			catch(Exception e)
			{
				return newSeedNumber;
			}
		}
		
		return newSeedNumber;
	}
}
