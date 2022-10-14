package lineage.world.object;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Summon;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Door;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectMap;
import lineage.network.packet.server.S_ObjectMoving;
import lineage.network.packet.server.S_ObjectRemove;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.EventController;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.TeleportInstance;
import lineage.world.object.monster.FloatingEye;
import lineage.world.object.monster.event.JackLantern;
import lineage.world.object.npc.background.Door;
import lineage.world.object.npc.background.Firewall;
import lineage.world.object.npc.background.LifeStream;
import lineage.world.object.npc.background.Switch;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomDoorman;
import lineage.world.object.npc.kingdom.KingdomGuard;

public class object {
	protected List<object> insideList;		// 12셀 내에있는 객체만 관리할 용도로 사용
	protected List<object> allList;			// 40셀 내에 있는 객체만 관리할 용도로 사용.
	protected List<object> temp_list;		// 주변셀 검색에 임시 담기용으로 사용.
	protected long objectId;
	protected String name;
	protected String title;
	protected int clanId;
	protected String clanName;
	protected object own;				// 객체를 관리하고 있는 객체
	protected long own_objectId;		// 객체를 관리하고 있는 객체의 아이디
	protected String own_name;			// 객체를 관리하고 있는 객체의 이름
	protected int x;
	protected int y;
	protected int map;
	protected int homeX;
	protected int homeY;
	protected int homeMap;
	protected int homeLoc;
	protected int homeHeading;
	protected int homeTile;				// Door 객체에서 사용중. 스폰된 위치에 고유 타일값 기록을 위해.
	protected int tempX;
	protected int tempY;
	protected int gfx;
	protected int gfxMode;
	protected int classGfx;
	protected int classGfxMode;
	protected int lawful;
	protected int heading;			// 0~7
	protected int light;			// 0~15
	protected int speed;			// 0~2
	protected boolean brave;		// 용기 및 와퍼 상태
	protected boolean eva;			// 에바의 축복 상태
	protected boolean wisdom;		// 지혜의 물약 상태
	protected long count;
	protected int tempCount;		//
	protected int classSex;				// 0~1
	protected int classType;			// pc[0~3]
	protected boolean dead;
	protected boolean fight;
	protected boolean moving;
	protected boolean poison;			// 독감염 여부.
	protected boolean lock_high;		// 굳은상태 여부. 데미지 입지 않음.
	protected boolean lock_low;			// 굳은상태 여부. 데미지 입음.
	protected boolean invis;			// 투명상태 여부.
	protected boolean transparent;		// 절대적 투명상태 여부. 객체를 뚫을 수 있음. 몬스터도사용중(잭-오-랜턴)
	protected boolean worldDelete = true;//
	protected object temp_object_1;		// 임시 저장용 변수.
	protected int temp_hp;
	protected int temp_mp;
	private int CurseParalyzeCounter;	// 커스:패럴라이즈가 실제 동작되는 시점 체크를 위해 사용하는 변수.
	protected Summon summon;			// 서먼 객체 관리 변수. 자신이 해당 서먼객체 소속일경우 사용하는 변수.
	protected boolean isQuestMonster;  // Felix: Have boolean value check if created by Quest	
	

	/**
	 * .몹드랍
	 */
	private long _delaytime = 0;		//.몹드랍
	private boolean isBoardView = false; // 도움말
	
	// 인공지능 처리 변수
	protected long ai_time;
	private long ai_start_time;
	protected int ai_status;			// 인공지능제거[-1] 기본[0] 등등....
	protected long ai_showment_time;	// 멘트 발생된 마지막 시간값 임시 저장용.
	protected int ai_showment_idx;		// 출력된 멘트 위치 저장 변수.
	protected boolean ai_showment;		// 멘트 발생여부.
	
	// 버프관리 쪽
	private boolean BuffDecreaseWeight;
	private boolean BuffHolyWeapon;
	private boolean BuffEnchantWeapon;
	private boolean BuffMonsterEyeMeat;
	private boolean BuffCurseParalyze;
	private boolean BuffBlessWeapon;
	private boolean BuffInvisiBility;
	private boolean BuffImmuneToHarm;
	private boolean BuffDecayPotion;
	private boolean BuffAbsoluteBarrier;
	private boolean BuffGlowingAura;
	private boolean BuffFireWeapon;
	private boolean BuffWindShot;
	private boolean BuffEraseMagic;
	private boolean BuffBurningWeapon;
	private boolean BuffStormShot;
	private boolean BuffWisdom;
	private boolean BuffEva;
	private boolean BuffBlessOfFire;
	private boolean BuffCurseGhoul;
	private boolean BuffCurseFloatingEye;
	private boolean BuffChattingClose;
	private boolean BuffDisease;
	private boolean BuffSilence;
	private boolean BuffEyeOfStorm;
	private boolean BuffPolluteWater;
	private boolean BuffWeakness;
	private boolean BuffCounterMagic;
	private boolean BuffCriminal;
	private boolean BuffMeditation;
	private boolean BuffFogOfSleeping;
	private boolean BuffEnchantVenom;
	private boolean BuffBurningSpirit;
	private boolean BuffVenomResist;
	private boolean BuffDoubleBreak;
	private boolean BuffUncannyDodge;
	private boolean BuffShadowFang;
	private boolean BuffBerserks;
	private int BuffAdvanceSpiritHp;
	private int BuffAdvanceSpiritMp;
	private float BuffExpPotion;
	
	// 디비처리에 사용되는 변수.
	private Object database_key;	// npc_spawnlist(name), monster_spawnlist(uid)

	public object(){
		insideList = new ArrayList<object>();
		allList = new ArrayList<object>();
		temp_list = new ArrayList<object>();
		// Felix : Default false for isQuestMonster
		setQuestMonster(false);
		
		close();
	}

