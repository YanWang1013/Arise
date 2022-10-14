package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.RobotController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectWho extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectWho(data, length);
		else
			((C_ObjectWho)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectWho(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		// 접속된 전체유저 출력.
		if(pc.getGm()>=Lineage.GMCODE){
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "+------------------------------"));
			for(PcInstance use : World.getPcList())
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("| Lv.%2d %s", use.getLevel(), use.getName())));
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "+------------------------------"));
		}
		
		String name = readS();
		PcInstance use = World.findPc(name);
		if(use != null){
			String msg = Lineage.object_who;
			
			// 이름
			msg = msg.replace("name", use.getName());
			// 호칭
			msg = msg.replace("title", use.getTitle()==null ? "" : use.getTitle());
			// 혈맹
			msg = msg.replace("clan", use.getClanId()==0 ? "" : String.format("[%s]", use.getClanName()));
			// 웹 아이디
			msg = msg.replace("webid", use.getWebId()==null ? "" : use.getWebId());
			// 웹 닉
			msg = msg.replace("webnick", use.getWebNick()==null ? "" : use.getWebNick());
			// 웹 이름
			msg = msg.replace("webname", use.getWebName()==null ? "" : use.getWebName());
			// 웹 성별
			msg = msg.replace("websex", use.getWebSex()==0 ? "여" : "남");
			// 웹 생일
			msg = msg.replace("webbirth", use.getWebBirth()==null ? "" : use.getWebBirth());
			// 피케이
			msg = msg.replace("pkcount", String.valueOf(use.getPkCount()));
			// 라우풀
			if(use.getLawful() < Lineage.NEUTRAL)
				msg = msg.replace("lawful", "(Chaotic)");
			else if(use.getLawful()>=Lineage.NEUTRAL && use.getLawful()<Lineage.NEUTRAL+500)
				msg = msg.replace("lawful", "(Neutral)");
			else
				msg = msg.replace("lawful", "(Lawful)");
			
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
		}
		pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 81, String.valueOf(World.getPcSize()+RobotController.count)));
		
		return this;
	}
}
