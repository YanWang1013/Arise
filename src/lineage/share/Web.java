package lineage.share;

import java.io.BufferedReader;
import java.io.FileReader;

public final class Web {
	
	// 모듈 사용 여부.
	static public boolean is;

	// 게시판 모듈 구분
	static public String board_module;
	
	// 게시판 디비 접근 정보
	static public String driver;
	static public String url;
	static public String id;
	static public String pw;
	
	// 암호화 함수어떤거 사용할지 구분용
	static public boolean old_password;
	
	// 킴스큐에 사용되는 식별자
	static public String kimsq_identifier;
	
	// 웹채팅 사용 여부
	static public boolean chatting;
	// 웹채팅에 사용될 서버 포트
	static public int chatting_server_port;
	
	/**
	 * 필요한 정보 초기화 처리하는 함수.
	 */
	static public void init(){
		TimeLine.start("Web..");
		
		try {
			BufferedReader lnrr = new BufferedReader( new FileReader("web.conf"));
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
					else if(key.equalsIgnoreCase("board_module"))
						board_module = value;
					else if(key.equalsIgnoreCase("driver"))
						driver = value;
					else if(key.equalsIgnoreCase("url"))
						url = value;
					else if(key.equalsIgnoreCase("id"))
						id = value;
					else if(key.equalsIgnoreCase("pw"))
						pw = value;
					else if(key.equalsIgnoreCase("old_password"))
						old_password = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("kimsq_identifier"))
						kimsq_identifier = value;
					else if(key.equalsIgnoreCase("chatting"))
						chatting = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("chatting_server_port"))
						chatting_server_port = Integer.valueOf(value);
					
				}
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Web.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
}
