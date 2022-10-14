package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterGroup;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.Map;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.monster.ArachnevilElder;
import lineage.world.object.monster.Baphomet;
import lineage.world.object.monster.Blob;
import lineage.world.object.monster.BombFlower;
import lineage.world.object.monster.Doppelganger;
import lineage.world.object.monster.FloatingEye;
import lineage.world.object.monster.Gremlin;
import lineage.world.object.monster.Slime;
import lineage.world.object.monster.Spartoi;
import lineage.world.object.monster.StoneGolem;
import lineage.world.object.monster.Wolf;
import lineage.world.object.monster.event.JackLantern;
import lineage.world.object.monster.quest.BetrayedOrcChief;
import lineage.world.object.monster.quest.BetrayerOfUndead;
import lineage.world.object.monster.quest.Bugbear;
import lineage.world.object.monster.quest.DarkElf;
import lineage.world.object.monster.quest.Darkmar;
import lineage.world.object.monster.quest.Dupelgenon;
import lineage.world.object.monster.quest.MutantGiantQueenAnt;
import lineage.world.object.monster.quest.OrcZombie;
import lineage.world.object.monster.quest.Ramia;
import lineage.world.object.monster.quest.Skeleton;
import lineage.world.object.monster.quest.Zombie;

public final class MonsterSpawnlistDatabase {

	static private List<MonsterInstance> pool;
	
