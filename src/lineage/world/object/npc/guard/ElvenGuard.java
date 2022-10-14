package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ElvenGuard extends PatrolGuard {
	
	public ElvenGuard(Npc npc){
		super(npc);
	}
	
	/**
	 * 요정외에 타 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman(){
		boolean find = false;
		for(object o : insideList){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(isAttack(pc, true) && pc.getClassType()!=Lineage.LINEAGE_CLASS_ELF){
					addAttackList(pc);
					find = true;
				}
			}
		}
		return find;
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type){
		// 데미지 처리.
		if(type==Lineage.ATTACK_TYPE_DIRECT || cha.getInventory()==null || cha.getInventory().getSlot(Lineage.SLOT_WEAPON)!=null || (cha.getClassType()!=Lineage.LINEAGE_CLASS_ELF && cha.getGm()==0)){
			super.toDamage(cha, dmg, type);
			return;
		}
		
		// 채집 처리
		if(cha instanceof PcInstance)
			toGatherUp((PcInstance)cha);
	}
	
	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		
		// 요정외 타 클레스 찾기.
		if(toSearchHuman())
			ChattingController.toChatting(this, "$804", Lineage.CHATTING_MODE_SHOUT);
	}
	
	/**
	 * 채집 처리 담당 함수.
	 * @param cha
	 */
	protected void toGatherUp(PcInstance pc){ }
}
