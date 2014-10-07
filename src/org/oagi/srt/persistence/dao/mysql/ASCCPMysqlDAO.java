package org.oagi.srt.persistence.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.ASCCPVO;

/**
 *
 * @author Jaehun Lee
 * @version 1.0
 *
 */
public class ASCCPMysqlDAO extends SRTDAO {

	private final String _tableName = "asccp";

	private final String _FIND_ALL_ASCCP_STATEMENT = 
			"SELECT ASCCP_ID, ASCCP_GUID, Property_Term, "
					+ "Definition, Role_Of_ACC_ID, Den, Created_By_User_ID, Last_Updated_By_User_ID, "
					+ "Creation_Timestamp, Last_Update_Timestamp, State, Module, Reusable_Indicator FROM " + _tableName;

	private final String _FIND_ASCCP_STATEMENT = 
			"SELECT ASCCP_ID, ASCCP_GUID, Property_Term, "
					+ "Definition, Role_Of_ACC_ID, Den, Created_By_User_ID, Last_Updated_By_User_ID, "
					+ "Creation_Timestamp, Last_Update_Timestamp, State, Module, Reusable_Indicator FROM " + _tableName;
	
	private final String _INSERT_ASCCP_STATEMENT = 
			"INSERT INTO " + _tableName + " (ASCCP_GUID, Property_Term, "
					+ "Definition, Role_Of_ACC_ID, Den, Created_By_User_ID, Last_Updated_By_User_ID, "
					+ "Creation_Timestamp, Last_Update_Timestamp, State, Module, Reusable_Indicator) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";

	private final String _UPDATE_ASCCP_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET Last_Update_Timestamp = CURRENT_TIMESTAMP, ASCCP_GUID = ?, Property_Term = ?, "
			+ "Definition = ?, Role_Of_ACC_ID = ?, Den = ?, Created_By_User_ID = ?, Last_Updated_By_User_ID = ?, "
			+ "Creation_Timestamp = ?, State = ? Module = ?, Reusable_Indicator =? WHERE ASCCP_ID = ?";

	private final String _DELETE_ASCCP_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE ASCCP_ID = ?";

