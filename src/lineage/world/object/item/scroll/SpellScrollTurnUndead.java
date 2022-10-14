package lineage.world.object.item.scroll;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.TurnUndead;

public class SpellScrollTurnUndead extends ItemInstance {
	
	private Skill skill;
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollTurnUndead();
		item.setSkill( SkillDatabase.find(3, 1) );
		return item;
	}
	
	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 처리불가능한 패킷상태는 무시.
		if(!cbp.isRead(4))
			return;
		// 초기화
		object o = cha.findInsideList(cbp.readD());
		int x = cbp.readH();
		int y = cbp.readH();
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(o!=null && TurnUndead.onBuff(cha, o, skill, x, y)){
			// 수량 하향
			cha.getInventory().count(this, getCount()-1, true);
		}else{
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}

}
