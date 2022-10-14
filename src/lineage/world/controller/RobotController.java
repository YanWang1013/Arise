package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.share.Lineage;
import lineage.world.object.robot.BuffRobotInstance;
import lineage.world.object.robot.PcRobotInstance;

public class RobotController {

	// 버프사 관리 목록.
	static private List<BuffRobotInstance> list_buff;
	// 무인케릭 관리 목록.
	static private List<PcRobotInstance> list_pc;
	// 스폰된 로봇 개체수
	static public int count;
	
	static public void init(){
		// 자동버프 객체 초기화.
		list_buff = new ArrayList<BuffRobotInstance>();
		for(int i=0 ; i<1 ; ++i)
			list_buff.add( new BuffRobotInstance(33449, 32829, 4, 6, "자동버프", 734) );
		// 무인케릭 객체 초기화.
		list_pc = new ArrayList<PcRobotInstance>();
		for(int i=0 ; i<Lineage.robot_auto_pc_count ; ++i)
			list_pc.add( new PcRobotInstance() );
		
		if(Lineage.robot_auto_buff)
			toStartBuff();
		if(Lineage.robot_auto_pc)
			toStartPc();
	}
	
	static public void close(){
		//
	}
	
	static public void toTimer(long time){
		for(BuffRobotInstance bri : list_buff)
			bri.toTimer(time);
	}
	
	/**
	 * 자동 버프사 시작 처리 함수.
	 */
	static public void toStartBuff(){
		for(BuffRobotInstance bri : list_buff)
			bri.toWorldJoin();
		
		count += list_buff.size();
	}
	
	/**
	 * 자동 버프사 종료 처리 함수.
	 */
	static public void toStopBuff(){
		for(BuffRobotInstance bri : list_buff)
			bri.toWorldOut();
		
		count -= list_buff.size();
	}
	
	/**
	 * 무인 케릭 시작 처리 함수.
	 */
	static public void toStartPc(){
		//
	}
	
	/**
	 * 무인 케릭 종료 처리 함수.
	 */
	static public void toStopPc(){
		//
	}
}