	static public void init(Connection con){
		TimeLine.start("MonsterSpawnlistDatabase..");
		
		// 몬스터 스폰 처리.
		pool = new ArrayList<MonsterInstance>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();
			while(rs.next()){
				Monster m = MonsterDatabase.find(rs.getString("monster"));
				if(m != null){
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while(stt.hasMoreTokens()){
						String map = stt.nextToken();
						if(map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}
					ms.setReSpawn(rs.getInt("re_spawn") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if(ms.isGroup()){
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						if(g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if(g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if(g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if(g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
					}
					
					toSpawnMonster(ms, null);
				}
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 중복코드 방지용
	 * 	: gui 기능에서 사용중 lineage.gui.dialog.MonsterSpawn.step4()
	 */
	static public void toSpawnMonster(MonsterSpawnlist ms, Map map){
		// 버그 방지
		if(ms==null || ms.getMap().size()<=0)
			return;
		// 맵 확인.
		if(map == null){
			if(ms.getMap().size()>1)
				map = World.get_map(ms.getMap().get(Util.random(0, ms.getMap().size()-1)));
			else
				map = World.get_map(ms.getMap().get(0));
		}
		if(map == null)
			return;
		// 스폰처리.
		for(int i=0 ; i<ms.getCount() ; ++i){
			MonsterInstance mi = newInstance(ms.getMonster());
			if(mi == null)
				return;
			if(i == 0)
				mi.setMonsterSpawnlist(ms);
			mi.setDatabaseKey( Integer.valueOf(ms.getUid()) );
			if(ms.isGroup()){
				mi.setGroupMaster(mi);
				// 마스터 스폰.
				toSpawnMonster(mi, map, false, ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), true, true);
				// 관리객체 생성.
				for(MonsterGroup mg : ms.getListGroup()){
					for(int j=mg.getCount() ; j>0 ; --j){
						MonsterInstance a = newInstance(mg.getMonster());
						if(a != null){
							// 스폰
							toSpawnMonster(a, map, false, ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), true, true);
							// 마스터관리 목록에 등록.
							mi.getGroupList().add(a);
							// 관리하고있는 마스터가 누군지 지정.
							a.setGroupMaster(mi);
						}
					}
				}
			}else{
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), true, true);
			}
		}
	}
	
	/**
	 * 중복 코드를 막기위해 함수로 따로 뺌.
	 */
	static public void toSpawnMonster(MonsterInstance mi, Map m, boolean random, int x, int y, int map, int loc, int respawn, boolean drop, boolean ai){
		if(mi == null)
			return;
		
		int roop_cnt = 0;
		int lx = x;
		int ly = y;
		if(random){
			// 랜덤 좌표 스폰
			do{
				if(x>0){
					lx = Util.random(x-loc, x+loc);
					ly = Util.random(y-loc, y+loc);
				}else{
					lx = Util.random(m.locX1, m.locX2);
					ly = Util.random(m.locY1, m.locY2);
				}
				if(roop_cnt++>100){
					lx = x;
					ly = y;
					break;
				}
			}while(
					!World.isThroughObject(lx, ly+1, map, 0) || 
					!World.isThroughObject(lx, ly-1, map, 4) || 
					!World.isThroughObject(lx-1, ly, map, 2) || 
					!World.isThroughObject(lx+1, ly, map, 6) ||
					!World.isThroughObject(lx-1, ly+1, map, 1) ||
					!World.isThroughObject(lx+1, ly-1, map, 5) || 
					!World.isThroughObject(lx+1, ly+1, map, 7) || 
					!World.isThroughObject(lx-1, ly-1, map, 3)
				);
		}else{
			// 좌표 스폰
			lx = x;
			ly = y;
			if(loc>1 && x>0){
				while(
						!World.isThroughObject(lx, ly+1, map, 0) || 
						!World.isThroughObject(lx, ly-1, map, 4) || 
						!World.isThroughObject(lx-1, ly, map, 2) || 
						!World.isThroughObject(lx+1, ly, map, 6) ||
						!World.isThroughObject(lx-1, ly+1, map, 1) || 
						!World.isThroughObject(lx+1, ly-1, map, 5) || 
						!World.isThroughObject(lx+1, ly+1, map, 7) || 
						!World.isThroughObject(lx-1, ly-1, map, 3)
					){
					lx = Util.random(x-loc, x+loc);
					ly = Util.random(y-loc, y+loc);
					if(roop_cnt++>100){
						lx = x;
						ly = y;
						break;
					}
				}
			}
		}
		mi.setReSpawnTime(respawn);
		mi.setHomeX(lx);
		mi.setHomeY(ly);
		mi.setHomeLoc(loc);
		mi.setHomeMap(map);
		mi.toTeleport(lx, ly, map, false);
		if(drop)
			mi.readDrop();
		if(ai)
			AiThread.append(mi);
	}
	
	static public MonsterInstance newInstance(Monster m){
		MonsterInstance mi = null;
		if(m != null){
			switch(m.getNameIdNumber()){
				case 6:		// 괴물 눈
					mi = FloatingEye.clone(getPool(FloatingEye.class), m);
					break;
				case 7:		// 해골
					mi = Skeleton.clone(getPool(Skeleton.class), m);
					break;
				case 8:		// 슬라임
					mi = Slime.clone(getPool(Slime.class), m);
					break;
				case 56:	// 돌골렘
					mi = StoneGolem.clone(getPool(StoneGolem.class), m);
					break;
				case 57:	// 좀비
					mi = Zombie.clone(getPool(Zombie.class), m);
					break;
				case 268:	// 늑대
				case 904:	// 세인트버나드
				case 905:	// 도베르만
				case 906:	// 콜리
				case 907:	// 세퍼드
				case 908:	// 비글
				case 1397:	// 여우
				case 1495:	// 곰
				case 1788:	// 허스키
					mi = Wolf.clone(getPool(Wolf.class), m);
					break;
				case 306:	// 바포메트
					mi = Baphomet.clone(getPool(Baphomet.class), m);
					break;
				case 318:	// 스파토이
					mi = Spartoi.clone(getPool(Spartoi.class), m);
					break;
				case 319:	// 웅골리언트
					mi = ArachnevilElder.clone(getPool(ArachnevilElder.class), m);
					break;
				case 325:	// 버그베어
					mi = Bugbear.clone(getPool(Bugbear.class), m);
					break;
				case 756:	// Felix : Add Dupelgenon
					mi = Dupelgenon.clone(getPool(Dupelgenon.class), m);
					break;
				case 758:	// 브롭
					mi = Blob.clone(getPool(Blob.class), m);
					break;
				case 1041:	// 오크좀비
					mi = OrcZombie.clone(getPool(OrcZombie.class), m);
					break;
				case 1042:	// 다크엘프
					mi = DarkElf.clone(getPool(DarkElf.class), m);
					break;
				case 1046:	// 그렘린
					mi = Gremlin.clone(getPool(Gremlin.class), m);
					break;
				case 1428:	// 라미아
					mi = Ramia.clone(getPool(Ramia.class), m);
					break;
				case 1554:	// 도펠갱어
					mi = Doppelganger.clone(getPool(Doppelganger.class), m);
					break;
				case 1571:	// 폭탄꽃
					mi = BombFlower.clone(getPool(BombFlower.class), m);
					break;
				case 2017:	// 다크마르
					mi = Darkmar.clone(getPool(Darkmar.class), m);
					break;
				case 2020:	// 언데드의 배신자
					mi = BetrayerOfUndead.clone(getPool(BetrayerOfUndead.class), m);
					break;
				case 2063:	// 잭-O-랜턴
				case 2064:	// 잭-0-랜턴
					mi = JackLantern.clone(getPool(JackLantern.class), m);
					break;
				case 2073:	// 변종 거대 여왕 개미
					mi = MutantGiantQueenAnt.clone(getPool(MutantGiantQueenAnt.class), m);
					break;
				case 2219:	// 배신당한 오크대장
					mi = BetrayedOrcChief.clone(getPool(BetrayedOrcChief.class), m);
					break;
				default:
					mi = MonsterInstance.clone(getPool(MonsterInstance.class), m);
					break;
			}
			mi.setObjectId(ServerDatabase.nextEtcObjId());
			mi.setGfx(m.getGfx());
			mi.setGfxMode(m.getGfxMode());
			mi.setClassGfx(m.getGfx());
			mi.setClassGfxMode(m.getGfxMode());
			mi.setName(m.getNameId());
			mi.setLevel(m.getLevel());
			mi.setExp(m.getExp());
			mi.setMaxHp(m.getHp());
			mi.setMaxMp(m.getMp());
			mi.setNowHp(m.getHp());
			mi.setNowMp(m.getMp());
			mi.setLawful(m.getLawful());
			mi.setEarthress(m.getResistanceEarth());
			mi.setFireress(m.getResistanceFire());
			mi.setWindress(m.getResistanceWind());
			mi.setWaterress(m.getResistanceWater());
			mi.setAiStatus(Lineage.AI_STATUS_WALK);
		}
		return mi;
	}
	
	static private MonsterInstance getPool(Class<?> c){
		MonsterInstance mon = null;
		for(MonsterInstance mi : pool){
			if(mi.getClass().equals(c)){
				mon = mi;
				break;
			}
		}
		if(mon != null)
			pool.remove(mon);
		return mon;
	}
	
	static public void setPool(MonsterInstance mi){
		mi.close();
		pool.add(mi);
		
//		lineage.share.System.println(MonsterSpawnlistDatabase.class.toString()+" : pool.add("+pool.size()+")");
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	static public void insert(
				Connection con, String name, String monster, boolean random, int count, int loc_size, 
				int x, int y, int map, int re_spawn, boolean groups, 
				String monster_1, int monster_1_count, String monster_2, int monster_2_count, 
				String monster_3, int monster_3_count, String monster_4, int monster_4_count
			){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("INSERT INTO monster_spawnlist SET name=?, monster=?, random=?, count=?, loc_size=?, spawn_x=?, spawn_y=?, spawn_map=?, re_spawn=?, groups=?, monster_1=?, monster_1_count=?, monster_2=?, monster_2_count=?, monster_3=?, monster_3_count=?, monster_4=?, monster_4_count=?");
			st.setString(1, name);
			st.setString(2, monster);
			st.setString(3, String.valueOf(random));
			st.setInt(4, count);
			st.setInt(5, loc_size);
			st.setInt(6, x);
			st.setInt(7, y);
			st.setInt(8, map);
			st.setInt(9, re_spawn);
			st.setString(10, String.valueOf(groups));
			st.setString(11, monster_1);
			st.setInt(12, monster_1_count);
			st.setString(13, monster_2);
			st.setInt(14, monster_2_count);
			st.setString(15, monster_3);
			st.setInt(16, monster_3_count);
			st.setString(17, monster_4);
			st.setInt(18, monster_4_count);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert()\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
}
