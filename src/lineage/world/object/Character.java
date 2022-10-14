package lineage.world.object;

import lineage.plugin.Plugin;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.BuffController;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.InnController;
import lineage.world.controller.KingdomController;

public class Character extends object {

	protected int level;
	protected int nowHp;
	protected int maxHp;
	private int dynamicHp;
	private int setitemHp;
	protected int nowMp;
	protected int maxMp;
	private int dynamicMp;
	private int setitemMp;
	private int ac;
	private int dynamicAc;
	private int setitemAc;
	private int Str;
	private int Con;
	private int Dex;
	private int Wis;
	private int Int;
	private int Cha;
	private int dynamicInt;
	private int dynamicStr;
	private int dynamicCon;
	private int dynamicDex;
	private int dynamicWis;
	private int dynamicCha;
	private int dynamicMr;
	private int dynamicSp;
	private int dynamicTicHp;
	private int dynamicTicMp;
	private int setitemInt;
	private int setitemStr;
	private int setitemCon;
	private int setitemDex;
	private int setitemWis;
	private int setitemCha;
	private int setitemMr;
	private int setitemSp;
	private int setitemTicHp;
	private int setitemTicMp;
	protected double exp;
	private int food;
	private double itemWeight;		// 동적인 아이템을 더 들수있게 하는 변수.
	private int TimeHpTic;			// 자연회복에 사용되는 변수
	private int TimeMpTic;			// 자연회복에 사용되는 변수
	private boolean hpMove;			// 자연회복에 사용되는 변수
	private boolean mpMove;			// 자연회복에 사용되는 변수
	private boolean hpFight;		// 자연회복에 사용되는 변수
	private boolean mpFight;		// 자연회복에 사용되는 변수
	protected int lvStr;			// 51이상 부터 찍는 레벨스탯 변수.
	protected int lvCon;
	protected int lvDex;
	protected int lvWis;
	protected int lvInt;
	protected int lvCha;
	private int dynamicEarthress;		// 땅 저항력
	private int dynamicWaterress;		// 물 저항력
	private int dynamicFireress;		// 불 저항력
	private int dynamicWindress;		// 바람 저항력
	private int setitemEarthress;		// 땅 저항력
	private int setitemWaterress;		// 물 저항력
	private int setitemFireress;		// 불 저항력
	private int setitemWindress;		// 바람 저항력
	private int earthress;		// 땅 저항력
	private int waterress;		// 물 저항력
	private int fireress;		// 불 저항력
	private int windress;		// 바람 저항력
	// 버프 딜레이 확인용 변수.
	public long delay_magic;
	// 동적 데미지 추가연산 처리 변수.
	private int dynamicAddDmg;		// 타격치
	private int dynamicAddDmgBow;	// 활타격치
	private int dynamicAddHit;		// 공성
	private int dynamicAddHitBow;	// 활명중
	private int recessCount;	// Felix: 인공지능 : 휴식상태 카운팅변수
	private boolean recess;		// Felix: 인공지능 : 휴식상태 여부

	public Character(){
		//
	}
	
	@Override
	public void close(){
		super.close();
		level = nowHp = maxHp = dynamicHp = nowMp = maxMp = dynamicMp = ac = dynamicAc =
		Str = Con = Dex = Wis = Int = Cha = dynamicInt = dynamicStr = dynamicCon = dynamicDex =
		dynamicWis = dynamicCha = dynamicTicHp = dynamicTicMp = lvStr = lvCon =
		lvDex = lvWis = lvInt = lvCha = dynamicSp = dynamicMr = food = dynamicEarthress = dynamicWaterress = 
		dynamicFireress = dynamicWindress = earthress = waterress = fireress = windress = dynamicAddDmg = 0;
		itemWeight = exp = setitemHp = 	setitemMp = setitemInt = setitemStr = setitemCon = setitemDex = setitemWis = 
		setitemCha = setitemMr = setitemSp = setitemTicHp = setitemTicMp = setitemEarthress = setitemWaterress = 
		setitemFireress = setitemWindress = dynamicAddDmgBow = dynamicAddHit = dynamicAddHit = dynamicAddHitBow = 0;
		hpMove = mpMove = hpFight = mpFight = false;
		delay_magic = 0;

		TimeHpTic = getHpTime();
		TimeMpTic = getMpTime();
	}

	public int getDynamicAddDmgBow() {
		return dynamicAddDmgBow;
	}

