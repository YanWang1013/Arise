package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Drop;
import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSkill;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.Inventory;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.BossController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.DamageController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.PartyController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.SummonInstance.SUMMON_MODE;
import lineage.world.object.item.potion.CurePoisonPotion;
import lineage.world.object.item.potion.GreaterHastePotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.magic.monster.ShareMagic;

public class MonsterInstance extends Character {

	protected Monster mon;					// 
	private MonsterSpawnlist monSpawn;		// 
	private AStar aStar;					// 길찾기 변수
	private int[][] iPath;				// 길찾기 변수
	private Node tail;					// 길찾기 변수
	private int iCurrentPath;				// 길찾기 변수
	private double add_exp;					// hp1당 지급될 경험치 값.
	private double dangerous_exp;			// 공격자 우선처리를위한 변수로 경험치의 70% 값을 저장.
	protected List<object> attackList;		// 전투 목록
	protected List<object> astarList;		// astar 무시할 객체 목록.
	protected List<Exp> expList;		// 경험치 지급해야할 목록
	private int ai_walk_stay_count;			// 랜덤워킹 중 잠시 휴식을 취하기위한 카운팅 값
	private int reSpawnTime;				// 재스폰 하기위한 대기 시간값.
	protected boolean boss;					// 보스 몬스터인지 여부. monster_spawnlist_boss 를 거쳐서 스폰도니것만 true가 됨.
	// 시체유지(toAiCorpse) 구간에서 사용중.
	// 재스폰대기(toAiSpawn) 구간에서 사용중.
	private long ai_time_temp_1;
	// 인벤토리
	protected Inventory inv;
	// 그룹몬스터에 사용되는 변수.
	private List<MonsterInstance> group_list;	// 현재 관리되고있는 몬스터 목록.
	private MonsterInstance group_master;		// 나를 관리하고있는 마스터객체 임시 저장용.
	// 동적 변환 값
	protected int dynamic_attack_area;			// 공격 범위 값.

	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new MonsterInstance();
		mi.setMonster(m);
		mi.setAddExp( (double)m.getExp() / (double)m.getHp() );
		mi.setDangerousExp( m.getExp() * 0.7 );
		return mi;
	}

	public MonsterInstance(){
		iPath = new int[300][2];
		aStar = new AStar();
		astarList = new ArrayList<object>();
		attackList = new ArrayList<object>();
		expList = new ArrayList<Exp>();
		group_list = new ArrayList<MonsterInstance>();
		// 인벤토리 활성화를 위해.
		InventoryController.toWorldJoin(this);
	}

	@Override
	public void close(){
		super.close();

		classType= Lineage.LINEAGE_CLASS_MONSTER;
		ai_time_temp_1 = reSpawnTime = dynamic_attack_area = 0;
		boss = false;
		monSpawn = null;
		group_master = null;
		if(attackList != null)
			attackList.clear();
		if(astarList != null)
			astarList.clear();
		if(expList != null){
			for(Exp e : expList)
				ExpDatabase.setPool(e);
			expList.clear();
		}
		if(inv != null){
			for(ItemInstance ii : inv.getList()){
				ItemDatabase.setPool(ii);
			}
			inv.getList().clear();
		}
		if(group_list != null)
			group_list.clear();
		CharacterController.toWorldOut(this);
	}

	public void setMonsterSpawnlist(MonsterSpawnlist monSpawn){
		this.monSpawn = monSpawn;
	}

	public MonsterSpawnlist getMonsterSpawnlist(){
		return monSpawn;
	}

	public void setAiTimeTemp1(long ai_time_temp_1) {
		this.ai_time_temp_1 = ai_time_temp_1;
	}

	public long getAiTimeTemp1(){
		return ai_time_temp_1;
	}

	public MonsterInstance getGroupMaster() {
		return group_master;
	}

	public void setGroupMaster(MonsterInstance group_master) {
		this.group_master = group_master;
	}

	/**
	 * 그룹에 속한 몬스터중 마스터를 찾아 리턴한다.
	 *  : 실제 마스터가 죽엇을경우 그룹에 속한 목록에서 살아있는 놈을 마스터로 잡는다.
	 *  : 모두 죽거나 하면 null을 리턴.
	 * @return
	 */
	public MonsterInstance getGroupMasterDynamic(){
		if(group_master!=null){
			if(group_master.isDead()){
				for(MonsterInstance mi : group_master.getGroupList()){
					if(!mi.isDead())
						return mi;
				}
			}else{
				return group_master;
			}
		}
		return null;
	}

	public List<MonsterInstance> getGroupList() {
		return group_list;
	}

	@Override
	public Inventory getInventory(){
		return inv;
	}

	@Override
	public void setInventory(Inventory inv){
		this.inv = inv;
	}

	public int getReSpawnTime() {
		return reSpawnTime;
	}

	@Override
	public void setReSpawnTime(int reSpawnTime) {
		this.reSpawnTime = reSpawnTime;
	}

	@Override
	public int getHpTime() {
		return Lineage.ai_monster_tic_time;
	}

	@Override
	public int hpTic(){
		return mon.getTicHp();
	}

	@Override
	public int mpTic(){
		return mon.getTicMp();
	}

	public void setAddExp(double add_exp) {
		this.add_exp = add_exp;
	}

	public void setDangerousExp(double dangerous_exp){
		this.dangerous_exp = dangerous_exp;
	}

	public void setMonster(Monster m){
		this.mon = m;
		// 시전되는 스킬목록에서 거리범위가 세팅된게 잇을경우 그것으로 거리변경함.
		// 이렇게 해야 몬스터 인공지능 발동시 공격거리가 최상위로 잡혀서 리얼해짐.
		for(MonsterSkill ms : m.getSkillList()){
			if(ms.getRange()>0 && dynamic_attack_area<ms.getRange())
				dynamic_attack_area = ms.getRange();
		}
		// 틱값이 존재한다면 관리를위해 등록.
		if(m.getTicHp()>0 || m.getTicMp()>0)
			CharacterController.toWorldJoin(this);
	}

	public Monster getMonster(){
		return mon;
	}

	protected int getAtkRange(){
		return dynamic_attack_area>0 ? dynamic_attack_area : mon.getAtkRange();
	}

	public boolean isBoss() {
		return boss;
	}

	public void setBoss(boolean boss) {
		this.boss = boss;
	}

	/**
	 * 해당 객체와 연결된 경험치 객체 찾기.
	 * @param o
	 * @return
	 */
	private Exp findExp(object o){
		for(Exp e : expList){
			if(e.getObject()!=null && e.getObject().getObjectId()==o.getObjectId())
				return e;
		}
		return null;
	}

	/**
	 * 몬스터와 전투중인 객체들중 가장 위험수위에 있는 객체를 찾아서 리턴.
	 * 몬스터를 공격한 객체목록(경험치지급목록) 에서 지급된 경험치의 70% 이상값을 가진 사용자가 최우선.
	 * @return
	 */
	protected object findDangerousObject(){
		object o = null;

		// 경험치의 70% 이상값을 가진 사용자 찾기.
		for(Exp e : expList){
			if(e.getObject()!=null && !astarList.contains(e.getObject())){
				// 70% 달하는 경험치 받아가는 놈 발견하면 무조건 공격하기.
				if(e.getExp()>=dangerous_exp)
					return e.getObject();
				// 가장근접한 객체 찾기.
				else if( o == null )
					o = e.getObject();
				else if(Util.getDistance(this, e.getObject())<Util.getDistance(this, o))
					o = e.getObject();
			}
		}
		if(o != null)
			return o;

		// 찾지 못했다면 공격자목록에 등록된 사용자에서 찾기.
		for(object oo : attackList){
			if(!astarList.contains(oo)){
				if( o == null )
					o = oo;
				else if(Util.getDistance(this, oo)<Util.getDistance(this, o))
					o = oo;
			}
		}
		return o;
	}

	@Override
	public void toRevival(object o){
		// 사용자가 부활을 시키는거라면 디비상에 부활가능 할때만 처리.
		if(o instanceof PcInstance && !mon.isRevival())
			return;

		if(dead){
			super.toReset(false);
			// 다이상태 풀기.
			dead = false;
			// 체력 채우기.
			setNowHp(level);
			// 패킷 처리.
			toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), o, this), false);
			// 상태 변경.
			ai_time_temp_1 = 0;
			setAiStatus(Lineage.AI_STATUS_WALK);
		}
	}

	@Override
	public void setNowHp(int nowHp){
		super.setNowHp(nowHp);
		if(!worldDelete && Lineage.monster_interface_hpbar && Lineage.server_version>144){
			for(object o : insideList){
				if(o instanceof PcInstance)
					o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, true));
			}
		}
	}

	/**
	 * 인공지능에 사용된 모든 변수 초기화.
	 * 객체를 스폰되기전 상태로 전환하는 함수.
	 *  : 펫을 길들인후 뒷처리 함수로 사용함.
	 *  : 펫 맡길때 제거용으로도 사용함.
	 *  : 테이밍몬스터후 뒷처리함수로 사용함.
	 * @param packet	: 패킷 처리 할지 여부.
	 */
	public void toAiClean(boolean packet){
		// 인벤토리 제거.
		for(ItemInstance ii : inv.getList()){
			ItemDatabase.setPool(ii);
		}
		inv.getList().clear();
		// 경험치 지급 제거.
		for(Exp e : expList)
			ExpDatabase.setPool(e);
		expList.clear();
		// 전투 관련 변수 초기화.
		attackList.clear();
		// 버프제거
		toReset(true);
		// 객체관리목록 제거.
		clearList(packet);
		World.remove(this);
		// 스폰 준비상태로 변환.
		setAiStatus(Lineage.AI_STATUS_SPAWN);
		ai_time_temp_1 = 0;
	}

	@Override
	public void setDead(boolean dead) {
		super.setDead(dead);
		if(dead){
			ai_time = 0;
			setAiStatus(Lineage.AI_STATUS_DEAD);
		}
	}

	@Override
	public void toDamage(Character cha, int dmg, int type){
//		System.out.print("toDamage>> dmg: " + dmg + " type: " +type + " \n");
		if(cha == null)
			return;

		// 서먼몬스터가 아닐때만.
		if(ai_status!=Lineage.AI_STATUS_ATTACK && summon==null){
			// 전투상태가 아니엿을때 공격받으면 처리하기. 첫전투전환 시점으로 생각하면됨.
			// 일정 확율로 인벤에서 초록물약 찾아서 복용하기.
			// 아직 복용상태가 아닐때만
			if(getSpeed()==0 && Util.random(0, 10)==0){
				ItemInstance ii = getInventory().findDbNameId(234);
				if(ii != null)
					ii.toClick(this, null);
			}
		}
		// 경험치 지급될 목록에 추가.
		if(dmg>0)
			toExp(cha, dmg);
		// 공격목록에 추가.
		addAttackList(cha);
		// 동족인식.
		if(mon.getFamily().length()>0 && group_master==null){
			for(object inside_o : getInsideList()){
				if(inside_o instanceof MonsterInstance && !(inside_o instanceof SummonInstance)){
					MonsterInstance inside_mon = (MonsterInstance)inside_o;
					if(inside_mon.getMonster().getFamily().equalsIgnoreCase(mon.getFamily()) && inside_mon.getGroupMaster()==null)
						inside_mon.addAttackList(cha);
				}
			}
		}
		// 그룹 알림.
		for(MonsterInstance mi : group_list)
			mi.addAttackList(cha);
		if(group_master!=null && group_master.getObjectId()!=getObjectId())
			group_master.toDamage(cha, 0, type);
		// 손상 처리.
		if(mon.isToughskin() && type==Lineage.ATTACK_TYPE_WEAPON){
			ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);
			if(weapon!=null && weapon.getItem().isCanbedmg() && Util.random(0, 100)<10){
				weapon.setDurability(weapon.getDurability() + 1);
				if(Lineage.server_version >= 160)
					cha.toSender( S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), weapon) );
				cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 268, weapon.toString()) );
			}
		}
		// 길찾기 무시할 목록에서 제거.
		astarList.remove(cha);
	}

	/**
	 * 경험치 지급목록 처리 함수.
	 * @param cha
	 * @param dmg
	 */
	public void toExp(Character cha, int dmg){
		if(dmg==0)
			return;
		Exp e = findExp(cha);
		if(e == null){
			e = ExpDatabase.getPool();
			e.setObject(cha);
			expList.add(e);
		}

		// 연산
		double exp = add_exp;
		if(nowHp < dmg)
			exp *= nowHp;
		else
			exp *= dmg;
		// 축적.
		e.setExp( e.getExp() + exp );
	}

	@Override
	public void toGiveItem(object o, ItemInstance item, long count){
		int bress = item.getBress();
		String name = item.getItem().getName();

		super.toGiveItem(o, item, count);

		// 넘겨받은 아이템 복용 처리. 1개일경우 바로 처리.
		if(count == 1){
			ItemInstance temp = getInventory().find(name, bress, false);
			if(temp instanceof HastePotion || temp instanceof GreaterHastePotion || temp instanceof HealingPotion || temp instanceof CurePoisonPotion)
				temp.toClick(this, null);

		}
	}

	/**
	 * 공격자 목록에 등록처리 함수.
	 * @param o
	 */
	public void addAttackList(object o){
		// Felix : Add check if recess mode
		if(!isDead() && !o.isDead() && !isRecess() && !o.isRecess() && o.getObjectId()!=getObjectId() && !attackList.contains(o) && !group_list.contains(o)){
			// 공격목록에 추가.
			attackList.add(o);
		}
	}

	public List<object> getAttackList(){
		return attackList;
	}

	/**
	 * 전투목록에서 원하는 위치에있는 객체 찾아서 리턴.
	 * @param index
	 * @return
	 */
	protected object getAttackList(int index){
		if(attackList.size()>index)
			return attackList.get(index);
		else
			return null;
	}

	protected object getExpList(int index){
		if(expList.size()>index)
			return expList.get(index).getObject();
		else
			return null;
	}

	protected void removeAttackList(object o){
		if(o!=null)
			attackList.remove(o);
	}

	/**
	 * 몬스터별 drop_list 정보를 참고해서 확율 연산후 인벤토리에 갱신.
	 */
	public void readDrop(){
		if(inv == null)
			return;
		// 인벤토리 아이템 제거.
		for(ItemInstance ii : inv.getList())
			ItemDatabase.setPool(ii);
		inv.getList().clear();
		// 인벤토리에 드랍아이템 등록.
		for(Drop d : mon.getDropList()){
			Item item = ItemDatabase.find(d.getItemName());
			if(item != null){
				int chance = d.getChance() + item.getDropChance();
				if(Util.random(0,Lineage.chance_max_drop) <= chance*Lineage.rate_drop){
					ItemInstance ii = ItemDatabase.newInstance(item);
					if(ii != null){
						ii.setBress(d.getItemBress());
						ii.setCount(Util.random(d.getCountMin()==0 ? 1 : d.getCountMin(), d.getCountMax()==0 ? 1 : d.getCountMax()));
						inv.append(ii, true);
					}
				}
			}
		}
		// 아덴 등록.
//		if(mon.isAdenDrop()){
//			int count = (int)(Util.random(level*2, level*3) * Lineage.rate_aden);
//			if(count > 0){
//				ItemInstance aden = ItemDatabase.newInstance(ItemDatabase.find("아데나") );
//				if(aden != null){
//					x = Util.random(getX()-1, getX()+1);
//					y = Util.random(getY()-1, getY()+1);
//
//					aden.setObjectId(ServerDatabase.nextItemObjId());
//					aden.setCount(count);
//					inv.append(aden, true);
//				}
//			}
//		}
	}

	/**
	 * 해당객체를 공격해도 되는지 분석하는 함수.
	 * @param o
	 * @param walk	: 랜덤워킹 상태 체크인지 구분용
	 * @return
	 */
	public boolean isAttack(object o, boolean walk){
		if(o == null)
			return false;
		if(o.isDead())
			return false;
		if(o.isRecess()) // Felix : Add recess mode
			return false;
		if(o.isWorldDelete())
			return false;
		if(o.isTransparent())
			return false;
		if(!Util.isDistance(this, o, Lineage.SEARCH_MONSTER_TARGET_LOCATION))
			return false;
		if(walk){
			if(o.getGfx()!=o.getClassGfx() && !mon.isAtkPoly())
				return false;
		}
		// 특정 몬스터들은 굳은상태를 확인해서 무시하기.
		switch(mon.getNameIdNumber()){
		case 962:	// 바실리스크
		case 969:	// 코카트리스
			if(o.isLockHigh())
				return false;
		}
		// 투망상태일경우 공격목록에 없으면 무시.
		if(!mon.isAtkInvis() && o.isInvis())
			return attackList.contains(o);

		if(this instanceof SummonInstance){
			// 소환및 펫은 거의다.. 
			return true;
		}else{
			// 몬스터는 pc, sum, pet
			// 랜덤워킹중에 선인식 체크함. 랜덤워킹이라면 서먼몬스터는 제외.
			if(walk)
				return o instanceof PcInstance;
			else
				return o instanceof PcInstance || o instanceof SummonInstance;
		}
	}

	/**
	 * 매개변수 좌표로 A스타를 발동시켜 이동시키기.
	 * 객체가 존재하는 지역은 패스하도록 함.
	 * 이동할때마다 aStar가 새로 그려지기때문에 과부하가 심함.
	 */
	public void toMoving(object o, final int x, final int y, final int h, final boolean astar){
		if(astar){
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
				toMoving(iPath[iCurrentPath][0], iPath[iCurrentPath][1], Util.calcheading(this.x, this.y, iPath[iCurrentPath][0], iPath[iCurrentPath][1]));
			}else{
				if(o != null){
					// o 객체가 마스터일경우 휴식모드로 전환.
					if(this instanceof SummonInstance){
						SummonInstance si = (SummonInstance)this;
						if(si.getSummon()!=null && si.getSummon().getMasterObjectId()==o.getObjectId()){
							si.setSummonMode(SUMMON_MODE.Rest);
							return;
						}
					}
					// 그외엔 에이스타 무시목록에 등록.
					astarList.add(o);
				}
			}
		}else{
			toMoving(x, y, h);
		}
	}

	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg){
		// 일반적인 공격mode와 다를경우 프레임값 재 설정.
		if(this.gfxMode+Lineage.GFX_MODE_ATTACK != gfxMode)
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode);
		
		if (!isRecess() || this.gfx != 901) { // Felix : Allow damage only if not recess mode and not 901 (Hide Dupelgenon)
			int effect = bow ? mon.getArrowGfx()!=0 ? mon.getArrowGfx() : 66 : 0;
			int dmg = DamageController.getDamage(this, o, bow, null, null, alpha_dmg);
			// System.out.print("Dupelgenon Attack: " + this.gfx + " - " + dmg + " \n");
			DamageController.toDamage(this, o, dmg, Lineage.ATTACK_TYPE_WEAPON);
			toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, gfxMode, dmg, effect, bow, true, 0, 0), false);
		}
	}

	@Override
	public void toAi(long time){
		switch(ai_status){
		// 공격목록이 발생하면 공격모드로 변경
		case Lineage.AI_STATUS_WALK:
		case Lineage.AI_STATUS_PICKUP:
			if(attackList.size()>0)
				setAiStatus(Lineage.AI_STATUS_ATTACK);
			break;

			// 전투 처리부분은 항상 타켓들이 공격가능한지 확인할 필요가 있음.
		case Lineage.AI_STATUS_ATTACK:
		case Lineage.AI_STATUS_ESCAPE:
			temp_list.clear();
			for(object o : attackList){
				if(!isAttack(o, false) || astarList.contains(o))
					temp_list.add(o);
			}
			for(object o : temp_list){
				attackList.remove(o);
				Exp e = findExp(o);
				if(e != null){
					expList.remove(e);
					ExpDatabase.setPool(e);
				}
			}
			// 전투목록이 없을경우 랜덤워킹으로 변경.
			if(attackList.size()==0)
				setAiStatus(Lineage.AI_STATUS_WALK);
			break;
		// Felix: if recess mode, stop attack
		case Lineage.AI_STATUS_RECESS: 
			temp_list.clear();
			for(object o : attackList){
				if(!isAttack(o, false))
					temp_list.add(o);
			}
			for(object o : temp_list)
				attackList.remove(o);
		}

		super.toAi(time);
	}

	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		// 선인식 체크
		if(mon.getAtkType()==1 && group_master==null){
			for(object o : insideList){
				if(isAttack(o, true) && !astarList.contains(o))
					// 공격목록에 등록.
					if (o.getGm() < Lineage.GMCODE) {
						addAttackList(o);
					}
			}
		}

		// 아이템 줍기 체크
		if(attackList.size()==0 && mon.isPickup() && group_master==null){
			for(object o : insideList){
				if(o instanceof ItemInstance && !astarList.contains(o)){
					// 아이템이 발견되면 아이템줍기 모드로 전환.
					setAiStatus(Lineage.AI_STATUS_PICKUP);
					return;
				}
			}
		}
		// 멘트
		toMent(time);

		// 아직 휴식카운팅값이 남앗을경우 리턴.
		if(ai_walk_stay_count-->0)
			return;
		// Felix: if recess mode, stop random walking and stay
		if (isRecess())
			return;
		// Astar 발동처리하다가 길이막혀서 이동못하던 객체를 모아놓은 변수를 일정주기마다 클린하기.
		if(Util.random(0, 10) == 0)
			astarList.clear();

		do{
			switch(Util.random(0, 10)){
			case 0:
			case 1:
				ai_walk_stay_count = Util.random(3, 6);
				break;
			case 2:
			case 3:
				setHeading(getHeading()+1);
				break;
			case 4:
			case 5:
				setHeading(getHeading()-1);
				break;
			case 6:
			case 7:
				setHeading(Util.random(0, 7));
				break;
			}
			// 이동 좌표 추출.
			int x = Util.getXY(heading, true)+this.x;
			int y = Util.getXY(heading, false)+this.y;
			// 그룹 마스터로 지정된놈 추출.
			MonsterInstance master = getGroupMasterDynamic();
			// 스폰된 위치에서 너무 벗어낫을경우 스폰쪽으로 유도하기.
			if(master==null || master.getObjectId()==getObjectId()){
				if(!Util.isDistance(x, y, map, homeX, homeY, homeMap, Lineage.SEARCH_LOCATIONRANGE)){
					heading = Util.calcheading(this, homeX, homeY);
					x = Util.getXY(heading, true)+this.x;
					y = Util.getXY(heading, false)+this.y;
				}
				// 마스터가 존재하는 그룹몬스터일경우 마스터가 있는 좌표 내에서 왓다갓다 하기.
			}else{
				// 이동 범위는 마스터 목록에 등록된 순번+3으로 범위지정. 등록순번이 뒤에잇을수록 이동반경은 넓어짐.
				if(!Util.isDistance(x, y, map, master.getX(), master.getY(), master.getMap(), group_master.getGroupList().indexOf(this)+3)){
					heading = Util.calcheading(this, master.getX(), master.getY());
					x = Util.getXY(heading, true)+this.x;
					y = Util.getXY(heading, false)+this.y;
				}
			}
			// 해당 좌표 이동가능한지 체크.
			boolean tail = World.isThroughObject(this.x, this.y, this.map, heading) && World.isMapdynamic(x, y, map)==false;
			if(tail){
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				toMoving(null, x, y, heading, false);
			}else{
				if(Util.random(0, 3) != 0)
					continue;
			}
		}while(false);
	}

	@Override
	public void toAiAttack(long time){
		super.toAiAttack(time);

		// 멘트
		toMent(time);

		// 공격자 확인.
		object o = findDangerousObject();

		// 객체를 찾지못했다면 무시.
		if(o == null)
			return;

		// 2거리 이상은 모두 활공격으로 판단.
		boolean bow = getAtkRange()>2;
		// 객체 거리 확인
		if(Util.isDistance(this, o, getAtkRange()) && Util.isAreaAttack(this, o)){
			// 객체 공격
			super.toAiAttack(time);
			// 공격 시전했는지 확인용.
			boolean is_attack = false;
			// 스킬 확인하기.
			for(MonsterSkill ms : mon.getSkillList()){
				// 마법시전 시도.
				if(ShareMagic.init(this, o, ms, 0, 0, bow)){
					// 시전 성공시 루프종료.
					is_attack = true;
					break;
				}
			}
			// 마법 시전이 실패됫을때.
			if(!is_attack){
				if(Util.isDistance(this, o, mon.getAtkRange())){
					// 물리공격 범위내로 잇을경우 처리.
					toAttack(o, 0, 0, mon.getAtkRange()>2, gfxMode+Lineage.GFX_MODE_ATTACK, 0);
				}else{
					// 객체에게 접근.
					if (!isRecess()) { // Felix : move only if not recess mode
						ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
						toMoving(o, o.getX(), o.getY(), 0, true);
					}
				}
			}
		}else{
			if (!isRecess()) { // Felix : move only if not recess mode
				ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
				// 객체 이동
				toMoving(o, o.getX(), o.getY(), 0, true);
			}
		}

		// 일정확율로 인벤에 있는 체력회복물약 복용하기.
		if(Util.random(0, 5) == 0){
			// 현재 hp의 백분율값 추출.
			int now_percent = (int)(((double)getNowHp()/(double)getTotalHp()) * 100);
			// 복용해야한다면
			if(Lineage.ai_auto_healingpotion_percent >= now_percent){
				// 찾고
				ItemInstance ii = getInventory().find(HealingPotion.class);
				// 복용
				if(ii != null)
					ii.toClick(this, null);
			}
		}
	}

	@Override
	protected void toAiDead(long time){
		super.toAiDead(time);

		// 멘트
		toMent(time);
		// 아이템 드랍
		int x = 0;
		int y = 0;
		boolean add = false;
		for(ItemInstance ii : inv.getList()){
			add = false;
			// 오토루팅 처리.
			if( isAutoPickup(ii) ){
				// 경험치 지급될 목록을 순회하면서 확율 확인하기.
				for(object o : attackList){
					Exp e = findExp(o);
					// 화면안에 있고, 경험치 지급할게 있을때
					if(e!=null && e.getObject()!=null && isAutoPickupItem(e.getObject())){
						Character cha = (Character)o;
						// 확율 체크.
						if( Util.random(0, Lineage.auto_pickup_percent) <= (e.getExp()/mon.getExp())*Lineage.auto_pickup_percent ){
							// 지급 처리.
							if(toAutoPickupItem(cha, ii)){
								add = true;
								break;
							}
						}
					}
				}
				if( !add ){
					// 지급하지 못햇을경우 랜덤으로 아무에게나 주기.
					for(int i=0 ; i<100 ; ++i){
						Exp e = expList.get( Util.random(0, expList.size()-1) );
						if(e!=null && e.getObject()!=null && isAutoPickupItem(e.getObject())){
							Character cha = (Character)e.getObject(); 
							if(toAutoPickupItem(cha, ii)){
								add = true;
								break;
							}
						}
					}
				}
			}
			// 땅에 드랍.
			if( !add ){
				if(ii.getObjectId() == 0)
					ii.setObjectId(ServerDatabase.nextItemObjId());
				x = Util.random(getX()-1, getX()+1);
				y = Util.random(getY()-1, getY()+1);

				if(World.isThroughObject(x, y+1, map, 0))
					ii.toTeleport(x, y, map, false);
				else
					ii.toTeleport(this.x, this.y, map, false);
				// 드랍됫다는거 알리기.
				ii.toDrop(this);
			}
		}
		inv.getList().clear();
		// 경험치 지급
		for(object o : attackList){
			Exp e = findExp(o);
			if(e != null){
				if(o instanceof Character){
					Character cha = (Character)o;
					// 화면안에 존재할 경우에만 경험치 지급.
					if(Util.isDistance(this, cha, Lineage.SEARCH_LOCATIONRANGE)){
						// 파티 경험치 처리. 실패하면 혼자 독식.
						if(!PartyController.toExp(cha, this, e.getExp()) && !cha.isDead()){
							// 유저와 펫만 처리.
							if(cha instanceof PcInstance || cha instanceof PetInstance){
								// 경험치 지급.
								cha.toExp(this, e.getExp());
								// 라우풀 지급.
								cha.setLawful((int)(cha.getLawful() + (((level * 3) / 2) * Lineage.rate_lawful)));
							}
						}
					}
				}
				ExpDatabase.setPool(e);
			}
		}
		// 크리스마스 이벤트 양말 지급.
		if(Lineage.event_christmas && Util.random(0, 100)<10 && attackList.size()>0){
			object o = attackList.get(0);
			if(o instanceof PcInstance){
				ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("빨간 양말"));
				if(!toAutoPickupItem((PcInstance)o, ii))
					ItemDatabase.setPool(ii);
			}
		}

		ai_time_temp_1 = 0;
		// 전투 관련 변수 초기화.
		expList.clear();
		attackList.clear();
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_CORPSE);
	}
	// Felix: 
	@Override
	protected void toAiRecess(long time){
		super.toAiRecess(time);

		// 멘트
		toMent(time);		

		//ai_time_temp_1 = 0;
		// 전투 관련 변수 초기화.
		// expList.clear();
		attackList.clear();
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_RECESS);
	}

	@Override
	protected void toAiCorpse(long time){
		super.toAiCorpse(time);

		if(ai_time_temp_1 == 0)
			ai_time_temp_1 = time;

		// 시체 유지
		if(summon != null){
			if(this instanceof PetInstance && ai_time_temp_1+Lineage.ai_pet_corpse_time > time)
				return;
			if(this instanceof SummonInstance && ai_time_temp_1+Lineage.ai_summon_corpse_time > time)
				return;
		}else{
			if(this instanceof MonsterInstance && ai_time_temp_1+Lineage.ai_corpse_time > time)
				return;
		}

		ai_time_temp_1 = 0;
		// 버프제거
		toReset(true);
		// 시체 제거
		clearList(true);
		World.remove(this);
		// 상태 변환.
		setAiStatus(Lineage.AI_STATUS_SPAWN);
	}

	@Override
	protected void toAiSpawn(long time){
		super.toAiSpawn(time);


		// 스폰 유지딜레이 값 초기화.
		if(ai_time_temp_1 == 0)
			ai_time_temp_1 = time;
		// 그룹몬스터쪽에 그룹원들이 스폰할 상태인지 확인. 아닐경우 딜레이 시키기.
		if(group_master!=null){
			if(group_master.getObjectId()!=getObjectId()){
				if(ai_time_temp_1 != 1)
					ai_time_temp_1 = time;
			}else{
				if(getGroupMasterDynamic() != null)
					ai_time_temp_1 = time;
			}
		}
		// 스폰 대기.
		if(ai_time_temp_1+reSpawnTime > time){

		}else{
			// 리스폰값이 정의되어 있지않다면 재스폰 할 필요 없음.
			// 서먼몬스터에서도 이걸 호출함.
			if(reSpawnTime==0){
				//
				toAiThreadDelete();
				// 보스 객체일경우 관리중인 목록에서 제거. 그래야 다시 시간됫을때 스폰됨.
				if(boss)
					BossController.toWorldOut(this);
			}else{
				// 그룹몬스터 마스터가 스폰한다면 해당 그룹원들도 스폰시키기.
				if(group_master!=null && group_master.getObjectId()==getObjectId()){
					for(MonsterInstance mi : group_master.getGroupList())
						mi.setAiTimeTemp1(1);
				}

				// 드랍 목록 인벤토리에 갱신.
				readDrop();
				// 상태 변환
				setDead(false);
				setNowHp(getMaxHp());
				setNowMp(getMaxMp());
				// gfx 복구
				setGfx(getClassGfx());
				setGfxMode(getClassGfxMode());
				// 스폰
				if(monSpawn != null){
					MonsterSpawnlistDatabase.toSpawnMonster(this, World.get_map(homeMap), monSpawn.isRandom(), monSpawn.getX(), monSpawn.getY(), map, monSpawn.getLocSize(), monSpawn.getReSpawn(), false, false);
				}else{
					if(group_master==null)
						toTeleport(homeX, homeY, homeMap, false);
					else
						toTeleport(group_master.getX(), group_master.getY(), group_master.getMap(), false);
				}
				// 멘트
				toMent(time);
				// 상태 변환.
				setAiStatus(Lineage.AI_STATUS_WALK);
			}
		}

	}

	@Override
	public void toAiEscape(long time){
		super.toAiEscape(time);
		// 멘트
		toMent(time);

		// 전투목록에서 가장 근접한 사용자 찾기.
		object o = null;
		for(object oo : attackList){
			if(o == null)
				o = oo;
			else if(Util.getDistance(this, oo)<Util.getDistance(this, o))
				o = oo;
		}

		// 못찾앗을경우 무시. 가끔생길수 잇는 현상이기에..
		if(o==null){
			setAiStatus(Lineage.AI_STATUS_WALK);
			return;
		}

		// 반대방향 이동처리.
		heading = Util.oppositionHeading(this, o);
		int temp_heading = heading;
		do{
			// 이동 좌표 추출.
			int x = Util.getXY(heading, true)+this.x;
			int y = Util.getXY(heading, false)+this.y;
			// 해당 좌표 이동가능한지 체크.
			boolean tail = World.isThroughObject(this.x, this.y, this.map, heading) && World.isMapdynamic(x, y, map)==false;
			if(tail){
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				toMoving(null, x, y, heading, false);
				break;
			}else{
				setHeading( heading + 1);
				if(temp_heading == heading)
					break;
			}
		}while(true);
	}

	@Override
	protected void toAiPickup(long time){
		// 가장 근접한 아이템 찾기.
		object o = null;
		for(object oo : insideList){
			if(oo instanceof ItemInstance && !astarList.contains(o)){
				if( o == null )
					o = oo;
				else if(Util.getDistance(this, oo)<Util.getDistance(this, o))
					o = oo;
			}
		}
		// 못찾앗을경우 다시 랜덤워킹으로 전환.
		if(o==null){
			setAiStatus(Lineage.AI_STATUS_WALK);
			// 소환객체라면 아이템 줍기 완료 상태로 전환.
			if(this instanceof SummonInstance)
				((SummonInstance)this).setSummonMode(SUMMON_MODE.ItemPickUpFinal);
			return;
		}

		// 객체 거리 확인
		if(Util.isDistance(this, o, 0)){
			super.toAiPickup(time);
			// 줍기
			inv.toPickup(o, o.getCount());
		}else{
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
			// 아이템쪽으로 이동.
			toMoving(o, o.getX(), o.getY(), 0, true);
		}
	}

	/**
	 * 오토루팅 처리시 처리해도 되는 객체인지 확인하는 함수.
	 *  : 중복코드 방지용.
	 * @param o
	 * @return
	 */
	private boolean isAutoPickupItem(object o){
		// 죽지않았고, 케릭터면서, 범위안에 있을경우.
		return !o.isDead() && o instanceof PcInstance && Util.isDistance(o, this, Lineage.SEARCH_LOCATIONRANGE);
	}

	/**
	 * 자동루팅 상황에서 아이템 지급처리 담당하는 함수.
	 *  : 중복 코드 방지용.
	 * @param cha
	 * @param ii
	 * @return
	 */
	private boolean toAutoPickupItem(Character cha, ItemInstance ii){
		if(cha.isAutoPickup() && cha.getInventory().isAppend(ii, ii.getCount(), false)){
			// 메세지 변수.
			String msg = ii.toString();
			// 혹시모를 처리변수가 있을지 모르기때문에 아래와같이 패턴 적용.
			ii.toDrop(this);
			ii.toPickup(cha);
			// 지급.
			ItemInstance temp = cha.getInventory().find(ii.getItem().getName(), ii.getBress(), true);
			if(temp != null){
				cha.getInventory().count(temp, temp.getCount()+ii.getCount(), true);
				ItemDatabase.setPool(ii);
			}else{
				if(ii.getObjectId() == 0)
					ii.setObjectId(ServerDatabase.nextItemObjId());
				cha.getInventory().append(ii, true);
			}
			// \f1%0%s 당신에게 %1%o 주었습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), msg));
			return true;
		}
		return false;
	}

	/**
	 * 인공지능 처리구간에서 주기적으로 호출됨.
	 *  : 상태에 맞는 멘트를 구사할지 여부 확인하여 표현 처리하는 함수.
	 * @param time	: 진행되고있는 시간.
	 */
	private void toMent(long time){
		int msgTime = -1;
		int msgSize = 0;
		List<String> msgList = null;
		switch(ai_status){
		case Lineage.AI_STATUS_WALK:
			msgTime = mon.getMsgWalkTime();
			msgSize = mon.getMsgWalk().size();
			msgList = mon.getMsgWalk();
			break;
		case Lineage.AI_STATUS_ATTACK:
			msgTime = mon.getMsgAtkTime();
			msgSize = mon.getMsgAtk().size();
			msgList = mon.getMsgAtk();
			break;
		case Lineage.AI_STATUS_DEAD:
			msgTime = mon.getMsgDieTime();
			msgSize = mon.getMsgDie().size();
			msgList = mon.getMsgDie();
			break;
		case Lineage.AI_STATUS_RECESS: // Felix : add recess mode
			msgTime = mon.getMsgRecessTime();
			msgSize = mon.getMsgRecess().size();
			msgList = mon.getMsgRecess();
			break;
		case Lineage.AI_STATUS_SPAWN:
			msgTime = mon.getMsgSpawnTime();
			msgSize = mon.getMsgSpawn().size();
			msgList = mon.getMsgSpawn();
			break;
		case Lineage.AI_STATUS_ESCAPE:
			msgTime = mon.getMsgEscapeTime();
			msgSize = mon.getMsgEscape().size();
			msgList = mon.getMsgEscape();
			break;
		}

		// 멘트를 표현할 수 있는 디비상태일경우.
		if(msgTime!=-1 && msgSize>0 && msgList!=null){
			if(msgTime == 0){
				// 한번만 처리하기.
				if(!ai_showment){
					toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), this, 0x02, msgList.get(0)), true);
					ai_showment = true;
				}
			}else{
				// 시간마다 표현하기.
				if(time-ai_showment_time >= msgTime){
					ai_showment_time = time;
					if(ai_showment_idx>=msgSize)
						ai_showment_idx = 0;
					toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), this, 0x02, msgList.get(ai_showment_idx++)), true);
				}
			}
		}
	}

	/**
	 * 해당 아이템 오토루팅 할지 여부.
	 * @param ii
	 * @return
	 */
	private boolean isAutoPickup(ItemInstance ii){
		// 아데나 오토루팅 여부
		if(ii.getItem().getNameIdNumber()==4){
			return Lineage.auto_pickup_aden;
		}else{
			return Lineage.auto_pickup;
		}
	}

	@Override
	public int getTotalStr(){
		return super.getTotalStr() + mon.getStr();
	}

	@Override
	public int getTotalDex(){
		return super.getTotalDex() + mon.getDex();
	}

	@Override
	public int getTotalCon(){
		return super.getTotalCon() + mon.getCon();
	}

	@Override
	public int getTotalWis(){
		return super.getTotalWis() + mon.getWis();
	}

	@Override
	public int getTotalInt(){
		return super.getTotalInt() + mon.getInt();
	}

	@Override
	public int getTotalCha(){
		return super.getTotalCha() + mon.getCha();
	}

	@Override
	public int getTotalAc(){
		return super.getTotalAc() + mon.getAc();
	}

	@Override
	public int getDynamicMr(){
		return super.getDynamicMr() + mon.getMr();
	}

}
