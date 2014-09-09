package com.xuemeixi.project.MongoDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xuemeixi.project.domain.Transaction;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.xuemeixi.project.domain.Transaction;

public class MongoAccess {

	private MongoClient mongo = null;
	private String dbName = "";
	private String tableName= "";
	
	public MongoAccess(MongoClient mongo, String dbName, String tableName) 
	{
		this.mongo = mongo;
		this.dbName = dbName;
		this.tableName = tableName;
	}


	public List<Transaction> getAllTransactions()
	{
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		if(mongo != null)
		{
			try
			{
				DB db = mongo.getDB(this.dbName);
				DBCollection table = db.getCollection(this.tableName);
				
				List<DBObject> docs = table.find().toArray();
				
				for(DBObject doc : docs)
				{
					long transactionId = ((Number)doc.get("TransactionId")).longValue();
					Date orderDate = (Date) doc.get("OrderDate");
					long orderQuantity = ((Number)doc.get("OrderQuantity")).longValue();
					double price = ((Number)doc.get("Price")).doubleValue();
					double cost = ((Number)doc.get("Cost")).doubleValue();
					
					transactions.add(new Transaction(transactionId, orderDate, orderQuantity, price, cost));
				}
			}
			catch(Exception e)
			{
				transactions.clear();
			}
		}
		
		return transactions;
	}

}
