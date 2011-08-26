package uk.ac.strath.uportal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityTypes;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.IEntityGroup;

public class StrathGroupsDAO {
	private static final Log log = LogFactory.getLog(StrathGroupsDAO.class);

	private String connectionString = "jdbc:mysql://localhost/simplegroups";

	public Map<String, String> findGroups(Integer parent) throws SQLException {
		log.debug("findGroups entered for " + parent);

		HashMap<String, String> result = new HashMap<String, String>();
		Connection conn = DriverManager.getConnection(connectionString, "root",
				"tnbjb007");
		PreparedStatement st;
		if (parent != null) {
			st = conn
					.prepareStatement("SELECT id, name FROM grp WHERE parent = ?");
			st.setInt(1, parent);
		} else {
			st = conn
					.prepareStatement("SELECT id, name FROM grp WHERE parent is null");
		}
		st.execute();
		ResultSet rs = st.getResultSet();
		while (rs.next()) {
			result.put(String.valueOf(rs.getInt(1)), rs.getString(2));
		}
		st.close();
		conn.close();

		return result;
	}

	public Map<String, String> findContainingGroupsForEntity(String member_key)
			throws SQLException {
		log.debug("findContainingGroupsForEntity entered for " + member_key);

		HashMap<String, String> result = new HashMap<String, String>();
		Connection conn = DriverManager.getConnection(connectionString, "root",
				"tnbjb007");
		PreparedStatement st;
		st = conn.prepareStatement("SELECT g.id, g.name FROM grp g, member m WHERE m.groupid = g.id and m.member_key = ?");
		st.setString(1, member_key);
		st.execute();
		ResultSet rs = st.getResultSet();
		while (rs.next()) {
			result.put(String.valueOf(rs.getInt(1)), rs.getString(2));
		}
		st.close();
		conn.close();

		return result;
	}

	public IEntityGroup findGroup(Integer id) throws SQLException {
		log.debug("findGroup entered for " + id);

		IEntityGroup result = null;

		Connection conn = DriverManager.getConnection(connectionString, "root",
				"tnbjb007");
		PreparedStatement st;
		if (id != null) {
			st = conn.prepareStatement("SELECT id, name FROM grp WHERE id = ?");
			st.setInt(1, id);
		} else {
			return null;
		}
		st.execute();
		ResultSet rs = st.getResultSet();
		if (rs.next()) {
			result = new EntityGroupImpl(new Integer(rs.getInt(1)).toString(),
					EntityTypes.GROUP_ENTITY_TYPE);
			result.setName(rs.getString(2));
			log.debug("Name set to " + rs.getString(2) + " for id " + id);
		} else {
			log.debug("No results found for findGroup(" + id + ")");
		}
		st.close();
		conn.close();

		return result;
	}

	public boolean contains(Integer groupId, String memberKey)
			throws SQLException {
		Connection conn = DriverManager.getConnection(connectionString, "root",
				"tnbjb007");
		PreparedStatement st;
		if (groupId != null) {
			st = conn
					.prepareStatement("SELECT member_key FROM members WHERE member_key = ?");
			st.setString(1, memberKey);
		} else {
			return false;
		}
		st.execute();
		ResultSet rs = st.getResultSet();
		boolean result;
		if (rs.next()) {
			result = true;
		} else {
			result = false;
		}
		st.close();
		conn.close();

		return result;
	}

	public List<String> findMembers(Integer groupId) throws SQLException {
		log.debug("findMembers entered for " + groupId);

		List<String> result = new ArrayList<String>();
		Connection conn = DriverManager.getConnection(connectionString, "root",
				"tnbjb007");
		PreparedStatement st;
		if (groupId != null) {
			st = conn.prepareStatement("SELECT member_key FROM member WHERE groupid = ?");
			st.setInt(1, groupId);
		} else {
			log.warn("groupId is null - returning empty list");
			return result;
		}
		st.execute();
		ResultSet rs = st.getResultSet();
		while (rs.next()) {
			log.debug("found " + rs.getString(1));
			result.add(rs.getString(1));
		}
		st.close();
		conn.close();

		return result;
	}

}
