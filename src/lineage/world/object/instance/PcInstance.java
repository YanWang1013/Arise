package lineage.world.object.instance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Kingdom;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.database.TeleportResetDatabase;
import lineage.database.WebDatabase;
import lineage.network.Client;
import lineage.network.Server;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterExp;
import lineage.network.packet.server.S_CharacterHp;
import lineage.network.packet.server.S_CharacterMp;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_InterfaceRead;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectInvis;
import lineage.network.packet.server.S_ObjectLawful;
import lineage.network.packet.server.S_ObjectLight;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.network.packet.server.S_Potal;
import lineage.network.packet.server.S_Weather;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.CommandController;
import lineage.world.controller.DamageController;
import lineage.world.controller.DungeonController;
import lineage.world.controller.FriendController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.LetterController;
import lineage.world.controller.LocationController;
import lineage.world.controller.PartyController;
import lineage.world.controller.QuestController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.controller.UserShopController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.Criminal;
import lineage.world.object.monster.Blob;
import lineage.world.object.npc.background.Cracker;
import lineage.world.object.npc.teleporter.Esmereld;
import lineage.world.object.robot.BuffRobotInstance;

public class PcInstance extends Character {

	private Client client;
	private int attribute;					// 요정 클레스들 속성마법 값	1[물] 2[바람] 3[땅] 4[불]
	// 운영자 여부 판단 변수.
	private int gm;
	// 스피드핵 처리 참고용 변수
	private long FrameTime;
	private int MovingCounting;
	private int MovingWarningCounting;
	// 파티변수
	private long partyid;
	// 채팅 사용 유무
	private boolean chattingWhisper;
	private boolean chattingGlobal;
	private boolean chattingTrade;
	
	
	
	// 자신에 hp바를 머리위에 표현할지 여부.
	private boolean is_hpbar;
	
	// 피케이 처리 변수
	private int PkCount;		// 누적된 피케이 횟수.
	private long PkTime;		// 최근에 실행한 피케이 시간.
	private int DeathCount;					//데스카운트
	// 인벤토리
	private Inventory inv;
	// 차단된 이름 목록
	private List<String> listBlockName;
	private Esmereld npc_esmereld;	// 에스메랄다 npc 포인터
	// 홈페이지에 변수값 들
	private String webId;			// 아이디
	private String webName;			// 이름
	private String webNick;			// 닉네임
	private String webBirth;		// 생년월일
	private int webSex;				// 성별
	// 딜레이용
	private long message_time;		// 슈롬 메세지가 표현된 마지막 시간 저장.
	private long auto_save_time;	// 자동저장 이전시간 기록용.
	private long premium_item_time;	// 자동지급 시간 저장.
	// 로그 기록용
	private long register_date;		// 케릭 생성 시간
	// 죽으면서 착감된 경험치 실시간 기록용.
	private double lost_exp;
	// 오토루팅 처리할지 여부를 확인할 변수.
	private boolean auto_pickup;
	// 인터페이스 및 인벤 정보.
	private byte[] db_interface;
	
	// 추가 경험치 지급처리에 사용되는 변수(버프에서 증가 혹은 감사시킴)
	private double dynamicExp;
	
	// 엘릭서 사용된 갯수
	private int elixir;
	
	
	public PcInstance(Client client){
		this.client = client;
		listBlockName = new ArrayList<String>();
	}

