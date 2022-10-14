package lineage.share;

import java.io.BufferedReader;
import java.io.FileReader;

public final class Socket {

	static public int PORT = 2000;
	static public boolean PRINTPACKET = false;
	
	// 클라이언트가 초당 서버로 전송할 수 있는 패킷 량.
	static public int packet_send_max;
	
	static public void init(){
		TimeLine.start("Socket..");
		
		try {
			BufferedReader lnrr = new BufferedReader( new FileReader("socket.conf"));
			String line;
			while ( (line = lnrr.readLine()) != null){
				if(line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				if(pos>0){
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos+1, line.length()).trim();
					
					if(key.equalsIgnoreCase("Port"))
						PORT = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("print_packet"))
						PRINTPACKET = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("packet_send_max"))
						packet_send_max = Integer.valueOf(value);
					
				}
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Socket.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
}
