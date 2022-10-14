package lineage.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_WorldTime;
import lineage.plugin.Plugin;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;

public final class World {

	static private Map<Integer, lineage.bean.lineage.Map> list;
	static private List<PcInstance> pc_list;
	static private int clear_time_count;

	static public void init(){
		TimeLine.start("World..");

		list = new HashMap<Integer, lineage.bean.lineage.Map>();
		pc_list = new ArrayList<PcInstance>();

		try {
			File f = new File(String.format("maps/%d/Cache", Lineage.server_version)); 
			// 폴더가 존재할경우 
			if(f.isDirectory()){ 
				// 캐쉬파일로부터 맵 로딩 
				read(false); 
				// map Cache 폴더가 존재하지 않을경우 새롭게 추출해서 생성한다. 
			}else{ 
				lineage.share.System.println("캐쉬 폴더가 존재하지 않습니다."); 
				// 폴더생성 
				f.mkdir(); 
				// txt파일로부터 맵 로딩 
				read(true); 
				// 캐쉬파일 작성 
				writeCache(); 
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", World.class.toString());
			lineage.share.System.println(e);
		}

		TimeLine.end();
	}

	static private void writeCache() throws Exception{ 
		lineage.share.System.println("캐쉬 파일을 생성하고 있습니다."); 
		BufferedOutputStream bw = null;
		for(lineage.bean.lineage.Map m : list.values()){ 
			bw = new BufferedOutputStream(new FileOutputStream(String.format("maps/%d/Cache/%d.data", Lineage.server_version, +m.mapid))); 
			bw.write(m.data); 
			bw.close(); 
		}
		lineage.share.System.println(" (완료)"); 
	} 

	static private void read(boolean type) throws Exception{ 
		// text로부터 읽는거 알림용 
		if(type)
			lineage.share.System.println("Text 파일에서 월드맵 정보를 추출하고 있습니다.");

		String maps; 
		StringTokenizer st1; 
		/** 맵좌표정보 추출 **/ 
		BufferedReader lnrr = new BufferedReader( new FileReader(String.format("maps/%d/maps.csv", Lineage.server_version))); 
		/** byte임시 저장용 **/ 
		byte[] temp = new byte[22149121]; 
		/** 첫번째라인 패스 용 **/ 
		while ( (maps = lnrr.readLine()) != null){ 
			if(maps.startsWith("#")) {
				continue;
			} 
			st1 = new StringTokenizer(maps, ","); 
			int readID = Integer.parseInt(st1.nextToken()); 
			int x1 = Integer.parseInt(st1.nextToken()); 
			int x2 = Integer.parseInt(st1.nextToken()); 
			int y1 = Integer.parseInt(st1.nextToken()); 
			int y2 = Integer.parseInt(st1.nextToken()); 
			int size = Integer.parseInt(st1.nextToken()); 
			if(type) 
				readText(temp, readID, size, x1, x2, y1, y2); 
			else 
				readCache(readID, x1, x2, y1, y2, size);
		}
	} 

	static private void readText(byte[] temp, int readID, int size, int x1, int x2, int y1, int y2) throws Exception{ 
		int TotalSize = -1; 
		String line; 
		StringTokenizer st; 
		/** 파일로부터 byte로 읽기 **/ 
		BufferedReader lnr = new BufferedReader( new FileReader(String.format("maps/%d/Text/%d.txt", Lineage.server_version, readID))); 
		while ( (line = lnr.readLine()) != null){ 
			st = new StringTokenizer(line, ","); 
			for(int i=0 ; i<size ; ++i){ 
				int t = Integer.parseInt(st.nextToken()); 
				if(Byte.MAX_VALUE<t) {
					temp[++TotalSize] = Byte.MAX_VALUE;
				} else {
					temp[++TotalSize] = (byte)t;
				} 
			} 
		} 
		/** 추출한 맵정보를 사이즈에맞게 새로 작성 **/ 
		byte[] MAP = new byte[TotalSize-1]; 
		System.arraycopy(temp, 0, MAP, 0, MAP.length); 
		lineage.bean.lineage.Map m = new lineage.bean.lineage.Map(); 
		m.mapid = readID; 
		m.locX1 = x1;
		m.locX2 = x2;
		m.locY1 = y1;
		m.locY2 = y2;
		m.size = size;
		m.data = MAP;
		m.data_size = m.data.length;
		m.dataDynamic = new byte[m.data_size];
		list.put(m.mapid, m); 
	} 

	static private void readCache(int readID, int x1, int x2, int y1, int y2, int size) throws Exception{ 
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(String.format("maps/%d/Cache/%d.data", Lineage.server_version, readID))); 
		byte[] data = new byte[bis.available()]; 
		bis.read(data, 0, data.length); 
		/** 추출한 맵정보를 사이즈에맞게 새로 작성 **/ 
		lineage.bean.lineage.Map m = new lineage.bean.lineage.Map(); 
		m.mapid = readID;
		m.locX1 = x1;
		m.locX2 = x2;
		m.locY1 = y1;
		m.locY2 = y2;
		m.size = size;
		m.data = data;
		m.data_size = data.length;
		m.dataDynamic = new byte[m.data_size];
		list.put(m.mapid, m);
	}

