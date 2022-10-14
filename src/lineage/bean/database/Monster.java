package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class Monster {
	private String name;
	private String nameId;
	private int nameIdNumber;
	private int gfx;
	private int gfxMode;
	private int level;
	private int hp;
	private int mp;
	private int stealHp;
	private int stealMp;
	private int ticHp;
	private int ticMp;
	private int Str;
	private int Dex;
	private int Con;
	private int Int;
	private int Wis;
	private int Cha;
	private int mr;
	private int ac;
	private int exp;
	private int lawful;
	private String size;
	private String family;
	private int atkType;
	private int atkRange;
	private int arrowGfx;
	private boolean atkInvis;
	private boolean atkPoly;
	private boolean die;
	private boolean pickup;
	private boolean tame;
	private boolean revival;
	private boolean toughskin;
	private boolean adenDrop;
	private boolean buff;
	private int resistanceEarth;
	private int resistanceFire;
	private int resistanceWind;
	private int resistanceWater;
	private int resistanceUndead;
	private int msgAtkTime;
	private int msgDieTime;
	private int msgRecessTime; // Felix : add recess mode
	private int msgSpawnTime;
	private int msgEscapeTime;
	private int msgWalkTime;
	private List<String> msgAtk = new ArrayList<String>();
	private List<String> msgDie = new ArrayList<String>();
	private List<String> msgRecess = new ArrayList<String>(); // Felix : add recess mode
	private List<String> msgSpawn = new ArrayList<String>();
	private List<String> msgEscape = new ArrayList<String>();
	private List<String> msgWalk = new ArrayList<String>();
	private List<Drop> list = new ArrayList<Drop>();
	private List<MonsterSkill> list_skill = new ArrayList<MonsterSkill>();
	public int getNameIdNumber() {
		return nameIdNumber;
	}
	public void setNameIdNumber(int nameIdNumber) {
		this.nameIdNumber = nameIdNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameId() {
		return nameId;
	}
	public void setNameId(String nameId) {
		this.nameId = nameId;
	}
	public int getGfx() {
		return gfx;
	}
	public void setGfx(int gfx) {
		this.gfx = gfx;
	}
	public int getGfxMode() {
		return gfxMode;
	}
	public void setGfxMode(int gfxMode) {
		this.gfxMode = gfxMode;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getStealHp() {
		return stealHp;
	}
	public void setStealHp(int stealHp) {
		this.stealHp = stealHp;
	}
	public int getStealMp() {
		return stealMp;
	}
	public void setStealMp(int stealMp) {
		this.stealMp = stealMp;
	}
	public int getTicHp() {
		return ticHp;
	}
	public void setTicHp(int ticHp) {
		this.ticHp = ticHp;
	}
	public int getTicMp() {
		return ticMp;
	}
	public void setTicMp(int ticMp) {
		this.ticMp = ticMp;
	}
	public int getStr() {
		return Str;
	}
	public void setStr(int str) {
		Str = str;
	}
	public int getDex() {
		return Dex;
	}
	public void setDex(int dex) {
		Dex = dex;
	}
	public int getCon() {
		return Con;
	}
	public void setCon(int con) {
		Con = con;
	}
	public int getInt() {
		return Int;
	}
	public void setInt(int i) {
		Int = i;
	}
	public int getWis() {
		return Wis;
	}
	public void setWis(int wis) {
		Wis = wis;
	}
	public int getCha() {
		return Cha;
	}
	public void setCha(int cha) {
		Cha = cha;
	}
	public int getMr() {
		return mr;
	}
	public void setMr(int mr) {
		this.mr = mr;
	}
	public int getAc() {
		return ac;
	}
	public void setAc(int ac) {
		this.ac = ac;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getLawful() {
		return lawful;
	}
	public void setLawful(int lawful) {
		this.lawful = lawful;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public int getAtkType() {
		return atkType;
	}
	public void setAtkType(int atkType) {
		this.atkType = atkType;
	}
	public int getAtkRange() {
		return atkRange;
	}
	public void setAtkRange(int atkRange) {
		this.atkRange = atkRange;
	}
	public boolean isAtkInvis() {
		return atkInvis;
	}
	public void setAtkInvis(boolean atkInvis) {
		this.atkInvis = atkInvis;
	}
	public boolean isAtkPoly() {
		return atkPoly;
	}
	public void setAtkPoly(boolean atkPoly) {
		this.atkPoly = atkPoly;
	}
	public boolean isDie() {
		return die;
	}
	public void setDie(boolean die) {
		this.die = die;
	}
	public boolean isPickup() {
		return pickup;
	}
	public void setPickup(boolean pickup) {
		this.pickup = pickup;
	}
	public boolean isTame() {
		return tame;
	}
	public void setTame(boolean tame) {
		this.tame = tame;
	}
	public boolean isRevival() {
		return revival;
	}
	public void setRevival(boolean revival) {
		this.revival = revival;
	}
	public boolean isToughskin() {
		return toughskin;
	}
	public void setToughskin(boolean toughskin) {
		this.toughskin = toughskin;
	}
	public boolean isAdenDrop() {
		return adenDrop;
	}
	public void setAdenDrop(boolean adenDrop) {
		this.adenDrop = adenDrop;
	}
	public int getResistanceEarth() {
		return resistanceEarth;
	}
	public void setResistanceEarth(int resistanceEarth) {
		this.resistanceEarth = resistanceEarth;
	}
	public int getResistanceFire() {
		return resistanceFire;
	}
	public void setResistanceFire(int resistanceFire) {
		this.resistanceFire = resistanceFire;
	}
	public int getResistanceWind() {
		return resistanceWind;
	}
	public void setResistanceWind(int resistanceWind) {
		this.resistanceWind = resistanceWind;
	}
	public int getResistanceWater() {
		return resistanceWater;
	}
	public void setResistanceWater(int resistanceWater) {
		this.resistanceWater = resistanceWater;
	}
	public int getResistanceUndead() {
		return resistanceUndead;
	}
	public void setResistanceUndead(int resistanceUndead) {
		this.resistanceUndead = resistanceUndead;
	}
	public List<String> getMsgAtk() {
		return msgAtk;
	}
	public List<String> getMsgDie() {
		return msgDie;
	}
	public List<String> getMsgSpawn() {
		return msgSpawn;
	}
	public List<String> getMsgEscape() {
		return msgEscape;
	}
	public List<String> getMsgWalk(){
		return msgWalk;
	}
	public List<Drop> getDropList(){
		return list;
	}
	public int getArrowGfx() {
		return arrowGfx;
	}
	public void setArrowGfx(int arrowGfx) {
		this.arrowGfx = arrowGfx;
	}
	public List<MonsterSkill> getSkillList(){
		return list_skill;
	}
	public int getMsgAtkTime() {
		return msgAtkTime;
	}
	public void setMsgAtkTime(int msgAtkTime) {
		this.msgAtkTime = msgAtkTime;
	}
	public int getMsgDieTime() {
		return msgDieTime;
	}
	public void setMsgDieTime(int msgDieTime) {
		this.msgDieTime = msgDieTime;
	}
	public int getMsgSpawnTime() {
		return msgSpawnTime;
	}
	public void setMsgSpawnTime(int msgSpawnTime) {
		this.msgSpawnTime = msgSpawnTime;
	}
	public int getMsgEscapeTime() {
		return msgEscapeTime;
	}
	public void setMsgEscapeTime(int msgEscapeTime) {
		this.msgEscapeTime = msgEscapeTime;
	}
	public int getMsgWalkTime() {
		return msgWalkTime;
	}
	public void setMsgWalkTime(int msgWalkTime) {
		this.msgWalkTime = msgWalkTime;
	}
	public boolean isBuff() {
		return buff;
	}
	public void setBuff(boolean buff) {
		this.buff = buff;
	}
	public int getMsgRecessTime() {
		return msgRecessTime;
	}
	public void setMsgRecessTime(int msgRecessTime) {
		this.msgRecessTime = msgRecessTime;
	}
	public List<String> getMsgRecess() {
		return msgRecess;
	}
	public void setMsgRecess(List<String> msgRecess) {
		this.msgRecess = msgRecess;
	}
}