	public boolean insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		ASCCPVO asccpVO = (ASCCPVO)obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_ASCCP_STATEMENT);
			ps.setString(1, asccpVO.getASCCPGUID());
			ps.setString(2, asccpVO.getPropertyTerm());
			ps.setString(3, asccpVO.getDefinition());
			ps.setInt(4, asccpVO.getRoleOfACCID());
			ps.setString(5, asccpVO.getDEN());
			ps.setInt(6, asccpVO.getCreatedByUserId());
			ps.setInt(7, asccpVO.getLastUpdatedByUserId());
			ps.setTimestamp(8, asccpVO.getLastUpdateTimestamp());
			ps.setInt(9, asccpVO.getState());
			ps.setString(10, asccpVO.getModule());
			ps.setBoolean(11, asccpVO.getReusableIndicator());

			ps.executeUpdate();

			//ResultSet tableKeys = ps.getGeneratedKeys();
			//tableKeys.next();
			//int autoGeneratedID = tableKeys.getInt(1);

			ps.close();
			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback();
			throw new SRTDAOException(SRTDAOException.DAO_INSERT_ERROR, e);
		} catch (SQLException e) {
			e.printStackTrace();
			tx.rollback();
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			tx.close();
		}
		return true;
	}

	public SRTObject findObject(QueryCondition qc) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASCCPVO asccpVO = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ASCCP_STATEMENT;

			String WHERE_OR_AND = " WHERE ";
			int nCond = qc.getSize();
			if (nCond > 0) {
				for (int n = 0; n < nCond; n++) {
					sql += WHERE_OR_AND + qc.getField(n) + " = ?";
					WHERE_OR_AND = " AND ";
				}
			}
			
			int nCond2 = qc.getLikeSize();
			if (nCond2 > 0) {
				for (int n = 0; n < nCond2; n++) {
					sql += WHERE_OR_AND + qc.getLikeField(n) + " like ?";
					WHERE_OR_AND = " AND ";
				}
			}
			
			ps = conn.prepareStatement(sql);
			if (nCond > 0) {
				for (int n = 0; n < nCond; n++) {
					Object value = qc.getValue(n);
					if (value instanceof String) {
						ps.setString(n+1, (String) value);
					} else if (value instanceof Integer) {
						ps.setInt(n+1, ((Integer) value).intValue());
					}
				}
			}
			
			if (nCond2 > 0) {
				for (int n = 0; n < nCond2; n++) {
					Object value = qc.getLikeValue(n);
					if (value instanceof String) {
						ps.setString(nCond + n + 1, (String) value);
					} else if (value instanceof Integer) {
						ps.setInt(nCond + n + 1, ((Integer) value).intValue());
					}
				}
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				asccpVO = new ASCCPVO();
				asccpVO.setASCCPID(rs.getInt("ASCCP_ID"));
				asccpVO.setASCCPGUID(rs.getString("ASCCP_GUID"));
				asccpVO.setPropertyTerm(rs.getString("Property_Term"));
				asccpVO.setDefinition(rs.getString("Definition"));
				asccpVO.setRoleOfACCID(rs.getInt("Role_Of_ACC_ID"));
				asccpVO.setDEN(rs.getString("DEN"));
				asccpVO.setCreatedByUserId(rs.getInt("Created_By_User_ID"));
				asccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By_User_ID"));
				asccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				asccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				asccpVO.setState(rs.getInt("State"));
				asccpVO.setModule(rs.getString("Module"));
				asccpVO.setReusableIndicator(rs.getBoolean("ReusableIndicator"));
			}
			tx.commit();
			conn.close();
		} catch (BfPersistenceException e) {
			throw new SRTDAOException(SRTDAOException.DAO_FIND_ERROR, e);
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}
		return asccpVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_ASCCP_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ASCCPVO asccpVO = new ASCCPVO();
				asccpVO.setASCCPID(rs.getInt("ASCCP_ID"));
				asccpVO.setASCCPGUID(rs.getString("ASCCP_GUID"));
				asccpVO.setPropertyTerm(rs.getString("Property_Term"));
				asccpVO.setDefinition(rs.getString("Definition"));
				asccpVO.setRoleOfACCID(rs.getInt("Role_Of_ACC_ID"));
				asccpVO.setDEN(rs.getString("DEN"));
				asccpVO.setCreatedByUserId(rs.getInt("Created_By_User_ID"));
				asccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By_User_ID"));
				asccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				asccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				asccpVO.setState(rs.getInt("State"));
				asccpVO.setModule(rs.getString("Module"));
				asccpVO.setReusableIndicator(rs.getBoolean("ReusableIndicator"));
				list.add(asccpVO);
			}
			tx.commit();
			conn.close();
		} catch (BfPersistenceException e) {
			throw new SRTDAOException(SRTDAOException.DAO_FIND_ERROR, e);
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}

		return list;
	}

	public boolean updateObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		ASCCPVO asccpVO = (ASCCPVO)obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_ASCCP_STATEMENT);

			ps.setString(1, asccpVO.getASCCPGUID());
			ps.setString(2, asccpVO.getPropertyTerm());
			ps.setString(3, asccpVO.getDefinition());
			ps.setInt(4, asccpVO.getRoleOfACCID());
			ps.setString(5, asccpVO.getDEN());
			ps.setInt(6, asccpVO.getCreatedByUserId());
			ps.setInt(7, asccpVO.getLastUpdatedByUserId());
			ps.setTimestamp(8, asccpVO.getLastUpdateTimestamp());
			ps.setInt(9, asccpVO.getState());
			ps.setString(10, asccpVO.getModule());
			ps.setBoolean(11, asccpVO.getReusableIndicator());
			ps.executeUpdate();

			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.DAO_UPDATE_ERROR, e);
		} catch (SQLException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}

		return true;
	}

	public boolean deleteObject(SRTObject obj) throws SRTDAOException {
		ASCCPVO asccpVO = (ASCCPVO)obj;

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_ASCCP_STATEMENT);
			ps.setInt(1, asccpVO.getASCCPID());
			ps.executeUpdate();

			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.DAO_DELETE_ERROR, e);
		} catch (SQLException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}

		return true;

	}

	@Override
	public ArrayList<SRTObject> findObjects(QueryCondition qc)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return null;
	}
}