	/**
	 * 맵아이디에 해당하는 클레스 찾아서 리턴.
	 */
	static public lineage.bean.lineage.Map get_map(int map){
		return list.get(map);
	}

	/**
	 * 좌표에 해당하는 타일값 추출.
	 */
	static public int get_map(int x, int y, int map){
		lineage.bean.lineage.Map m = get_map(map);
		if(m!=null){
			if(x < m.locX1)
				return 0;
			if(y < m.locY1)
				return 0;
			int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
			return pos>=m.data_size || pos<0 ? 0 : m.data[ pos ];
		}
		return 0;
	}

	/**
	 * 좌표에 해당하는 타일값 변경.
	 */
	static public void set_map(int x, int y, int map, int tail){
		lineage.bean.lineage.Map m = get_map(map);
		if(m!=null){
			int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
			if(pos<m.data_size && pos>=0)
				m.data[ pos ] = (byte)tail;
		}
	}

	/**
	 * 객체가 좌표값에 얼마만큼 존재하는지 실시간으로 업데이트하는 함수.
	 *  : 몬스터 인공지능에서 과부하를 줄이기위해 insideList를 참고하지 않고 해당 함수를 이용해
	 *    실시간으로 변경되는 좌표에 카운팅값을 참고하기 위함.
	 *  : 기본값은 0이며, 0보다 크면 해당좌표에 객체가 존재한다고 판단하여 해당 좌표로 이동하지 않음.
	 * @param x
	 * @param y
	 * @param map
	 * @param plus	: + 할지 - 할지 구분.
	 */
	static public void update_mapDynamic(int x, int y, int map, boolean plus){
		lineage.bean.lineage.Map m = get_map(map);
		if(m!=null){
			int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
			if(pos<m.data_size && pos>=0){
				synchronized (m.dataDynamic) {
					if(plus){
						m.dataDynamic[ pos ] += 1;
					}else{
						m.dataDynamic[ pos ] -= 1;
						if(m.dataDynamic[ pos ] < 0)
							m.dataDynamic[ pos ] = 0;
					}
				}
			}
		}
	}

	/**
	 * 해당 좌표에 객채가 존재하는지 확인하는 함수.
	 *  : 위 함수 주석에 설명했듯이. 해당 변수는 해당좌표에 객채가 몇개존재하는지 실사간으로 변하는 변수임.
	 *    해당 변수를 통해 좌표에 객채가 존재하는지 확인 가능.
	 * @param x
	 * @param y
	 * @param map
	 * @return
	 */
	static public boolean isMapdynamic(int x, int y, int map){
		lineage.bean.lineage.Map m = get_map(map);
		if(m!=null){
			int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
			if(pos<m.data_size && pos>=0)
				synchronized (m.dataDynamic) {
					return m.dataDynamic[ pos ] != 0;
				}
		}
		return false;
	}

	static public int getMapdynamic(int x, int y, int map){
		lineage.bean.lineage.Map m = get_map(map);
		if(m!=null){
			int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
			if(pos<m.data_size && pos>=0)
				synchronized (m.dataDynamic) {
					return m.dataDynamic[ pos ];
				}
		}
		return 0;
	}

	static public int getZone(int x, int y, int map){
		return get_map(x, y, map) & 48;
	}

	/**
	 * 컴뱃존 여부 확인.
	 */
	static public boolean isCombatZone(int x, int y, int map){
		return getZone(x, y, map)==Lineage.COMBAT_ZONE;
	}

	/**
	 * 세이프존 여부 확인.
	 */
	static public boolean isSafetyZone(int x, int y, int map){
		return getZone(x, y, map)==Lineage.SAFETY_ZONE;
	}

	/**
	 * 노말존 여부 확인.
	 */
	static public boolean isNormalZone(int x, int y, int map){
		return getZone(x, y, map)==Lineage.NORMAL_ZONE;
	}

