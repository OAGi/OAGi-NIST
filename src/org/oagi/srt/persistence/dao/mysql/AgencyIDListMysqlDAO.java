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
import org.oagi.srt.persistence.dto.AgencyIDListVO;

/**
*
* @author Jaehun Lee
* @version 1.0
*
*/

public class AgencyIDListMysqlDAO extends SRTDAO {
	private final String _tableName = "agency_id_list";
	
	private final String _FIND_ALL_Agency_ID_List_STATEMENT =
			"SELECT Agency_ID_List_ID, Agency_ID_List_GUID, Enumeration_Type_GUID, Name, List_ID, Agency_ID, Version_ID, Definition "
			+ "FROM " + _tableName;
	
	private final String _FIND_Agency_ID_List_STATEMENT =
			"SELECT Agency_ID_List_ID, Agency_ID_List_GUID, Enumeration_Type_GUID, Name, List_ID, Agency_ID, Version_ID, Definition "
			+ "FROM " + _tableName;
	
	private final String _INSERT_Agency_ID_List_STATEMENT = 
			"INSERT INTO " + _tableName + " (Agency_ID_List_GUID, Enumeration_Type_GUID, Name, List_ID, Agency_ID, Version_ID, Definition)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	private final String _UPDATE_Agency_ID_List_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET Agency_ID_List_GUID = ?,"
			+ " Enumeration_Type_GUID = ?, Name = ?, List_ID = ?, Agency_ID = ?,"
			+ " Version_ID = ?, Definition = ? WHERE Agency_ID_List_ID = ?";
	
	private final String _DELETE_Agency_ID_List_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE Agency_ID_List_ID = ?";
	
	
	public boolean insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO) obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_Agency_ID_List_STATEMENT);
			ps.setString(1, agencyidlistVO.getAgencyIDListGUID());
			ps.setString(2, agencyidlistVO.getEnumerationTypeGUID());
			ps.setString(3, agencyidlistVO.getName());
			ps.setString(4, agencyidlistVO.getListID());
			//ps.setInt(5, agencyidlistVO.getAgencyID());
			ps.setNull(5, java.sql.Types.INTEGER);
			ps.setString(6, agencyidlistVO.getVersionID());
			ps.setString(7, agencyidlistVO.getDefinition());
			ps.executeUpdate();
//			ResultSet tableKeys = ps.getGeneratedKeys();
//			tableKeys.next();
//			int autoGeneratedID = tableKeys.getInt(1);
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
		AgencyIDListVO agencyidlistVO = new AgencyIDListVO();
		try {
			Connection conn = tx.open();
			String sql = _FIND_Agency_ID_List_STATEMENT;

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
				agencyidlistVO.setAgencyIDListID(rs.getInt("Agency_ID_List_ID"));
				agencyidlistVO.setAgencyIDListGUID(rs.getString("Agency_ID_List_GUID"));
				agencyidlistVO.setEnumerationTypeGUID(rs.getString("Enumeration_Type_GUID"));
				agencyidlistVO.setName(rs.getString("Name"));
				agencyidlistVO.setListID(rs.getString("List_ID"));
				agencyidlistVO.setAgencyID(rs.getInt("Agency_ID"));
				agencyidlistVO.setVersionID(rs.getString("Version_ID"));
				agencyidlistVO.setDefinition(rs.getString("Definition"));
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
		return agencyidlistVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_Agency_ID_List_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				AgencyIDListVO agencyidlistVO = new AgencyIDListVO();
				agencyidlistVO.setAgencyIDListID(rs.getInt("Agency_ID_List_ID"));
				agencyidlistVO.setAgencyIDListGUID(rs.getString("Agency_ID_List_GUID"));
				agencyidlistVO.setEnumerationTypeGUID(rs.getString("Enumeration_Type_GUID"));
				agencyidlistVO.setName(rs.getString("Name"));
				agencyidlistVO.setListID(rs.getString("List_ID"));
				agencyidlistVO.setAgencyID(rs.getInt("Agency_ID"));
				agencyidlistVO.setVersionID(rs.getString("Version_ID"));
				agencyidlistVO.setDefinition(rs.getString("Definition"));
				list.add(agencyidlistVO);
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
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO) obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_Agency_ID_List_STATEMENT);
			ps.setString(1, agencyidlistVO.getAgencyIDListGUID());
			ps.setString(2, agencyidlistVO.getEnumerationTypeGUID());
			ps.setString(3, agencyidlistVO.getName());
			ps.setString(4, agencyidlistVO.getListID());
			ps.setInt(5, agencyidlistVO.getAgencyID());
			ps.setString(6, agencyidlistVO.getVersionID());
			ps.setString(7, agencyidlistVO.getDefinition());
			ps.setInt(8, agencyidlistVO.getAgencyIDListID());
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
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO) obj;
		
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_Agency_ID_List_STATEMENT);
			ps.setInt(1, agencyidlistVO.getAgencyIDListID());
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
