package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ExpReStorePowerPotion extends ItemInstance
{
  public static synchronized ItemInstance clone(ItemInstance paramItemInstance)
  {
    if (paramItemInstance == null)
      paramItemInstance = new ExpReStorePowerPotion();
    return paramItemInstance;
  }

  public void toClick(Character paramCharacter, ClientBasePacket paramClientBasePacket)
  {
    if ((paramCharacter instanceof PcInstance))
    {
      PcInstance localPcInstance = (PcInstance)paramCharacter;
      if (localPcInstance.getLostExp() > 0.0D)
      {
        localPcInstance.setExp(localPcInstance.getExp() + localPcInstance.getLostExp());
        localPcInstance.setLostExp(0.0D);
        localPcInstance.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), localPcInstance));
        ChattingController.toChatting(paramCharacter, "드래곤의 힘이 파도처럼 치솟아 오릅니다. 경험치가 복구 되었습니다.", 20);
      }
      else
      {
        ChattingController.toChatting(paramCharacter, "드래곤의 힘이 사라집니다. 경험치가 복구 되지 않았습니다.", 20);
      }
    }
    paramCharacter.getInventory().count(this, getCount() - 1L, true);
  }
}
