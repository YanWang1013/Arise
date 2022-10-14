package lineage;

import java.util.Random;

public class FakeUserCount extends Thread{//extends TimerTask{
	private static FakeUserCount _instance;	
	public int gmFakeUserCount = 0;
	public int fakeUserCount = 0;
	private Random _random = new Random();
	
	public static FakeUserCount getInstance() {
		if (_instance == null) {
			_instance = new FakeUserCount();
		}
		return _instance;
	}
	
	public FakeUserCount(){
		this.start();
	}
	
	public void run() {
		while(true){
			try{
				if(gmFakeUserCount > fakeUserCount){
					fakeUserCount+= _random.nextInt(2);
					if(gmFakeUserCount < fakeUserCount){
						fakeUserCount = gmFakeUserCount;
					}
				}else if(gmFakeUserCount < fakeUserCount){
					fakeUserCount-= _random.nextInt(2);
					if(gmFakeUserCount > fakeUserCount){
						fakeUserCount = gmFakeUserCount;
					}
				}
				sleep(500L);
			}catch(Exception e){e.printStackTrace();}
		}
	}


}
