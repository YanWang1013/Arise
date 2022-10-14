package lineage.share;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.LogConnect;
import lineage.bean.LogExp;
import lineage.bean.LogInterface;
import lineage.network.Server;
import lineage.network.codec.Decoder;
import lineage.network.codec.Encoder;
import lineage.util.Util;
import lineage.world.object.instance.PcInstance;

public class Log {
	
	// 로그 시스템 사용 여부
	static public boolean is;
	
	// 각 로그 파일에 기록할 딜레이 값들
	static public int chatting_delay;
	static private long temp_time_chatting;
	static public int connect_delay;
	static private long temp_time_connect;
	static public int exp_delay;
	static private long temp_time_exp;
	static public int network_delay;
	static private long temp_time_network;
	static public int sp_running_delay;
	static private long temp_time_sp_running;
	// 서버 가동 시간 저장 변수.
	static public long sp_running_time;
	
	// 로그 기록될 경로 변수
	static final private String LOG_DIR				= "log/";
	static final private String LOG_DIR_CHATTING	= LOG_DIR+"chatting/";
	static final private String LOG_DIR_CONNECT		= LOG_DIR+"connect/";
	static final private String LOG_DIR_EXP			= LOG_DIR+"exp/";
	static final private String LOG_DIR_NETWORK		= LOG_DIR+"network/";
	static final private String LOG_DIR_SPRUNNING	= LOG_DIR+"sp_running/";
	static final private String LOG_DIR_ITEM		= LOG_DIR+"item/";
	
	// 로그가 기록될 메모리
	static private List<String> list_chatting;
	static private List<LogConnect> list_connect;
	static private List<LogExp> list_exp;
	static private List<String> list_network;
	static private List<String> list_sp_running;
	
	// 재사용 목록
	static private List<LogInterface> pool;
	
	static {
		sp_running_time = System.currentTimeMillis();
		pool = new ArrayList<LogInterface>();
		list_chatting = new ArrayList<String>();
		list_connect = new ArrayList<LogConnect>();
		list_exp = new ArrayList<LogExp>();
		list_network = new ArrayList<String>();
		list_sp_running = new ArrayList<String>();
	}
	
