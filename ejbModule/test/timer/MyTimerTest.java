package test.timer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
//@Startup
public class MyTimerTest {

	@Inject private MyTimer myTimer1;
	@Inject private MyTimer myTimer2;
	
	
	@PostConstruct
	public void run() {
		String message1 = "Test1";
		String message2 = "Test2";
		myTimer1.initTimer(message1,10000L);
		myTimer2.initTimer(message2,5000L);
	}
}