	/**
	 * 사용 다된 객체에 대한 메모리 정리 함수.
	 */
	public void close(){
		name = title = clanName = own_name = null;
		temp_object_1 = null;
		summon = null;
		own_objectId = objectId = x = y = map = clanId = homeX = homeY = homeMap = gfx = gfxMode = classGfx =
		classGfxMode = lawful = heading = light = speed = classSex = classType = homeHeading = tempCount = 0;
		ai_start_time = ai_time = ai_status = 0;
		count = 1;
		wisdom = eva = brave = dead = fight = moving = poison = lock_low = lock_high = invis = transparent = false;
		BuffDecreaseWeight = BuffHolyWeapon = BuffEnchantWeapon = BuffMonsterEyeMeat = BuffCurseParalyze = BuffBlessWeapon = 
		BuffInvisiBility = BuffImmuneToHarm = BuffDecayPotion = BuffAbsoluteBarrier = BuffGlowingAura = BuffFireWeapon = 
		BuffWindShot = BuffEraseMagic = BuffBurningWeapon = BuffStormShot = BuffWisdom = BuffEva = 
		BuffBlessOfFire = BuffCurseGhoul = BuffCurseFloatingEye = BuffChattingClose = BuffDisease = BuffSilence = 
		BuffEyeOfStorm = BuffWeakness = BuffCounterMagic = BuffCriminal = BuffMeditation = BuffFogOfSleeping = 
		BuffEnchantVenom = BuffBurningSpirit = BuffVenomResist = BuffDoubleBreak = BuffUncannyDodge = BuffShadowFang = 
		BuffBerserks = false;
		BuffAdvanceSpiritHp = BuffAdvanceSpiritMp = 0;
		temp_hp = temp_mp = -1;
		BuffExpPotion = 0;
		worldDelete = true;
		own = null;
		database_key = null;
		if(insideList != null)
			insideList.clear();
		if(allList != null)
			allList.clear();
		if(temp_list != null)
			temp_list.clear();
		// 적용된 버프가 있을수 있으므로.
		BuffController.toWorldOut(this);
	}

	public Object getDatabaseKey() {
		return database_key;
	}

	public void setDatabaseKey(Object database_key) {
		this.database_key = database_key;
	}

	public Summon getSummon() {
		return summon;
	}

	public void setSummon(Summon summon) {
		this.summon = summon;
	}
	
	public void setAiStatus(int ai_status){
		this.ai_status = ai_status;
		// ai 상태 변경될때마다 멘트표현 변수 초기화.
		ai_showment = false;
		// 멘트 표현위치 초기화.
		ai_showment_idx = 0;
		// 멘트 표현시간 초기화.
		ai_showment_time = 0;
	}

	public int getAiStatus() {
		return ai_status;
	}

	public long getAiTime() {
		return ai_time;
	}

	public void setAiTime(long ai_time) {
		this.ai_time = ai_time;
	}
	
	public float getBuffExpPotion(){
		return BuffExpPotion;
	}
	
	public void setBuffExpPotion(float BuffExpPotion){
		this.BuffExpPotion = BuffExpPotion;
	}

	public int getBuffAdvanceSpiritHp() {
		return BuffAdvanceSpiritHp;
	}

	public void setBuffAdvanceSpiritHp(int buffAdvanceSpiritHp) {
		BuffAdvanceSpiritHp = buffAdvanceSpiritHp;
	}

	public int getBuffAdvanceSpiritMp() {
		return BuffAdvanceSpiritMp;
	}

	public void setBuffAdvanceSpiritMp(int buffAdvanceSpiritMp) {
		BuffAdvanceSpiritMp = buffAdvanceSpiritMp;
	}

	public boolean isBuffCriminal() {
		return BuffCriminal;
	}

	public void setBuffCriminal(boolean buffCriminal) {
		BuffCriminal = buffCriminal;
	}

	public boolean isBuffMeditation() {
		return BuffMeditation;
	}

	public void setBuffMeditation(boolean buffMeditation) {
		BuffMeditation = buffMeditation;
	}

	public boolean isBuffFogOfSleeping() {
		return BuffFogOfSleeping;
	}

	public void setBuffFogOfSleeping(boolean buffFogOfSleeping) {
		BuffFogOfSleeping = buffFogOfSleeping;
	}

	public boolean isBuffCounterMagic() {
		return BuffCounterMagic;
	}

	public void setBuffCounterMagic(boolean buffCounterMagic) {
		BuffCounterMagic = buffCounterMagic;
	}

	public boolean isBuffWeakness() {
		return BuffWeakness;
	}

	public void setBuffWeakness(boolean buffWeakness) {
		BuffWeakness = buffWeakness;
	}

	public boolean isBuffEyeOfStorm() {
		return BuffEyeOfStorm;
	}

	public void setBuffEyeOfStorm(boolean buffEyeOfStorm) {
		BuffEyeOfStorm = buffEyeOfStorm;
	}

	public boolean isBuffSilence() {
		return BuffSilence;
	}

	public void setBuffSilence(boolean buffSilence) {
		BuffSilence = buffSilence;
	}

	public boolean isBuffDisease() {
		return BuffDisease;
	}

	public void setBuffDisease(boolean buffDisease) {
		BuffDisease = buffDisease;
	}

	public boolean isBuffChattingClose() {
		return BuffChattingClose;
	}

	public void setBuffChattingClose(boolean buffChattingClose) {
		BuffChattingClose = buffChattingClose;
	}

	public boolean isBuffCurseFloatingEye() {
		return BuffCurseFloatingEye;
	}

	public void setBuffCurseFloatingEye(boolean buffCurseFloatingEye) {
		BuffCurseFloatingEye = buffCurseFloatingEye;
	}

	public boolean isBuffCurseGhoul() {
		return BuffCurseGhoul;
	}

	public void setBuffCurseGhoul(boolean buffCurseGhoul) {
		BuffCurseGhoul = buffCurseGhoul;
	}

	public boolean isBuffBlessOfFire() {
		return BuffBlessOfFire;
	}

	public void setBuffBlessOfFire(boolean buffBlessOfFire) {
		BuffBlessOfFire = buffBlessOfFire;
	}

	public boolean isBuffEva() {
		return BuffEva;
	}

	public void setBuffEva(boolean buffEva) {
		BuffEva = buffEva;
	}

	public boolean isBuffWisdom() {
		return BuffWisdom;
	}

