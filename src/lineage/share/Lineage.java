package lineage.share;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.FirstInventory;
import lineage.bean.database.FirstSpawn;
import lineage.bean.database.FirstSpell;

public final class Lineage {
	
	// 서버 버전 : 144, 161, 230, 300 등..
	static public int server_version				= 0;
	static public int GMCODE 						= 0;
	static public boolean CHARACTER_QUEST 			= true;
	
	// 공격 방식 구분용
	static public final int ATTACK_TYPE_WEAPON		= 0;
	static public final int ATTACK_TYPE_BOW			= 1;
	static public final int ATTACK_TYPE_MAGIC		= 2;
	static public final int ATTACK_TYPE_DIRECT		= 3;	// 경비에게 데미지 당햇다고 요청하는구간에 필요해서 만듬.
	
	// 창고 처리 구분 종류
	static public final int DWARF_TYPE_NONE			= 0;
	static public final int DWARF_TYPE_CLAN			= 1;
	static public final int DWARF_TYPE_ELF			= 2;
	
	// 채팅 구분 종류
	static public final int CHATTING_MODE_NORMAL	= 0;
	static public final int CHATTING_MODE_SHOUT		= 2;
	static public final int CHATTING_MODE_GLOBAL	= 3;
	static public final int CHATTING_MODE_CLAN		= 4;
	static public final int CHATTING_MODE_PARTY		= 11;
	static public final int CHATTING_MODE_TRADE		= 12;
	static public final int CHATTING_MODE_MESSAGE	= 20;
	
	// 각 성별 고유 아이디
	static public final int KINGDOM_KENT		= 1;
	static public final int KINGDOM_ORCISH		= 2;
	static public final int KINGDOM_WINDAWOOD	= 3;
	static public final int KINGDOM_GIRAN		= 4;
	static public final int KINGDOM_HEINE		= 5;
	static public final int KINGDOM_ABYSS		= 6;
	static public final int KINGDOM_ADEN		= 7;
	
	// 인공지능 상태 변수
	static public final int AI_STATUS_DELETE	= -1;		// 죽은상태 쓰레드에서 제거처리됨.
	static public final int AI_STATUS_WALK		= 0;		// 랜덤워킹 상태
	static public final int AI_STATUS_ATTACK	= 1;		// 공격 상태
	static public final int AI_STATUS_DEAD		= 2;		// 죽은 상태
	static public final int AI_STATUS_CORPSE	= 3;		// 시체 상태
	static public final int AI_STATUS_SPAWN		= 4;		// 스폰 상태
	static public final int AI_STATUS_ESCAPE	= 5;		// 도망 상태
	static public final int AI_STATUS_PICKUP	= 6;		// 아이템 줍기 상태
	static public final int AI_STATUS_RECESS	= 7;		// Felix: Recess Mode
	
	// 시체 유지 시간
	static public int ai_corpse_time			= 40 * 1000;
	static public int ai_summon_corpse_time		= 40 * 1000;
	static public int ai_pet_corpse_time		= 120 * 1000;
	// 몬스터 체력회복제 복용 타이밍에 퍼센트값.
	static public int ai_auto_healingpotion_percent;
	// 몬스터 자연회복 틱타임 주기값.
	static public int ai_monster_tic_time;
	// 몬스터 레벨에따른 경험치 지급연산 처리 사용 여부.
	static public boolean monster_level_exp;
	// 소환된 몬스터가 아이템을 드랍할지 여부.
	static public boolean monster_summon_item_drop;
	
	// 펫 최대 렙업값.
	static public int pet_level_max;
	
	// 아이템 별 밝기 값.
	static public final int CANDLE_LIGHT		= 8;
	static public final int LAMP_LIGHT			= 10;
	static public final int LANTERN_LIGHT		= 14;
	
	// 메모리상에 아이템 장착 슬롯 아이디
	static public final int SLOT_HELM			= 0x00;
	static public final int SLOT_EARRING		= 0x01;
	static public final int SLOT_NECKLACE		= 0x02;
	static public final int SLOT_SHIRT			= 0x03;
	static public final int SLOT_ARMOR			= 0x04;
	static public final int SLOT_CLOAK			= 0x05;
	static public final int SLOT_RING_LEFT		= 0x06;
	static public final int SLOT_RING_RIGHT		= 0x07;
	static public final int SLOT_BELT			= 0x08;
	static public final int SLOT_GLOVE 			= 0x09;
	static public final int SLOT_SHIELD 		= 0x0A;
	static public final int SLOT_WEAPON 		= 0x0B;
	static public final int SLOT_BOOTS 			= 0x0C;
	static public final int SLOT_NONE			= 0x0D;

	// 해당 무기 착용시 변화되는 gfxmode값
	static public final int WEAPON_NONE				= 0x00;
	static public final int WEAPON_SWORD			= 0x04;
	static public final int WEAPON_TOHANDSWORD		= 0x32;
	static public final int WEAPON_AXE				= 0x0B;
	static public final int WEAPON_BOW				= 0x14;
	static public final int WEAPON_SPEAR			= 0x18;
	static public final int WEAPON_WAND				= 0x28;
	static public final int WEAPON_DAGGER			= 0x2E;
	static public final int WEAPON_BLUNT			= 0x0B;
	static public final int WEAPON_CLAW				= 0x3A;
	static public final int WEAPON_EDORYU			= 0x36;
	static public final int WEAPON_THROWINGKNIFE	= 0x0B6A;
	static public final int WEAPON_ARROW			= 0x42;
	static public final int WEAPON_GAUNTLET			= 0x3E;
	static public final int WEAPON_CHAINSWORD		= 0x18;
	static public final int WEAPON_KEYRINK			= 0x3a;
	
	// 클레스별 종류
	static public final int LINEAGE_CLASS_ROYAL			= 0x00;
	static public final int LINEAGE_CLASS_KNIGHT		= 0x01;
	static public final int LINEAGE_CLASS_ELF			= 0x02;
	static public final int LINEAGE_CLASS_WIZARD		= 0x03;
	static public final int LINEAGE_CLASS_DARKELF		= 0x04;
	static public final int LINEAGE_CLASS_DRAGONKNIGHT	= 0x05;
	static public final int LINEAGE_CLASS_BLACKWIZARD	= 0x06;
	static public final int LINEAGE_CLASS_MONSTER		= 0x0A;
	
	static public final int LINEAGE_ROYAL			= 1;
	static public final int LINEAGE_KNIGHT			= 2;
	static public final int LINEAGE_ELF				= 4;
	static public final int LINEAGE_WIZARD			= 8;
	