	/**
	 * 오브젝트의 통과가능 여부를 리턴 이동하기전 미리 던져서 리턴받음.
	 */
	static public boolean isThroughObject(int x, int y, int map, int dir){
		switch(dir){
		case 0:
			return (get_map(x, y, map)&2)>0;
		case 1:
			return (get_map(x, y, map)&2)>0 && (get_map(x, y-1, map)&1)>0;
		case 2:
			return (get_map(x, y, map)&1)>0;
		case 3:
			return ((get_map(x, y+1, map)&2)>0 && (get_map(x, y+1, map)&1)>0) || ((get_map(x, y, map)&1)>0 && (get_map(x+1, y+1, map)&2)>0);
		case 4:
			return (get_map(x, y+1, map)&2)>0;
		case 5:
			return ((get_map(x, y+1, map)&2)>0 && (get_map(x-1, y+1, map)&1)>0) || ((get_map(x-1, y, map)&1)>0 && (get_map(x-1, y+1, map)&2)>0);
		case 6:
			return (get_map(x-1, y, map)&1) > 0;
		case 7:
			return ((get_map(x, y, map)&2)>0 && (get_map(x-1, y-1, map)&1)>0) || ((get_map(x-1, y, map)&1)>0 && (get_map(x-1, y, map)&2)>0);
		}
		return false;
	}

	/**
	 * 마법이나 화살등 원거리 공격효과의 통과가능 여부를 리턴 이동하기전 미리 던져서 리턴받음.
	 */
	static public boolean isThroughAttack(int x, int y, int map, int dir){
		//		lineage.share.System.println( get_map(x, y, map) );
		switch(dir){
		case 0:
			return (get_map(x, y, map)&8)>0;
		case 1:
			return ((get_map(x, y, map)&8)>0 && (get_map(x, y-1, map)&4)>0) || ((get_map(x, y, map)&4)>0 && (get_map(x+1, y, map)&8)>0);
		case 2:
			return (get_map(x, y, map)&4)>0;
		case 3:
			return ((get_map(x, y+1, map)&8)>0 && (get_map(x, y+1, map)&4)>0) || ((get_map(x, y, map)&4)>0 && (get_map(x+1, y+1, map)&8)>0);
		case 4:
			return (get_map(x, y+1, map)&8)>0;
		case 5:
			return ((get_map(x, y+1, map)&8)>0 && (get_map(x-1, y+1, map)&4)>0) || ((get_map(x-1, y, map)&4)>0 && (get_map(x-1, y+1, map)&8)>0);
		case 6:
			return (get_map(x-1, y, map)&4) > 0;
		case 7:
			return ((get_map(x, y, map)&8)>0 && (get_map(x-1, y-1, map)&4)>0) || ((get_map(x-1, y, map)&4)>0 && (get_map(x-1, y, map)&8)>0);
		}
		return false;
	}

	/**
	 * 해당 객체의 좌표를 확인해서 공격이 가능한지 판단하는 메서드.
	 */
	static public boolean isAttack(object cha, object use){
		// 세이프존 확인.
		if( isSafetyZone(cha.getX(), cha.getY(), cha.getMap()) ||
				isSafetyZone(use.getX(), use.getY(), use.getMap()) ){
			if(use instanceof PcInstance && cha instanceof PcInstance)
				return false;
			// 펫이 도망갓을때를 염두해서 아래와같이 조건넣음.
			if(use instanceof SummonInstance && cha instanceof PcInstance)
				return use.getSummon()==null;
			if(use instanceof PcInstance && cha instanceof SummonInstance)
				return cha.getSummon()==null;
		}
		// nonpvp 확인. 컴뱃존만 가능하도록하기.
		if(Lineage.nonpvp){
			if(cha instanceof PcInstance && use instanceof PcInstance)
				return isCombatZone(cha.getX(), cha.getY(), cha.getMap()) && isCombatZone(use.getX(), use.getY(), use.getMap());
			if(cha instanceof SummonInstance && use instanceof PcInstance)
				return isCombatZone(cha.getX(), cha.getY(), cha.getMap()) && isCombatZone(use.getX(), use.getY(), use.getMap());
			if(cha instanceof PcInstance && use instanceof SummonInstance)
				return isCombatZone(cha.getX(), cha.getY(), cha.getMap()) && isCombatZone(use.getX(), use.getY(), use.getMap());
		}
		return true;
	}

	static public void remove(object o){
		try {
			lineage.bean.lineage.Map m = list.get(o.getMap());
			if(m!=null)
				m.remove(o);
		} catch (Exception e) { }
	}

	static public void append(object o){
		lineage.bean.lineage.Map m = list.get(o.getMap());
		if(m!=null)
			m.append(o);
	}

	static public void getLocationList(object o, int loc, List<object> r_list){
		lineage.bean.lineage.Map m = list.get(o.getMap());
		if(m!=null)
			m.getList(o, loc, r_list);
	}

