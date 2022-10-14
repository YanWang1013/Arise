package lineage.network.packet.client;

import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.AgitController;
import lineage.world.controller.ClanController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class C_Ask extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Ask(data, length);
		else
			((C_Ask)bp).clone(data, length);
		return bp;
	}
	
	public C_Ask(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(!isRead(3) || pc==null || pc.isWorldDelete())
			return this;
		
		int type = readH();	// 구분자
		int yn = readC();	// 승낙 여부
		switch(type){
			case 97:	// 가입
				ClanController.toJoinFinal(pc, yn==1);
				break;
			case 180:	// 변신
				String name = readS();
				if(name!=null && name.length()>0)
					// 시전 처리.
					ShapeChange.init(pc, pc, PolyDatabase.getPolyName(name), 7200, 1);
				break;
			case 217:	// 전쟁 선포 혈전 부분
				break;
			case 221:	// 항복 요청
				break;
			case 252:	// 트레이드
				TradeController.toTrade(pc, yn==1);
				break;
			case 321:	// 부활 여부
				if(yn==1)
					pc.toRevivalFinal(null);
				break;
			case 325:	// 펫 이름 바꾸기
				if(pc.getSummon().getTempSi()==null || !pc.getSummon().getTempSi().getName().startsWith("$")){
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 326));
				}else{
					SummonController.updateName(pc.getSummon().getTempSi(), readS());
				}
				break;
			case 422:	// 파티
				PartyController.toParty(pc, yn==1);
				break;
			case 479:	// 새로운 스탯 추가
				if(pc.isLvStat(false)){
					String stat = readS();
					if(stat.equalsIgnoreCase("str")){
						if(pc.getStr()+pc.getLvStr() < Lineage.stat_max)
							pc.setLvStr(pc.getLvStr()+1);
						else
							// \f1한 능력치의 최대값은 25 입니다. 다른 능력치를 선택해 주세요.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 481));
					}else if(stat.equalsIgnoreCase("dex")){
						if(pc.getDex()+pc.getLvDex() < Lineage.stat_max)
							pc.setLvDex(pc.getLvDex()+1);
						else
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 481));
					}else if(stat.equalsIgnoreCase("con")){
						if(pc.getCon()+pc.getLvCon() < Lineage.stat_max)
							pc.setLvCon(pc.getLvCon()+1);
						else
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 481));
					}else if(stat.equalsIgnoreCase("cha")){
						if(pc.getCha()+pc.getLvCha() < Lineage.stat_max)
							pc.setLvCha(pc.getLvCha()+1);
						else
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 481));
					}else if(stat.equalsIgnoreCase("wis")){
						if(pc.getWis()+pc.getLvWis() < Lineage.stat_max)
							pc.setLvWis(pc.getLvWis()+1);
						else
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 481));
					}else{
						if(pc.getInt()+pc.getLvInt() < Lineage.stat_max)
							pc.setLvInt(pc.getLvInt()+1);
						else
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 481));
					}
					pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
				}
				break;
			case 512:	// 아지트 집 이름 변경.
				AgitController.toNameChange(pc, readS());
				break;
			case 729:	// 콜 클렌
				ClanController.toCallPledgeMember(pc, yn==1);
				break;
			case 953:	// 파티 신청
				break;

		}
		
		return this;
	}
}