	@Override
	public void close(){
		super.close();
		auto_pickup = false;
		chattingWhisper = chattingGlobal = chattingTrade = true;
		lost_exp = premium_item_time = register_date = auto_save_time = message_time = PkTime = partyid = FrameTime =
		attribute = MovingCounting = MovingWarningCounting = PkCount = webSex = gm = elixir = 0;
		inv = null;
		npc_esmereld = null;
		webId = webBirth = webName = webNick = null;
		db_interface = null;
		if(listBlockName != null)
			listBlockName.clear();
	}
	@Override
	public int getGm(){ return gm; }
	@Override
	public void setGm(int gm){ this.gm = gm; }
	public List<String> getListBlockName(){ return listBlockName; }
	public int getPkCount() { return PkCount; }
	public void setPkCount(int pkCount) { PkCount = pkCount; }
	public long getPkTime() { return PkTime; }
	public void setPkTime(long pkTime) { PkTime = pkTime; }
	@Override
	public long getPartyId(){ return partyid; }
	@Override
	public void setPartyId(long partyid){ this.partyid = partyid; }
	public Client getClient(){ return client; }
	@Override
	public Inventory getInventory(){ return inv; }
	@Override
	public void setInventory(Inventory inv){ this.inv = inv; }
	public boolean isChattingTrade() { return chattingTrade; }
	public void setChattingTrade(boolean chattingTrade) { this.chattingTrade = chattingTrade; }
	public boolean isChattingWhisper() { return chattingWhisper; }
	public void setChattingWhisper(boolean chattingWhisper) { this.chattingWhisper = chattingWhisper; }
	public boolean isChattingGlobal() { return chattingGlobal; }
	public void setChattingGlobal(boolean chattingGlobal) { this.chattingGlobal = chattingGlobal; }
	@Override
	public int getAttribute(){ return attribute; }
	public void setAttribute(final int attribute){ this.attribute = attribute; }
	public void setNpcEsmereld(Esmereld esmereld){ npc_esmereld = esmereld;}
	public Esmereld getNpcEsmereld(){ return npc_esmereld; }
	
	public double getDynamicExp() {
		return dynamicExp;
	}

	public void setDynamicExp(double dynamicExp) {
		this.dynamicExp = dynamicExp;
	}

