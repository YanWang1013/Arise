package lineage.world.object.instance;

import lineage.bean.database.Npc;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.controller.CharacterController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.npc.guard.ElvenGuard;

public class GuardInstance extends CraftInstance {

	private AStar aStar;					// 길찾기 변수
	private int[][] iPath;				// 길찾기 변수
	private Node tail;					// 길찾기 변수
	private int iCurrentPath;				// 길찾기 변수
	
	public GuardInstance(Npc npc){
		super(npc);
		iPath = new int[300][2];
		aStar = new AStar();
		// 자연회복 관리목록에 등록.
		CharacterController.toWorldJoin(this);
	}
	
	@Override
	public void close(){
		super.close();
		// 자연회복 제거
		CharacterController.toWorldOut(this);
	}
	
	/**
	 * pk 유저 찾는 함수.
	 */
	protected void toSearchPKer(){
		for(object o : insideList){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(isAttack(pc, true) && pc.getPkTime()>0)
					addAttackList(pc);
			}
		}
	}

	@Override
	public void toDamage(Character cha, int dmg, int type){
		super.toDamage(cha, dmg, type);
		// 주변 경비병에게도 알리기.
		for(object o : insideList){
			if(o instanceof GuardInstance)
				((GuardInstance) o).addAttackList(cha);
		}
	}

	// Felix : Ignore if cha is 183 (Dupelgenon)
	@Override
	public void addAttackList(object o){
		if(o.getGfx() == 183 && o.isQuestMonster())
			return;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// 요숲 경비병들은 별개 구분 처리.
		if(this instanceof ElvenGuard && attackList.size()==0){
			super.toTalk(pc, cbp);
			return;
		}
		// 할거 없음.
	}

	/**
	 * 매개변수 좌표로 A스타를 발동시켜 이동시키기.
	 * 객체가 존재하는 지역은 패스하도록 함.
	 * 이동할때마다 aStar가 새로 그려지기때문에 과부하가 심함.
	 */
	@Override
	public void toMoving(final int x, final int y, final int h){
		aStar.cleanTail();
		tail = aStar.searchTail(this, x, y, true);
		if(tail != null){
			iCurrentPath = -1;
			while(tail != null){
				if(tail.x==getX() && tail.y==getY()){
					// 현재위치 라면 종료
					break;
				}
				iPath[++iCurrentPath][0] = tail.x;
				iPath[iCurrentPath][1] = tail.y;
				tail = tail.prev;
			}
			super.toMoving(iPath[iCurrentPath][0], iPath[iCurrentPath][1], Util.calcheading(this.x, this.y, iPath[iCurrentPath][0], iPath[iCurrentPath][1]));
		}else{
			// 뭘하면 좋을가..
		}
	}
	
	@Override
	public void toAiAttack(long time){
		// 공격자 확인.
		object o = null;
		// 일정확율로 타켓 변경.
		if(Util.random(0, 3) == 0)
			o = getAttackList( Util.random(0, attackList.size()-1) );
		else
			o = getAttackList(0);
		// 2거리 이상은 모두 활공격으로 판단.
		boolean bow = npc.getAtkRange() > 2;
		// 객체 거리 확인
		if(Util.isDistance(this, o, npc.getAtkRange()) && Util.isAreaAttack(this, o)){
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_ATTACK);
			// 객체 공격
			toAttack(o, 0, 0, bow, gfxMode+Lineage.GFX_MODE_ATTACK, 0);
		}else{
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
			// 객체 이동
			toMoving(o.getX(), o.getY(), 0);
		}
	}
}