	public void setDynamicAddDmgBow(int dynamicAddDmgBow) {
		this.dynamicAddDmgBow = dynamicAddDmgBow;
	}

	public int getDynamicAddHit() {
		return dynamicAddHit;
	}

	public void setDynamicAddHit(int dynamicAddHit) {
		this.dynamicAddHit = dynamicAddHit;
	}

	public int getDynamicAddHitBow() {
		return dynamicAddHitBow;
	}

	public void setDynamicAddHitBow(int dynamicAddHitBow) {
		this.dynamicAddHitBow = dynamicAddHitBow;
	}

	public int getDynamicAddDmg() {
		return dynamicAddDmg;
	}

	public void setDynamicAddDmg(int dynamicAddDmg) {
		this.dynamicAddDmg = dynamicAddDmg;
	}

	public void setLevel(int level){
		this.level = level;
	}
	@Override
	public int getLevel(){
		return level;
	}
	@Override
	public void setNowHp(int nowHp){
		if(!dead){
			if(getTotalHp() < nowHp){
				nowHp = getTotalHp();
			}else if(nowHp <= 0){
				if(getGm()>=Lineage.GMCODE){
					nowHp = getTotalHp();
				}else{
					nowHp = 0;
					setDead(true);
				}
			}
			this.nowHp = nowHp;
		}
	}
	@Override
	public int getNowHp(){
		if(getTotalHp() < nowHp)
			nowHp = getTotalHp();
		return nowHp;
	}
	@Override
	public void setMaxHp(int maxHp){
		this.maxHp = maxHp;
	}
	
