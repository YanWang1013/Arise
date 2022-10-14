package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ElixirPotion extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ElixirPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;
		
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			// 최대 엘릭서값보다 낮을때만 처리.
			if(pc.getElixir() < Lineage.item_elixir_max){
				// 스탯 상승.
				switch(getItem().getNameIdNumber()){
					case 2530:	// str
						if(pc.getStr()+pc.getLvStr() < Lineage.stat_max)
							pc.setStr(cha.getStr()+1);
						break;
					case 2532:	// dex
						if(pc.getDex()+pc.getLvDex() < Lineage.stat_max)
							pc.setDex(cha.getDex()+1);
						break;
					case 2531:	// con
						if(pc.getCon()+pc.getLvCon() < Lineage.stat_max)
							pc.setCon(cha.getCon()+1);
						break;
					case 2534:	// wis
						if(pc.getWis()+pc.getLvWis() < Lineage.stat_max)
							pc.setWis(cha.getWis()+1);
						break;
					case 2533:	// int
						if(pc.getInt()+pc.getLvInt() < Lineage.stat_max)
							pc.setInt(cha.getInt()+1);
						break;
					case 2535:	// cha
						if(pc.getCha()+pc.getLvCha() < Lineage.stat_max)
							pc.setCha(cha.getCha()+1);
						break;
				}
				// 엘릭서값 상승.
				pc.setElixir( pc.getElixir()+1 );
				// 스탯 갱신을위해 패킷 전송.
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
			}else{
				// \f1아무일도 일어나지 않았습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			}
		}
		
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
	
}
