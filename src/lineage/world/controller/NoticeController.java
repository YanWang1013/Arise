package lineage.world.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;

public class NoticeController {

	static private long real_notice_time;
	static private int real_notice_idx;
	static private List<String> real_notice;
	
	static public void init(){
		TimeLine.start("NoticeController..");
		
		real_notice = new ArrayList<String>();
		try {
			BufferedReader lnrr = new BufferedReader( new FileReader("notice.txt"));
			String line;
			while ( (line = lnrr.readLine()) != null){
				line = line.trim();
				if(line.startsWith("#") || line.length()<=0)
					continue;
				
				real_notice.add(line);
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", NoticeController.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
	static public void toTimer(long time){
		if(time-real_notice_time >= Lineage.notice_delay){
			real_notice_time = time;
			if(real_notice.size() == 0)
				return;
			if(real_notice.size() <= real_notice_idx)
				real_notice_idx = 0;
			
			World.toSender( S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), real_notice.get(real_notice_idx++)) );
		}
	}
	
}
