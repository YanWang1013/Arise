package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class MarketTeleporter extends TeleportInstance {

	// 어느 마을에 시장텔레포터인지 구분용.
	private String type;
	
	public MarketTeleporter(Npc npc){
		super(npc);
	}
	
	@Override
	public void setTitle(String title) {
		type = title;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(type.equalsIgnoreCase("giran")){
			if(pc.getLawful()<Lineage.NEUTRAL)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtzteleC"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtztele"));
		}else if(type.equalsIgnoreCase("gludin")){
			if(pc.getLawful()<Lineage.NEUTRAL)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltzteleC"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltztele"));
		}else if(type.equalsIgnoreCase("silver")){
			if(pc.getLawful()<Lineage.NEUTRAL)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktzteleC"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktztele"));
		}else if(type.equalsIgnoreCase("oren")){
			if(pc.getLawful()<Lineage.NEUTRAL)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortzteleC"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortztele"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			if(this.type.equalsIgnoreCase("giran"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "grtztele1"));
			else if(this.type.equalsIgnoreCase("gludin"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gltztele1"));
			else if(this.type.equalsIgnoreCase("silver"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sktztele1"));
			else if(this.type.equalsIgnoreCase("oren"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ortztele1"));
		}else if(action.equalsIgnoreCase("teleport giran-giran-trade-zone")){
			pc.toPotal(32724, 32841, 350);
		}else if(action.equalsIgnoreCase("teleport gludio-gludin-trade-zone")){
			pc.toPotal(32786, 32818, 340);
		}else if(action.equalsIgnoreCase("teleport silver-silver-trade-zone")){
			pc.toPotal(32733, 32792, 370);
		}else if(action.equalsIgnoreCase("teleport oren-oren-trade-zone")){
			pc.toPotal(32733, 32792, 360);
		}
	}

}
