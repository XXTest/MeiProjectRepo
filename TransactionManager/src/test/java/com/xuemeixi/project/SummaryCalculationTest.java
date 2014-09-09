package com.xuemeixi.project;

import java.util.*;

import com.xuemeixi.project.domain.Transaction;
import com.xuemeixi.project.Helper.SummaryHelper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class SummaryCalculationTest extends TestCase
{
    
    public SummaryCalculationTest( String testName )
    {
        super( testName );
    }

    
    public void testCalculation01()
    {
		ArrayList<Transaction> trans = new ArrayList<Transaction>();
		
		Calendar cal = new GregorianCalendar(2014, 9, 1);
		Date dt0 = cal.getTime();
		
		Transaction tr = new Transaction(1, dt0, 500, 1000, 400);
		Transaction tr2 = new Transaction(2, dt0, 1500, 2000, 800);
		Transaction tr3 = new Transaction(3, dt0, 2500, 2000, 600);
		
		trans.add(tr);
		trans.add(tr2);
		trans.add(tr3);
		
		ArrayList<Transaction> dailyResult = new ArrayList<Transaction>();
		ArrayList<Long> dailyAverage = new ArrayList<Long>();
		Transaction totalResult = new Transaction();
		ArrayList<Long> totalAverage = new ArrayList<Long>();
		
		SummaryHelper.calculate(trans, dailyResult, dailyAverage, totalResult, totalAverage);
		
		boolean countFlag = dailyResult.size() == 1;
		boolean averageFlag = dailyAverage.get(0).longValue() == 1500;
		boolean revenueFlag = dailyResult.get(0).getPrice() == 5000.0;
		boolean costFlag = dailyResult.get(0).getCost() == 1800.0;
		
        assertTrue( "date count is not correct",countFlag );
		assertTrue( "daily average quantity is not correct", averageFlag);
		assertTrue( "daily revenue is not correct", revenueFlag);
		assertTrue( "daily cost is not correct", costFlag);
    }
	
	public void testCalculation02()
	{
		ArrayList<Transaction> trans = new ArrayList<Transaction>();
		
		Calendar cal = new GregorianCalendar(2014, 9, 1);
		Date dt0 = cal.getTime();
		
		cal.add(Calendar.DATE, 1);
		Date dt2 = cal.getTime();
		
		Transaction tr = new Transaction(1, dt0, 500, 1000, 400);
		Transaction tr2 = new Transaction(2, dt2, 1500, 2000, 800);
		Transaction tr3 = new Transaction(3, dt2, 2500, 2000, 600);
		
		trans.add(tr);
		trans.add(tr2);
		trans.add(tr3);
		
		ArrayList<Transaction> dailyResult = new ArrayList<Transaction>();
		ArrayList<Long> dailyAverage = new ArrayList<Long>();
		Transaction totalResult = new Transaction();
		ArrayList<Long> totalAverage = new ArrayList<Long>();
		
		SummaryHelper.calculate(trans, dailyResult, dailyAverage, totalResult, totalAverage);
		
		boolean countFlag = dailyResult.size() == 2;
		boolean quantityFlag = dailyResult.get(1).getOrderQuantity() == 4000;
		boolean averageFlag = dailyAverage.get(1).longValue() == 2000;
		
		assertTrue( "date count should be 2 in this test",countFlag );
		assertTrue( "daily total quantity is not correct",quantityFlag );
		assertTrue( "daily average quantity is not correct",averageFlag );
	}
}