	@Override
	public int getMaxHp(){
		return maxHp;
	}
	public void setDynamicHp(int dynamicHp){
		this.dynamicHp = dynamicHp;
	}
	public int getDynamicHp(){
		return dynamicHp;
	}
	@Override
	public void setNowMp(int nowMp){
		if(!dead){
			if(getTotalMp() < nowMp){
				nowMp = getTotalMp();
			}else if(nowMp <= 0){
				nowMp = 0;
			}
			this.nowMp = nowMp;
		}
	}
	@Override
	public int getNowMp(){
		if(getTotalMp() < nowMp)
			nowMp = getTotalMp();
		return nowMp;
	}
	@Override
	public void setMaxMp(int maxMp){
		this.maxMp = maxMp;
	}
	@Override
	public int getMaxMp(){
		return maxMp;
	}
	public void setDynamicMp(int dynamicMp){
		this.dynamicMp = dynamicMp;
	}
	public int getDynamicMp(){
		return dynamicMp;
	}
	public void setAc(int ac){
		if(ac<0)
			ac = 0;
		if(ac > Lineage.MAX_AC)
			ac = Lineage.MAX_AC;
		this.ac = ac;
	}
	public int getAc(){
		return ac;
	}
	public void setDynamicAc(int dynamicAc){
		this.dynamicAc = dynamicAc;
	}
	public int getDynamicAc(){
		return dynamicAc;
	}
	public void setStr(int Str){
		this.Str = Str;
	}
	public int getStr(){
		return Str;
	}
	public void setCon(int Con){
		this.Con = Con;
	}
	public int getCon(){
		return Con;
	}
	public void setDex(int Dex){
		this.Dex = Dex;
	}
	public int getDex(){
		return Dex;
	}
	public void setWis(int Wis){
		this.Wis = Wis;
	}
	public int getWis(){
		return Wis;
	}
	public void setInt(int Int){
		this.Int = Int;
	}
	public int getInt(){
		return Int;
	}
	public void setCha(int Cha){
		this.Cha = Cha;
	}
	public int getCha(){
		return Cha;
	}
	public void setDynamicStr(int dynamicStr){
		this.dynamicStr = dynamicStr;
	}
	public int getDynamicStr(){
		return dynamicStr;
	}
	public void setDynamicCon(int dynamicCon){
		this.dynamicCon = dynamicCon;
	}
	public int getDynamicCon(){
		return dynamicCon;
	}
	public void setDynamicDex(int dynamicDex){
		this.dynamicDex = dynamicDex;
	}
	public int getDynamicDex(){
		return dynamicDex;
	}
	public void setDynamicWis(int dynamicWis){
		this.dynamicWis = dynamicWis;
	}
	public int getDynamicWis(){
		return dynamicWis;
	}
	public void setDynamicInt(int dynamicInt){
		this.dynamicInt = dynamicInt;
	}
	public int getDynamicInt(){
		return dynamicInt;
	}
	public void setDynamicCha(int dynamicCha){
		this.dynamicCha = dynamicCha;
	}
	public int getDynamicCha(){
		return dynamicCha;
	}
	public int getLvStr(){
		return lvStr;
	}
	public void setLvStr(int lvStr){
		this.lvStr = lvStr;
	}
	public int getLvCon(){
		return lvCon;
	}
	public void setLvCon(int lvCon){
		this.lvCon = lvCon;
	}
	public int getLvDex(){
		return lvDex;
	}
	public void setLvDex(int lvDex){
		this.lvDex = lvDex;
	}
	public int getLvWis(){
		return lvWis;
	}
	public void setLvWis(int lvWis){
		this.lvWis = lvWis;
	}
	public int getLvInt(){
		return lvInt;
	}
	public void setLvInt(int lvInt){
		this.lvInt = lvInt;
	}
	public int getLvCha(){
		return lvCha;
	}
	public void setLvCha(int lvCha){
		this.lvCha = lvCha;
	}
	public int getDynamicMr(){
		return dynamicMr;
	}
	public void setDynamicMr(int dynamicMr){
		this.dynamicMr = dynamicMr;
	}
	public int getDynamicSp(){
		return dynamicSp;
	}
	public void setDynamicSp(int dynamicSp){
		this.dynamicSp = dynamicSp;
	}
	public int getDynamicTicHp(){
		return dynamicTicHp;
	}
	public void setDynamicTicHp(int dynamicTicHp){
		this.dynamicTicHp = dynamicTicHp;
	}
	public int getDynamicTicMp(){
		return dynamicTicMp;
	}
	public void setDynamicTicMp(int dynamicTicMp){
		this.dynamicTicMp = dynamicTicMp;
	}
	public double getItemWeight(){
		return itemWeight;
	}
	public void setItemWeight(double itemWeight){
		this.itemWeight = itemWeight;
	}
	public double getExp(){
		return exp;
	}
	public void setExp(double exp){
		if(exp<0)
			exp = 0;
		this.exp = exp;
	}
	public int getFood(){
		return food;
	}
	public void setFood(int food){
		if(food>=Lineage.MAX_FOOD)
			food = Lineage.MAX_FOOD;
		if(food<0)
			food = 0;
		this.food = food;
	}
	public int getDynamicEarthress() {
		return dynamicEarthress;
	}
	public void setDynamicEarthress(int dynamicEarthress) {
		this.dynamicEarthress = dynamicEarthress;
	}
	public int getDynamicWaterress() {
		return dynamicWaterress;
	}
	public void setDynamicWaterress(int dynamicWaterress) {
		this.dynamicWaterress = dynamicWaterress;
	}
	public int getDynamicFireress() {
		return dynamicFireress;
	}
	public void setDynamicFireress(int dynamicFireress) {
		this.dynamicFireress = dynamicFireress;
	}
	public int getDynamicWindress() {
		return dynamicWindress;
	}
	public void setDynamicWindress(int dynamicWindress) {
		this.dynamicWindress = dynamicWindress;
	}
	public int getEarthress() {
		return earthress;
	}
	public void setEarthress(int earthress) {
		this.earthress = earthress;
	}
	public int getWaterress() {
		return waterress;
	}
	public void setWaterress(int waterress) {
		this.waterress = waterress;
	}
	public int getFireress() {
		return fireress;
	}
	public void setFireress(int fireress) {
		this.fireress = fireress;
	}
	public int getWindress() {
		return windress;
	}
	public void setWindress(int windress) {
		this.windress = windress;
	}

	public int getSetitemHp() {
		return setitemHp;
	}

	public void setSetitemHp(int setitemHp) {
		this.setitemHp = setitemHp;
	}

	public int getSetitemMp() {
		return setitemMp;
	}

	public void setSetitemMp(int setitemMp) {
		this.setitemMp = setitemMp;
	}

	public int getSetitemAc() {
		return setitemAc;
	}

	public void setSetitemAc(int setitemAc) {
		this.setitemAc = setitemAc;
	}

	public int getSetitemInt() {
		return setitemInt;
	}

	public void setSetitemInt(int setitemInt) {
		this.setitemInt = setitemInt;
	}

	public int getSetitemStr() {
		return setitemStr;
	}

	public void setSetitemStr(int setitemStr) {
		this.setitemStr = setitemStr;
	}

	public int getSetitemCon() {
		return setitemCon;
	}

