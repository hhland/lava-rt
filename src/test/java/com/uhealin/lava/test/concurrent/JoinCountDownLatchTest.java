package com.uhealin.lava.test.concurrent;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.uhealin.lava.test.BaseTest;

public class JoinCountDownLatchTest  extends BaseTest{

	static CountDownLatch countDownLatch=new CountDownLatch(2);
	
	
	@Test
	public void testJoin() throws InterruptedException{
		Thread parse1=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				println("parse1");
			}
		})
		,parse2=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				println("parse2");
			}
		})		
		;
		
		parse1.start();
		parse2.start();
		parse1.join();
		parse2.join();
		println("parse all");
		
	}
	
	
	@Test
	public void testCountDownLatch() throws InterruptedException{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				println(1);
				countDownLatch.countDown();
				println(2);
				countDownLatch.countDown();
			}
		}).start();
		countDownLatch.await();
		println(3);
	}
	
}