	static public void appendPc(PcInstance pc){
		if(pc == null)
			return;

		if(!pc_list.contains(pc))
			pc_list.add(pc);
	}

	static public void removePc(PcInstance pc){
		if(pc == null)
			return;

		pc_list.remove(pc);
	}

	static public PcInstance findPc(String name){
		if(name == null)
			return null;

		// 찾을 객체 검색.
		for(PcInstance pc : pc_list){
			if(name.equalsIgnoreCase(pc.getName()))
				return pc;
		}
		return null;
	}

	static public int getPcSize(){
		return pc_list.size();
	}

	/**
	 * 월드에 접속되있는 사용자들에게 패킷 전송 처리하는 함수.
	 * @param packet
	 */
	static public void toSender(BasePacket packet){
		if(packet instanceof ServerBasePacket){
			ServerBasePacket sbp = (ServerBasePacket)packet;
			for(PcInstance pc : pc_list)
				pc.toSender( ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()) );
		}
		BasePacketPooling.setPool(packet);
	}

	/**
	 * 월드에 접속되있는 사용자들중 해당 맵에있는 사용자에게만 패킷전송 처리하는 함수.
	 * @param packet
	 * @param map
	 */
	static public void toSender(BasePacket packet, int map){
		if(packet instanceof ServerBasePacket){
			ServerBasePacket sbp = (ServerBasePacket)packet;
			for(PcInstance pc : pc_list){
				if(pc.getMap() == map)
					pc.toSender( ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()) );
			}
		}
		BasePacketPooling.setPool(packet);
	}

	static public List<PcInstance> getPcList(){
		return pc_list;
	}

	/**
	 * 타이머가 주기적으로 호출함.
	 */
	static public void toTimer(long time){
		ServerDatabase.nextTime();

		if((ServerDatabase.LineageWorldTime%300)==0){
			// 리니지 월드 시간 전송
			toSender( S_WorldTime.clone(BasePacketPooling.getPool(S_WorldTime.class)) );
		}

		// 플러그인 호출
		Plugin p = PluginController.find(World.class);
		if(p != null)
			((lineage.plugin.bean.World)p).toTimer(time);

		if (Lineage.world_item_delay != 0) {
			clear_time_count++;
			if (clear_time_count < Lineage.world_item_delay) {
				if (Lineage.world_item_delay - clear_time_count == 30) { ChattingController.toWorldMessage("30초 후 리니지 월드가 청소됩니다."); }
				else if (Lineage.world_item_delay - clear_time_count == 10) { ChattingController.toWorldMessage("10초 후 리니지 월드가 청소됩니다."); }
				else if (Lineage.world_item_delay - clear_time_count == 5) { ChattingController.toWorldMessage("5초 후 리니지 월드가 청소됩니다."); }
			}else if (clear_time_count >= Lineage.world_item_delay) {
				clear_time_count = 0;
				clearWorldItem();
				ChattingController.toWorldMessage("리니지 월드가 청소되었습니다.");
			}
		}
	}

	/**
	 * 리니지 월드에 드랍된 아이템 전체 제거 처리 함수.
	 */
	static public void clearWorldItem(){
		for(lineage.bean.lineage.Map m : list.values())
			m.clearWorldItem();
	}

	/**
	 * 특정 맵에 해당하는 아이템만 제거할때.
	 * @param map
	 */
	static public void clearWorldItem(int map){
		lineage.bean.lineage.Map m = list.get(map);
		if(m != null)
			m.clearWorldItem();
	}

	/**
	 * 맵 갯수 리턴.
	 * @return
	 */
	static public int getMapSize(){
		return list.size();
	}

	/**
	 * 맵에등록된 전체 개체 갯수 리턴.
	 * @return
	 */
	static public int getSize(){
		int count = 0;
		for(lineage.bean.lineage.Map m : list.values())
			count += m.getSize();
		return count;
	}

	/**
	 * 읽어들인 맵정보들 순회하면서 맵아이디를 String으로 변환해서 배열 리턴함.
	 * @return
	 */
	static public String[] toStringArrayMap(){
		String[] array = new String[list.size()];
		int idx = 0;
		for(lineage.bean.lineage.Map m : list.values())
			array[idx++] = String.valueOf(m.mapid);
		return array;
	}

	/**
	 * 물속 맵에 있는지 확인해주는 함수.
	 * @param o
	 * @return
	 */
	static public boolean isAquaMap(object o){
		for(int map : Lineage.MAP_AQUA)
			if(map == o.getMap())
				return true;
		return false;
	}

}
