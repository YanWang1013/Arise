package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_SkillList extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, int lv[]){
		if(bp == null)
			bp = new S_SkillList(lv);
		else
			((S_SkillList)bp).toClone(lv);
		return bp;
	}
	
	public S_SkillList(int lv[]){
		toClone(lv);
	}
	
	public void toClone(int lv[]){
		clear();
		
		writeC(Opcodes.S_OPCODE_SKILLINV);
		writeC(0x15);	// 스킬 갯수
		writeC(lv[0]);	// 1단계
		writeC(lv[1]);	// 2단계
		writeC(lv[2]);	// 3단계
		writeC(lv[3]);	// 4단계
		writeC(lv[4]);	// 5단계
		writeC(lv[5]);	// 6단계
		writeC(lv[6]);	// 7단계
		writeC(lv[7]);	// 8단계
		writeC(lv[8]);	// 9단계
		writeC(lv[9]);	// 10단계
		// 기사 마법
		writeC(lv[10]);
		writeC(lv[11]);
		// 다크엘프 마법
		writeC(lv[12]);
		writeC(lv[13]);
		// 군주 마법
		writeC(lv[14]);	// 7단계	1-트루타겟 2-글로잉오라 4-샤이닝오라 8-콜클렌
		writeC(lv[15]);	// 아직 없는듯
		// 요정스킬
		writeC(lv[16]);	// 정령마법 1단계	1-레지스트매직 2-바디투마인드 4-텔레포트투마더
		writeC(lv[17]);	// 정령마법 2단계	1-클리어마인드 2-레지스트엘리멘트
		writeC(lv[18]);	// 정령마법 3단계	1-리턴투네이처 2-블러드투소울 4-프로텍션프롬엘리멘트 8-파이어웨폰 16-윈드샷 32-윈드워크 64-어스스킨 128-인탱글
		writeC(lv[19]);	// 정령마법 4단계	1-이레이즈매직 2-서먼레서엘리멘탈 4-블레스오브파이어 8-아이오브스톰 16-어스바인드 32-네이쳐스터치 64-블레스오브어스
		writeC(lv[20]);	// 정령마법 5단계	1-에어리어오브사일런스 2-서먼그레이터엘리멘탈 4-버닝웨폰 8-네이쳐스블레싱 16-콜오브네이쳐 32-스톰샷 64-윈드세클 128-아이언스킨
	}
}