	public String getWebId() {
		return webId;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public String getWebNick() {
		return webNick;
	}

	public void setWebNick(String webNick) {
		this.webNick = webNick;
	}

	public String getWebBirth() {
		return webBirth;
	}

	public void setWebBirth(String webBirth) {
		this.webBirth = webBirth;
	}

	public int getWebSex() {
		return webSex;
	}

	public void setWebSex(int webSex) {
		this.webSex = webSex;
	}

	public long getRegisterDate() {
		return register_date;
	}

	public void setRegisterDate(long register_date) {
		this.register_date = register_date;
	}

	public double getLostExp() {
		return lost_exp;
	}

	public void setLostExp(double lost_exp) {
		this.lost_exp = lost_exp;
	}

	@Override
	public void setHeading(int heading){
		super.setHeading(heading);
		if(!worldDelete)
			toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
	}

	@Override
	public void setLawful(int lawful){
		super.setLawful(lawful);
		if(!worldDelete)
			toSender(S_ObjectLawful.clone(BasePacketPooling.getPool(S_ObjectLawful.class), this), true);
	}
	
	@Override
	public void setNowHp(int nowhp){
		super.setNowHp(nowhp);
		if(!worldDelete){
			toSender(S_CharacterHp.clone(BasePacketPooling.getPool(S_CharacterHp.class), this));
			PartyController.toUpdate(this);
		}
	}
	
	@Override
	public void setNowMp(int nowmp){
		super.setNowMp(nowmp);
		if(!worldDelete)
			toSender(S_CharacterMp.clone(BasePacketPooling.getPool(S_CharacterMp.class), this));
	}

	@Override
	public void setLight(int light) {
		super.setLight(light);
		if(!worldDelete)
			toSender(S_ObjectLight.clone(BasePacketPooling.getPool(S_ObjectLight.class), this), true);
	}

	@Override
	public void setInvis(boolean invis) {
		super.setInvis(invis);
		if(!worldDelete)
			toSender(S_ObjectInvis.clone(BasePacketPooling.getPool(S_ObjectInvis.class), this), true);
	}
	
	@Override
	public void setFood(int food){
		super.setFood(food);
		if(!worldDelete)
			toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type){
		// 버그 방지 및 자기자신이 공격햇을경우 무시.
		if(cha==null || cha.getObjectId()==getObjectId())
			return;
		
		// 소환객체에게 알리기.
		SummonController.toDamage(this, cha, dmg);
		
		// 공격자가 사용자일때 구간.
		if(cha instanceof PcInstance){
			// 공격자가 보라돌이상태가 아니라면 보라도리로 변경하기.
			if(Lineage.server_version>182 && !cha.isBuffCriminal() && World.isNormalZone(getX(), getY(), getMap()))
				Criminal.init(cha);
			// 경비병에게 도움요청.
			DamageController.toGuardHelper(this, cha);
		}
		
		// 공격자가 브롭 이라면 일정확율 방어구 손상 시키기.
		if(cha instanceof Blob){
			for(int i=Lineage.SLOT_HELM ; i<Lineage.SLOT_NONE ; ++i){
				ItemInstance item = inv.getSlot(i);
				if(item!=null && item instanceof ItemArmorInstance && Util.random(0, 100)<=10){
					item.setDurability(item.getDurability() + 1);
					if(Lineage.server_version<=144){
						toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
						toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
					}else{
						toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
					}
					// \f1당신의 %0%s 손상되었습니다.
					toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 268, item.toString()));
					break;
				}
			}
		}
	}
	
	@Override
	public void setExp(double exp){
		if(isDead()){
			// 경험치 하향시키려 할때만.
			if(this.exp>exp) {
				this.exp = exp;
			return;
			}
		}
		
		Exp max = ExpDatabase.find(Lineage.level_max);
		Exp max_prev = ExpDatabase.find(Lineage.level_max-1);
		if(max!=null && max_prev!=null && exp>0 && level<=max.getLevel()){
			Exp e = ExpDatabase.find(level);
			if(max_prev.getBonus() >= exp){
				this.exp = exp;
			}else{
				this.exp = max_prev.getBonus();
			}
			if(e != null){
				boolean lvUp = e.getBonus() <= this.exp;
				if(lvUp){
					int hp = CharacterController.toStatusUP(this, true);
					int mp = CharacterController.toStatusUP(this, false);
					for(int i=1 ; i <= Lineage.level_max ; i++){
						e = ExpDatabase.find(i);
						if(getExp() < e.getBonus())
							break;
					}
					for(int i=e.getLevel()-level ; i > 1 ; i--){
						hp += CharacterController.toStatusUP(this, true);
						mp += CharacterController.toStatusUP(this, false);
					}
					int new_hp = getMaxHp()+hp;
					int new_mp = getMaxMp()+mp;

					switch(classType){
						case Lineage.LINEAGE_CLASS_ROYAL:
							if(new_hp >= Lineage.royal_max_hp)
								new_hp = Lineage.royal_max_hp;
							if(new_mp >= Lineage.royal_max_mp)
								new_mp = Lineage.royal_max_mp;
							break;
						case Lineage.LINEAGE_CLASS_KNIGHT:
							if(new_hp >= Lineage.knight_max_hp)
								new_hp = Lineage.knight_max_hp;
							if(new_mp >= Lineage.knight_max_mp)
								new_mp = Lineage.knight_max_mp;
							break;
						case Lineage.LINEAGE_CLASS_ELF:
							if(new_hp >= Lineage.elf_max_hp)
								new_hp = Lineage.elf_max_hp;
							if(new_mp >= Lineage.elf_max_mp)
								new_mp = Lineage.elf_max_mp;
							break;
						case Lineage.LINEAGE_CLASS_WIZARD:
							if(new_hp >= Lineage.wizard_max_hp)
								new_hp = Lineage.wizard_max_hp;
							if(new_mp >= Lineage.wizard_max_mp)
								new_mp = Lineage.wizard_max_mp;
							break;
						case Lineage.LINEAGE_CLASS_DARKELF:
							if(new_hp >= Lineage.darkelf_max_hp)
								new_hp = Lineage.darkelf_max_hp;
							if(new_mp >= Lineage.darkelf_max_mp)
								new_mp = Lineage.darkelf_max_mp;
							break;
						case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
							if(new_hp >= Lineage.dragonknight_max_hp)
								new_hp = Lineage.dragonknight_max_hp;
							if(new_mp >= Lineage.dragonknight_max_mp)
								new_mp = Lineage.dragonknight_max_mp;
							break;
						case Lineage.LINEAGE_CLASS_BLACKWIZARD:
							if(new_hp >= Lineage.blackwizard_max_hp)
								new_hp = Lineage.blackwizard_max_hp;
							if(new_mp >= Lineage.blackwizard_max_mp)
								new_mp = Lineage.blackwizard_max_mp;
							break;
					}

					setMaxHp( new_hp );
					setMaxMp( new_mp );
					setNowHp( getNowHp()+hp );
					setNowMp( getNowMp()+mp );
					setLevel( e.getLevel() );

					// 레벨 51이상시 스탯보너스 부분
					if(level>50)
						toLvStat(true);

					toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
				}else{
					toSender(S_CharacterExp.clone(BasePacketPooling.getPool(S_CharacterExp.class), this));
				}
			}
		}
	}

	/**
	 * 50이상 레벨 보너스스탯 지급해야 하는지, 체크용 메서드
	 */
	public boolean toLvStat(boolean packet){
		int lvStat = level-50;
		if(lvStat>0){
			int totalStat = lvStr+lvDex+lvCon+lvInt+lvWis+lvCha;
			if(totalStat < lvStat){
				if(packet)
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "RaiseAttr"));
				return true;
			}
		}
		return false;
	}

	/**
	 * 사용자 정보 저장 처리 함수.
	 */
	public void toSave(){
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			toSave(con);
		} catch (Exception e) {
			lineage.share.System.println(PcInstance.class.toString()+" : toSave()");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}

	/**
	 * 사용자 정보 저장 처리 함수.
	 */
	public void toSave(Connection con){
		// 저장
		CharactersDatabase.saveInventory(con, this);
		CharactersDatabase.saveSkill(con, this);
		CharactersDatabase.saveBuff(con, this);
		CharactersDatabase.saveBook(con, this);
		CharactersDatabase.saveCharacter(con, this);
		SummonController.toSave(con, this);
		FriendController.toSave(con, this);
		// 로그 기록.
		Log.appendConnect(getRegisterDate(), client.getAccountIp(), client.getAccountId(), getName(), "정보저장");
	}

	/**
	 * 월드 진입할때 호출됨.
	 */
	public void toWorldJoin(Connection con){
		// 버그 방지.
		if(!isWorldDelete())
			return;

		// 인터페이스 전송.	db_interface
		if(db_interface != null)
			toSender( S_InterfaceRead.clone(BasePacketPooling.getPool(S_InterfaceRead.class), db_interface) );
		
		// 메모리 세팅
		setAutoPickup(Lineage.auto_pickup);
		World.appendPc(this);
		BookController.toWorldJoin(this);
		ClanController.toWorldJoin(this);
		InventoryController.toWorldJoin(this);
		SkillController.toWorldJoin(this);
		CharacterController.toWorldJoin(this);
		TradeController.toWorldJoin(this);
		BuffController.toWorldJoin(this);
		SummonController.toWorldJoin(this);
		QuestController.toWorldJoin(this);
		ChattingController.toWorldJoin(this);
		// 운영자일때 정보 갱신
		// 기억리스트 추출 및 전송
		CharactersDatabase.readBook(con, this);
		// 스킬 추출 및 전송
		CharactersDatabase.readSkill(con, this);
		// 케릭터 정보 전송
		toSender( S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this) );
		// 월드 스폰.
		super.toTeleport(x, y, map, false);
		// 인벤토리 추출 및 전송
		CharactersDatabase.readInventory(con, this);
		// sp, mr 갱신.
		toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
		// 상태에따라 패킷전송 [광전사의 도끼를 착용하고 있을수 있기때문에 추가됨.]
		// 버프 잡아주기
		CharactersDatabase.readBuff(con, this);
		// 날씨 보내기
		toSender( S_Weather.clone(BasePacketPooling.getPool(S_Weather.class), S_Weather.WEATHER_FAIR) );
		// 성처리
		KingdomController.toWorldJoin(this);
		// 스탯 확인
		toLvStat(true);
		// 편지 확인
		LetterController.toWorldJoin(con, this);
		// 친구 목록 갱신.
		FriendController.toWorldJoin(con, this);
		// 홈페이지 연동 처리.
		WebDatabase.toWorldJoin(this);
		// 서버 상태에 따른 처리
		if(Lineage.event_buff)
			CommandController.toBuff(this);
		// %0님께서 방금 게임에 접속하셨습니다.
		if(Lineage.world_message_join)
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("%s님께서 방금 게임에 접속하셨습니다.", getName())));
		
		// 로그 기록.
		Log.appendConnect(getRegisterDate(), client.getAccountIp(), client.getAccountId(), getName(), "접속");
	}

	/**
	 * 월드에서 나갈때 호출됨.
	 */
	public void toWorldOut(){
		// 버그 방지.
		if(isWorldDelete())
			return;
		
		// 로그 기록.
		Log.appendConnect(getRegisterDate(), client.getAccountIp(), client.getAccountId(), getName(), "종료");
		// 사용자 정보 저장전에 처리해야될 메모리 처리
		TradeController.toWorldOut(this);			// 거래중인 아이템 때문에 저장전에 처리.
		// 죽어있을경우에 처리를 위해.
		toReset(true);
		// 에스메랄다 미래보기 상태라면
		if(npc_esmereld != null)
			npc_esmereld.toTeleport(this);
		// 근처 마을로 좌표 변경.
		if( TeleportResetDatabase.toLocation(this) ){
			x = homeX;
			y = homeY;
			map = homeMap;
		}
		// 성주면에 있는지 확인. 있을경우 근처마을로 좌표 변경.
		Kingdom k = KingdomController.findKingdomLocation(this);
		if(k!=null && k.getClanId()>0 && k.getClanId()!=getClanId()){
			TeleportHomeDatabase.toLocation(this);
			x = homeX;
			y = homeY;
			map = homeMap;
		}
		// 사용자 정보 저장
		toSave();
		// 이전에 관리중이던 목록 제거
		clearList(true);
		// 월드에 갱신
		World.remove(this);
		// 사용된 메모리 제거
		World.removePc(this);
		SummonController.toWorldOut(this);
		BookController.toWorldOut(this);
		ClanController.toWorldOut(this);
		InventoryController.toWorldOut(this);
		SkillController.toWorldOut(this);
		CharacterController.toWorldOut(this);
		PartyController.toWorldOut(this);
		UserShopController.toStop(this);
		QuestController.toWorldOut(this);
		ChattingController.toWorldOut(this);
		FriendController.toWorldOut(this);
		// 홈페이지 연동 처리.
		// WebDatabase.toWorldOut(this);
		// 메모리 초기화
		close();
	}
	
	@Override
	public void toReset(boolean world_out){
		super.toReset(world_out);
		if(dead){
			try {
				if(world_out){
					// 죽은상태로 월드를 나갈경우 좌표를 근처 마을로..
					// 성이 존재한다면 내성으로 (잊섬 제외)
					Kingdom k = KingdomController.find(this);
					if(k!=null && map!=70){
						homeX = k.getX();
						homeY = k.getY();
						homeMap = k.getMap();
					}else{
						TeleportHomeDatabase.toLocation(this);
					}
					x = homeX;
					y = homeY;
					map = homeMap;
				}
				// 다이상태 풀기.
				dead = false;
				// 체력 채우기.
				setNowHp(level);
				// food게이지 하향.
				if(getFood() > Lineage.MIN_FOOD)
					setFood(Lineage.MIN_FOOD);
				// gfx 복구
				gfx = classGfx;
				if(inv.getSlot(Lineage.SLOT_WEAPON) != null){
					gfxMode = classGfxMode + inv.getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode();
				}else{
					gfxMode = classGfxMode;
				}
			} catch (Exception e) { }
		}
	}

	@Override
	public void toRevival(object o){
		if(dead){
			temp_object_1 = o;
			toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 321));
		}
	}

	@Override
	public void toRevivalFinal(object o){
		if(dead){
			// 리셋처리.
			super.toReset(false);
			// 다이상태 풀기.
			dead = false;
			// 체력 채우기.
			setNowHp(temp_hp!=-1 ? temp_hp : level);
			setNowMp(temp_mp!=-1 ? temp_mp : nowMp);
			// 패킷 처리.
			toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), temp_object_1, this), true);
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 230), true);
			//
			temp_object_1 = null;
			temp_hp = temp_mp = -1;
		}
	}

	@Override
	public void toMoving(final int x, final int y, final int h){
		// System.out.print("toMoving : " + x + " " + y + " \n");
		// 이동 알리기.
		CharacterController.toMoving(this);
		
		if(Util.isDistance(this.x, this.y, map, x, y, map, 1) && isFrameSpeed(Lineage.FRAMESPEED_MOVING)){
			super.toMoving(x, y, h);
			
			if(World.get_map(x, y, map)==127){
				// 던전 이동 처리
				DungeonController.toDungeon(this);
				// 163이하 버전 공성전 처리.
				if(Lineage.server_version <= 163){
					// 현재 위치에 성정보 추출.
					Kingdom k = KingdomController.findKingdomLocation(this);
					// 공성중이면서, 옥좌라면 면류관 픽업처리를 통해 성주 변경처리하기.
					if(k!=null && k.isWar() && k.isThrone(this)){
						k.getCrown().toPickup(this);
					}
				}
			}
		}else{
			toTeleport(this.x, this.y, map, false);
		}
	}

	@Override
	public void toSender(BasePacket bp){
		if(client!=null)
			client.toSender(bp);
		else
			BasePacketPooling.setPool(bp);
	}
	
	@Override
	public void toPotal(int x, int y, int map){
		homeX = x;
		homeY = y;
		homeMap = map;
		toSender(S_Potal.clone(BasePacketPooling.getPool(S_Potal.class), this.map, map));
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		// 버그방지.
		if(World.get_map(map) == null){
			Server.close(getClient());
			return;
		}
		// 2.00이하 버전에서 텔레포트후 sp, mr 이 정상표현 안되는 문제로 인해 추가.
		if(Lineage.server_version <= 200)
			toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
		super.toTeleport(x, y, map, effect);
		// 소환객체 텔레포트
		SummonController.toTeleport(this);
	}

	/**
	 * gfx와 gfxmode에 따른 프레임값을 추출함.
	 * 행동에 따라 속도 정의하고. 해당프임값이 이전에 시행했던 시간차를 연산하여
	 * 다음 해동을 취해도 되는지 판단하는 메서드.
	 */
	public boolean isFrameSpeed(int type){
		double frame = SpriteFrameDatabase.find(gfx, gfxMode) - 100;
		long time = System.currentTimeMillis();
		switch(speed){
			case 1:
				frame -= frame*0.3;
				break;
			case 2:
				frame += frame*0.3;
				break;
		}
		if(brave)
			frame -= frame*0.3;
		switch(type){
			case Lineage.FRAMESPEED_ATTACK:
				if(time-FrameTime >= frame){
					FrameTime = time;
					return true;
				}
				break;
			case Lineage.FRAMESPEED_MOVING:
				if(time-FrameTime >= frame){
					FrameTime = time;
					MovingCounting = 0;
					MovingWarningCounting = 0;
				}else{
					FrameTime = time;
					// 1단계 체크
					if(++MovingCounting>3){
						MovingCounting = 0;
						++MovingWarningCounting;
					}
					// 2단계 체크
					if(MovingWarningCounting>3){
						MovingCounting = 0;
						MovingWarningCounting = 0;
						return false;
					}
				}
				return true;
			case Lineage.FRAMESPEED_MAGIC:
				if(time-FrameTime >= frame){
					FrameTime = time;
					return true;
				}
				break;
		}

		return false;
	}

	/**
	 * 50이상 레벨 보너스스탯 지급해야 하는지, 체크용 메서드
	 */
	public boolean isLvStat(boolean packet){
		int lvStat = level-50;
		if(lvStat>0){
			int totalStat = lvStr+lvDex+lvCon+lvInt+lvWis+lvCha;
			if(totalStat < lvStat){
				if(packet)
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "RaiseAttr"));
				return true;
			}
		}
		return false;
	}

	/**
	 * 매개변수 객체에게 물리공격을 가할때 처리하는 메서드.
	 */
	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg){
		setFight(true);
		int effect = 0;
		int dmg = 0;
		ItemInstance weapon = inv.getSlot(Lineage.SLOT_WEAPON);
		ItemInstance arrow = null;
		if(bow && weapon!=null){
			arrow = weapon.getItem().getType2().equalsIgnoreCase("gauntlet") ? inv.findThrowingKnife() : inv.findArrow();
			
			if(weapon.getItem().getEffect() > 0){
				effect = weapon.getItem().getEffect();
			}else{
				if(arrow != null)
					effect = arrow.getItem().getEffect();
			}
		}

		// 좌표에따라 방향전환.
		heading = Util.calcheading(this, x, y);
		
		// 장로 변신중이면 콜라이트닝 이팩트 표현.
		if(Lineage.server_version<=200 && getGfx()==32)
			effect = 10;

		// 공속 확인.
		if(gm>0 || (isFrameSpeed(Lineage.FRAMESPEED_ATTACK) && !isTransparent()) ){
			// 죽엇는지 확인.
			if(!isDead()){
				// 무게 확인. 82% 미만
				if(inv.getWeightPercent()<24){
					
					// 데미지 추출
					dmg = DamageController.getDamage(this, o, bow, weapon, arrow, 0);
					if(dmg > 0){
						// 무기에 따른 처리.
						if(weapon != null){
							// 무기에게 공격했다는거 알리기.
							if(weapon.toDamage(this, o)){
								dmg += weapon.toDamage(dmg);
								if(weapon.toDamageEffect() > 0)
									effect = weapon.toDamageEffect();
							}
						}

//						System.out.print("PcInstance toAttack>> dmg: " + dmg + " type: " +bow + " \n");
						// 데미지 입은거 처리하는 구간.
						DamageController.toDamage(this, o, dmg, bow ? Lineage.ATTACK_TYPE_BOW : Lineage.ATTACK_TYPE_WEAPON);
						
						// 버프 로봇을 어택한경우 데미지값 0 으로 처리. 하단에서 처리해야 상단에서 객체가 함수처리를 정상적으로 함.
						if(o instanceof BuffRobotInstance)
							dmg = 0;
					}
					
					toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, getGfxMode()+Lineage.GFX_MODE_ATTACK, dmg, effect, bow, effect>0, x, y), true);

					// 화살 갯수 하향
					if(bow && arrow!=null)
						inv.count(arrow, arrow.getCount()-1, true);
				}else{
					// \f1소지품이 너무 무거워서 전투를 할 수 없습니다.
					toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 110));
				}
			}
		}
	}
	
	@Override
	public void toExp(object o, double exp){
		// 경험치 포지션상태일경우 더 지급.
		if(getBuffExpPotion() > 0)
			exp += exp * getBuffExpPotion();
		// 배율에따른 경험치 증가.
		exp *= Lineage.rate_exp;
		
		// 레벨별 지급될 경험치 감소.
		if(getLevel()<50){
			exp = exp / 1;
		}else if(getLevel()<52){
			exp = exp / 2;
		}else if(getLevel()<55){
			exp = exp / 3;
		}else if(getLevel()<60){
			exp = exp / 4;
		}else if(getLevel()<65){
			exp = exp / 5;
		}else if(getLevel()<70){
			exp = exp / 6;
		}else if(getLevel()<75){
			exp = exp / 8;
		}else if(getLevel()<80){
			exp = exp / 10;
		}else if(getLevel()<85){
			exp = exp / 12;
		}else if(getLevel()<90){
			exp = exp / 14;
		}else if(getLevel()<95){
			exp = exp / 16;
		}else if(getLevel()<98){
			exp = exp / 18;
		}else if(getLevel()<99){
			exp = exp / 50;
		}else{
			exp = exp / 1000;
		}

		setExp(getExp() + exp);
		// 로그 기록.
		int o_lv = 0;
		String o_name = null;
		int o_exp = 0;
		if(o instanceof MonsterInstance){
			MonsterInstance mon = (MonsterInstance)o;
			o_lv = mon.getMonster().getLevel();
			o_name = mon.getMonster().getName();
			o_exp = mon.getMonster().getExp();
		}
		if(o instanceof Cracker){
			o_name = "허수아비";
		}
		if(o instanceof NpcInstance){
			NpcInstance npc = (NpcInstance)o;
			o_name = npc.getNpc().getName();
		}
		Log.appendExp(getRegisterDate(), getLevel(), (int)exp, (int)getExp(), o_lv, o_name, o_exp);
	}
	
	@Override
	public void toTimer(long time){
		// 서버 메세지 표현 처리.
		if(Common.SERVER_MESSAGE && message_time<=time){
			message_time = time + Common.SERVER_MESSAGE_TIME;
			for(String msg : Common.SERVER_MESSAGE_LIST)
				ChattingController.toChatting(this, msg, Lineage.CHATTING_MODE_MESSAGE);
		}
		
		// 초보존일경우 12이상일때 강제 이동시키기.
		if(getMap()==68 || getMap()==69){
			if(getGm()==0 && getLevel()>12){
				if(getMap()==68)
					LocationController.toTalkingIsland(this);
				else
					LocationController.toSilverknightTown(this);
				toPotal(getHomeX(), getHomeY(), getHomeMap());
			}
		}
		
		// 자동저장 처리 구간.
		if(Lineage.auto_save_time>0 && auto_save_time<=time){
			// 월드 접속하고 타이머에 등록된후 해당 함수가 호출됫을때 auto_save_time 값이 0 임.
			// 접속하자마자 저장하는걸 방지하기위해 아래 코드 삽입.
			if(auto_save_time != 0)
				toSave();
			auto_save_time = time + Lineage.auto_save_time;
		}
		
		// 현재 진행된 time값과 비교하여 24시간이 오바됫을경우 0으로 초기화
		if(getPkTime()>0 && getPkTime()+(1000*60*60*24)<=time)
			setPkTime(0);
		
		// 수중에 있을경우 상태에따라 hp 감소처리
		if(World.isAquaMap(this)){
			// 수중에서 숨쉴수 있는 아이템 착용중인지 확인.
			// 수중에서 숨쉴 수 있는 버프상태인지 확인.
			if(!getInventory().isAquaEquipped() && !isBuffEva())
				setNowHp( getNowHp() - Util.random(1, 2) );
		}
		
		// 아이템 자동 지급.
		if(Lineage.world_premium_item_is && premium_item_time<=time){
			if(premium_item_time != 0){
				// 아이템 지급
				ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(Lineage.world_premium_item));
				if(ii != null){
					ii.setCount(Util.random(Lineage.world_premium_item_min, Lineage.world_premium_item_max));
					toGiveItem(null, ii, ii.getCount());
				}
			}
			premium_item_time = time + Lineage.world_premium_item_delay;
		}
		
		if(isGM_HPBAR()) {
			for (object o : insideList) {
				if (o instanceof MonsterInstance) {
					MonsterInstance mon = (MonsterInstance) o;
					toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), mon, true));
				} else if (o instanceof NpcInstance) {
					NpcInstance npc = (NpcInstance) o;
					toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), npc, true));
				}
			}
		}
		
	}
	
	public boolean isAutoPickup(){
		return auto_pickup;
	}
	
	public void setAutoPickup(boolean is){
		auto_pickup = is;
	}
	
	@Override
	public boolean isHpbar() {
		return is_hpbar;
	}

	@Override
	public void setHpbar(boolean is_hpbar) {
		this.is_hpbar = is_hpbar;
	}

	public byte[] getDbInterface() {
		return db_interface;
	}

	public void setDbInterface(byte[] dbInterface) {
		db_interface = dbInterface;
	}

	public int getElixir() {
		return elixir;
	}

	public void setElixir(int elixir) {
		this.elixir = elixir;
	}
	
	/**
	 * 몬스터나 엔피씨의 HP바를 보여주는 운영자 명령어에 사용되는 변수,
	 */
	private boolean _gmhpbar;
	public void setGM_HPBAR(boolean result) { _gmhpbar = result; }
	public boolean isGM_HPBAR() { return _gmhpbar; }

	public int getDeathCount()
	{
		return DeathCount;
	}

	public void setDeathCount(int paramInt)
	{
		this.DeathCount = paramInt;
	}

	
	
	
	
}
