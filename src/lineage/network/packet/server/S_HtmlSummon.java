package lineage.network.packet.server;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SummonInstance;

public class S_HtmlSummon extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, SummonInstance si){
		if(bp == null)
			bp = new S_HtmlSummon(si);
		else
			((S_HtmlSummon)bp).clone(si);
		return bp;
	}
	
	public S_HtmlSummon(SummonInstance si){
		clone(si);
	}
	
	public void clone(SummonInstance si){
		clear();

		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(si.getObjectId());
		
		if(si instanceof PetInstance){
			PetInstance pi = (PetInstance)si;
			writeS("anicom");
			writeC(0);
			writeH(0x0a);		// 문자열 갯수
			switch(pi.getSummonMode()){
				case AggressiveMode:
					writeS("$469");	// 공격 태세
					break;
				case DefensiveMode:
					writeS("$470");	// 방어 태세
					break;
				case Deploy:
					writeS("$476");	// 산개
					break;
				case Alert:
					writeS("$472");	// 경계
					break;
				case ItemPickUp:
					writeS("$613");	// 수집
					break;
				case Rest:
					writeS("$471");	// 휴식
					break;
			}
			writeS(String.valueOf(pi.getNowHp()));	// 현재 hp
			writeS(String.valueOf(pi.getTotalHp()));	// 최대 hp
			writeS(String.valueOf(pi.getNowMp()));	// 현재 mp
			writeS(String.valueOf(pi.getTotalMp()));	// 최대 mp
			writeS(String.valueOf(pi.getLevel()));	// 레벨
			writeS(pi.getName());
			switch(pi.getFoodMode()){
				case Veryhungry:
					writeS("$608");
					break;
				case Littlehungry:
					writeS("$609");
					break;
				case NeitherHungryNorFull:
					writeS("$610");
					break;
				case LittleFull:
					writeS("$611");
					break;
				case VeryFull:
					writeS("$612");
					break;
			}

			Exp exp = ExpDatabase.find( pi.getLevel() );
			double a = exp.getBonus() - exp.getExp();
			double b = pi.getExp() - a;
			writeS(String.valueOf((int)((b/exp.getExp())*100)));		// 경험치
			writeS(String.valueOf(pi.getLawful()-Lineage.NEUTRAL));	// 라우풀
			
		}else{
			writeS("moncom");
			if(Lineage.server_version > 144)
				writeC(0x00);
			writeH(9);		// 문자열 갯수
			switch(si.getSummonMode()){
				case AggressiveMode:
					writeS("$469");	// 공격 태세
					break;
				case DefensiveMode:
					writeS("$470");	// 방어 태세
					break;
				case Deploy:
					writeS("$476");	// 산개
					break;
				case Alert:
					writeS("$472");	// 경계
					break;
				case ItemPickUp:
					writeS("$613");	// 수집
					break;
				case Rest:
					writeS("$471");	// 휴식
					break;
			}
			writeS(String.valueOf(si.getNowHp()));	// 현재 hp
			writeS(String.valueOf(si.getTotalHp()));	// 최대 hp
			writeS(String.valueOf(si.getNowMp()));	// 현재 mp
			writeS(String.valueOf(si.getTotalMp()));	// 최대 mp
			writeS(String.valueOf(si.getLevel()));	// 레벨
			writeS(si.getName());
			writeS("0");
			writeS("792"); // 790, 792
		}
	}
}