	public void setBuffWisdom(boolean buffWisdom) {
		BuffWisdom = buffWisdom;
	}

	public boolean isBuffStormShot() {
		return BuffStormShot;
	}

	public void setBuffStormShot(boolean buffStormShot) {
		BuffStormShot = buffStormShot;
	}

	public boolean isBuffBurningWeapon() {
		return BuffBurningWeapon;
	}

	public void setBuffBurningWeapon(boolean buffBurningWeapon) {
		BuffBurningWeapon = buffBurningWeapon;
	}

	public boolean isBuffEraseMagic() {
		return BuffEraseMagic;
	}

	public void setBuffEraseMagic(boolean buffEraseMagic) {
		BuffEraseMagic = buffEraseMagic;
	}

	public boolean isBuffWindShot() {
		return BuffWindShot;
	}

	public void setBuffWindShot(boolean buffWindShot) {
		BuffWindShot = buffWindShot;
	}

	public boolean isBuffFireWeapon() {
		return BuffFireWeapon;
	}

	public void setBuffFireWeapon(boolean buffFireWeapon) {
		BuffFireWeapon = buffFireWeapon;
	}
	
	public boolean isBuffPolluteWater() {
		return BuffPolluteWater;
	}

	public void setBuffPolluteWater(boolean buffPolluteWater) {
		BuffPolluteWater = buffPolluteWater;
	}

	public boolean isBuffGlowingAura() {
		return BuffGlowingAura;
	}

	public void setBuffGlowingAura(boolean buffGlowingAura) {
		BuffGlowingAura = buffGlowingAura;
	}

	public boolean isBuffAbsoluteBarrier() {
		return BuffAbsoluteBarrier;
	}

	public void setBuffAbsoluteBarrier(boolean buffAbsoluteBarrier) {
		BuffAbsoluteBarrier = buffAbsoluteBarrier;
	}

	public boolean isBuffDecayPotion() {
		return BuffDecayPotion;
	}

	public void setBuffDecayPotion(boolean buffDecayPotion) {
		BuffDecayPotion = buffDecayPotion;
	}

	public boolean isBuffImmuneToHarm() {
		return BuffImmuneToHarm;
	}

	public void setBuffImmuneToHarm(boolean buffImmuneToHarm) {
		BuffImmuneToHarm = buffImmuneToHarm;
	}

	public boolean isBuffInvisiBility() {
		return BuffInvisiBility;
	}

	public void setBuffInvisiBility(boolean buffInvisiBility) {
		BuffInvisiBility = buffInvisiBility;
	}

	public boolean isBuffBlessWeapon() {
		return BuffBlessWeapon;
	}

	public void setBuffBlessWeapon(boolean buffBlessWeapon) {
		BuffBlessWeapon = buffBlessWeapon;
	}

	public boolean isBuffCurseParalyze() {
		return BuffCurseParalyze;
	}

	public void setBuffCurseParalyze(boolean buffCurseParalyze) {
		BuffCurseParalyze = buffCurseParalyze;
	}

	public int getCurseParalyzeCounter() {
		return CurseParalyzeCounter;
	}

	public void setCurseParalyzeCounter(int curseParalyzeCounter) {
		CurseParalyzeCounter = curseParalyzeCounter;
	}

	public boolean isBuffDecreaseWeight() {
		return BuffDecreaseWeight;
	}

	public void setBuffDecreaseWeight(boolean BuffDecreaseWeight) {
		this.BuffDecreaseWeight = BuffDecreaseWeight;
	}

	public boolean isBuffHolyWeapon() {
		return BuffHolyWeapon;
	}

	public void setBuffHolyWeapon(boolean buffHolyWeapon) {
		BuffHolyWeapon = buffHolyWeapon;
	}
	
	public void setWorldDelete(boolean worldDelete){
		this.worldDelete = worldDelete;
	}
	
	public boolean isBuffEnchantWeapon() {
		return BuffEnchantWeapon;
	}

	public void setBuffEnchantWeapon(boolean buffEnchantWeapon) {
		BuffEnchantWeapon = buffEnchantWeapon;
	}

	public boolean isBuffMonsterEyeMeat() {
		return BuffMonsterEyeMeat;
	}

	public void setBuffMonsterEyeMeat(boolean buffMonsterEyeMeat) {
		BuffMonsterEyeMeat = buffMonsterEyeMeat;
	}

	public boolean isBuffEnchantVenom() {
		return BuffEnchantVenom;
	}

	public void setBuffEnchantVenom(boolean buffEnchantVenom) {
		BuffEnchantVenom = buffEnchantVenom;
	}

	public boolean isBuffBurningSpirit() {
		return BuffBurningSpirit;
	}

	public void setBuffBurningSpirit(boolean buffBurningSpirit) {
		BuffBurningSpirit = buffBurningSpirit;
	}

	public boolean isBuffVenomResist() {
		return BuffVenomResist;
	}

	public void setBuffVenomResist(boolean buffVenomResist) {
		BuffVenomResist = buffVenomResist;
	}

	public boolean isBuffDoubleBreak() {
		return BuffDoubleBreak;
	}

	public void setBuffDoubleBreak(boolean buffDoubleBreak) {
		BuffDoubleBreak = buffDoubleBreak;
	}

	public boolean isBuffUncannyDodge() {
		return BuffUncannyDodge;
	}

	public void setBuffUncannyDodge(boolean buffUncannyDodge) {
		BuffUncannyDodge = buffUncannyDodge;
	}

	public boolean isBuffShadowFang() {
		return BuffShadowFang;
	}

	public void setBuffShadowFang(boolean buffShadowFang) {
		BuffShadowFang = buffShadowFang;
	}

	public boolean isBuffBerserks() {
		return BuffBerserks;
	}

	public void setBuffBerserks(boolean buffBerserks) {
		BuffBerserks = buffBerserks;
	}

	public boolean isWorldDelete(){
		return worldDelete;
	}
	
