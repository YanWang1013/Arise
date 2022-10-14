package lineage.world.object.robot;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Trade;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.RobotController;
import lineage.world.controller.TradeController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.BurningWeapon;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.IronSkin;

public class BuffRobotInstance extends RobotInstance {
	
	// 타이머 처리에 사용되는 함수. 멘트표현에 딜레이를 주기 위해. 
	private long time_ment;
	// 홍보 멘트 목록
	private List<String> list_ment;
	// 현재 표현된 홍보문구 위치 확인용.
	private int idx_ment;
	// 거래가 요청된 시간에서 10초 정도 더 늘린값. 타이머함수에서 해당 시간이 오바되면 자동 거래취소되게 하기위해.
	private long time_trade;
	// 같이 버프시전할 객체들
	private List<BuffRobotInstance> list;
	
	public BuffRobotInstance(){
		//
	}
	
	public BuffRobotInstance(int x, int y, int map, int heading, String name, int gfx){
		this.objectId = ServerDatabase.nextEtcObjId();
		this.x = x;
		this.y = y;
		this.map = map;
		this.heading = heading;
		this.name = name;
		this.gfx = gfx;
		
		// 보조 케릭
		list = new ArrayList<BuffRobotInstance>();
		for(int i=1 ; i<5 ; ++i){
			BuffRobotInstance o = new BuffRobotInstance();
			o.setObjectId(ServerDatabase.nextEtcObjId());
			o.setX(x + i);
			o.setY(y);
			o.setMap(map);
			o.setHeading(heading);
			o.setGfx( i<3 ? (Util.random(0, 1)==0 ? 1186 : 734) : (Util.random(0, 1)==0 ? 37 : 138) );
			o.setName(String.format("%s%d", name, i));
			list.add(o);
		}
		
		// 멘트 목록.
		list_ment = new ArrayList<String>();
		list_ment.add(String.format("버프 단돈 %d냥~!!", Lineage.robot_auto_buff_aden));
		list_ment.add("행복한 버프 받고 열렙하세요~");
		list_ment.add("자동버프중 앞에서 어택해주세요.");
	}
	
	@Override
	public void toWorldJoin(){
		super.toWorldJoin();
		
		for(object o : list)
			o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
		
		RobotController.count += list.size();
	}
	
	@Override
	public void toWorldOut(){
		super.toWorldOut();
		
		for(object o : list){
			o.clearList(true);
			World.remove(o);
		}
		
		RobotController.count -= list.size();
	}
	
	@Override
	public int getGm(){
		return 1;
	}
	
	@Override
	public void setNowHp(int nowhp){}
	
	@Override
	public void toDamage(Character cha, int dmg, int type){
		// 교환 요청.
		TradeController.toTrade(this, cha);
		// 교환창에 아이템등록까지 대기시간 설정.
		time_trade = System.currentTimeMillis() + (1000 * 10);
	}
	
	@Override
	public void toTradeCancel(Character cha){
		//
	}
	
	@Override
	public void toTradeOk(Character cha){
		// 헤이스트
		Haste.init(this, SkillDatabase.find(6, 2), cha.getObjectId());
		// 블레스 웨폰
		BlessWeapon.init(list.get(0), SkillDatabase.find(6, 7), cha.getObjectId());
		// 풀 힐
		Heal.init(list.get(1), SkillDatabase.find(8, 0), cha.getObjectId());
		// 버닝 웨폰
		BurningWeapon.init(list.get(2), SkillDatabase.find(21, 2), cha.getObjectId());
		// 아이언 스킨
		IronSkin.init(list.get(3), SkillDatabase.find(21, 7), cha.getObjectId());
	}
	
	@Override
	public void toTimer(long time){
		// 거래에 등록된 아이템 확인.
		Trade t = TradeController.find(this);
		if(t!=null && t.isAden(false, Lineage.robot_auto_buff_aden)){
			// 거래 승인 요청.
			TradeController.toTradeOk(this);
		}
		// 거래요청된 상태에서 10초가 지낫을경우 자동 취소처리.
		if(time_trade!=0 && time_trade-time<=0){
			time_trade = 0;
			// 거래 취소 요청.
			TradeController.toTradeCancel(this);
		}
		// 멘트 처리 구간.
		if(time_ment-time <= 0){
			// 5초마다 홍보하기.
			time_ment = time + (1000 * 5);
			// 표현할 문구 위치값이 오바됫을경우 초기화.
			if(idx_ment >= list_ment.size())
				idx_ment = 0;
			// 홍보문구 표현.
			ChattingController.toChatting(this, list_ment.get(idx_ment++), Lineage.CHATTING_MODE_NORMAL);
		}
	}
	
}