	// 필드 존 체크용
	static public final int NORMAL_ZONE			= 0x00;
	static public final int SAFETY_ZONE			= 0x10;
	static public final int COMBAT_ZONE			= 0x20;
	
	// 만 카오틱 값 32768
	static public final int CHAOTIC	= 32768;
	// 뉴트럴
	static public final int NEUTRAL = 65536;
	// 만 라이풀 값 32767
	static public final int LAWFUL	= 98303;
	
	static public int item_enchant_armor_max = 0; // 방어구 만인챈 수치
	static public int	item_enchant_weapon_max = 0; // 무기 만인챈 수치

	// 오브젝트 gfx별 모드 값
	static public final int GFX_MODE_WALK					= 0;
	static public final int GFX_MODE_ATTACK					= 1;
	static public final int GFX_MODE_DAMAGE					= 2;
	static public final int GFX_MODE_BREATH					= 3;
	static public final int GFX_MODE_RISE					= 4;
	static public final int GFX_MODE_ATTACK_R_CLAW			= 5;
	static public final int GFX_MODE_DEAD					= 8;
	static public final int GFX_MODE_HIDE					= 11; // Felix: Add recess mode
	static public final int GFX_MODE_ATTACK_L_CLAW			= 12;
	static public final int GFX_MODE_GET					= 15;
	static public final int GFX_MODE_THROW					= 16;
	static public final int GFX_MODE_WAND					= 17;
	static public final int GFX_MODE_ZAP					= 17;
	static public final int GFX_MODE_SPELL_DIRECTION		= 18;
	static public final int GFX_MODE_SPELL_NO_DIRECTION		= 19;
	static public final int GFX_MODE_OPEN					= 28;
	static public final int GFX_MODE_CLOSE					= 29;
	static public final int GFX_MODE_ALT_ATTACK				= 30;
	static public final int GFX_MODE_SEPLL_DIRECTION_EXTRA	= 31;
	static public final int GFX_MODE_DOORACTION				= 32;
	static public final int GFX_MODE_SWITCH					= 100;
	static public final int GFX_MODE_TYPE					= 102;
	static public final int GFX_MODE_ATTR					= 104;
	static public final int GFX_MODE_CLOTHES				= 105;
	static public final int GFX_MODE_EFFECT					= 109;
	static public final int GFX_MODE_EFFECT_NONE			= 220;	// 몬스터 고유 gfxmode로 공격취햇을때 이팩트를 표현하면 안되기때문에 표현업는 effect 값 설정.

	// 프레임에 다른 행동 패턴값 정의
	static public final int FRAMESPEED_ATTACK			= 0x01;
	static public final int FRAMESPEED_MOVING			= 0x02;
	static public final int FRAMESPEED_MAGIC			= 0x03;

	// 글루딘 해골밭
	static public final int CHAOTICZONE1_X1			= 32884;
	static public final int CHAOTICZONE1_X2			= 32891;
	static public final int CHAOTICZONE1_Y1			= 32647;
	static public final int CHAOTICZONE1_Y2			= 32656;

	// 화전민 오크숲
	static public final int CHAOTICZONE2_X1			= 32664;
	static public final int CHAOTICZONE2_X2			= 32669;
	static public final int CHAOTICZONE2_Y1			= 32298;
	static public final int CHAOTICZONE2_Y2			= 32308;

	// 켄트숲
	static public final int LAWFULLZONE1_X1			= 33117;
	static public final int LAWFULLZONE1_X2			= 33128;
	static public final int LAWFULLZONE1_Y1			= 32931;
	static public final int LAWFULLZONE1_Y2			= 32942;

	// 요정숲
	static public final int LAWFULLZONE2_X1			= 33136;
	static public final int LAWFULLZONE2_X2			= 33146;
	static public final int LAWFULLZONE2_Y1			= 32236;
	static public final int LAWFULLZONE2_Y2			= 32245;

	// 요정숲
	static public final int TREEX1					= 33050;
	static public final int TREEX2					= 33058;
	static public final int TREEY1					= 32333;
	static public final int TREEY2					= 32341;
	
	// 퀘스트 종류
	static public final String QUEST_ZERO_ROYAL		= "request cloak of red";
	static public final String QUEST_ROYAL_LV15		= "request spellbook112";
	static public final String QUEST_ROYAL_LV30		= "quest 13 aria2";
	static public final String QUEST_ROYAL_LV45		= "quest 15 masha2";
	static public final String QUEST_KNIGHT_LV15	= "request hood of knight";
	static public final String QUEST_KNIGHT_LV30	= "quest 14 gunterkE2";
	static public final String QUEST_ELF_LV15		= "quest elf 15";
	static public final String QUEST_ELF_LV15_DUPELGENON		= "quest 15 motherEV6"; // Jeong
	static public final String QUEST_ELF_LV30		= "quest 12 motherEE2";
	static public final String QUEST_WIZARD_LV15	= "quest wizard 15";
	static public final String QUEST_WIZARD_LV30	= "quest wizard 30";
	static public final String QUEST_TALKINGSCROLL	= "quest talking scroll";
	static public final String QUEST_NOVICE			= "quest novice";
	static public final String QUEST_LYRA			= "quest lyra";
	
	
	// 공성전 이벤트 종류
	static public final int KINGDOM_WARSTATUS_START	= 0;	// 시작
	static public final int KINGDOM_WARSTATUS_STOP	= 1;	// 종료
	static public final int KINGDOM_WARSTATUS_PLAY	= 2;	// 진행중
	static public final int KINGDOM_WARSTATUS_3		= 3;	// 주도권
	static public final int KINGDOM_WARSTATUS_4		= 4;	// 차지
	
	// 속성 계열 정보
	static public final int ELEMENT_NONE		= 0;	// 공통
	static public final int ELEMENT_EARTH		= 1;	// 땅
	static public final int ELEMENT_FIRE		= 2;	// 불
	static public final int ELEMENT_WIND		= 3;	// 바람
	static public final int ELEMENT_WATER		= 4;	// 물
	static public final int ELEMENT_LASER		= 5;	// 레이저
	static public final int ELEMENT_POISON		= 6;	// 독

	// 요숲 채집npc쪽 재채집이 가능한 주기적인 시간 설정.
	static public int elf_gatherup_time;

	// 공성전 진행 주기 일 단위
	static public int kingdom_war_day = 3;
	// 공성전 진행시간
	static public int kingdom_war_time = 120;
	// 공성중 공성존에서 사용자가 죽엇을때 경험치 떨굴지 여부.
	static public boolean kingdom_player_dead_expdown;
	// 공성중 공성존에서 사용자가 죽엇을때 아이템을 드랍할지 여부.
	static public boolean kingdom_player_dead_itemdrop;
	// 공성전 시작 및 종료후 면류관 아이템 처리를 할지 여부.
	static public boolean kingdom_crown;
	// 공성전 중 전쟁선포된 혈맹원들끼리 pvp 시 카오처리를 할지 여부.
	static public boolean kingdom_pvp_pk;
	// 켄트 성 용병 대장 훈트
	static public int kingdom_soldier_price;
	// npc 대화요청된후 잠깐 휴식되는 시간값.
	static public int npc_talk_stay_time;
	