	public void setSetitemCon(int setitemCon) {
		this.setitemCon = setitemCon;
	}

	public int getSetitemDex() {
		return setitemDex;
	}

	public void setSetitemDex(int setitemDex) {
		this.setitemDex = setitemDex;
	}

	public int getSetitemWis() {
		return setitemWis;
	}

	public void setSetitemWis(int setitemWis) {
		this.setitemWis = setitemWis;
	}

	public int getSetitemCha() {
		return setitemCha;
	}

	public void setSetitemCha(int setitemCha) {
		this.setitemCha = setitemCha;
	}

	public int getSetitemMr() {
		return setitemMr;
	}

	public void setSetitemMr(int setitemMr) {
		this.setitemMr = setitemMr;
	}

	public int getSetitemSp() {
		return setitemSp;
	}

	public void setSetitemSp(int setitemSp) {
		this.setitemSp = setitemSp;
	}

	public int getSetitemTicHp() {
		return setitemTicHp;
	}

	public void setSetitemTicHp(int setitemTicHp) {
		this.setitemTicHp = setitemTicHp;
	}

	public int getSetitemTicMp() {
		return setitemTicMp;
	}

	public void setSetitemTicMp(int setitemTicMp) {
		this.setitemTicMp = setitemTicMp;
	}

	public int getSetitemEarthress() {
		return setitemEarthress;
	}

	public void setSetitemEarthress(int setitemEarthress) {
		this.setitemEarthress = setitemEarthress;
	}

	public int getSetitemWaterress() {
		return setitemWaterress;
	}

	public void setSetitemWaterress(int setitemWaterress) {
		this.setitemWaterress = setitemWaterress;
	}

	public int getSetitemFireress() {
		return setitemFireress;
	}

	public void setSetitemFireress(int setitemFireress) {
		this.setitemFireress = setitemFireress;
	}

	public int getSetitemWindress() {
		return setitemWindress;
	}

	public void setSetitemWindress(int setitemWindress) {
		this.setitemWindress = setitemWindress;
	}

	public int getTotalStr(){ return Str + dynamicStr + lvStr + setitemStr; }
	public int getTotalDex(){ return Dex + dynamicDex + lvDex + setitemDex; }
	public int getTotalCon(){ return Con + dynamicCon + lvCon + setitemCon; }
	public int getTotalWis(){ return Wis + dynamicWis + lvWis + setitemWis; }
	public int getTotalInt(){ return Int + dynamicInt + lvInt + setitemInt; }
	public int getTotalCha(){ return Cha + dynamicCha + lvCha + setitemCha; }
	public int getTotalHp(){ return maxHp + dynamicHp + setitemHp; }
	public int getTotalMp(){ return maxMp + dynamicMp + setitemMp; }
	public int getTotalEarthress(){ return earthress + dynamicEarthress + setitemEarthress; }
	public int getTotalWaterress(){ return waterress + dynamicWaterress + setitemWaterress; }
	public int getTotalFireress(){ return fireress + dynamicFireress + setitemFireress; }
	public int getTotalWindress(){ return windress + dynamicWindress + setitemWindress; }
	public int getTotalAc(){
		int total_ac = ac + dynamicAc + setitemAc + (Lineage.is_dex_ac ? getAcDex() : 0);
		// -128
		return total_ac>138 ? 138 : total_ac;
	}

	// Felix: Recess Method
	@Override
	public boolean isRecess() {
		return recess;
	}

	public void setRecess(boolean recess) {
		if(recess){
			recessCount = 15;
		}
		this.recess = recess;
	}

	// Felix: 
	@Override
	protected void toAiRecess(long time){
		
		if(--recessCount<=0){
			recessCount = 15;
			setRecess(false);
			// setMoveRnd(100);
			// setMoveRndCount(5);
			setHeading(Util.random(0, 7));
		}

		// toItemDestroy(); // Handle Item
	}
	
	/**
	 * 현재 hp를 백분율로 연산하여 리턴함.
	 * @return
	 */
	public int getHpPercent() {
		double nowhp = getNowHp();
		double maxhp = getTotalHp();
		return (int)((nowhp/maxhp)*100);
	}


