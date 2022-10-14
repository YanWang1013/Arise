package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

import lineage.bean.database.FirstInventory;
import lineage.bean.database.FirstSpawn;
import lineage.bean.database.FirstSpell;
import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Book;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Inventory;
import lineage.network.Client;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryList;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Letter;
import lineage.world.object.magic.BlessOfEarth;
import lineage.world.object.magic.Blue;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.ChattingClose;
import lineage.world.object.magic.ClearMind;
import lineage.world.object.magic.CurseBlind;
import lineage.world.object.magic.CurseParalyze;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.EarthSkin;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.Eva;
import lineage.world.object.magic.FloatingEyeMeat;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.IceLance;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.InvisiBility;
import lineage.world.object.magic.IronSkin;
import lineage.world.object.magic.Light;
import lineage.world.object.magic.ResistElemental;
import lineage.world.object.magic.ShadowArmor;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Shield;
import lineage.world.object.magic.Slow;
import lineage.world.object.magic.Wafer;
import lineage.world.object.magic.Wisdom;
import lineage.world.object.magic.monster.CurseGhoul;
import lineage.world.object.magic.sp.ExpPotion;

public final class CharactersDatabase {
	
	/**
	 * 디비에있는 케릭터를 찾아서 좌표 변경하는 함수.
	 * @param con
	 * @param name
	 * @param x
	 * @param y
	 * @param map
	 */
	static public void updateLocation(Connection con, String name, int x, int y, int map){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE characters SET locX=?, locY=?, locMAP=? WHERE LOWER(name)=?");
			st.setInt(1, x);
			st.setInt(2, y);
			st.setInt(3, map);
			st.setString(4, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLocation(Connection con, String name, int x, int y, int map)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 디비에있는 케릭터 이름 목록 만들어서 리턴함.
	 * @param con
	 * @param r_list
	 */
	static public void getNameAllList(Connection con, List<String> r_list){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters ORDER BY name");
			rs = st.executeQuery();
			while(rs.next())
				r_list.add( rs.getString("name") );
		} catch (Exception e) {
			lineage.share.System.printf("%s : getNameAllList(Connection con, List<String> r_list)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 경매처리구간에서 Agit객체에 사용자 정보를 등록해야하는 일이 있음.<br/>
	 * 그 구간처리를 여기서 맡음.
	 * @param con
	 * @param name
	 * @param agit
	 */
	static public void updateAgit(Connection con, String name, Agit agit){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next()){
				agit.setClanId(rs.getInt("clanID"));
				agit.setClanName(rs.getString("clanNAME"));
				agit.setChaName(rs.getString("name"));
				agit.setChaObjectId(rs.getInt("objID"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateAgit(Connection con, String name, Agit agit)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterObjectId(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT objID FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt("objID");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterObjectId(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	
	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public long getCharacterRegisterDate(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT register_date FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				return rs.getLong("register_date");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterRegisterDate(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	
	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterLevel(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT level FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterLevel(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	
	/**
	 * 디비에서 이름에 해당하는 케릭터의 혈맹아이디 찾아서 리턴.
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterClanId(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT clanID FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClanId(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	
	/**
	 * 디비에서 이름에 해당하는 케릭터의 클레스 종류 리턴.
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterClass(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT class FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClass(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}

	/**
	 * 케릭터 테이블에서 해당하는 이름이 존재하는지 확인하는 함수.
	 * @param con
	 * @param name
	 * @return
	 */
	static public boolean isCharacterName(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT name FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacterName(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}
	
	/**
	 * 생성할수 없는 이름 목록에 확인하려는 이름이 존재하는지 체크하는 메서드.
	 * @param con
	 * @param name
	 * @return
	 */
	static public boolean isInvalidName(Connection con, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT name FROM bad_name WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isInvalidName(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}
	
	/**
	 * 케릭터 디비에 해당 오브젝트가 존재하는지 확인해주는 함수.
	 * @param con
	 * @param obj_id
	 * @return
	 */
	static public boolean isCharacterObjectId(Connection con, long obj_id){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters WHERE objID=?");
			st.setLong(1, obj_id);
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacterObjectId(Connection con, long obj_id)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}
	
	/**
	 * 케릭터 정보 등록 처리 함수.
	 * @param con
	 * @param obj_id
	 * @param name
	 * @param type
	 * @param sex
	 * @param hp
	 * @param mp
	 * @param Str
	 * @param Dex
	 * @param Con
	 * @param Wis
	 * @param Cha
	 * @param Int
	 * @param gfx
	 * @param x
	 * @param y
	 * @param map
	 * @param account_id
	 * @param account_uid
	 */
	static public void insertCharacter(Connection con, long obj_id, String name, int type, int sex, int hp, int mp, int Str, int Dex, int Con, int Wis, int Cha, int Int, int gfx, FirstSpawn fs, String account_id, int account_uid, long time){
		PreparedStatement st = null;
		try{
			// characters
			st = con.prepareStatement("INSERT INTO characters SET name=?, account=?, account_uid=?, objID=?, nowHP=?, maxHP=?, nowMP=?, maxMP=?, str=?, dex=?, con=?, wis=?, inter=?, cha=?, sex=?, class=?, locX=?, locY=?, locMAP=?, gfx=?, register_date=?, food=?");
			st.setString(1, name);
			st.setString(2, account_id);
			st.setInt(3, account_uid);
			st.setLong(4, obj_id);
			st.setInt(5, hp);
			st.setInt(6, hp);
			st.setInt(7, mp);
			st.setInt(8, mp);
			st.setInt(9, Str);
			st.setInt(10, Dex);
			st.setInt(11, Con);
			st.setInt(12, Wis);
			st.setInt(13, Int);
			st.setInt(14, Cha);
			st.setInt(15, sex);
			st.setInt(16, type);
			if(fs == null){
				st.setInt(17, 32576);
				st.setInt(18, 32926);
				st.setInt(19, 0);
			}else{
				st.setInt(17, fs.getX());
				st.setInt(18, fs.getY());
				st.setInt(19, fs.getMap());
			}
			st.setInt(20, gfx);
			st.setLong(21, time);
			st.setInt(22, Lineage.server_version>230 ? 5 : 65);
			st.executeUpdate();
			st.close();
			// characters_buff
			st = con.prepareStatement("INSERT INTO characters_buff SET name=?, objID=?");
			st.setString(1, name);
			st.setLong(2, obj_id);
			st.executeUpdate();
			st.close();
		} catch(Exception e) {
			lineage.share.System.printf("%s : insertCharacter(Connection con, long obj_id, String name, int type, int sex, int hp, int mp, int Str, int Dex, int Con, int Wis, int Cha, int Int, int gfx, FirstSpawn fs, String account_id, int account_uid, long time)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 초기 지급 아이템 처리 함수.
	 * @param con
	 * @param obj_id
	 * @param name
	 * @param type
	 */
	static public void insertInventory(Connection con, long obj_id, String name, int type){
		PreparedStatement st = null;
		try{
			List<FirstInventory> list = null;
			switch(type){
				case Lineage.LINEAGE_CLASS_ROYAL:
					list = Lineage.royal_first_inventory;
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					list = Lineage.knight_first_inventory;
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					list = Lineage.elf_first_inventory;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					list = Lineage.wizard_first_inventory;
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					list = Lineage.darkelf_first_inventory;
					break;
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					list = Lineage.dragonknight_first_inventory;
					break;
				case Lineage.LINEAGE_CLASS_BLACKWIZARD:
					list = Lineage.blackwizard_first_inventory;
					break;
			}
			if(list != null){
				for(FirstInventory fi : list){
					Item i = ItemDatabase.find(fi.getName());
					if(i != null){
						for(int j=i.isPiles()?1:fi.getCount() ; j>0 ; --j){
							st = con.prepareStatement("INSERT INTO characters_inventory SET objId=?, cha_objId=?, cha_name=?, name=?, count=?, nowtime=?");
							st.setLong(1, ServerDatabase.nextItemObjId());
							st.setLong(2, obj_id);
							st.setString(3, name);
							st.setString(4, fi.getName());
							st.setInt(5, i.isPiles() ? fi.getCount() : 1);
							st.setInt(6, i.getNameIdNumber()==67 || i.getNameIdNumber()==2 || i.getNameIdNumber()==326 ? 600 : 0);
							st.executeUpdate();
							st.close();
						}
					}
				}
			}
		} catch(Exception e) {
			lineage.share.System.printf("%s : insertInventory(Connection con, long obj_id, String name, int type)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public void insertSkill(Connection con, long obj_id, String name, int type){
		PreparedStatement st = null;
		try{
			List<FirstSpell> spell = null;
			switch(type){
				case Lineage.LINEAGE_CLASS_ROYAL:
					spell = Lineage.royal_first_spell;
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					spell = Lineage.knight_first_spell;
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					spell = Lineage.elf_first_spell;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					spell = Lineage.wizard_first_spell;
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					spell = Lineage.darkelf_first_spell;
					break;
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					spell = Lineage.dragonknight_first_spell;
					break;
				case Lineage.LINEAGE_CLASS_BLACKWIZARD:
					spell = Lineage.blackwizard_first_spell;
					break;
			}
			if(spell!=null) {
				for(FirstSpell fs : spell){
					st = con.prepareStatement("INSERT INTO characters_skill SET cha_objId=?, cha_name=?, skill=?");
					st.setLong(1, obj_id);
					st.setString(2, name);
					st.setInt(3, fs.getSpellUid());
					st.executeUpdate();
					st.close();
				}
			}
		} catch(Exception e) {
			lineage.share.System.printf("%s : insertSkill(Connection con, long obj_id, String name, int type)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * uid와 연결된 케릭터테이블에 해당하는 이름이 존재하는지 확인.
	 * @param con
	 * @param account_uid
	 * @param name
	 * @return
	 */
	static public boolean isCharacter(Connection con, int account_uid, String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters WHERE account_uid=? AND LOWER(name)=? AND block_date='0000-00-00 00:00:00'");
			st.setInt(1, account_uid);
			st.setString(2, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacter(Connection con, int account_uid, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}
	
	/**
	 * 해당하는 케릭터가 월드에 접속한 시간 갱신해주는 함수
	 * @param con
	 * @param name
	 */
	static public void updateCharacterJoinTimeStamp(Connection con, String name){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE characters SET join_date=? WHERE LOWER(name)=?");
			st.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			st.setString(2, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateCharacterJoinTimeStamp(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * name과 연결된 케릭터테이블에 정보 추출 pcinstance 객체 생성 리턴.
	 * @param con
	 * @param c
	 * @param name
	 * @return
	 */
	static public PcInstance readCharacter(Connection con, Client c, String name){
		PcInstance pc = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters WHERE account_uid=? AND LOWER(name)=?");
			st.setInt(1, c.getAccountUid());
			st.setString(2, name.toLowerCase());
			rs = st.executeQuery();
			if(rs.next()){
				pc = c.getPc();
				pc.setName(rs.getString("name"));
				pc.setObjectId(rs.getInt("objID"));
				pc.setLevel( rs.getInt("level") );
				pc.setMaxHp( rs.getInt("maxHP") );
				pc.setNowHp( rs.getInt("nowHP") );
				pc.setMaxMp( rs.getInt("maxMP") );
				pc.setNowMp( rs.getInt("nowMP") );
				pc.setStr( rs.getInt("str") );
				pc.setDex( rs.getInt("dex") );
				pc.setCon( rs.getInt("con") );
				pc.setWis( rs.getInt("wis") );
				pc.setInt( rs.getInt("inter") );
				pc.setCha( rs.getInt("cha") );
				pc.setClassSex( rs.getInt("sex") );
				pc.setClassType( rs.getInt("class") );
				pc.setExp( rs.getDouble("exp") );
				pc.setLostExp( rs.getDouble("lost_exp") );
				pc.setX(rs.getInt("locX"));
				pc.setY(rs.getInt("locY"));
				pc.setMap(rs.getInt("locMAP"));
				pc.setTitle( rs.getString("title") );
				pc.setFood( rs.getInt("food") );
				pc.setGfx( rs.getInt("gfx") );
				pc.setGfxMode( rs.getInt("gfxMode") );
				pc.setLawful( rs.getInt("lawful") );
				pc.setClanId( rs.getInt("clanID") );
				pc.setClanName( rs.getString("clanNAME") );
				pc.setPkCount( rs.getInt("pkcount") );
				try { pc.setPkTime( rs.getTimestamp("pkTime").getTime() ); } catch (Exception e) {}
				pc.setChattingGlobal( rs.getInt("global_chating")==1 );
				pc.setChattingTrade( rs.getInt("trade_chating")==1 );
				pc.setChattingWhisper( rs.getInt("whisper_chating")==1 );
				pc.setAttribute( rs.getInt("attribute") );
				pc.setLvStr( rs.getInt("lvStr") );
				pc.setLvCon( rs.getInt("lvCon") );
				pc.setLvDex( rs.getInt("lvDex") );
				pc.setLvWis( rs.getInt("lvWis") );
				pc.setLvInt( rs.getInt("lvInt") );
				pc.setLvCha( rs.getInt("lvCha") );
				pc.setElixir( rs.getInt("elixir") );
				pc.setGm( rs.getInt("isGm") );
				pc.setRegisterDate( rs.getLong("register_date") );
				if(rs.getString("save_interface").length() > 0)
					pc.setDbInterface( Util.StringToByte(rs.getString("save_interface")) );
				
				switch(pc.getClassType()){
					case Lineage.LINEAGE_CLASS_ROYAL:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.royal_male_gfx : Lineage.royal_female_gfx);
						break;
					case Lineage.LINEAGE_CLASS_KNIGHT:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.knight_male_gfx : Lineage.knight_female_gfx);
						break;
					case Lineage.LINEAGE_CLASS_ELF:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.elf_male_gfx : Lineage.elf_female_gfx);
						break;
					case Lineage.LINEAGE_CLASS_WIZARD:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
						break;
					case Lineage.LINEAGE_CLASS_DARKELF:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx);
						break;
					case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.dragonknight_male_gfx : Lineage.dragonknight_female_gfx);
						break;
					case Lineage.LINEAGE_CLASS_BLACKWIZARD:
						pc.setClassGfx(pc.getClassSex()==0 ? Lineage.blackwizard_male_gfx : Lineage.blackwizard_female_gfx);
						break;
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readCharacter(Connection con, Client c, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return pc;
	}
	
	/**
	 * 인벤토리 정보 추출.
	 * @param con
	 * @param pc
	 */
	static public void readInventory(Connection con, PcInstance pc){
		Inventory inv = pc.getInventory();
		if(inv==null)
			return;

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			while(rs.next()){
				ItemInstance item = ItemDatabase.newInstance( ItemDatabase.find(rs.getString("name")) );
				if(item!=null){
					item.setObjectId(rs.getInt(1));
					item.setCount(rs.getInt(5));
					item.setQuantity(rs.getInt(6));
					item.setEnLevel(rs.getInt(7));
					item.setEquipped(rs.getInt(8)==1);
					item.setDefinite(rs.getInt(9)==1);
					item.setBress(rs.getInt(10));
					item.setDurability(rs.getInt(11));
					item.setNowTime(rs.getInt(12));
					item.setPetObjectId(rs.getInt(13));
					item.setInnRoomKey(rs.getInt(14));
					item.setLetterUid(rs.getInt(15));
					item.setSlimeRaceTicket(rs.getString(16));
					
					// 편지지일경우 월드 업데이트를 우선함.
					if(item instanceof Letter){
						// 착용중인 아이템 정보 갱신.
						item.toWorldJoin(con, pc);
						// 인벤에 등록하면서 패킷 전송.
						inv.append( item, Lineage.server_version<=200 );
					}else{
						// 인벤에 등록하면서 패킷 전송.
						inv.append( item, Lineage.server_version<=200 );
						// 착용중인 아이템 정보 갱신.
						item.toWorldJoin(con, pc);
					}
				}
			}
			if(Lineage.server_version > 200)
				pc.toSender(S_InventoryList.clone(BasePacketPooling.getPool(S_InventoryList.class), inv));
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : readInventory(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 스킬 정보 추출.
	 * @param con
	 * @param pc
	 */
	static public void readSkill(Connection con, PcInstance pc){
		List<Skill> list = SkillController.find(pc);
		if(list==null)
			return;

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_skill WHERE cha_objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			while(rs.next()){
				Skill s = SkillDatabase.find(rs.getInt("skill"));
				if(s!=null)
					list.add(s);
			}
			
			SkillController.sendList(pc);
		} catch (Exception e) {
			lineage.share.System.printf("%s : readSkill(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 버프 정보 추출.
	 * @param con
	 * @param pc
	 */
	static public void readBuff(Connection con, PcInstance pc){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_buff WHERE objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			if(rs.next()){
				int light = rs.getInt("light");
				int shield = rs.getInt("shield");
				int curse_poison = rs.getInt("curse_poison");
				int curse_blind = rs.getInt("curse_blind");
				int slow = rs.getInt("slow");
				int curse_paralyze = rs.getInt("curse_paralyze");
				int enchant_dexterity = rs.getInt("enchant_dexterity");
				int enchant_mighty = rs.getInt("enchant_mighty");
				int haste = rs.getInt("haste");
				int invisibility = rs.getInt("invisibility");
				int shape_change = rs.getInt("shape_change");
				int immune_to_harm = rs.getInt("immune_to_harm");
				int bravery_potion = rs.getInt("bravery_potion");
				int elvenwafer = rs.getInt("elvenwafer");
				int eva = rs.getInt("eva");
				int wisdom = rs.getInt("wisdom");
				int blue_potion = rs.getInt("blue_potion");
				int floating_eye_meat = rs.getInt("floating_eye_meat");
				int clearmind = rs.getInt("clearmind");
				int resistelemental = rs.getInt("resistelemental");
				int icelance = rs.getInt("icelance");
				int earthskin = rs.getInt("earthskin");
				int ironskin = rs.getInt("ironskin");
				int blessearth = rs.getInt("blessearth");
				int curse_ghoul = rs.getInt("curse_ghoul");
				int chatting_close = rs.getInt("chatting_close");
				int holywalk = rs.getInt("holywalk");
				int shadowarmor = rs.getInt("shadowarmor");
				int exp_potion = rs.getInt("exp_potion");
				
				if(light>0)
					Light.init(pc, light);
				if(shield>0)
					Shield.init(pc, shield);
				if(curse_poison>0)
					CursePoison.init(pc, curse_poison);
				if(curse_blind>0)
					CurseBlind.init(pc, curse_blind);
				if(slow>0)
					Slow.init(pc, slow);
				if(curse_paralyze>0)
					CurseParalyze.init(pc, curse_paralyze);
				if(enchant_dexterity>0)
					EnchantDexterity.init(pc, enchant_dexterity);
				if(enchant_mighty>0)
					EnchantMighty.init(pc, enchant_mighty);
				if(haste>0)
					Haste.init(pc, haste);
				if(invisibility>0)
					InvisiBility.init(pc, invisibility);
				if(shape_change>0)
					ShapeChange.init(pc, shape_change);
				if(immune_to_harm>0)
					ImmuneToHarm.init(pc, immune_to_harm);
				if(bravery_potion>0)
					Bravery.init(pc, bravery_potion);
				if(elvenwafer>0)
					Wafer.init(pc, elvenwafer);
				if(eva>0)
					Eva.init(pc, eva);
				if(wisdom>0)
					Wisdom.init(pc, wisdom);
				if(blue_potion>0)
					Blue.init(pc, blue_potion);
				if(floating_eye_meat>0)
					FloatingEyeMeat.init(pc, floating_eye_meat);
				if(clearmind>0)
					ClearMind.init(pc, clearmind);
				if(resistelemental>0)
					ResistElemental.init(pc, resistelemental);
				if(icelance>0)
					IceLance.init(pc, icelance);
				if(earthskin>0)
					EarthSkin.init(pc, earthskin);
				if(ironskin>0)
					IronSkin.init(pc, ironskin);
				if(blessearth>0)
					BlessOfEarth.init(pc, blessearth);
				if(curse_ghoul>0)
					CurseGhoul.init(pc, curse_ghoul);
				if(chatting_close>0)
					ChattingClose.init(pc, chatting_close, true);
				if(holywalk>0)
					HolyWalk.init(pc, holywalk);
				if(shadowarmor>0)
					ShadowArmor.init(pc, shadowarmor);
				if(exp_potion>0)
					ExpPotion.init(pc, exp_potion);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readBuff(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 기억 정보 추출.
	 * @param con
	 * @param pc
	 */
	static public void readBook(Connection con, PcInstance pc){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_book WHERE objId=? ORDER BY location");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			while(rs.next()){
				Book b = BookController.getPool();
				b.setLocation(rs.getString("location"));
				b.setX(rs.getInt("locX"));
				b.setY(rs.getInt("locY"));
				b.setMap(rs.getInt("locMAP"));
				
				BookController.append(pc, b);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readBook(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 사용자 정보 저장 함수.
	 * @param con
	 * @param pc
	 */
	static public void saveCharacter(Connection con, PcInstance pc){
		PreparedStatement st = null;
		try {
			// 초기화.
			StringBuffer db_interface = new StringBuffer();
			// 추출.
			if(pc.getDbInterface() != null){
				for(byte d : pc.getDbInterface())
					db_interface.append( String.format("%02x", d & 0xff) );
			}
			// 처리.
			st = con.prepareStatement(	"UPDATE characters SET " +
										"level=?, nowHP=?, maxHP=?, nowMP=?, maxMP=?, ac=?, exp=?, locx=?, locy=?, locmap=?, title=?, " +
										"food=?, lawful=?, clanid=?, clanname=?, gfx=?, gfxmode=?, attribute=?, lvStr=?, lvCon=?, lvDex=?, " +
										"lvWis=?, lvInt=?, lvCha=?, global_chating=?, trade_chating=?, whisper_chating=?, pkcount=?, pkTime=?, " +
										"lost_exp=?, save_interface=?, elixir=?, isGm=? " +
										"WHERE objID=?"	);
			st.setInt(1, pc.getLevel());
			st.setInt(2, pc.getNowHp());
			st.setInt(3, pc.getMaxHp());
			st.setInt(4, pc.getNowMp());
			st.setInt(5, pc.getMaxMp());
			st.setInt(6, pc.getAc());
			st.setDouble(7, pc.getExp());
			st.setInt(8, pc.getX());
			st.setInt(9, pc.getY());
			st.setInt(10, pc.getMap());
			st.setString(11, pc.getTitle()==null ? "" : pc.getTitle());
			st.setInt(12, pc.getFood());
			st.setInt(13, pc.getLawful());
			st.setInt(14, pc.getClanId());
			st.setString(15, pc.getClanName()==null ? "" : pc.getClanName());
			st.setInt(16, pc.getGfx());
			st.setInt(17, pc.getGfxMode()<0 ? 0 : pc.getGfxMode());
			st.setInt(18, pc.getAttribute());
			st.setInt(19, pc.getLvStr());
			st.setInt(20, pc.getLvCon());
			st.setInt(21, pc.getLvDex());
			st.setInt(22, pc.getLvWis());
			st.setInt(23, pc.getLvInt());
			st.setInt(24, pc.getLvCha());
			st.setInt(25, pc.isChattingGlobal() ? 1 : 0);
			st.setInt(26, pc.isChattingTrade() ? 1 : 0);
			st.setInt(27, pc.isChattingWhisper() ? 1 : 0);
			st.setInt(28, pc.getPkCount());
			if(pc.getPkTime() == 0)
				st.setString(29, "0000-00-00 00:00:00");
			else
				st.setTimestamp(29, new Timestamp(pc.getPkTime()));
			st.setDouble(30, pc.getLostExp());
			st.setString(31, db_interface.toString());
			st.setInt(32, pc.getElixir());
			st.setInt(33, pc.getGm());
			st.setLong(34, pc.getObjectId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveCharacter(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 인벤토리 정보 저장 함수.
	 * @param con
	 * @param pc
	 */
	static public void saveInventory(Connection con, PcInstance pc){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement( "DELETE FROM characters_inventory WHERE cha_objId=?" );
			st.setLong(1, pc.getObjectId());
			st.executeUpdate();
			st.close();
			
			Inventory inv = pc.getInventory();
			if(inv!=null){
				for(ItemInstance item : inv.getList()){
					if(!item.getItem().isInventorySave()){
						// 저장 할필요 없는 아이템은 무시.
						if(item.isEquipped())
							// 착용되잇을경우 해제.
							item.toClick(pc, null);
						continue;
					}
					
					try {
						st = con.prepareStatement( "INSERT INTO characters_inventory SET " +
								"objId=?, cha_objId=?, cha_name=?, name=?, count=?, quantity=?, en=?, equipped=?, definite=?, bress=?, " +
								"durability=?, nowtime=?, pet_objid=?, inn_key=?, letter_uid=?, slimerace=?" );
						st.setLong(1, item.getObjectId());
						st.setLong(2, pc.getObjectId());
						st.setString(3, pc.getName());
						st.setString(4, item.getItem().getName());
						st.setLong(5, item.getCount());
						st.setInt(6, item.getQuantity());
						st.setInt(7, item.getEnLevel());
						st.setInt(8, item.isEquipped() ? 1 : 0);
						st.setInt(9, item.isDefinite() ? 1 : 0);
						st.setInt(10, item.getBress());
						st.setInt(11, item.getDurability());
						st.setInt(12, item.getNowTime());
						st.setLong(13, item.getPetObjectId());
						st.setLong(14, item.getInnRoomKey());
						st.setInt(15, item.getLetterUid());
						st.setString(16, item.getSlimeRaceTicket());
						st.executeUpdate();
						st.close();
					} catch (Exception e) {
						
					} finally {
						DatabaseConnection.close(st);
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveInventory(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 스킬 정보 저장 함수.
	 * @param con
	 * @param pc
	 */
	static public void saveSkill(Connection con, PcInstance pc){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement( "DELETE FROM characters_skill WHERE cha_objId=?" );
			st.setLong(1, pc.getObjectId());
			st.executeUpdate();
			st.close();
			
			List<Skill> list = SkillController.find(pc);
			if(list!=null){
				for(Skill s : list){
					st = con.prepareStatement( "INSERT INTO characters_skill SET cha_objId=?, cha_name=?, skill=?, skill_name=?" );
					st.setLong(1, pc.getObjectId());
					st.setString(2, pc.getName());
					st.setInt(3, s.getUid());
					st.setString(4, s.getName());
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveSkill(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 버프 저장 함수.
	 * @param con
	 * @param pc
	 */
	static public void saveBuff(Connection con, PcInstance pc){
		PreparedStatement st = null;
		try{
			Buff b = BuffController.find(pc);
			BuffInterface light = null;
			BuffInterface shield = null;
			BuffInterface curse_poison = null;
			BuffInterface curse_blind = null;
			BuffInterface slow = null;
			BuffInterface curse_paralyze = null;
			BuffInterface enchant_dexterity = null;
			BuffInterface enchant_mighty = null;
			BuffInterface haste = null;
			BuffInterface invisibility = null;
			BuffInterface shape_change = null;
			BuffInterface immune_to_harm = null;
			BuffInterface bravery_potion = null;
			BuffInterface elvenwafer = null;
			BuffInterface eva = null;
			BuffInterface wisdom = null;
			BuffInterface blue_potion = null;
			BuffInterface floating_eye_meat = null;
			BuffInterface clearmind = null;
			BuffInterface resistelemental = null;
			BuffInterface icelance = null;
			BuffInterface earthskin = null;
			BuffInterface ironskin = null;
			BuffInterface blessearth = null;
			BuffInterface curse_ghoul = null;
			BuffInterface chatting_close = null;
			BuffInterface holywalk = null;
			BuffInterface shadowarmor = null;
			BuffInterface exp_potion = null;
			if(b != null){
				light = b.find(Light.class);
				shield = b.find(Shield.class);
				curse_poison = b.find(CursePoison.class);
				curse_blind = b.find(CurseBlind.class);
				slow = b.find(Slow.class);
				curse_paralyze = b.find(CurseParalyze.class);
				enchant_dexterity = b.find(EnchantDexterity.class);
				enchant_mighty = b.find(EnchantMighty.class);
				haste = b.find(Haste.class);
				invisibility = b.find(InvisiBility.class);
				shape_change = b.find(ShapeChange.class);
				immune_to_harm = b.find(ImmuneToHarm.class);
				bravery_potion = b.find(Bravery.class);
				elvenwafer = b.find(Wafer.class);
				eva = b.find(Eva.class);
				wisdom = b.find(Wisdom.class);
				blue_potion = b.find(Blue.class);
				floating_eye_meat = b.find(FloatingEyeMeat.class);
				clearmind = b.find(ClearMind.class);
				resistelemental = b.find(ResistElemental.class);
				icelance = b.find(IceLance.class);
				blessearth = b.find(BlessOfEarth.class);
				earthskin = b.find(EarthSkin.class);
				ironskin = b.find(IronSkin.class);
				curse_ghoul = b.find(CurseGhoul.class);
				chatting_close = b.find(ChattingClose.class);
				holywalk = b.find(HolyWalk.class);
				shadowarmor = b.find(ShadowArmor.class);
				exp_potion = b.find(ExpPotion.class);
			}
			
			st = con.prepareStatement(	"UPDATE characters_buff SET " +
					"name=?, light=?, shield=?, curse_poison=?, curse_blind=?, slow=?, curse_paralyze=?, enchant_dexterity=?, enchant_mighty=?, " +
					"haste=?, invisibility=?, shape_change=?, immune_to_harm=?, bravery_potion=?, elvenwafer=?, eva=?, wisdom=?, blue_potion=?, " +
					"floating_eye_meat=?, clearmind=?, resistelemental=?, icelance=?, earthskin=?, ironskin=?, blessearth=?, curse_ghoul=?, " +
					"chatting_close=?, holywalk=?, shadowarmor=?, exp_potion=? " +
					"WHERE objId=?"	);
			st.setString(1, pc.getName());
			st.setInt(2, light==null ? 0 : light.getTime());
			st.setInt(3, shield==null ? 0 : shield.getTime());
			st.setInt(4, curse_poison==null ? 0 : curse_poison.getTime());
			st.setInt(5, curse_blind==null ? 0 : curse_blind.getTime());
			st.setInt(6, slow==null ? 0 : slow.getTime());
			st.setInt(7, curse_paralyze==null ? 0 : curse_paralyze.getTime());
			st.setInt(8, enchant_dexterity==null ? 0 : enchant_dexterity.getTime());
			st.setInt(9, enchant_mighty==null ? 0 : enchant_mighty.getTime());
			st.setInt(10, haste==null ? 0 : haste.getTime());
			st.setInt(11, invisibility==null ? 0 : invisibility.getTime());
			st.setInt(12, shape_change==null ? 0 : shape_change.getTime());
			st.setInt(13, immune_to_harm==null ? 0 : immune_to_harm.getTime());
			st.setInt(14, bravery_potion==null ? 0 : bravery_potion.getTime());
			st.setInt(15, elvenwafer==null ? 0 : elvenwafer.getTime());
			st.setInt(16, eva==null ? 0 : eva.getTime());
			st.setInt(17, wisdom==null ? 0 : wisdom.getTime());
			st.setInt(18, blue_potion==null ? 0 : blue_potion.getTime());
			st.setInt(19, floating_eye_meat==null ? 0 : floating_eye_meat.getTime());
			st.setInt(20, clearmind==null ? 0 : clearmind.getTime());
			st.setInt(21, resistelemental==null ? 0 : resistelemental.getTime());
			st.setInt(22, icelance==null ? 0 : icelance.getTime());
			st.setInt(23, earthskin==null ? 0 : earthskin.getTime());
			st.setInt(24, ironskin==null ? 0 : ironskin.getTime());
			st.setInt(25, blessearth==null ? 0 : blessearth.getTime());
			st.setInt(26, curse_ghoul==null ? 0 : curse_ghoul.getTime());
			st.setInt(27, chatting_close==null ? 0 : chatting_close.getTime());
			st.setInt(28, holywalk==null ? 0 : holywalk.getTime());
			st.setInt(29, shadowarmor==null ? 0 : shadowarmor.getTime());
			st.setInt(30, exp_potion==null ? 0 : exp_potion.getTime());
			st.setLong(31, pc.getObjectId());
			st.executeUpdate();
			PluginController.init(CharactersDatabase.class, "saveBuff", pc, con);
		} catch(Exception e) {
			lineage.share.System.printf("%s : saveBuff(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 기억 정보 저장 함수.
	 * @param con
	 * @param pc
	 */
	static public void saveBook(Connection con, PcInstance pc){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("DELETE FROM characters_book WHERE objId=?");
			st.setLong(1, pc.getObjectId());
			st.executeUpdate();
			st.close();
			
			List<Book> list = BookController.find(pc);
			if(list == null)
				return;
			for(Book b : list){
				st = con.prepareStatement("INSERT INTO characters_book SET objId=?, name=?, location=?, locX=?, locY=?, locMAP=?");
				st.setLong(1, pc.getObjectId());
				st.setString(2, pc.getName());
				st.setString(3, b.getLocation());
				st.setInt(4, b.getX());
				st.setInt(5, b.getY());
				st.setInt(6, b.getMap());
				st.executeUpdate();
				st.close();
			}
		} catch(Exception e) {
			lineage.share.System.printf("%s : saveBook(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
}