	// 요정숲 정령의돌 최대스폰 갯수.
	static public int elvenforest_elementalstone_spawn_count;
	
	// 요정숲 정령의돌 스폰 주기 시간값.
	static public int elvenforest_elementalstone_spawn_time;
	
	// 요정숲 정령의돌 스폰갯수 지정값.
	static public int elvenforest_elementalstone_min_count;
	static public int elvenforest_elementalstone_max_count;
	
	// 군주
	static public List<FirstSpawn> royal_spawn = new ArrayList<FirstSpawn>();
	static public int royal_male_gfx;
	static public int royal_female_gfx;
	static public int royal_hp;
	static public int royal_mp;
	static public int royal_max_hp;
	static public int royal_max_mp;
	static public List<FirstSpell> royal_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> royal_first_inventory = new ArrayList<FirstInventory>();
	static public int royal_stat_str;
	static public int royal_stat_con;
	static public int royal_stat_dex;
	static public int royal_stat_wis;
	static public int royal_stat_cha;
	static public int royal_stat_int;
	static public int royal_stat_dice;

	// 기사
	static public List<FirstSpawn> knight_spawn = new ArrayList<FirstSpawn>();
	static public int knight_male_gfx;
	static public int knight_female_gfx;
	static public int knight_hp;
	static public int knight_mp;
	static public int knight_max_hp;
	static public int knight_max_mp;
	static public List<FirstSpell> knight_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> knight_first_inventory = new ArrayList<FirstInventory>();
	static public int knight_stat_str;
	static public int knight_stat_con;
	static public int knight_stat_dex;
	static public int knight_stat_wis;
	static public int knight_stat_cha;
	static public int knight_stat_int;
	static public int knight_stat_dice;

	// 요정
	static public List<FirstSpawn> elf_spawn = new ArrayList<FirstSpawn>();
	static public int elf_male_gfx;
	static public int elf_female_gfx;
	static public int elf_hp;
	static public int elf_mp;
	static public int elf_max_hp;
	static public int elf_max_mp;
	static public List<FirstSpell> elf_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> elf_first_inventory = new ArrayList<FirstInventory>();
	static public int elf_stat_str;
	static public int elf_stat_con;
	static public int elf_stat_dex;
	static public int elf_stat_wis;
	static public int elf_stat_cha;
	static public int elf_stat_int;
	static public int elf_stat_dice;

	// 마법사 정보
	static public List<FirstSpawn> wizard_spawn = new ArrayList<FirstSpawn>();
	static public int wizard_male_gfx;
	static public int wizard_female_gfx;
	static public int wizard_hp;
	static public int wizard_mp;
	static public int wizard_max_hp;
	static public int wizard_max_mp;
	static public List<FirstSpell> wizard_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> wizard_first_inventory = new ArrayList<FirstInventory>();
	static public int wizard_stat_str;
	static public int wizard_stat_con;
	static public int wizard_stat_dex;
	static public int wizard_stat_wis;
	static public int wizard_stat_cha;
	static public int wizard_stat_int;
	static public int wizard_stat_dice;
	
	// 다크엘프 정보
	static public List<FirstSpawn> darkelf_spawn = new ArrayList<FirstSpawn>();
	static public int darkelf_male_gfx;
	static public int darkelf_female_gfx;
	static public int darkelf_hp;
	static public int darkelf_mp;
	static public int darkelf_max_hp;
	static public int darkelf_max_mp;
	static public List<FirstSpell> darkelf_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> darkelf_first_inventory = new ArrayList<FirstInventory>();

	// 용기사 정보
	static public List<FirstSpawn> dragonknight_spawn = new ArrayList<FirstSpawn>();
	static public int dragonknight_male_gfx;
	static public int dragonknight_female_gfx;
	static public int dragonknight_hp;
	static public int dragonknight_mp;
	static public int dragonknight_max_hp;
	static public int dragonknight_max_mp;
	static public List<FirstSpell> dragonknight_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> dragonknight_first_inventory = new ArrayList<FirstInventory>();

	// 환술사 정보
	static public List<FirstSpawn> blackwizard_spawn = new ArrayList<FirstSpawn>();
	static public int blackwizard_male_gfx;
	static public int blackwizard_female_gfx;
	static public int blackwizard_hp;
	static public int blackwizard_mp;
	static public int blackwizard_max_hp;
	static public int blackwizard_max_mp;
	static public List<FirstSpell> blackwizard_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> blackwizard_first_inventory = new ArrayList<FirstInventory>();

	// 인벤토리 최대 갯수
	static public int inventory_max;

	// 인벤토리에 아이템 무게게이지 최대값
	static public int inventory_weight_max;

	// 최대 렙업값
	static public int level_max;

	// 파티원 최대수
	static public int party_max;

	// 창고이용 레벨
	static public int warehouse_level;
	// 창고 아이템 찾을때 비용
	static public int warehouse_price;
	// 창고에 등록가능한 최대값
	static public int warehouse_max;
	// 창고 아이템 찾을때 비용 - 요정숲
	static public int warehouse_price_elf;
	
	// 펫 찾을때 비용
	static public int warehouse_pet_price;
	// 펫 사용자에게 데미지 가할때 1/3으로 들어가게 할지 여부.
	static public boolean pet_damage_to_player;

	// 순수스탯 최대값
	static public int stat_max;

	// 여관방 최대갯수
	static public int inn_max;
	// 여관방 최대접근 인원수
	static public int inn_in_max;
	// 여관방 대여 비용
	static public int inn_price;
	// 여관 대여 시간
	static public int inn_time;
	// 여관 홀 최대갯수
	static public int inn_hall_max;
	// 여관 홀 최대접근 인원수
	static public int inn_hall_in_max;
	// 여관 홀 대여 비용
	static public int inn_hall_price;
	
	// 스핵 경고 최대치.
	static public boolean speedhack = false;
	static public int speedhack_warning_count = 3;
	
	// 게시판 글 작성 가격.
	static public int board_write_price;
	// 랭킹 게시판 업데이트 딜레이.
	static public int board_rank_update_delay;

	// 슬라임 레이스표 가격 설정
	static public int slime_race_price;