	/**
	 * 틱 타임 추출
	 */
	public int getHpTime() {
		int time = 42;
		if (food >= Lineage.MAX_FOOD * 0.16) {
			// 25
			time -= 15;
			if (food >= Lineage.MAX_FOOD * 0.5) {
				// 15
				time -= 8;
				if (food >= Lineage.MAX_FOOD * 0.96) {
					// 10
					time -= 5;
				}
			}
		}
		// 아지트좌표 일경우 틱처리
		if(AgitController.isAgitLocation(this))
			time -= 8;
		//세이프티존에서 틱처리
		if(World.isSafetyZone(getX(), getY(), getMap()))
			time -= 10;
		// 순수스탯에따른 틱감소하기
		time -= toOriginalStatHpTic();
		if(time<0)
			time = 0;
		return time;
	}

	/**
	 * 틱 타임 추출
	 */
	public int getMpTime() {
		int time = 45;
		if(food >= Lineage.MAX_FOOD*0.16){
			// 30
			time -= 8;
			if(food >= Lineage.MAX_FOOD*0.5){
				// 20
				time -= 5;
				if(food >= Lineage.MAX_FOOD*0.96){
					// 15
					time -= 5;
				}
			}
		}
		// 아지트좌표 일경우 틱처리
		if(AgitController.isAgitLocation(this))
			time -= 10;
		//세이프티존에서 틱처리
		if(World.isSafetyZone(getX(), getY(), getMap()))
			time -= 10;
		// 순수스탯에따른 틱감소하기
		time -= toOriginalStatMpTic();
		if(time<0)
			time = 0;
		return time;
	}
	
	public boolean isHpTic(){
		if(--TimeHpTic <= 0){
			moving = false;
			fight = false;
			hpFight = false;
			hpMove = false;
			TimeHpTic = getHpTime();
			return true;
		}
		return false;
	}
	
	public boolean isMpTic(){
		if(--TimeMpTic <= 0){
			moving = false;
			fight = false;
			mpFight = false;
			mpMove = false;
			TimeMpTic = getMpTime();
			return true;
		}
		return false;
	}
	
	/**
	 * 자연회복의 hp상승값을 리턴함.
	 */
	public int hpTic(){
		int tic = dynamicTicHp + setitemTicHp + 4;

		// 스탯에 따른 틱 계산
		switch(getTotalCon()){
			case 25:
			case 24:
				tic += 8;
			case 23:
			case 22:
				tic += 7;
			case 21:
			case 20:
				tic += 6;
			case 19:
			case 18:
				tic += 5;
			case 17:
			case 16:
				tic += 4;
			case 15:
			case 14:
			case 13:
			case 12:
				tic += 3;
			case 11:
			case 10:
			case 9:
			case 8:
			case 7:
			case 6:
				tic += 2;
			default:
				if(getTotalCon() > 25)
					tic += 10;
				else
					tic += 1;
		}

		// 여관맵일경우 틱처리
		if(InnController.isInnMap(this))
			tic += 20;

		// 네이처스 터치 상태일경우
//		if(isBuffNaturesTouch()){
//			int buff_tic = level - 9;
//			if(buff_tic>0){
//				if(buff_tic>15)
//					buff_tic = 15;
//				tic += buff_tic;
//			}
//		}
		
		// 설정된 틱에 -4 하여 랜덤값 추출
		tic = Util.random(tic-4, tic);

		// 0보다 작을경우 0을 리턴하도록 함.
		return tic<0 ? 0 : tic;
	}
	
	/**
	 * 자연회복의 mp상승값을 리턴함.
	 */
	public int mpTic(){
		int tic = dynamicTicMp + setitemTicMp;

		// 스탯에 따른 틱 계산
		switch(getTotalWis()){
			case 25:
			case 24:
				tic += 6;
			case 23:
			case 22:
			case 21:
				tic += 5;
			case 20:
			case 19:
			case 18:
				tic += 4;
			case 17:
			case 16:
			case 15:
				tic += 3;
			case 14:
			case 13:
			case 12:
				tic += 2;
			default:
				if(getTotalWis() > 25)
					tic += 7;
				else
					tic += 1;
		}

		// 여관맵일경우 틱처리
		if(InnController.isInnMap(this))
			tic += 20; //10
		// 아지트일경우 틱처리
		if(AgitController.isAgitLocation(this))
			tic += 10;	//3
		// 내성 틱처리
		if(KingdomController.isKingdomInsideLocation(this))
			tic += 10;	//3
		// 엄마나무 틱처리
		if(getClassType()==0x02 && ElvenforestController.isTreeZone(this))
			tic += 10;	//10

		// 0보다 작을경우 0을 리턴하도록 함.
		return tic<0 ? 0 : tic;
	}
	