	/**
	 * 필요한 정보 초기화 처리하는 함수.
	 */
	static public void init(){
		try {
			BufferedReader lnrr = new BufferedReader( new FileReader("log.conf"));
			String line;
			while ( (line = lnrr.readLine()) != null){
				if(line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				if(pos>0){
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos+1, line.length()).trim();
					
					if(key.equalsIgnoreCase("is"))
						is = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("chatting_delay"))
						chatting_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("connect_delay"))
						connect_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("exp_delay"))
						exp_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("network_delay"))
						network_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("sp_running_delay"))
						sp_running_delay = Integer.valueOf(value) * 1000 * 60;
					
				}
			}
			lnrr.close();
			
			// 로그 디렉토리 확인.
			mkdirs();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Log.class.toString());
			lineage.share.System.println(e);
		}
	}
	
	static public void close(){
		if(!is)
			return;
		
		// 로그 저장.
		toTimer(0);
	}

	static public void toTimer(long time){
		if(!is)
			return;
		
		// 로그 저장.
		toSaveChatting(time);
		toSaveConnect(time);
		toSaveExp(time);
		toSaveNetwork(time);
		toSaveSpRunning(time);
	}
	
	/**
	 * 로그 기록 함수.
	 * @param msg
	 */
	static public void appendSpRunning(String msg){
		if(!is)
			return;
		
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		// time	내용
		list_sp_running.add( String.format("[%s\t%d]\t%s", timeString, time, msg) );
	}
	
	/**
	 * 경험치 로그 기록 함수.
	 * @param key
	 * @param lv
	 * @param add_exp
	 * @param total_exp
	 * @param monster_lv
	 * @param monster_name
	 * @param monster_exp
	 */
	static public void appendExp(long key, int lv, int add_exp, int total_exp, int monster_lv, String monster_name, int monster_exp){
		if(!is)
			return;
		
		// 찾기
		LogExp le = null;
		for(LogExp l : list_exp){
			if(l.getDate() == key){
				le = l;
				break;
			}
		}
		// 없을경우 생성 및 관리목록에 등록.
		if(le == null){
			LogInterface li = getPool(LogExp.class);
			if(li == null)
				li = new LogExp();
			le = (LogExp)li;
			// 정보 초기화.
			le.setDate( key );
			// 등록
			list_exp.add( le );
		}
		
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		// time	경험치배율	레벨	지급된경험치	누적경험치	몬스터레벨	몬스터이름	몬스터경험치
		le.getList().add( String.format("[%s\t%d]\t%f\t%d\t%d\t%d\t%d\t%s\t%d", timeString, time, Lineage.rate_exp, lv, add_exp, total_exp, monster_lv, monster_name, monster_exp) );
	}
	
	/**
	 * 케릭터 생성하게되면 이놈도 호출됨.
	 * 관리목록에 등록할 객체 생성 및 초기화 처리 함수.
	 * @param key
	 * @return
	 */
	static private LogConnect toConnect(long key){
		LogInterface li = getPool(LogConnect.class);
		if(li == null)
			li = new LogConnect();
		LogConnect lc = (LogConnect)li;
		
		// 정보 초기화.
		lc.setDate( key );
		return lc;
	}
	
	/**
	 * 케릭터 생성시 호출됨.
	 * 	필요한 객체생성 하고 관리목록에 등록.
	 * 	로그 갱신처리.
	 * @param ip
	 * @param id
	 * @param name
	 * @param time
	 */
	static public void toConnect(String ip, String id, String name, long time){
		if(!is)
			return;
		
		LogConnect lc = toConnect(time);
		// 정보 초기화.
		lc.setDate( time );
		// 관리목록에 등록.
		list_connect.add( lc );
		// 로그 갱신.
		appendConnect(time, ip, id, name, "생성");
	}
	
	/**
	 * 로그 기록 함수.
	 * @param key
	 * @param ip
	 * @param id
	 * @param msg
	 */
	static public void appendConnect(long key, String ip, String id, String name, String msg){
		if(!is)
			return;
		
		// 찾기
		LogConnect lc = null;
		for(LogConnect l : list_connect){
			if(l.getDate() == key){
				lc = l;
				break;
			}
		}
		// 없을경우 생성 및 관리목록에 등록.
		if(lc == null){
			lc = toConnect(key);
			list_connect.add( lc );
		}
		
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		// time	아이피	계정명	내용(접속, 종료, 클라종료, 케릭생성, 케릭삭제)
		lc.getList().add( String.format("[%s\t%d]\t%s\t%s\t%s\t%s", timeString, time, ip, id, name, msg) );
	}
	
	/**
	 * 채팅 로그 기록용
	 * @param pc
	 * @param msg
	 * @param mode
	 */
	static public void appendChatting(PcInstance pc, String msg, int mode){
		if(!is)
			return;
		
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		String ip = pc==null ? "null" : pc.getClient().getAccountIp();
		String id = pc==null ? "null" : pc.getClient().getAccountId();
		String name = pc==null ? "null" : pc.getName();
		
		// time	아이피	계정명	케릭이름	채팅종류	채팅내용
		list_chatting.add( String.format("[%s\t%d]\t%s\t%s\t%s\t%d\t%s", timeString, time, ip, id, name, mode, msg) );
	}
	
	/**
	 * 로그정보 파일에기록처리하는 함수.
	 * @param file_name
	 * @param list
	 */
	static public void write(String file_name, List<String> list){
		try {
			// 파일 이어쓰기 모드로 객체 생성, 없을경우 자동생성.
			FileWriter fw = new FileWriter(file_name, true);
			// 로그 기록.
			for(String s : list)
				fw.append( String.format("%s\r\n", s) );
			// 파일 출력.
			fw.flush();
			fw.close();
		} catch (Exception e) { }
	}
	
	/**
	 * 로그정보 파일에기록처리하는 함수.
	 * @param file_name
	 * @param list
	 */
	static public void write(String file_name, String data){
		try {
			// 파일 이어쓰기 모드로 객체 생성, 없을경우 자동생성.
			FileWriter fw = new FileWriter(file_name, true);
			// 로그 기록.
			fw.append( data );
			// 파일 출력.
			fw.flush();
			fw.close();
		} catch (Exception e) { }
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	/**
	 * 로그에 사용되는 디렉토리 생성 함수.
	 */
	static private void mkdirs(){
		File dir = new File(LOG_DIR_CHATTING);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_CONNECT);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_EXP);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_NETWORK);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_SPRUNNING);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_ITEM);
		if(!dir.isDirectory())
			dir.mkdirs();
	}
	
	/**
	 * 채팅 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveChatting(long time){
		if(time==0 || temp_time_chatting<=time){
			temp_time_chatting = time + chatting_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_CHATTING, Util.getLocaleString(time==0 ? System.currentTimeMillis() : time, false));
			write(file_name, list_chatting);
			list_chatting.clear();
		}
	}
	
	/**
	 * 접속 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveConnect(long time){
		if(time==0 || temp_time_connect<=time){
			temp_time_connect = time + connect_delay;
			
			for(LogConnect lc : list_connect){
				String file_name = String.format("%s%d.log", LOG_DIR_CONNECT, lc.getDate());
				write(file_name, lc.getList());
				setPool(lc);
			}
			list_connect.clear();
		}
	}
	
	/**
	 * 경험치 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveExp(long time){
		if(time==0 || temp_time_exp<=time){
			temp_time_exp = time + exp_delay;
			
			for(LogExp le : list_exp){
				String file_name = String.format("%s%d.log", LOG_DIR_EXP, le.getDate());
				write(file_name, le.getList());
				setPool(le);
			}
			list_exp.clear();
		}
	}
	
	/**
	 * 패킷량 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveNetwork(long time){
		if(time==0 || temp_time_network<=time){
			temp_time_network = time + network_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_NETWORK, Util.getLocaleString(time==0 ? System.currentTimeMillis() : time, false));
			write(file_name, list_network);
			list_network.clear();
		}
		
		appendNetwork(time);
	}
	
	/**
	 * 패킷량 로그 기록 처리 함수.
	 * @param time
	 */
	static private void appendNetwork(long time){
		// 종료되는 시점에서는 할 필요 없음.
		if(time == 0)
			return;
		
		// time	recv	send	클라갯수	아이피|recv|send
		list_network.add( String.format("[%s\t%d]\t%d\t%d\t%d\t%s", Util.getLocaleString(time, true), time, Decoder.recv_length, Encoder.send_length, Server.getClientSize(), Server.getLogNetwork()) );
		Encoder.send_length = Decoder.recv_length = 0;
	}
	
	/**
	 * 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveSpRunning(long time){
		if(time==0 || temp_time_sp_running<=time){
			temp_time_sp_running = time + sp_running_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_SPRUNNING, Util.getLocaleString(sp_running_time, true));
			write(file_name, list_sp_running);
			list_sp_running.clear();
		}
	}
	
	static private LogInterface getPool(Class<?> c){
		LogInterface li = null;
		for(LogInterface o : pool){
			if(o.getClass().equals(c)){
				li = o;
				break;
			}
		}
		if(li != null)
			pool.remove(li);
		return li;
	}
	
	static private void setPool(LogInterface li){
		li.close();
		pool.add(li);
	}
}