	// 배율
	static public double rate_enchant = 1;
	static public double rate_drop = 1;
	static public double rate_exp = 1;
	static public double rate_lawful = 1;
	static public double rate_aden = 1;
	static public double rate_party = 1;
	static public double rate_exp_pet = 1;

	// 채팅 레벨설정
	static public int chatting_level_global;
	static public int chatting_level_normal;
	static public int chatting_level_whisper;

	// pvp 설정
	static public boolean nonpvp;
	
	//허수아비 데미지 표현 할건지 말건지
	static public boolean view_cracker_damage = false;
	
	// 계정 자동생성 여부.
	static public boolean account_auto_create;
	// ip 당 소유가능한 계정 값.
	static public int account_ip_count;
	
	// 정액제 활성화 여부.
	static public boolean flat_rate;
	// 신규생성 계정에 대한 정액시간값. 분단위
	static public int flat_rate_price;
	
	// 에스메랄다 미래보기 유지시간을 몇초로 할지. 초단위
	static public int esmereld_sec;
	
	// 기본 세율 값
	static public int min_tax;
	static public int max_tax;
	
	// /누구 명령어 구성표
	static public String object_who;
	
	// 드랍된 아이템 유지시간값. 해당시간이 오버되면 제거됨.
	static public int world_item_delay;
	
	// 오토루팅 활성화 여부.
	static public boolean auto_pickup;
	// 오토루팅 아데나 활성화 여부.
	static public boolean auto_pickup_aden;
	// 오토루팅 퍼센트 범위.
	static public int auto_pickup_percent;
	// 자동저장 시간. 분단위
	static public int auto_save_time;
	
	// 덱방 활성화 여부
	static public boolean is_dex_ac;
	
	// 드랍 찬스 범위 최대값.
	static public double chance_max_drop;
	
	// 죽엇을때 아이템 떨굴지 여부.
	static public boolean player_dead_itemdrop;
	// 죽엇을때 경험치 떨굴지 여부.
	static public boolean player_dead_expdown;

	static public int SEARCH_LOCATIONRANGE				= 12;	// 주변셀 검색 범위
	static public int SEARCH_MONSTER_TARGET_LOCATION	= 17;	// 몬스터가 타겟을 쫓아가는 검색 범위
	static public int SEARCH_WORLD_LOCATION				= 40;	// 전체 객체 검색을 시도할 범위

	// 배고품게이지 최대값
	static public final int MAX_FOOD						= 225;
	// 사용자 케릭 죽엇을때 세팅할 배고품게이지 값
	public final static int MIN_FOOD						= 40;

	// 혈맹처리 에 참고 정보
	static public final int CLAN_MAKE_LEV					= 5;
	static public final int CLAN_NAME_MIN_SIZE				= 2;
	static public final int CLAN_NAME_MAX_SIZE				= 20;

	// 최대 ac
	static public final int MAX_AC							= 138;	// -128
	
	// door 오픈 유지값.
	static public int door_open_delay						= 4;
	
	// 슈롬 마법공식 사용유무.
	static public boolean shurom_magic_calculated;
	
	// 몬스터 hp 바 보게할지 여부.
	static public boolean monster_interface_hpbar;
	// npc hp 바 보게할지 여부.
	static public boolean npc_interface_hpbar;
	
	// 무기 손상도 최대치값.
	static public int item_durability_max;
	// 아이템 착용 처리 변수 (old(false), new(true))
	static public boolean item_equipped_type;
	// 축복받은 악세서리들에 아이템은 인첸트가 가능하도록할지 여부.
	static public boolean item_accessory_bless_enchant;
	// 축복받은 변신 주문서일경우 레벨제한을 해제할지 여부.
	static public boolean item_polymorph_bless;
	// 인첸트 범위값들
	static public int item_enchant_armor_curse_min;	// 저주
	static public int item_enchant_armor_curse_max;
	static public int item_enchant_armor_normal_min;	// 보통
	static public int item_enchant_armor_normal_max;
	static public int item_enchant_armor_bless_min;		// 축
	static public int item_enchant_armor_bless_max;
	static public int item_enchant_weapon_curse_min;	// 저주
	static public int item_enchant_weapon_curse_max;
	static public int item_enchant_weapon_normal_min;	// 보통
	static public int item_enchant_weapon_normal_max;
	static public int item_enchant_weapon_bless_min;	// 축
	static public int item_enchant_weapon_bless_max;
	// 축인첸시 대상 아이템이 확률적 축아이템으로 변경되게할지 여부.
	static public boolean item_enchant_bless = true;
	// 아이템 랜덤 추출쪽 확율 체크용 변수.
	static public int item_bundle_chance = 100;
	// 엘릭서 복용 최대 갯수.
	static public int item_elixir_max = 5;
	
	// 쓰레드 설정
	// 인공지능 쓰레드 갯수.
	static public int thread_ai = 4;
	// 이벤트 쓰레드 갯수.
	static public int thread_event = 10;
	
	// 클라 설정
	// 클라이언트 핑체크 시간값
	static public int client_ping_time = 120;
	
	// 공지사항 표현 주기.
	static public int notice_delay;
	
	// 이벤트 들
	static public boolean event_poly;		// 변신 이벤트
	static public boolean event_buff;		// 버프 자동지급 이벤트
	static public boolean event_illusion;	// 환상 이벤트
	static public boolean event_christmas;	// 크리스마스 이벤트
	static public boolean event_halloween;	// 할로윈 이벤트
	static public boolean event_lyra;		// 라이라 토템 이벤트
	
	// 가속 아이템 프레임
	static public boolean bravery_potion_frame;
	static public boolean elven_wafer_frame;
	static public boolean holywalk_frame;
	
	// 접속자수 표현시 사용되는 변수.
	static public int world_player_count = 1;
	// 플레이어 수 임의 지정 값
	static public int world_player_count_init = 0;
	
	// 프리미엄 아이템 자동지급
	static public boolean world_premium_item_is;
	static public String world_premium_item;
	static public int world_premium_item_min;
	static public int world_premium_item_max;
	static public int world_premium_item_delay;
	
	// 로봇(무인케릭) 자동버프사들 사용여부
	static public boolean robot_auto_buff;
	// 자동버프에 필요한 아데나 수량
	static public int robot_auto_buff_aden;
	// 무인케릭 사용유무
	static public boolean robot_auto_pc;
	// 무인케릭 활성화될 갯수.
	static public int robot_auto_pc_count;
	
	// 해당 값보다 높은인첸이 이뤄졌을경우 월드에 메세지 표현처리함.
	static public int world_message_enchant_weapon;
	static public int world_message_enchant_armor;
	// 사용자들이 월드에 접속시 접속했다는 메세지를 전체 유저에게 알릴지 여부.
	static public boolean world_message_join;
	