	/**
	 * 클레스별 순수스탯에 따른 mpTic +@ 리턴
	 */
	int toOriginalStatMpTic(){
		int sum = 0;
		int wis = Wis;
		switch(classType){
			case Lineage.LINEAGE_CLASS_ROYAL:
				wis -= 11;
				if(wis>=2)
					sum += 1;
				if(wis>=4)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				wis -= 9;
				if(wis>=2)
					sum += 1;
				if(wis>=4)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				wis -= 12;
				if(wis>=3)
					sum += 1;
				if(wis>=6)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				wis -= 12;
				if(wis>=2)
					sum += 1;
				if(wis>=4)
					sum += 1;
				if(wis>=6)
					sum += 1;
				break;
		}
		return sum;
	}
	
	/**
	 * 클레스별 hp회복틱 리턴
	 */
	int toOriginalStatHpTic(){
		int sum = 0;
		int con = Con;
		switch(classType){
			case Lineage.LINEAGE_CLASS_ROYAL:
				con -= 10;
				if(con>=3)
					sum += 1;
				if(con>=5)
					sum += 1;
				if(con>=7)
					sum += 1;
				if(con>=8)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				con -= 14;
				if(con>=2)
					sum += 2;
				if(con>=4)
					sum += 2;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				con -= 12;
				if(con>=2)
					sum += 1;
				if(con>=4)
					sum += 1;
				if(con>=5)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				con -= 12;
				if(con>=5)
					sum += 1;
				if(con>=6)
					sum += 1;
				break;
		}
		return sum;
	}
	
	/**
	 * 클레스별 최대 무게소지 상승값 +@ 리턴
	 */
	public int toOriginalStatStrWeight(){
		// 플러그인 에서 추출.
		Plugin p = PluginController.find(Character.class);
		if(p != null)
			return ((lineage.plugin.bean.Character)p).toOriginalStatStrWeight(this);
		
		int sum = 0;
		int str = Str;
		switch(classType){
			case Lineage.LINEAGE_CLASS_ROYAL:
				str -= 13;
				if(str>=1)
					sum += 1;
				if(str>=4)
					sum += 1;
				if(str>=7)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				str -= 11;
				if(str>=5)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				str -= 8;
				if(str>=1)
					sum += 1;
				break;
		}
		return sum;
	}
	
	/**
	 * 클레스별 콘에따른 최대무게소지 상승값 +@ 리턴
	 */
	public int toOriginalStatConWeight(){
		int sum = 0;
		int con = Con;
		switch(classType){
			case Lineage.LINEAGE_CLASS_ROYAL:
				con -= 10;
				if(con>=1)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				con -= 14;
				if(con>=1)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				con -= 12;
				if(con>=3)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				con -= 12;
				if(con>=1)
					sum += 1;
				if(con>=3)
					sum += 1;
				break;
		}
		return sum;
	}

	/**
	 * 케릭터는 자연회복에 영향을 받는 클레스임.
	 * 그렇기 때문에 이동하는 초기 시점이라면 자연회복을 좀 지연해야됨.
	 */
	@Override
	public void setMoving(boolean moving) {
		if(!this.moving && moving){
			if(!hpMove){
				hpMove = true;
				TimeHpTic += 5;
			}
			if(!mpMove){
				mpMove = true;
				TimeMpTic += 5;
			}
		}
		super.setMoving(moving);
	}
	
	/**
	 * 케릭터는 자연회복에 영향을 받는 클레스임.
	 * 그렇기 때문에 전투가 활성화 되는 초기 시점이라면 자연회복을 좀 지연해야됨.
	 */
	@Override
	public void setFight(boolean fight){
		if(!this.fight && fight){
			if(!hpFight){
				hpFight = true;
				TimeHpTic += 15;
			}
			if(!mpFight){
				mpFight = true;
				TimeMpTic += 15;
			}
		}
		super.setFight(fight);
	}
	
	@Override
	public void toReset(boolean world_out){
		super.toReset(world_out);
		if(dead)
			// 버프제거..
			BuffController.removeAll(this);
	}
	
	/**
	 * 덱스에 따른 ac +@ 연산 리턴.
	 * @return
	 */
	private int getAcDex() {
		int total_dex = Dex + lvDex;
		int gab = total_dex>=18 ? 4 : total_dex<=17 && total_dex>=16 ? 5 : total_dex<=15 && total_dex>=13 ? 6 : total_dex<=12 && total_dex>=10 ? 7 : 8;
		return level / gab;
	}
	
}
