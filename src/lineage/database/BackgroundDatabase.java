package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.RankBoardInstance;
import lineage.world.object.npc.background.Cracker;
import lineage.world.object.npc.background.Door;
import lineage.world.object.npc.background.PigeonGroup;
import lineage.world.object.npc.background.Sign;
import lineage.world.object.npc.background.Switch;
import lineage.world.object.npc.background.TreasureBox;
import lineage.world.object.npc.quest.MotherOfTheForestAndElves;

public final class BackgroundDatabase {

	static private List<BackgroundInstance> pool;
	
	static public void init(Connection con){
		TimeLine.start("BackgroundDatabase..");
		
		pool = new ArrayList<BackgroundInstance>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM background_spawnlist");
			rs = st.executeQuery();
			while(rs.next()){
				object o = toObject( rs.getInt("gfx"), rs.getString("title") );
				o.setObjectId( ServerDatabase.nextEtcObjId() );
				o.setName( rs.getString("nameid") );
				o.setGfx( rs.getInt("gfx") );
				o.setGfxMode( rs.getInt("gfx_mode") );
				o.setLawful( rs.getInt("lawful") );
				o.setClassGfx( o.getGfx() );
				o.setClassGfxMode( o.getGfxMode() );
				o.setLight( rs.getInt("light") );
				o.setHomeX( rs.getInt("locX") );
				o.setHomeY( rs.getInt("locY") );
				o.setHomeMap( rs.getInt("locMap") );
				o.setHomeLoc( rs.getInt("locSize") );
				o.setHeading( rs.getInt("heading") );
				o.toTeleport(o.getHomeX(), o.getHomeY(), o.getHomeMap(), false);
				
				if(o instanceof BoardInstance)
					((BoardInstance)o).setType( rs.getString("title") );
				else if(o instanceof Door)
					((Door)o).setKey(rs.getInt("item_nameid"), rs.getInt("item_count"), rs.getString("item_remove").equalsIgnoreCase("true"));
				else
					o.setTitle( rs.getString("title") );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : void init(Connection con)\r\n", BackgroundDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * gfx에 맞는 객체 만들어서 리턴.
	 * @param gfx
	 * @param title
	 * @return
	 */
	static public object toObject(int gfx, String title){
		switch(gfx){
			case 88:	// [말섬던전2층] 문
			case 89:	// [말섬던전2층] 문
			case 90:	// [말섬던전1층] 문
			case 91:	// [말섬던전1층] 문
			case 92:	// [말섬던전2층] 문
			case 93:	// [말섬던전2층] 은회색열쇠 문
			case 251:
			case 442:	// [켄트마을] 문
			case 444:	// [은기사마을] 문
			case 446:	// [은기사마을] 문
			case 447:	// [켄트마을] 문
			case 448:	// [은기사마을] 문
			case 766:	// [화민촌] 문
			case 767:	// [화민촌] 문
			case 768:	// [화민촌] 문
			case 845:	// [은기사마을] 문
			case 1010:	// [은기사마을] 문
			case 1327:	// [기란] 문
			case 1328:	// [기란] 문
			case 1330:	// [기란] 문
			case 1331:	// [기란] 문
			case 1332:	// [기란] 문
			case 1333:	// [기란] 문
			case 1334:	// [기란] 문
			case 1335:	// [기란] 문
			case 1341:	// [기란] 문
			case 1342:	// [기란] 문
			case 1347:	// [기란] 문
			case 1348:	// [기란] 문
			case 1349:	// [기란] 문
			case 1350:	// [기란] 문
			case 1351:	// [기란] 문
			case 1352:	// [기란] 문
			case 1371:	// [기란] 문
			case 1372:	// [기란] 문
			case 1373:	// [기란] 문
			case 1487:	// [기란] 문
			case 1664:	// [하이네] 문
			case 1665:	// [하이네] 문
			case 1688:	// 
			case 1689:	// [하이네] 문
			case 1690:	// [하이네] 문
			case 1691:	// [하이네] 문
			case 1692:	// [하이네] 문
			case 1700:	// [하이네] 문
			case 1701:	// [하이네] 문
			case 1734:	// [하이네] 문
			case 1735:	// [하이네] 문
			case 1738:	// [하이네] 문
			case 1739:	// [하이네] 문
			case 1740:	// [하이네] 문
			case 1741:	// [하이네] 문
			case 1742:	// [하이네] 문
			case 1743:	// [하이네] 문
			case 1744:	// [하이네] 문
			case 1750:	// [하이네] 문
			case 1751:	// [하이네] 문
			case 2083:	// [노래하는섬] 문, [숨겨진계곡] 문
			case 2089:	// [숨겨진계곡] 문
			case 2114:	// [숨겨진계곡] 문
			case 2128:	// [숨겨진계곡] 문
			case 2136:	// [노래하는섬] 문
			case 2160:	// [오렌] 문
			case 2161:	// [오렌] 문
			case 2162:	// [오렌] 문
			case 2163:	// [오렌] 문
			case 2164:	// [오렌] 문
			case 2189:	// [오렌] 문
			case 2190:	// [오렌] 문
			case 2191:	// [오렌] 문
			case 2192:	// [오렌] 문
			case 2301:	// [말섬] 게렝의집 문
			case 2303:	// [말섬] 마을 문
			case 2304:	// [말섬] 마을 문
			case 2305:	// [말섬] 마을 문
			case 2344:	// [말섬] 마을 문
			case 2345:	// [말섬] 마을 문
			case 2346:	// [말섬] 여관 문
			case 2547:
			case 2548:
			case 2554:
			case 2555:
			case 2556:
			case 2557:
			case 2558:
			case 2574:
			case 2575:
			case 2576:
			case 2577:
			case 2578:
			case 2579:
			case 2580:
			case 2581:
			case 2582:
			case 2583:
			case 2584:
			case 2585:
			case 2586:
			case 2587:
			case 2588:
			case 2589:
			case 2590:
			case 2591:
			case 2592:
			case 2593:
			case 2594:
			case 2595:
			case 2596:
			case 2597:
			case 2598:
			case 2599:
			case 2600:
			case 2603:
			case 2605:
			case 2606:
			case 2610:
			case 2628:
			case 2629:
			case 2630:
			case 2631:
			case 2634:
			case 2635:
			case 2682:
				return new Door();
			case 841:	// 푯말
			case 1536:
			case 1537:
				return new Sign();
			case 114:	// 허수아비
			case 116:	// 허수아비
			case 2084:	// [숨겨진계곡] 허수아비, [노래하는섬] 허수아비
				return new Cracker();
			case 1094:	// 비둘기
				return new PigeonGroup();
			case 1546:	// 마을 게시판.
			case 1552:	// 경매 게시판.
			case 2207:	// 마을 게시판.
				if(title.equalsIgnoreCase("rank"))
					return new RankBoardInstance();
				else
					return new BoardInstance();
			case 869:	// 숲과 요정의 어머니
				return new MotherOfTheForestAndElves();
			case 125:	// 스위치 (발판)
			case 126:
				return new Switch();
			case 122:	// 보물상자
				return new TreasureBox();
			default:
				return BackgroundInstance.clone(getPool(BackgroundInstance.class));
		}
	}
	
	static public BackgroundInstance getPool(Class<?> c){
		BackgroundInstance b = null;
		for(BackgroundInstance bi : pool){
			if(bi.getClass().equals(c)){
				b = bi;
				break;
			}
		}
		if(b != null)
			pool.remove(b);
		return b;
	}
	
	static public void setPool(BackgroundInstance bi){
		bi.close();
		pool.add(bi);
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
