package lineage.share;

import java.util.ArrayList;
import java.util.List;


public final class Common {

	// 클라이언트 접속 최대치값.
	static public String requestor = "giro";
	
	// 모든 쓰레드들의 휴식시간 ms
	static public final int THREAD_SLEEP	= 20;
	static public final int TIMER_SLEEP		= 1000;
	
	// 버퍼 사이즈
	static public final int BUFSIZE = 1024;
	// 문자열 사이즈
	static public final int STRSIZE = 20;
	
	// 문자열 인코딩 방식 지정
	// KSC5601, EUC-KR, UTF-8
	static public final String CHARSET = "EUC-KR";
	
	// 시스템 구동방식이 console 모드인지 gui 모드인지 확인하는 변수.
	static public boolean system_config_console = true;
	
	// 월드 접속시 표현될 메세지 정보.
	static public boolean SERVER_MESSAGE;				// 서버 메세지 표현할지 여부.
	static public List<String> SERVER_MESSAGE_LIST;		// 서버 메세지
	static public int SERVER_MESSAGE_TIME;				// 서버 메세지 주기적으로 표현할 시간값. 밀리세컨드 단위.
	
	static public String OS_NAME;
	static public String SERVER_NAME = "Arise";
	
	/**
	 * 도움말 밑에 라인
	 */
	static public String HELPER_LINE = "-------------------------------------------------------------------------";
	
	static public void init(){
		TimeLine.start("Common..");
		
		OS_NAME = java.lang.System.getProperty("os.name");
		// 월드 접속시 메세지를 유저들에게 표현할 지 여부.
		SERVER_MESSAGE = true;
		// 몇초마다 표현할지 설정. 600초로 설정되잇음.
		SERVER_MESSAGE_TIME = 1000 * 600;
		// 출력할 메세지들.
		SERVER_MESSAGE_LIST = new ArrayList<String>();
		SERVER_MESSAGE_LIST.add( "어라이즈는 2001년 그 시절의 향수를 다시 느껴볼 수 있는 공간입니다." );
		
		TimeLine.end();
	}
	
}
