package lineage.network.packet.server;

import lineage.bean.lineage.Useshop;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.world.controller.UserShopController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;

public class S_ObjectAdd extends S_Inventory {

	static public BasePacket clone(BasePacket bp, object o, object oo){
		if(bp == null)
			bp = new S_ObjectAdd(o, oo);
		else
			((S_ObjectAdd)bp).toClone(o, oo);
		return bp;
	}
	
	public S_ObjectAdd(object o, object oo){
		toClone(o, oo);
	}
	
	public void toClone(object o, object oo){
		clear();

		String name = o.getName();
		int hp = 0xff;
		int lev = o instanceof PcInstance ? 0 : o.getLevel();
		boolean isHpbar = (o instanceof SummonInstance && o.getOwnObjectId()==oo.getObjectId()) || (o.getPartyId()>0 && o.getPartyId()==oo.getPartyId()) || (Lineage.monster_interface_hpbar && o instanceof MonsterInstance) || (Lineage.npc_interface_hpbar && o instanceof NpcInstance) || (o.isHpbar() && o.getObjectId()==oo.getObjectId());
//		boolean isHpbar = (o instanceof SummonInstance && o.getOwnObjectId()==oo.getObjectId()) || (o.getPartyId()>0 && o.getPartyId()==oo.getPartyId()) || (Lineage.monster_interface_hpbar && o instanceof MonsterInstance) || (Lineage.npc_interface_hpbar && o instanceof NpcInstance);
		byte[] msg = null;
		
		if(o instanceof ItemInstance){
			name = getName((ItemInstance)o);
		}
		
		if(o instanceof PcInstance){
			Useshop us = UserShopController.find((PcInstance)o);
			if(us != null)
				msg = us.getMsg();
		}
		
		// hp바 표현 부분
		if(isHpbar){
			Character cha = (Character)o;
			double nowhp = cha.getNowHp();
			double maxhp = cha.getTotalHp();
			hp = (int)((nowhp/maxhp)*100);
		}
		
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(o.getX());
		writeH(o.getY());
		writeD(o.getObjectId());
		writeH(o.getGfx());
		writeC(o.getGfxMode());
		writeC(o.getHeading());
		writeC(o.getLight());
		writeC(o.getSpeed()); // 0:보통 1:빠름 2:느림
		writeD((int)o.getCount());			// 객체가 가지고있는 갯수
		writeH(o.getLawful());
		writeS(name);
		writeS(o.getTitle());
		writeC(o.getStatus(oo));	// 세팅 - 0:mob,item(atk pointer), 1:poisoned(독), 2:invisable(투명), 4:pc, 8:cursed(저주), 16:brave(용기), 32:와퍼, 64:무빙악셀레이션(다엘마법), 128:invisable but name
		writeD(o.getClanId());		// 클렌 아이디
		writeS(o.getClanName());	// 클렌 이름
		writeS(o.getOwnName());	// 팻호칭 "psjump의" 같은거
		writeC(0);		// ??
		writeC(hp);		// HP바 부분
		writeC(0);		// 딸꾹 거리는 부분
		writeC(lev);	// PC = 0, Mon = Lv
		writeB(msg);	// 개인상점 광고부분
		writeC(0xFF);
		writeC(0xFF);
		
	}
	
}
