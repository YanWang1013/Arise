package lineage.network.packet.server;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.controller.SkillController;
import lineage.world.object.instance.PcInstance;

public class S_SkillBuyList extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, PcInstance pc){
		if(bp == null)
			bp = new S_SkillBuyList(pc);
		else
			((S_SkillBuyList)bp).toClone(pc);
		return bp;
	}
	
	public S_SkillBuyList(PcInstance pc){
		toClone(pc);
	}
	
	public void toClone(PcInstance pc){
		clear();
		
		int count = SkillController.getBuySkillCount(pc);

		writeC(Opcodes.S_OPCODE_SKILLBUY);
		writeD(100);
		writeH(count);
		if(count>0){
			int idx = SkillController.getBuySkillIdx(pc);
			for(int i=0 ; i<=idx ; ++i){
				Skill s = SkillController.find(pc, i+1, false);
				if(s == null)
					writeD(i);
			}
		}else{
			writeH(1);
			writeD(120);
		}
	}
	
}
