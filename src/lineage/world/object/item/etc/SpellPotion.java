package lineage.world.object.item.etc;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class SpellPotion extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;

		// 패킷처리
		if(getItem().getEffect() > 0)
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
		// 스킬 사용.
		int spell_id = 0;
		try {
			spell_id = Integer.valueOf(getItem().getType2().substring(13));
		} catch (Exception e) {}
		Skill skill = SkillDatabase.find(spell_id);
		if(skill != null)
			SkillController.toSkill(cha, null, skill, true, 0, null);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