	// 캐릭터 삭제 여부
	static public boolean character_delete = true;
	
	// mr 최대치값 설정.
	static public int max_mr;
	
	// 메모리 재사용에 사용될 객체 적재 최대 갯수 값.
	static public int pool_max = 100;
	
	// 콜롯세움 설정 값들
	static public boolean colosseum_talkingisland = false;
	static public boolean colosseum_silverknighttown = false;
	static public boolean colosseum_gludin = false;
	static public boolean colosseum_windawood = false;
	static public boolean colosseum_kent = false;
	
	// psjump
	static public boolean psjump_sp_print = true;
	
	// 수중 맵 목록
	static public int MAP_AQUA[] = {
		63, 65
	};
	
	// 서먼객체가 이동 불가능한 맵. 또는 소환 불가능한 맵
	static public int SummonTeleportImpossibleMap[] = {
		63, 65
	};
	
	// 펫이 이동 불가능한 맵. (사용자가 텔레포트할때 해당 값을 확인해서 처리할지 여부 구분함.)
	static public int PetTeleportImpossibleMap[] = {
		5, 6, 22, 63, 65, 70, 83, 84, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 201, 202, 203, 204, 209, 210, 211, 
		212, 213, 214, 215, 216, 221, 222, 223, 224, 225, 226, 227, 228
	};

