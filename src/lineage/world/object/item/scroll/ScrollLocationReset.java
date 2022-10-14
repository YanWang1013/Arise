package lineage.world.object.item.scroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.database.DatabaseConnection;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollLocationReset extends ItemInstance
{
	public static synchronized ItemInstance clone(ItemInstance paramItemInstance)
	{
		if (paramItemInstance == null)
			paramItemInstance = new ScrollLocationReset();
		return paramItemInstance;
	}

	@SuppressWarnings("resource")
	public void toClick(Character paramCharacter, ClientBasePacket paramClientBasePacket)
	{
		long l1 = java.lang.System.currentTimeMillis() / 1000L;
		if (paramCharacter.getDelaytime() + 5L > l1)
		{
			long l2 = paramCharacter.getDelaytime() + 5L - l1;
			ChattingController.toChatting(paramCharacter, l2 + "초간의 지연시간이 필요합니다.", 20);
			return;
		}
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		try
		{
			localConnection = DatabaseConnection.getLineage();
			localPreparedStatement = localConnection.prepareStatement("SELECT account FROM characters WHERE LOWER(name) = ? GROUP BY account");
			localPreparedStatement.setString(1, paramCharacter.getName());
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				localPreparedStatement = localConnection.prepareStatement("UPDATE characters SET locX=?, locY=?, locMap=? WHERE account=? AND locMap NOT IN (666)");
				localPreparedStatement.setInt(1, 33430);
				localPreparedStatement.setInt(2, 32800);
				localPreparedStatement.setInt(3, 4);
				localPreparedStatement.setString(4, localResultSet.getString("account"));
				localPreparedStatement.executeUpdate();
				ChattingController.toChatting(paramCharacter, "[" + localResultSet.getString("account") + "] 계정의 모든 캐릭터 좌표 복구 완료!", 20);
			}
		}
		catch (Exception localException)
		{
			lineage.share.System.printf("%s : toRepairLocation(object o, StringTokenizer st)\r\n", new Object[] { CommandController.class.toString() });
			lineage.share.System.println(localException);
		}
		finally
		{
			DatabaseConnection.close(localConnection, localPreparedStatement, localResultSet);
		}
		paramCharacter.setDelaytime(l1);
	}
}

/* Location:           D:\orim.jar
 * Qualified Name:     lineage.world.object.item.scroll.ScrollLocationReset
 * JD-Core Version:    0.6.0
 */
