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
import org.oagi.srt.persistence.dto.Agency_ID_List_ValueVO;

/**
*
* @author Jaehun Lee
* @version 1.0
*
*/

public class Agency_ID_List_ValueMysqlDAO extends SRTDAO {
	private final String _tableName = "agency_id_list_value";
	
	private final String _FIND_ALL_Agency_ID_Value_List_STATEMENT =
			"SELECT Agency_ID_List_Value_ID, Value, Name, Definition, Owner_Agency_ID_List_ID "
			+ "FROM " + _tableName;
	
	private final String _FIND_Agency_ID_Value_List_STATEMENT =
			"SELECT Agency_ID_List_Value_ID, Value, Name, Definition, Owner_Agency_ID_List_ID "
			+ "FROM " + _tableName;
	
	private final String _INSERT_Agency_ID_Value_List_STATEMENT = 
			"INSERT INTO " + _tableName + " (Value, Name, Definition, Owner_Agency_ID_List_ID)"
			+ " VALUES (?, ?, ?, ?)";
	
	private final String _UPDATE_Agency_ID_Value_List_STATEMENT = 
			"UPDATE " + _tableName
			+ " Agency_ID_List_Value_ID = ?, Value = ?,"
			+ " Name = ?, Definition = ?, Owner_Agency_ID_List_ID = ?"
			+ " WHERE Agency_ID_List_Value_ID = ?";
	
	private final String _DELETE_Agency_ID_Value_List_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE Agency_ID_List_Value_ID = ?";
	
	
	public boolean insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Agency_ID_List_ValueVO agencyidlistvalueVO = (Agency_ID_List_ValueVO) obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_Agency_ID_Value_List_STATEMENT);
			ps.setString(1, agencyidlistvalueVO.getValue());
			ps.setString(2, agencyidlistvalueVO.getName());
			ps.setString(3, agencyidlistvalueVO.getDefinition());
			ps.setInt(4, agencyidlistvalueVO.getOwnerAgencyIDListID());
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
		Agency_ID_List_ValueVO agencyidlistvalueVO = new Agency_ID_List_ValueVO();
		try {
			Connection conn = tx.open();
			String sql = _FIND_Agency_ID_Value_List_STATEMENT;

			String WHERE_OR_AND = " WHERE ";
			int nCond = qc.getSize();
			if (nCond > 0) {
				for (int n = 0; n < nCond; n++) {
					sql += WHERE_OR_AND + qc.getField(n) + " = ?";
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

			rs = ps.executeQuery();
			if (rs.next()) {
				agencyidlistvalueVO.setAgencyIDListValueID(rs.getInt("Agency_ID_List_Value_ID"));
				agencyidlistvalueVO.setValue(rs.getString("Value"));
				agencyidlistvalueVO.setName(rs.getString("Name"));
				agencyidlistvalueVO.setDefinition(rs.getString("Definition"));
				agencyidlistvalueVO.setOwnerAgencyIDListID(rs.getInt("Owner_Agency_ID_List_ID"));
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
		return agencyidlistvalueVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_Agency_ID_Value_List_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Agency_ID_List_ValueVO agencyidlistvalueVO = new Agency_ID_List_ValueVO();
				agencyidlistvalueVO.setAgencyIDListValueID(rs.getInt("Agency_ID_List_Value_ID"));
				agencyidlistvalueVO.setValue(rs.getString("Value"));
				agencyidlistvalueVO.setName(rs.getString("Name"));
				agencyidlistvalueVO.setDefinition(rs.getString("Definition"));
				agencyidlistvalueVO.setOwnerAgencyIDListID(rs.getInt("Owner_Agency_ID_List_ID"));
				list.add(agencyidlistvalueVO);
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
		Agency_ID_List_ValueVO agencyidlistvalueVO = (Agency_ID_List_ValueVO) obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_Agency_ID_Value_List_STATEMENT);
			ps.setString(1, agencyidlistvalueVO.getValue());
			ps.setString(2, agencyidlistvalueVO.getName());
			ps.setString(3, agencyidlistvalueVO.getDefinition());
			ps.setInt(4, agencyidlistvalueVO.getOwnerAgencyIDListID());
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
		Agency_ID_List_ValueVO agencyidlistvalueVO = (Agency_ID_List_ValueVO) obj;
		
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_Agency_ID_Value_List_STATEMENT);
			ps.setInt(1, agencyidlistvalueVO.getAgencyIDListValueID());
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
}