	public int getX(){
		return x;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public int getMap(){
		return map;
	}
	
	public void setMap(int map){
		this.map = map;
	}
	
	public int getHomeX() {
		return homeX;
	}

	public void setHomeX(int homeX) {
		this.homeX = homeX;
	}

	public int getHomeY() {
		return homeY;
	}

	public void setHomeY(int homeY) {
		this.homeY = homeY;
	}

	public int getHomeMap() {
		return homeMap;
	}

	public void setHomeMap(int homeMap) {
		this.homeMap = homeMap;
	}

	public int getHomeLoc() {
		return homeLoc;
	}

	public void setHomeLoc(int homeLoc) {
		this.homeLoc = homeLoc;
	}

	public int getHomeHeading() {
		return homeHeading;
	}

	public void setHomeHeading(int homeHeading) {
		this.homeHeading = homeHeading;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public long getObjectId(){
		return objectId;
	}
	
	public void setObjectId(long objectId){
		this.objectId = objectId;
	}
	
	public void setClassSex(int classSex){
		this.classSex = classSex;
	}
	
	public int getClassSex(){
		return classSex;
	}
	
	public void setClassType(int classType){
		this.classType = classType;
	}
	
	public int getClassType(){
		return classType;
	}
	
	public void setGfx(int gfx){
		this.gfx = gfx;
	}
	
	public int getGfx(){
		return gfx;
	}
	
	public int getGfxMode() {
		return dead ? 8 : gfxMode;
	}

	public void setGfxMode(int gfxMode) {
		this.gfxMode = gfxMode;
	}

	public int getClassGfx() {
		return classGfx;
	}

	public void setClassGfx(int classGfx) {
		this.classGfx = classGfx;
	}

	public int getClassGfxMode() {
		return classGfxMode;
	}

	public void setClassGfxMode(int classGfxMode) {
		this.classGfxMode = classGfxMode;
	}
	
	public int getLawful() {
		return lawful;
	}

	public void setLawful(int lawful) {
		if(lawful>Lineage.LAWFUL){
			lawful = Lineage.LAWFUL;
		}else if(lawful<Lineage.CHAOTIC){
			lawful = Lineage.CHAOTIC;
		}
		this.lawful = lawful;
	}

	public boolean isDead() {
		return dead;
	}

	/**
	 * 객체가 죽은 상태인지 설정하는 메서드.
	 * @param dead
	 */
	public void setDead(boolean dead) {
		if(!this.dead && dead && !worldDelete){
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 8), true);
			// 동적값 갱신. 죽은 객체는 해당좌표에 객체가 없는것으로 판단해야함.
			World.update_mapDynamic(x, y, map, false);
		}
		this.dead = dead;
	}

	public int getHeading() {
		return heading;
	}

	public void setHeading(int heading) {
		if(heading<0 || heading>7)
			heading = 0;
		this.heading = heading;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isBrave() {
		return brave;
	}

	public void setBrave(boolean brave) {
		this.brave = brave;
	}
	
	
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getTempCount() {
		return tempCount;
	}

	public void setTempCount(int tempCount) {
		this.tempCount = tempCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}
	
	public long getOwnObjectId(){
		return own_objectId;
	}
	
	public void setOwnObjectId(long own_objectId){
		this.own_objectId = own_objectId;
	}
	
	public String getOwnName(){
		return own_name;
	}
	
	public void setOwnName(String own_name){
		this.own_name = own_name;
	}
	
	public void setMoving(boolean moving){
		this.moving = moving;
	}
	
	public void setFight(boolean fight){
		this.fight = fight;
	}
	
	public boolean isInvis() {
		return invis;
	}

	public void setInvis(boolean invis) {
		this.invis = invis;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isPoison() {
		return poison;
	}

	public void setPoison(boolean poison) {
		this.poison = poison;
	}

	public void removeAllList(object o){
		allList.remove(o);
	}
	
	public void removeInsideList(object o){
		insideList.remove(o);
	}
	
	public void appendAllList(object o){
		allList.add(o);
	}
	
	public void appendInsideList(object o){
		insideList.add(o);
	}
	
	public boolean isContainsAllList(object o){
		return allList.contains(o);
	}
	
	public boolean isContainsInsideList(object o){
		return insideList.contains(o);
	}
	
	public object findInsideList(long object_id){
		for(object o : insideList){
			if(o.getObjectId()==object_id)
				return o;
		}
		return null;
	}
	
	public void findInsideList(int x, int y, List<object> r_list){
		for(object o : insideList){
			if(o.getX()==x && o.getY()==y)
				r_list.add(o);
		}
	}
	
	public List<object> getInsideList(){
		return insideList;
	}
	
	public List<object> getAllList(){
		return allList;
	}
	
	public void setTempHp(int hp){ temp_hp = hp; }
	public void setTempMp(int mp){ temp_mp = mp; }

	public void setLockLow(boolean lock){ lock_low = lock; }
	public void setLockHigh(boolean lock){ lock_high = lock; }
	public boolean isLockLow(){ return lock_low; }
	public boolean isLockHigh(){ return lock_high; }
	public boolean isLock(){ return lock_low || lock_high; }
	
	public void setNowHp(int nowhp){}
	public void setMaxHp(int maxhp){}
	public int getMaxHp(){ return 0; }
	public void setNowMp(int nowmp){}
	public void setMaxMp(int maxmp){}
	public int getMaxMp(){ return 0; }
	public int getLevel(){ return 0; }
	public int getNowHp(){ return 0; }
	public int getNowMp(){ return 0; }
	public int getGm(){ return 0; }
	public void setGm(int gm){}
	public void setInventory(Inventory inv){}
	public Inventory getInventory(){ return null; }
	public long getPartyId(){ return 0; }
	public void setPartyId(long partyId){}
	public void setReSpawnTime(int reSpawnTime) {}

	/**
	 * 객체 그리는 패킷에서 호출해서 사용.
	 * SObjectAdd
	 * @param o
	 */
	public int getStatus(object o){
		int status = 0;
		if(poison)
			status += 1;
		if(brave){
			switch(classType){
				case Lineage.LINEAGE_CLASS_ELF:
					status += Lineage.elven_wafer_frame ? 48 : 16;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					status += Lineage.holywalk_frame ? 64 : 16;
					break;
				default:	// 군주 + 기사 + 용기사 + 환술사 + 기타객체
					status += Lineage.bravery_potion_frame ? 16 : 48;
					break;
			}
		}
		if(isLock())
			status += 8;
		if(invis)
			status += 2;
		if(transparent){
			status += 128;
			// 잭렌턴일경우 상대방이 호박 가면을 착용중인지에 따라 상태 변경.
			if(this instanceof JackLantern && o instanceof PcInstance && o.getInventory()!=null){
				ItemInstance helm = o.getInventory().getSlot(Lineage.SLOT_HELM);
				if(helm!=null && helm.getItem().getNameIdNumber()==2067)
					status -= 128;
			}
		}
		if(this instanceof PcInstance)
			status += 4;
		return status;
	}
	
	/**
	 * 사용자 객체 정령속성 타입값 확인하는 함수.
	 * @return
	 */
	public int getAttribute(){ return Lineage.ELEMENT_NONE; }
	
	/**
	 * 문 객체가 오버리드해서 사용하며,
	 * 문이 닫혀있는지 알려주는 함수.
	 * @return
	 */
	public boolean isDoorClose(){ return false; }
	
	/**
	 * 월드에있는 객체를 클릭했을때 호출되는 메서드.
	 *  : door클릭하면 호출됨.
	 *  : 아이템 더블클릭하면 호출됨.
	 * @param cha
	 * @param cbp
	 */
	public void toClick(Character cha, ClientBasePacket cbp){}
	
	/**
	 * 매개변수 객체 에게 근접 및 장거리 물리공격을 가할때 처리하는 메서드.
	 * @param o
	 * @param x
	 * @param y
	 * @param bow
	 */
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg){}
	
	/**
	 * 특정 npc를 클릭했을경우 대화를 요청처리하는 메서드.
	 * @param pc
	 * @param cbp
	 */
	public void toTalk(PcInstance pc, ClientBasePacket cbp){}
	
	/**
	 * 특정 npc를 클릭했을경우 대화를 요청처리하는 메서드.
	 * @param pc
	 * @param action
	 * @param type
	 * @param cbp
	 */
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){}
	
