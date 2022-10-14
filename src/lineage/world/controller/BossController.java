package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lineage.bean.database.Boss;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.object.instance.MonsterInstance;

public final class BossController {

	static private List<MonsterInstance> list;

	static private Calendar calendar;
	static public void init(){
		TimeLine.start("BossController..");
		
		list = new ArrayList<MonsterInstance>();
		calendar = Calendar.getInstance();
		
		TimeLine.end();
	}
	
	static public void toTimer(long time){
		
		List<Boss> list = MonsterBossSpawnlistDatabase.getList();
		if(list.size() > 0){
			// 현재 리니지 시간.
//			int hour = ServerDatabase.getLineageTimeHour();
//			int min = ServerDatabase.getLineageTimeMinute();
			calendar.setTimeInMillis(time);
			Date date = calendar.getTime();
			int hour = date.getHours();
			int min = date.getMinutes();
			// 스폰할 보스 루프.
			for(Boss b : list){
				// 버그 방지
				if(b.getMap().size()<=0)
					continue;
				
				if(b.isSpawnTime(hour, min) && isSpawn(b)==false){
					// 객체 생성.
					MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(b.getMon());
					if(mi != null){
						BossController.list.add(mi);
						// 정보 갱신.
						mi.setBoss(true);
						mi.setHomeX(b.getX());
						mi.setHomeY(b.getY());
						mi.setHomeMap(b.getMap().get(Util.random(0, b.getMap().size()-1)));
						mi.toTeleport(mi.getHomeX(), mi.getHomeY(), mi.getHomeMap(), false);
						mi.readDrop();
						// 인공지능쓰레드에 등록.
						AiThread.append(mi);
					}
				}
			}
		}
	}
	
	/**
	 * 현재 스폰된 상태인지 확인해주는 함수.
	 * @param b
	 * @return
	 */
	static public boolean isSpawn(Boss b){
		for(MonsterInstance mi : list){
			if(mi.getMonster() == b.getMon())
				return true;
		}
		return false;
	}
	
	/**
	 * boss변수가 true인 객체들은 월드에서 사라질때 이 함수가 호출됨.
	 * @param mi
	 */
	static public void toWorldOut(MonsterInstance mi){
		list.remove(mi);
	}
}
