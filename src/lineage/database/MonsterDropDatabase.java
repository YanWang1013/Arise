package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.database.Drop;
import lineage.bean.database.Monster;
import lineage.share.TimeLine;

public final class MonsterDropDatabase {

	static public void init(Connection con) {
		TimeLine.start("MonsterDropDatabase..");

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_drop");
			rs = st.executeQuery();
			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster_name"));
				if (m != null) {
					Drop d = new Drop();

					d.setName(rs.getString("name"));
					d.setMonName(rs.getString("monster_name"));
					d.setItemName(rs.getString("item_name"));
					d.setItemBress(rs.getInt("item_bress"));
					d.setCountMin(rs.getInt("count_min"));
					d.setCountMax(rs.getInt("count_max"));
					d.setChance(rs.getInt("chance"));

					m.getDropList().add(d);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterDropDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	public static void DropInit() {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		try {
			localConnection = DatabaseConnection.getLineage();
			localPreparedStatement = localConnection.prepareStatement("SELECT name FROM monster_drop GROUP BY name");
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next()) {
				Monster localMonster = MonsterDatabase.find(localResultSet.getString("name"));
				if (localMonster != null)
					localMonster.getDropList().clear();
			}
		} catch (Exception localException) {
			lineage.share.System.printf("%s : DropInit()\r\n", new Object[] { MonsterDropDatabase.class.toString() });
			lineage.share.System.println(localException);
		} finally {
			DatabaseConnection.close(localConnection, localPreparedStatement, localResultSet);
		}
	}

}
