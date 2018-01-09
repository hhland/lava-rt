package com.uhealin.lava.test.concurrent;

import java.util.Map.Entry;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.uhealin.lava.test.BaseTest;



public class CyclicBarrierTest extends BaseTest{

	
	@Test
	public void testBankWaterRun(){
		BankWaterRun bankWaterRun=new BankWaterRun(4);
		bankWaterRun.count();
		
	}
	
	
	public class BankWaterRun implements Runnable{

		private int size;
		
		public BankWaterRun(int size){
			this.size=size;
			cyclicBarrier=new CyclicBarrier(size,this);
			executor=Executors.newFixedThreadPool(size);
		}
		
		private CyclicBarrier cyclicBarrier;
		
		private Executor executor;
		
		private ConcurrentHashMap<String, Integer> sheetBankWaterCount=new ConcurrentHashMap<String,Integer>();
		
		private void count(){
			for (int i = 0; i < size; i++) {
				executor.execute(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						sheetBankWaterCount.put(Thread.currentThread().getName(), 50);
						
						try {
							cyclicBarrier.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BrokenBarrierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result=0;
			
			for(Entry<String, Integer> sheet: sheetBankWaterCount.entrySet()){
				result+=sheet.getValue();
			}
			sheetBankWaterCount.put("result", result);
			println(result);
			
		}
		
	}
	
}