	/**
	 * 성에 세율 변경요청 처리 함수.
	 * @param pc
	 * @param tax_rate
	 */
	public void toTaxSetting(PcInstance pc, int tax_rate){}
	
	/**
	 * 공금 입금 요청 처리 함수.
	 * @param pc
	 * @param count
	 */
	public void toTaxPut(PcInstance pc, int count){}
	
	/**
	 * 공금 출금 요청 처리 함수.
	 * @param pc
	 * @param count
	 */
	public void toTaxGet(PcInstance pc, int count){}
	
	/**
	 * 상점 및 창고에서 물품 처리시 호출하는 메서드.
	 * @param pc
	 * @param cbp
	 */
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp){}
	
	/**
	 * 던전 이동 및 npc를 이용한 텔레포트시 호출해서 사용함.
	 * @param x
	 * @param y
	 * @param map
	 */
	public void toPotal(int x, int y, int map){}
	
	/**
	 * 객체정보를 초기화할때 사용하는 메서드. 
	 *  : pc에서는 케릭터가 죽고 리스하거나 종료할때 호출해서 상태변환용으로도 사용.
	 * @param world_out
	 */
	public void toReset(boolean world_out){}
	
	/**
	 * 부활 처리 메서드.
	 */
	public void toRevival(object o){}
	
	/**
	 * 부활 처리 메서드.
	 */
	public void toRevivalFinal(object o){}

	/**
	 * 경험치 등록처리 함수.
	 * @param o
	 * @param exp
	 */
	public void toExp(object o, double exp){}
	
	/**
	 * 오토루팅 리능 사용여부 처리 함수.
	 * @return
	 */
	public boolean isAutoPickup(){ return false; }
	
	/**
	 * 오토루팅 리능 사용여부 처리 함수.
	 * @param is
	 */
	public void setAutoPickup(boolean is){}
	
	/**
	 * hp바를 표현할지 여부를 리턴함.
	 * @return
	 */
	public boolean isHpbar() { return false; }
	
	/**
	 * hp바를 머리위에 표현할지를 설정처리하는 함수.
	 * @param is
	 */
	public void setHpbar(boolean is){}

	/**
	 * 교환 처리가 취소됫다면 호출됨.
	 *  : lineage.bean.lineage.Trade.toCancel() 에서 호출함.
	 */
	public void toTradeCancel(Character cha){}
	
	/**
	 * 교환 처리가 성공했다면 호출됨.
	 *  : lineage.bean.lineage.Trade.toOk() 에서 호출함.
	 */
	public void toTradeOk(Character cha){}
	
	/**
	 * 다른 사용자가 강제적으로 아이템을 넘기려할때 호출되는 메서드.
	 * @param o
	 * @param item
	 * @param count
	 */
	public void toGiveItem(object o, ItemInstance item, long count){
		if(getInventory()==null || item.isEquipped() || count<=0 || item.getCount()<count)
			return;
		
		// 지급될 아이템 이름.
		String item_name = item.getName();
		// 아이템 던진거 알리기용.
		if(o!=null && o instanceof Character && this instanceof Character)
			item.toGiveItem((Character)o, (Character)this);
		// 인벤에서 겹칠수 있는 아이템 찾기
		ItemInstance temp = getInventory().find(item.getItem().getName(), item.getBress(), true);
		if(temp!=null){
			// 존재하면 갯수 갱신
			getInventory().count(temp, temp.getCount()+count, true);
			if(o!=null && o.getInventory()!=null)
				// 던진놈의 아이템 갯수도 갱신
				o.getInventory().count(item, item.getCount()-count, true);
			else
				ItemDatabase.setPool(item);
		}else{
			// 객체아이디값이 설정안된 상태일경우 세팅.
			if(item.getObjectId()<=0)
				item.setObjectId(ServerDatabase.nextItemObjId());
			if(item.getCount()-count<=0){
				// 전체 이동
				temp = item;
				if(o!=null && o.getInventory()!=null)
					// 던진놈의 인벤에서 제거.
					o.getInventory().remove(item, true);
			}else{
				// 일부분 이동
				temp = ItemDatabase.newInstance(item);
				temp.setObjectId(ServerDatabase.nextItemObjId());
				temp.setCount(count);
				if(o!=null && o.getInventory()!=null)
					// 던진놈의 아이템 갯수 갱신
					o.getInventory().count(item, item.getCount()-count, true);
				else
					ItemDatabase.setPool(item);
			}
			// 처리할 아이템 새로 등록.
			getInventory().append(temp, true);
		}
		
		if(o==null || o instanceof ItemInstance)
			// %0%o 얻었습니다.
			toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 403, count==1 ? item_name : String.format("%s (%d)", item_name, count)));
		else
			// \f1%0%s 당신에게 %1%o 주었습니다.
			toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, o.getName(), count==1 ? item_name : String.format("%s (%d)", item_name, count)));
	}
	
	/**
	 * 아이템이 강제로 넘어간걸 알리는 용
	 * @param cha		: 던진 객체
	 * @param target	: 받은 객체
	 */
	public void toGiveItem(Character cha, Character target){}
	
	/**
	 * 다른 객체로부터 데미지를 입었을때 호출됨.
	 * @param cha	: 가격자.
	 * @param dmg	: 입혀진 데미지.
	 * @param type	: 공격 방식.
	 */
	public void toDamage(Character cha, int dmg, int type){}
	
	/**
	 * HyperText 대한 요청 처리.
	 * @param pc
	 * @param count
	 * @param a
	 * @param request
	 */
	public void toHyperText(PcInstance pc, ClientBasePacket cbp){}
	
	/**
	 * CharacterController에 등록된 객체는 해당 함수가 주기적으로 호출됨.
	 * @param time
	 */
	public void toTimer(long time){}

	/**
	 * 해당 객체가 픽업됫을때 호출되는 메서드.
	 * @param cha
	 */
	public void toPickup(Character cha){}
	
	/**
	 * Felix: 디텍션과 같은 마법 당햇을때 호출됨.
	 */
	public void toMagicalAttackEncounters(Character cha){}
	
	/**
	 * 매개변수인 pc가 일반 채팅을 하게되면 주변 객체를 검색하게되고<br>
	 * 주변객체들은 해당 메서드를 호출받게됨.<br>
	 * 해당 메서드를 이용해서 대화처리를 하면됨.
	 * @param pc
	 * @param msg
	 */
	public void toChatting(object o, String msg){}
	
	/**
	 * 문짝 센드처리함수.
	 *  : 객체가 이동중 해당 객체가 닫혀있는지 여부를 확인. 닫혀잇다면 해당 함수를 호출하여
	 *    사용자가 해당 필드에 위치를 이동 불가능하도록 처리.
	 *  : Door이나 KingdomDoor 객체에 오버리드되서 사용할것이며, 1픽셀 값이상으로 이동 불가능하게 해야할 경우가 있음.
	 * @param o
	 */
	public void toDoorSend(object o){
		if(homeLoc > 0){
			for(int i=0 ; i<homeLoc ; ++i){
				switch(heading){
					case 2:	// 4방향으로 증가.
					case 6:
						if(o == null){
							toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y+i, heading, isDoorClose()), false);
						}else{
							o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y+i, heading, isDoorClose()));
						}
						// 타일 변경.
						World.set_map(x, y+i, map, isDoorClose() ? 16 : homeTile);
						World.set_map(x-1, y+i, map, isDoorClose() ? 16 : homeTile);
						break;
					case 4:	// 6방향으로 증가.
						if(o == null){
							toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x-i, y, heading, isDoorClose()), false);
						}else{
							o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x-i, y, heading, isDoorClose()));
						}
						// 타일 변경.
						World.set_map(x-i, y, map, isDoorClose() ? 16 : homeTile);
						World.set_map(x-i, y+1, map, isDoorClose() ? 16 : homeTile);
						break;
				}
			}
		}else{
			if(o == null){
				toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), this), false);
			}else{
				o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), this));
			}
			// 타일 변경.
			switch(heading){
				case 2:
				case 6:
					World.set_map(x, y, map, isDoorClose() ? 16 : homeTile);
					World.set_map(x-1, y, map, isDoorClose() ? 16 : homeTile);
					break;
				case 4:	// 6방향으로 증가.
					World.set_map(x, y, map, isDoorClose() ? 16 : homeTile);
					World.set_map(x, y+1, map, isDoorClose() ? 16 : homeTile);
					break;
			}
		}
	}
	
	/**
	 * 인공지능 활성화된 객체를 인공지능 처리목록에서 제거를 원할때 외부에서 호출해서 사용할수 있도록 함수 제공.
	 *  : toAiSpawn함수를 이용할경우 재스폰값이 0일때만 제거처리함.
	 *  : 해당 함수를 이용하면 재스폰값이 0이상이더라도 그냥 제거함.
	 */
	public void toAiThreadDelete(){
		// 주변객체 관리 제거
		clearList(true);
		// 월드에서 제거
		World.remove(this);
		// 상태 변경. 그래야 인공지능 쓰레드에서 제거됨.
		// 그후 풀에 등록함.
		setAiStatus(Lineage.AI_STATUS_DELETE);
	}
	
	/**
	 * 랜덤워킹 처리 함수.
	 */
	protected void toAiWalk(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
	}
	
	/**
	 * 전투 처리 함수.
	 */
	public void toAiAttack(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_ATTACK);
	}
	
	/**
	 * 죽은 객체 처리 함수.
	 */
	protected void toAiDead(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_DEAD);
	}
	
	/**
	 * 시체 유지 및 제거 처리 함수.
	 */
	protected void toAiCorpse(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_DEAD);
	}
	
	/**
	 * 재스폰 처리 함수.
	 */
	protected void toAiSpawn(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_DEAD);
	}
	
	/**
	 * 도망가기 처리 함수.
	 */
	public void toAiEscape(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
	}
	
	/**
	 * 아이템 줍기 처리 함수.
	 */
	protected void toAiPickup(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_GET);
	}
	/**
	 * Felix: 인공지능쪽에서 사용되며, 휴식중인지 여부 리턴.
	 * @return
	 */
	public boolean isRecess(){ return false; }
	
	/**
	 * Felix: 인공지능쪽에서 사용되며, 휴식을 취해야하는지 조건검색할때 사용함.
	 * @param time
	 * @return
	 */
	public boolean isRecess(long time){ return false; }
	
	/**
	 * Felix: 인공지능 메서드, 휴식중일때 주기적으로 호출됨.
	 */
	protected void toAiRecess(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_HIDE);
	}
	/**
	 * 인공지능 처리 요청 함수.
	 * @param time
	 */
	public void toAi(long time){
		ai_start_time = time;

		// 몬스터일경우 각 몬스터별로 인공지능 처리하기위해 확인.
		MonsterInstance mi = null;
		if(this instanceof MonsterInstance)
			mi = (MonsterInstance)this;
		
		// 일반 적인 인공지능 패턴
		// 랜덤워킹, 죽은거체크, 시체유지, 도망가기, 스폰멘트, 죽을때멘트, 공격할때멘트
		switch(ai_status){
			case Lineage.AI_STATUS_DELETE:
				break;
			case Lineage.AI_STATUS_WALK:
				toAiWalk(time);
				break;
			case Lineage.AI_STATUS_ATTACK:
				if(mi != null){
					switch(mi.getMonster().getNameIdNumber()){
						case 6:		// 괴물 눈
							FloatingEye.toAiAttack(mi, time);
							break;
						default:
							toAiAttack(time);
							break;
					}
				}else{
					toAiAttack(time);
				}
				break;
			case Lineage.AI_STATUS_DEAD:
				toAiDead(time);
				break;
			case Lineage.AI_STATUS_RECESS: // Felix: add recess mode
				toAiRecess(time);
				break;
			case Lineage.AI_STATUS_CORPSE:
				toAiCorpse(time);
				break;
			case Lineage.AI_STATUS_SPAWN:
				toAiSpawn(time);
				break;
			case Lineage.AI_STATUS_ESCAPE:
				if(mi != null){
					switch(mi.getMonster().getNameIdNumber()){
						case 6:		// 괴물 눈
							FloatingEye.toAiEscape(mi, time);
							break;
						default:
							toAiEscape(time);
							break;
					}
				}else{
					toAiEscape(time);
				}
				break;
			case Lineage.AI_STATUS_PICKUP:
				toAiPickup(time);
				break;
			default:
				ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
				break;
		}
	}

	/**
	 * 주변객체에게 전송하면서 나에게도 전송할지 여부
	 * @param bp
	 * @param me
	 */
	public void toSender(BasePacket bp, boolean me){
		if(bp instanceof ServerBasePacket){
			ServerBasePacket sbp = (ServerBasePacket)bp;
			for(object o : insideList){
				if(o instanceof PcInstance)
					o.toSender( ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()) );
			}
			if(me)
				toSender(bp);
			else
				BasePacketPooling.setPool(bp);
		}else{
			BasePacketPooling.setPool(bp);
		}
	}
	
	/**
	 * 패킷 전송 처리
	 * @param bp
	 */
	public void toSender(BasePacket bp){
		// 풀에 다시 넣기
		BasePacketPooling.setPool(bp);
	}
	
	/**
	 * 이동 요청 처리 함수.
	 * @param x
	 * @param y
	 * @param h
	 */
	public void toMoving(final int x, final int y, final int h){
		// 동적값 갱신.
		if(	!transparent )
			World.update_mapDynamic(this.x, this.y, this.map, false);
		// 좌표 변경.
		this.x = x;
		this.y = y;
		this.heading = h;
		// 동적값 갱신.
		if(	!transparent )
			World.update_mapDynamic(x, y, map, true);
		// 주변객체 갱신
		if( !Util.isDistance(tempX, tempY, map, x, y, map, Lineage.SEARCH_LOCATIONRANGE) ){
			tempX = x;
			tempY = y;
			// 이전에 관리중이던 목록 갱신
			for(object o : allList)
				o.removeAllList(this);
			allList.clear();
			// 객체 갱신
			temp_list.clear();
			World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp_list);
			for(object o : temp_list){
				if(isList(o)){
					// 전체 관리목록에 등록.
					appendAllList(o);
					o.appendAllList(this);
				}
			}
			// 이벤트 처리 요청.
			if(this instanceof PcInstance)
				EventController.toUpdate((PcInstance)this);
		}
		// 주변객체 패킷 및 갱신 처리
		for(object o : allList){
			if(Util.isDistance(this, o, Lineage.SEARCH_LOCATIONRANGE)){
				if(isContainsInsideList(o)){
					if(o instanceof PcInstance)
						o.toSender( S_ObjectMoving.clone(BasePacketPooling.getPool(S_ObjectMoving.class), this) );
				}else{
					appendInsideList(o);
					o.appendInsideList(this);
					if(this instanceof PcInstance){
						toSender( S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), o, this) );
						if(o.isDoorClose())
							o.toDoorSend(this);
					}
					if(o instanceof PcInstance){
						o.toSender( S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, o) );
					}
				}
			}else{
				if(isContainsInsideList(o)){
					removeInsideList(o);
					o.removeInsideList(this);
					if(this instanceof PcInstance)
						toSender( S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), o) );
					if(o instanceof PcInstance)
						o.toSender( S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), this) );
				}
			}
		}
	}
	
	/**
	 * 텔레포트 처리 함수.
	 * @param x
	 * @param y
	 * @param map
	 * @param effect
	 */
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		if(effect)
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 169), this instanceof PcInstance);
		// 이전에 관리중이던 목록 제거
		clearList(true);
		// 월드에 갱신
		World.remove(this);
		// 동적값 갱신.
		if(	(this instanceof Character || this instanceof TeleportInstance || this instanceof DwarfInstance || this instanceof ShopInstance || 
			(this instanceof BackgroundInstance && !(this instanceof Switch) && !(this instanceof Firewall) && !(this instanceof LifeStream)) ||
			this instanceof BoardInstance) && !transparent	){
			World.update_mapDynamic(this.x, this.y, this.map, false);
		}
		// 좌표 변경.
		this.x = x;
		this.y = y;
		this.map = map;
		tempX = x;
		tempY = y;
		// 동적값 갱신.
		if(	(this instanceof Character || this instanceof TeleportInstance || this instanceof DwarfInstance || 
				this instanceof ShopInstance || (this instanceof BackgroundInstance && !(this instanceof Switch)) ||
				this instanceof BoardInstance) && !transparent	)
			World.update_mapDynamic(x, y, map, true);
		// 월드에 갱신.
		World.append(this);
		// 패킷 처리
		if(this instanceof PcInstance){
			toSender( S_ObjectMap.clone(BasePacketPooling.getPool(S_ObjectMap.class), this) );
			toSender( S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this) );
		}
		// 객체 갱신
		temp_list.clear();
		World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp_list);
		// 순회
		for(object o : temp_list){
			if(isList(o)){
				// 전체 관리목록에 등록.
				appendAllList(o);
				o.appendAllList(this);
				// 화면내에 있을경우
				if(Util.isDistance(this, o, Lineage.SEARCH_LOCATIONRANGE)){
					// 화면내에 관리 목록에 등록
					appendInsideList(o);
					o.appendInsideList(this);
					// 사용자들 패킷 처리
					if(this instanceof PcInstance){
						toSender( S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), o, this) );
						if(o.isDoorClose())
							o.toDoorSend(this);
					}
					if(o instanceof PcInstance){
						o.toSender( S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, o) );
					}
				}
			}
		}
	}

	/**
	 * 관리중이던 객체 초기화처리 하는 함수.
	 */
	public void clearList(boolean packet){
		try {
			// LOCATIONRANGE 셀 내에 있는 목록들 둘러보면서 나를 제거.
			for( object o : insideList ){
				o.removeInsideList(this);
				if(packet && o instanceof PcInstance)
					o.toSender( S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), this) );
			}
			for( object o : allList ){
				o.removeAllList(this);
			}
			allList.clear();
			insideList.clear();
			World.update_mapDynamic(x, y, map, false);
		} catch (Exception e) { }
	}
	
	/**
	 * 인공지능.
	 * 객체의 인공지능이 활성화해야 할 시간이 됐는지 확인하는 메서드.
	 */
	public boolean isAi(long time){
		long speed = ai_time;
		long temp = time-ai_start_time;
		
		switch(this.speed){
			case 1:
				speed -= (long)(speed*0.3);
				break;
			case 2:
				speed += (long)(speed*0.3);
				break;
		}

//		if(time==0 || (temp>=speed && ai_time<=temp)){
		if(time==0 || temp>=speed){
			ai_start_time = time;
			// 락걸린 상태라면 true가 리턴되며, ai를 활성화 하면 안되기때문에 false로 변환해야됨.
			return !isLock();
		}

		return false;
	}
	
	/**
	 * 월드에서 객체를 추출한후 해당 객체를 관리목록에 등록할지 여부를 이 함수가 판단.
	 *  : 텔레포트후, 이동후 등에서 호출해서 사용중.
	 * @param o
	 * @return
	 */
	private boolean isList(object o){
		if(getObjectId()==o.getObjectId())
			return false;
		
		// 사용자일경우 무조건 등록.
		if(this instanceof PcInstance || o instanceof PcInstance)
			return true;
		// 펫일 경우.
		if(this instanceof PetInstance)
			// 아이템
			return o instanceof ItemInstance;
		// 몬스터일 경우.
		if(this instanceof MonsterInstance){
			MonsterInstance mon = (MonsterInstance)this;
			// 아이템
			if(o instanceof ItemInstance)
				return mon.getMonster().isPickup();
			// 동족
			if(o instanceof MonsterInstance)
				return mon.getMonster().getFamily().length()>0 && ((MonsterInstance)o).getMonster().getFamily().equalsIgnoreCase(mon.getMonster().getFamily());
		}
		// 스위치 일경우. (법사30퀘 스위치)
		if(this instanceof Switch){
			// 문
			return o instanceof Door;
		}
		// 아이템일 경우
		if(this instanceof ItemInstance){
			if(o instanceof PetInstance)
				return true;
			if(o instanceof MonsterInstance)
				return ((MonsterInstance)o).getMonster().isPickup();
		}
		// 성문 일경우
		if(this instanceof KingdomDoor)
			return o instanceof KingdomGuard || o instanceof KingdomDoorman;
		// 성경비 일경우
		if(this instanceof KingdomGuard)
			return o instanceof KingdomDoor || o instanceof KingdomGuard || o instanceof KingdomCastleTop;
		// 경비 일경우
		if(this instanceof GuardInstance)
			return o instanceof GuardInstance && !(o instanceof KingdomGuard);
		// 수호탑일 경우
		if(this instanceof KingdomCastleTop)
			return o instanceof KingdomGuard;
		// 성 문지기 일경우
		if(this instanceof KingdomDoorman)
			return o instanceof KingdomDoor;
		// 배경에쓰이는 객체일 경우
		if(this instanceof BackgroundInstance){
			// 파이어월 일경우 hp 처리하는 객체는 관리목록에 등록.
			if(this instanceof Firewall)
				return o instanceof Character;
			// 라이프 스트림역시~
			if(this instanceof LifeStream)
				return o instanceof Character;
		}
		return false;
	}

	public boolean isBoardView() {
		return this.isBoardView;
	}

	public void setBoardView(boolean paramBoolean) {
		this.isBoardView = paramBoolean;
	}

	/**
	 * .몹드랍
	 * 
	 * @return
	 */
	public long getDelaytime() {
		return this._delaytime;
	}

	public void setDelaytime(long paramLong) {
		this._delaytime = paramLong;
	}
	
	public boolean isQuestMonster() {
		return isQuestMonster;
	}

	public void setQuestMonster(boolean isQuestMonster) {
		this.isQuestMonster = isQuestMonster;
	}
}