	// 랜덤 텔레포트 가능한 맵
	static public int TeleportPossibleMap[] = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
			23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
			52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 68, 69, 75, 76, 77, 78, 79, 83, 84, 85, 86, 209, 210,
			211, 212, 213, 214, 215, 216, 240, 241, 242, 243, 300, 301, 304, 400, 401, 420, 440, 445, 480, 522, 523,
			524, 541, 542, 543 };
	
	// 오만의탑 지배자의 부적
	static public int TeleportPossibleMap1[] = {
		101, 102, 103, 104, 105, 106, 107, 108, 109, 110
	};
	
	static public int TeleportPossibleMapLength = TeleportPossibleMap.length;
	static public int TeleportPossibleMapLength1 = TeleportPossibleMap1.length;
	
	// 이뮨투함 데미지 감소
	public static double immune_to_harm;

	// 귀환 및 축순, 이반 불가능한 맵
	static public final int TeleportHomeImpossibilityMap[] = {
		70, 200, 303, 666
	};
	static public final int TeleportHomeImpossibilityMapLength = TeleportHomeImpossibilityMap.length;

	// 각 성별 외성 내부 좌표값
	static public final int KINGDOMLOCATION[][] = {
			{0,0,0,0,0,0},
			{33089, 33219, 32717, 32827, 4, KINGDOM_KENT},
			{32750, 32850, 32250, 32350, 4, KINGDOM_ORCISH},
			{32571, 32721, 33350, 33460, 4, KINGDOM_WINDAWOOD},
			{33559, 33686, 32615, 32755, 4, KINGDOM_GIRAN},
			{33458, 33583, 33315, 33490, 4, KINGDOM_HEINE},
			{32755, 32870, 32790, 32920, 66, KINGDOM_ABYSS},
			{34007, 34162, 33172, 33332, 4, KINGDOM_ADEN},
			{32888, 33070, 32839, 32953, 320, 0},
	};
	
	// 아지트 좌표값
	static public final int AGITLOCATION[][] = {
		{33368, 33375, 32651, 32654},	// 기란, 1번, 78평
		{33373, 33375, 32655, 32657},
		{33381, 33387, 32653, 32656},	// 기란, 2번, 45평
		{33392, 33404, 32650, 32657},	// 기란, 3번, 120평
		{33427, 33430, 32656, 32662},	// 기란, 4번, 45평
		{33442, 33445, 32665, 32672},	// 기란, 5번, 78평
		{33439, 33441, 32665, 32667},
		{33454, 33466, 32648, 32655},	// 기란, 6번, 120평
		{33476, 33479, 32665, 32671},	// 기란, 7번, 45평
		{33474, 33477, 32678, 32685},	// 기란, 8번, 78평
		{33471, 33473, 32678, 32680},
		{33453, 33460, 32694, 32697},	// 기란, 9번, 78평
		{33458, 33460, 32698, 32700},
		{33421, 33433, 32685, 32692},	// 기란, 10번, 120평
		{33412, 33415, 32674, 32681},	// 기란, 11번, 78평
		{33409, 33411, 32674, 32676},
		{33414, 33421, 32703, 32706},	// 기란, 12번, 78평
		{33419, 33421, 32707, 32709},
		{33372, 33384, 32692, 32699},	// 기란, 13번, 120평
		{33362, 33365, 32681, 32687},	// 기란, 14번, 45평
		{33363, 33366, 32669, 32676},	// 기란, 15번, 78평
		{33360, 33362, 32669, 32671},
		{33344, 33347, 32660, 32667},	// 기란, 16번, 78평
		{33341, 33343, 32660, 32662},
		{33345, 33348, 32672, 32678},	// 기란, 17번, 45평
		{33338, 33350, 32704, 32711},	// 기란, 18번, 120평
		{33349, 33356, 32728, 32731},	// 기란, 19번, 78평
		{33354, 33356, 32732, 32734},
		{33369, 33372, 32713, 32720},	// 기란, 20번, 78평
		{33366, 33368, 32713, 32715},
		{33380, 33383, 32712, 32718},	// 기란, 21번, 45평 
		{33401, 33413, 32733, 32740},	// 기란, 22번, 120평
		{33427, 33430, 32717, 32724},	// 기란, 23번, 78평
		{33424, 33426, 32717, 32719},
		{33448, 33451, 32729, 32735},	// 기란, 24번, 45평
		{33404, 33407, 32754, 32760},	// 기란, 25번, 45평
		{33363, 33375, 32755, 32762},	// 기란, 26번, 120평
		{33354, 33357, 32774, 32781},	// 기란, 27번, 78평
		{33351, 33353, 32774, 32776},
		{33355, 33361, 32787, 32790},	// 기란, 28번, 45평
		{33366, 33373, 32786, 32789},	// 기란, 29번, 78평
		{33371, 33373, 32790, 32792},
		{33383, 33386, 32773, 32779},	// 기란, 30번, 45평
		{33397, 33404, 32788, 32791},	// 기란, 31번, 78평
		{33402, 33404, 32792, 32794},
		{33479, 33486, 32788, 32791},	// 기란, 32번, 78평
		{33484, 33486, 32792, 32794},
		{33498, 33501, 32801, 32807},	// 기란, 33번, 45평
		{33379, 33385, 32802, 32805},	// 기란, 34번, 45평
		{33373, 33385, 32822, 32829},	// 기란, 35번, 120평
		{33398, 33401, 32810, 32816},	// 기란, 36번, 45평
		{33400, 33403, 32821, 32828},	// 기란, 37번, 78평
		{33397, 33399, 32821, 32823},
		{33431, 33438, 32838, 32841},	// 기란, 38번, 78평
		{33436, 33438, 32842, 32844},
		{33457, 33463, 32832, 32835},	// 기란, 39번, 45평
		{33385, 33392, 32845, 32848},	// 기란, 40번, 78평
		{33390, 33392, 32849, 32851},
		{33402, 33405, 32589, 32866},	// 기란, 41번, 78평
		{33399, 33401, 32859, 32861},
		{33414, 33417, 32850, 32856},	// 기란, 42번, 45평
		{33372, 33384, 32867, 32874},	// 기란, 43번, 120평
		{33425, 33437, 32865, 32872},	// 기란, 44번, 120평
		{33446, 33449, 32869, 32876},	// 기란, 45번, 78평
		{33443, 33445, 32869, 32871}
	};

	// 죽어도 경험치를 잃어버리지 않는 맵
	static public int MAP_EXP_NOT_LOSE[] = {
		303,208
	};
	
	// 죽어도 아이템을 드랍하지 않는 맵
	static public int MAP_ITEM_NOT_DROP[] = {
		303,208
	};
	
	
	/**
	 * 리니지에 사용되는 변수 초기화 함수.
	 */
	static public void init(){
		TimeLine.start("Lineage..");
		
		try {
			BufferedReader lnrr = new BufferedReader( new FileReader("lineage.conf"));
			String line;
			while ( (line = lnrr.readLine()) != null){
				if(line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				if(pos>0){
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos+1, line.length()).trim();
					
					if(key.equalsIgnoreCase("royal_spawn"))
						toFirstSpawn(royal_spawn, value);
					else if(key.equalsIgnoreCase("royal_male_gfx"))
						royal_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_female_gfx"))
						royal_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_hp"))
						royal_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_mp"))
						royal_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_max_hp"))
						royal_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_max_mp"))
						royal_max_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_spell"))
						toFirstSpell(royal_first_spell, value);
					else if(key.equalsIgnoreCase("royal_first_inventory"))
						toFirstInventory(royal_first_inventory, value);
					else if(key.equalsIgnoreCase("royal_stat_str"))
						royal_stat_str = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_stat_con"))
						royal_stat_con = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_stat_dex"))
						royal_stat_dex = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_stat_wis"))
						royal_stat_wis = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_stat_cha"))
						royal_stat_cha = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_stat_int"))
						royal_stat_int = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("royal_stat_dice"))
						royal_stat_dice = Integer.valueOf(value);
					
					else if(key.equalsIgnoreCase("knight_spawn"))
						toFirstSpawn(knight_spawn, value);
					else if(key.equalsIgnoreCase("knight_male_gfx"))
						knight_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_female_gfx"))
						knight_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_hp"))
						knight_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_mp"))
						knight_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_max_hp"))
						knight_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_max_mp"))
						knight_max_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_spell"))
						toFirstSpell(knight_first_spell, value);
					else if(key.equalsIgnoreCase("knight_first_inventory"))
						toFirstInventory(knight_first_inventory, value);
					else if(key.equalsIgnoreCase("knight_stat_str"))
						knight_stat_str = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_stat_con"))
						knight_stat_con = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_stat_dex"))
						knight_stat_dex = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_stat_wis"))
						knight_stat_wis = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_stat_cha"))
						knight_stat_cha = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_stat_int"))
						knight_stat_int = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("knight_stat_dice"))
						knight_stat_dice = Integer.valueOf(value);
					
					else if(key.equalsIgnoreCase("elf_spawn"))
						toFirstSpawn(elf_spawn, value);
					else if(key.equalsIgnoreCase("elf_male_gfx"))
						elf_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_female_gfx"))
						elf_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_hp"))
						elf_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_mp"))
						elf_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_max_hp"))
						elf_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_max_mp"))
						elf_max_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_spell"))
						toFirstSpell(elf_first_spell, value);
					else if(key.equalsIgnoreCase("elf_first_inventory"))
						toFirstInventory(elf_first_inventory, value);
					else if(key.equalsIgnoreCase("elf_stat_str"))
						elf_stat_str = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_stat_con"))
						elf_stat_con = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_stat_dex"))
						elf_stat_dex = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_stat_wis"))
						elf_stat_wis = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_stat_cha"))
						elf_stat_cha = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_stat_int"))
						elf_stat_int = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_stat_dice"))
						elf_stat_dice = Integer.valueOf(value);
					
					else if(key.equalsIgnoreCase("wizard_spawn"))
						toFirstSpawn(wizard_spawn, value);
					else if(key.equalsIgnoreCase("wizard_male_gfx"))
						wizard_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_female_gfx"))
						wizard_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_hp"))
						wizard_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_mp"))
						wizard_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_max_hp"))
						wizard_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_max_mp"))
						wizard_max_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_spell"))
						toFirstSpell(wizard_first_spell, value);
					else if(key.equalsIgnoreCase("wizard_first_inventory"))
						toFirstInventory(wizard_first_inventory, value);
					else if(key.equalsIgnoreCase("wizard_stat_str"))
						wizard_stat_str = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_stat_con"))
						wizard_stat_con = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_stat_dex"))
						wizard_stat_dex = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_stat_wis"))
						wizard_stat_wis = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_stat_cha"))
						wizard_stat_cha = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_stat_int"))
						wizard_stat_int = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("wizard_stat_dice"))
						wizard_stat_dice = Integer.valueOf(value);
					
					else if(key.equalsIgnoreCase("darkelf_spawn"))
						toFirstSpawn(darkelf_spawn, value);
					else if(key.equalsIgnoreCase("darkelf_male_gfx"))
						darkelf_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("darkelf_female_gfx"))
						darkelf_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("darkelf_hp"))
						darkelf_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("darkelf_mp"))
						darkelf_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("darkelf_max_hp"))
						darkelf_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("darkelf_max_mp"))
						darkelf_max_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("darkelf_spell"))
						toFirstSpell(darkelf_first_spell, value);
					else if(key.equalsIgnoreCase("darkelf_first_inventory"))
						toFirstInventory(darkelf_first_inventory, value);
					
					else if(key.equalsIgnoreCase("dragonknight_spawn"))
						toFirstSpawn(dragonknight_spawn, value);
					else if(key.equalsIgnoreCase("dragonknight_male_gfx"))
						dragonknight_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("dragonknight_female_gfx"))
						dragonknight_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("dragonknight_hp"))
						dragonknight_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("dragonknight_mp"))
						dragonknight_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("dragonknight_max_hp"))
						dragonknight_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("dragonknight_spell"))
						toFirstSpell(dragonknight_first_spell, value);
					else if(key.equalsIgnoreCase("dragonknight_first_inventory"))
						toFirstInventory(dragonknight_first_inventory, value);
					else if(key.equalsIgnoreCase("dragonknight_max_mp"))
						blackwizard_max_mp = Integer.valueOf(value);
					
					else if(key.equalsIgnoreCase("blackwizard_spawn"))
						toFirstSpawn(blackwizard_spawn, value);
					else if(key.equalsIgnoreCase("blackwizard_male_gfx"))
						blackwizard_male_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("blackwizard_female_gfx"))
						blackwizard_female_gfx = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("blackwizard_hp"))
						blackwizard_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("blackwizard_mp"))
						blackwizard_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("blackwizard_max_hp"))
						blackwizard_max_hp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("blackwizard_max_mp"))
						blackwizard_max_mp = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("blackwizard_spell"))
						toFirstSpell(blackwizard_first_spell, value);
					else if(key.equalsIgnoreCase("blackwizard_first_inventory"))
						toFirstInventory(blackwizard_first_inventory, value);
					
					else if(key.equalsIgnoreCase("item_enchant_armor_max"))
						item_enchant_armor_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("item_enchant_weapon_max"))
						item_enchant_weapon_max = Integer.valueOf(value);
					
					else if(key.equalsIgnoreCase("inventory_max"))
						inventory_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("inventory_weight_max"))
						inventory_weight_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("level_max"))
						level_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("party_max"))
						party_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("warehouse_level"))
						warehouse_level = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("warehouse_price"))
						warehouse_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("warehouse_price_elf"))
						warehouse_price_elf = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("warehouse_max"))
						warehouse_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("stat_max"))
						stat_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("rate_enchant"))
						rate_enchant = Double.valueOf(value);
					else if(key.equalsIgnoreCase("rate_drop"))
						rate_drop = Double.valueOf(value);
					else if(key.equalsIgnoreCase("rate_exp"))
						rate_exp = Double.valueOf(value);
					else if(key.equalsIgnoreCase("rate_lawful"))
						rate_lawful = Double.valueOf(value);
					else if(key.equalsIgnoreCase("rate_aden"))
						rate_aden = Double.valueOf(value);
					else if(key.equalsIgnoreCase("rate_party"))
						rate_party = Double.valueOf(value);
					else if(key.equalsIgnoreCase("rate_exp_pet"))
						rate_exp_pet = Double.valueOf(value);
					else if(key.equalsIgnoreCase("chatting_level_global"))
						chatting_level_global = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("chatting_level_normal"))
						chatting_level_normal = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("chatting_level_whisper"))
						chatting_level_whisper = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("nonpvp"))
						nonpvp = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("server_version"))
						server_version = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("GMCODE"))
						GMCODE = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("ai_corpse_time"))
						ai_corpse_time = Integer.valueOf(value) * 1000;
					else if(key.equalsIgnoreCase("ai_summon_corpse_time"))
						ai_summon_corpse_time = Integer.valueOf(value) * 1000;
					else if(key.equalsIgnoreCase("ai_pet_corpse_time"))
						ai_pet_corpse_time = Integer.valueOf(value) * 1000;
					else if(key.equalsIgnoreCase("ai_auto_healingpotion_percent"))
						ai_auto_healingpotion_percent = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("warehouse_pet_price"))
						warehouse_pet_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("flat_rate"))
						flat_rate = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("flat_rate_price"))
						flat_rate_price = Integer.valueOf(value) * 60;
					else if(key.equalsIgnoreCase("account_auto_create"))
						account_auto_create = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("inn_max"))
						inn_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("inn_in_max"))
						inn_in_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("inn_price"))
						inn_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("inn_time"))
						inn_time = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("board_write_price"))
						board_write_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("board_rank_update_delay"))
						board_rank_update_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("inn_hall_max"))
						inn_hall_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("inn_hall_in_max"))
						inn_hall_in_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("inn_hall_price"))
						inn_hall_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("min_tax"))
						min_tax = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("max_tax"))
						max_tax = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("kingdom_war_day"))
						kingdom_war_day = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("kingdom_war_time"))
						kingdom_war_time = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("kingdom_player_dead_expdown"))
						kingdom_player_dead_expdown = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("kingdom_player_dead_itemdrop"))
						kingdom_player_dead_itemdrop = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("kingdom_crown"))
						kingdom_crown = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("kingdom_pvp_pk"))
						kingdom_pvp_pk = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("kingdom_soldier_price")) //켄트 성 용병 대장 훈트
						kingdom_soldier_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("esmereld_sec"))
						esmereld_sec = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elf_gatherup_time"))
						elf_gatherup_time = Integer.valueOf(value) * 1000;
					else if(key.equalsIgnoreCase("npc_talk_stay_time"))
						npc_talk_stay_time = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elvenforest_elementalstone_spawn_count"))
						elvenforest_elementalstone_spawn_count = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elvenforest_elementalstone_spawn_time"))
						elvenforest_elementalstone_spawn_time = Integer.valueOf(value) * 1000;
					else if(key.equalsIgnoreCase("elvenforest_elementalstone_min_count"))
						elvenforest_elementalstone_min_count = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("elvenforest_elementalstone_max_count"))
						elvenforest_elementalstone_max_count = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("door_open_delay"))
						door_open_delay = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("object_who"))
						object_who = value;
					else if(key.equalsIgnoreCase("world_item_delay"))
						world_item_delay = Integer.valueOf(value) * 60;
					else if(key.equalsIgnoreCase("auto_pickup"))
						auto_pickup = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("auto_pickup_aden"))
						auto_pickup_aden = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("auto_pickup_percent"))
						auto_pickup_percent = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("is_dex_ac"))
						is_dex_ac = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("character_quest"))
						CHARACTER_QUEST = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("chance_max_drop"))
						chance_max_drop = Double.valueOf(value);
					else if(key.equalsIgnoreCase("player_dead_itemdrop"))
						player_dead_itemdrop = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("player_dead_expdown"))
						player_dead_expdown = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("shurom_magic_calculated"))
						shurom_magic_calculated = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("monster_interface_hpbar"))
						monster_interface_hpbar = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("npc_interface_hpbar"))
						npc_interface_hpbar = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("ai_monster_tic_time"))
						ai_monster_tic_time = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("item_durability_max"))
						item_durability_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("pet_level_max"))
						pet_level_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("notice_delay"))
						notice_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("event_poly"))
						event_poly = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("account_ip_count"))
						account_ip_count = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("event_buff"))
						event_buff = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("event_illusion"))
						event_illusion = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("event_christmas"))
						event_christmas = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("event_halloween"))
						event_halloween = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("event_lyra"))
						event_lyra = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("auto_save_time"))
						auto_save_time = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("slime_race_price"))
						slime_race_price = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("bravery_potion_frame"))
						bravery_potion_frame = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("elven_wafer_frame"))
						elven_wafer_frame = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("holywalk_frame"))
						holywalk_frame = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("world_premium_item_is"))
						world_premium_item_is = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("world_premium_item"))
						world_premium_item = value;
					else if(key.equalsIgnoreCase("world_premium_item_min"))
						world_premium_item_min = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("world_premium_item_max"))
						world_premium_item_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("world_premium_item_delay"))
						world_premium_item_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("item_equipped_type"))
						item_equipped_type = value.equalsIgnoreCase("new");
					else if(key.equalsIgnoreCase("robot_auto_buff"))
						robot_auto_buff = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("robot_auto_buff_aden"))
						robot_auto_buff_aden = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("world_message_enchant_weapon"))
						world_message_enchant_weapon = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("world_message_enchant_armor"))
						world_message_enchant_armor = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("world_message_join"))
						world_message_join = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("robot_auto_pc"))
						robot_auto_pc = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("robot_auto_pc_count"))
						robot_auto_pc_count = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("max_mr"))
						max_mr = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("monster_level_exp"))
						monster_level_exp = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("monster_summon_item_drop"))
						monster_summon_item_drop = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("pool_max"))
						pool_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("colosseum_talkingisland"))
						colosseum_talkingisland = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("colosseum_silverknighttown"))
						colosseum_silverknighttown = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("colosseum_gludin"))
						colosseum_gludin = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("colosseum_windawood"))
						colosseum_windawood = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("colosseum_kent"))
						colosseum_kent = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("item_accessory_bless_enchant"))
						item_accessory_bless_enchant = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("item_polymorph_bless"))
						item_polymorph_bless = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("psjump_sp_print"))
						psjump_sp_print = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("world_player_count"))
						world_player_count = Integer.valueOf(value);
					// 스핵
					else if(key.equalsIgnoreCase("speedhack"))
						speedhack = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("speedhack_warning_count"))
						speedhack_warning_count = Integer.valueOf(value);
					
					
					else if(key.equalsIgnoreCase("item_enchant_armor")){
						try {
							StringTokenizer st = new StringTokenizer(value, ",");
							//
							StringTokenizer stt = new StringTokenizer(st.nextToken().trim(), "~");
							item_enchant_armor_curse_min = Integer.valueOf(stt.nextToken());
							item_enchant_armor_curse_max = Integer.valueOf(stt.nextToken());
							//
							stt = new StringTokenizer(st.nextToken().trim(), "~");
							item_enchant_armor_normal_min = Integer.valueOf(stt.nextToken());
							item_enchant_armor_normal_max = Integer.valueOf(stt.nextToken());
							//
							stt = new StringTokenizer(st.nextToken().trim(), "~");
							item_enchant_armor_bless_min = Integer.valueOf(stt.nextToken());
							item_enchant_armor_bless_max = Integer.valueOf(stt.nextToken());
						} catch (Exception e) {}
					}else if(key.equalsIgnoreCase("item_enchant_weapon")){
						try {
							StringTokenizer st = new StringTokenizer(value, ",");
							//
							StringTokenizer stt = new StringTokenizer(st.nextToken().trim(), "~");
							item_enchant_weapon_curse_min = Integer.valueOf(stt.nextToken());
							item_enchant_weapon_curse_max = Integer.valueOf(stt.nextToken());
							//
							stt = new StringTokenizer(st.nextToken().trim(), "~");
							item_enchant_weapon_normal_min = Integer.valueOf(stt.nextToken());
							item_enchant_weapon_normal_max = Integer.valueOf(stt.nextToken());
							//
							stt = new StringTokenizer(st.nextToken().trim(), "~");
							item_enchant_weapon_bless_min = Integer.valueOf(stt.nextToken());
							item_enchant_weapon_bless_max = Integer.valueOf(stt.nextToken());
						} catch (Exception e) {}
					}else if(key.equalsIgnoreCase("item_enchant_bless"))
						item_enchant_bless = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("pet_damage_to_player"))
						pet_damage_to_player = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("item_elixir_max"))
						item_elixir_max = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("thread_ai"))
						thread_ai = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("thread_event"))
						thread_event = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("client_ping_time"))
						client_ping_time = Integer.valueOf(value);
				}
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Lineage.class.toString());
			lineage.share.System.println(e);
		}

		// 315 이상부터 해상도가 변경되기때문에
		if(server_version > 310){
			SEARCH_LOCATIONRANGE = 16;
			SEARCH_MONSTER_TARGET_LOCATION = 21;
		}
		
		TimeLine.end();
	}
	
	/**
	 * 순차적으로 배열된 클레스값을 제곱순에 클레스값으로 리턴하는 함수.
	 * @param class_type
	 * @return
	 */
	static public int getClassType(int class_type){
		switch(class_type){
			case LINEAGE_CLASS_ROYAL:
				return LINEAGE_ROYAL;
			case LINEAGE_CLASS_KNIGHT:
				return LINEAGE_KNIGHT;
			case LINEAGE_CLASS_ELF:
				return LINEAGE_ELF;
			case LINEAGE_CLASS_WIZARD:
				return LINEAGE_WIZARD;
		}
		return 0;
	}
	
	static private void toFirstSpell(List<FirstSpell> list, String value){
		list.clear();
		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens()){
			int uid = Integer.valueOf(st.nextToken());
			if(uid > 0)
				list.add( new FirstSpell(uid) );
		}
	}
	
	static private void toFirstInventory(List<FirstInventory> list, String value){
		list.clear();
		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens()){
			String db = st.nextToken().trim();
			int f_pos = db.indexOf("(");
			int e_pos = db.indexOf(")");
			String name = db.substring(0, f_pos).trim();
			int count = Integer.valueOf(db.substring(f_pos+1, e_pos).trim());
			list.add( new FirstInventory(name, count) );
		}
	}
	
	static private void toFirstSpawn(List<FirstSpawn> list, String value){
		list.clear();
		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens()){
			String db = st.nextToken().trim();
			String x = db.substring(0, db.indexOf(" ")).trim();
			String y = db.substring(x.length()+1, db.indexOf(" ", x.length()+1)).trim();
			String map = db.substring(x.length()+y.length()+2).trim();
			list.add( new FirstSpawn(Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(map)) );
		}
	}
}
