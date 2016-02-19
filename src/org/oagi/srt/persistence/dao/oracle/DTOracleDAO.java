package org.oagi.srt.persistence.dao.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.DTVO;

/**
 *
 * @author Yunsu Lee
 * @version 1.0
 *
 */
public class DTOracleDAO extends SRTDAO {

	private final String _tableName = "dt";

	private final String _FIND_ALL_DT_STATEMENT = 
			"SELECT DT_ID, guid, Type, Version_Num, Previous_Version_DT_ID, "
					+ "Data_Type_Term, Qualifier, Based_DT_ID, DEN, Content_Component_DEN, "
					+ "Definition, Content_Component_Definition, Revision_Doc, State, Created_By, owner_user_id, Last_Updated_By, "
					+ "Creation_Timestamp, Last_Update_Timestamp, "
					+ "revision_num, revision_tracking_num, revision_action, release_id, current_bdt_id, is_deprecated FROM " + _tableName;

	private final String _FIND_DT_STATEMENT = 
			"SELECT DT_ID, guid, Type, Version_Num, Previous_Version_DT_ID, "
					+ "Data_Type_Term, Qualifier, Based_DT_ID, DEN, Content_Component_DEN, "
					+ "Definition, Content_Component_Definition, Revision_Doc, State, Created_By, owner_user_id, Last_Updated_By, "
					+ "Creation_Timestamp, Last_Update_Timestamp, "
					+ "revision_num, revision_tracking_num, revision_action, release_id, current_bdt_id, is_deprecated FROM " + _tableName;

	private final String _INSERT_DT_STATEMENT = 
			"INSERT INTO " + _tableName + " (guid, Type, Version_Num, Previous_Version_DT_ID, "
					+ "Data_Type_Term, Qualifier, Based_DT_ID, DEN, Content_Component_DEN, "
					+ "Definition, Content_Component_Definition, Revision_Doc, State, Created_By, owner_user_id, Last_Updated_By, "
					+ "Creation_Timestamp, Last_Update_Timestamp, "
					+ "revision_num, revision_tracking_num, revision_action, release_id, current_bdt_id, is_deprecated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)";

	private final String _UPDATE_DT_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET Last_Update_Timestamp = CURRENT_TIMESTAMP, guid = ?, Type = ?, Version_Num = ?, Previous_Version_DT_ID = ?, "
			+ "Data_Type_Term = ?, Qualifier = ?, Based_DT_ID = ?, DEN = ?, Content_Component_DEN = ?, "
			+ "Definition = ?, Content_Component_Definition = ?, Revision_Doc = ?, State = ?, Created_By = ?, owner_user_id = ?, Last_Updated_By = ?, "
			+ "revision_num = ?, revision_tracking_num = ?, revision_action = ?, release_id = ?, current_bdt_id = ?, is_deprecated = ? WHERE DT_ID = ?";

	@Override
	public int findMaxId() throws SRTDAOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private final String _DELETE_DT_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE DT_ID = ?";

	public int insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		DTVO dtVO = (DTVO)obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_DT_STATEMENT);
			if( dtVO.getDTGUID()==null ||  dtVO.getDTGUID().length()==0 ||  dtVO.getDTGUID().isEmpty() ||  dtVO.getDTGUID().equals(""))				
				ps.setString(1,"\u00A0");
			else 	
				ps.setString(1, dtVO.getDTGUID());

			ps.setInt(2, dtVO.getDTType());
			if( dtVO.getVersionNumber()==null ||  dtVO.getVersionNumber().length()==0 ||  dtVO.getVersionNumber().isEmpty() ||  dtVO.getVersionNumber().equals(""))				
				ps.setString(3,"\u00A0");
			else 	
				ps.setString(3, dtVO.getVersionNumber());

			ps.setInt(4, dtVO.getPreviousVersionDTID());
			if( dtVO.getDataTypeTerm()==null ||  dtVO.getDataTypeTerm().length()==0 ||  dtVO.getDataTypeTerm().isEmpty() ||  dtVO.getDataTypeTerm().equals(""))				
				ps.setString(5,"\u00A0");
			else 	
				ps.setString(5, dtVO.getDataTypeTerm());

			if( dtVO.getQualifier()==null ||  dtVO.getQualifier().length()==0 ||  dtVO.getQualifier().isEmpty() ||  dtVO.getQualifier().equals(""))				
				ps.setString(6,"\u00A0");
			else 	
				ps.setString(6, dtVO.getQualifier());

			ps.setInt(7, dtVO.getBasedDTID());
			if(dtVO.getDEN()==null || dtVO.getDEN().length()==0 || dtVO.getDEN().isEmpty() || dtVO.getDEN().equals("")){
				ps.setString(8, "\u00A0");
			}
			else 
				ps.setString(8, dtVO.getDEN());
			ps.setString(9, dtVO.getContentComponentDEN());
			if(dtVO.getDefinition()==null || dtVO.getDefinition().length()==0 || dtVO.getDefinition().isEmpty() || dtVO.getDefinition().equals("")){
				ps.setString(10, "\u00A0");
			}
			else {
				String s = StringUtils.abbreviate(dtVO.getDefinition(), 4000);
				ps.setString(10, s);
			}
			if( dtVO.getContentComponentDefinition()==null ||  dtVO.getContentComponentDefinition().length()==0 ||  dtVO.getContentComponentDefinition().isEmpty() ||  dtVO.getContentComponentDefinition().equals(""))				
				ps.setString(11,"\u00A0");
			else 	
				ps.setString(11, dtVO.getContentComponentDefinition());

			if( dtVO.getRevisionDocumentation()==null ||  dtVO.getRevisionDocumentation().length()==0 ||  dtVO.getRevisionDocumentation().isEmpty() ||  dtVO.getRevisionDocumentation().equals(""))				
				ps.setString(12,"\u00A0");
			else 	
				ps.setString(12, dtVO.getRevisionDocumentation());

			ps.setInt(13, dtVO.getState());
			ps.setInt(14, dtVO.getCreatedByUserId());
			ps.setInt(15, dtVO.getOwnerUserId());
			ps.setInt(16, dtVO.getLastUpdatedByUserId());
			ps.setInt(17, dtVO.getRevisionNum());
			ps.setInt(18, dtVO.getRevisionTrackingNum());
			if( dtVO.getRevisionAction())				
				ps.setInt(19,1);
			else 	
				ps.setInt(19,0);

			ps.setInt(20, dtVO.getReleaseId());
			ps.setInt(21, dtVO.getCurrentBdtId());
			if( dtVO.getIs_deprecated())				
				ps.setInt(22,1);
			else 	
				ps.setInt(22,0);

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
		return 1;
	}

	public SRTObject findObject(QueryCondition qc) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		DTVO dtVO = null;
		Connection conn = null;
		
		try {
			conn = tx.open();
			String sql = _FIND_DT_STATEMENT;

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
				dtVO = new DTVO();
				dtVO.setDTID(rs.getInt("DT_ID"));
				dtVO.setDTGUID(rs.getString("guid"));
				dtVO.setDTType(rs.getInt("type"));
				dtVO.setVersionNumber(rs.getString("version_num"));
				dtVO.setPreviousVersionDTID(rs.getInt("previous_version_dt_id"));
				dtVO.setDataTypeTerm(rs.getString("data_type_term"));
				dtVO.setQualifier(rs.getString("qualifier"));
				dtVO.setBasedDTID(rs.getInt("Based_DT_ID"));
				dtVO.setDEN(rs.getString("DEN"));
				dtVO.setContentComponentDEN(rs.getString("Content_Component_DEN"));
				dtVO.setDefinition(rs.getString("Definition"));
				dtVO.setContentComponentDefinition("Content_Component_Definition");
				dtVO.setRevisionDocumentation(rs.getString("Revision_Doc"));
				dtVO.setState(rs.getInt("State"));
				dtVO.setCreatedByUserId(rs.getInt("Created_By"));
				dtVO.setOwnerUserId(rs.getInt("owner_user_id"));
				dtVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				dtVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				dtVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				dtVO.setRevisionNum(rs.getInt("revision_num"));
				dtVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				dtVO.setRevisionAction(rs.getBoolean("revision_action"));
				dtVO.setReleaseId(rs.getInt("release_id"));
				dtVO.setCurrentBdtId(rs.getInt("current_bdt_id"));
				dtVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
			}
			
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
			try {
				if(conn != null && !conn.isClosed())
					conn.close();
			} catch (SQLException e) {}
			tx.close();
		}
		return dtVO;
	}
	
	public SRTObject findObject(QueryCondition qc, Connection conn) throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		DTVO dtVO = null;
		
		try {
			String sql = _FIND_DT_STATEMENT;

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
				dtVO = new DTVO();
				dtVO.setDTID(rs.getInt("DT_ID"));
				dtVO.setDTGUID(rs.getString("guid"));
				dtVO.setDTType(rs.getInt("type"));
				dtVO.setVersionNumber(rs.getString("version_num"));
				dtVO.setPreviousVersionDTID(rs.getInt("previous_version_dt_id"));
				dtVO.setDataTypeTerm(rs.getString("data_type_term"));
				dtVO.setQualifier(rs.getString("qualifier"));
				dtVO.setBasedDTID(rs.getInt("Based_DT_ID"));
				dtVO.setDEN(rs.getString("DEN"));
				dtVO.setContentComponentDEN(rs.getString("Content_Component_DEN"));
				dtVO.setDefinition(rs.getString("Definition"));
				dtVO.setContentComponentDefinition("Content_Component_Definition");
				dtVO.setRevisionDocumentation(rs.getString("Revision_Doc"));
				dtVO.setState(rs.getInt("State"));
				dtVO.setCreatedByUserId(rs.getInt("Created_By"));
				dtVO.setOwnerUserId(rs.getInt("owner_user_id"));
				dtVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				dtVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				dtVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				dtVO.setRevisionNum(rs.getInt("revision_num"));
				dtVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				dtVO.setRevisionAction(rs.getBoolean("revision_action"));
				dtVO.setReleaseId(rs.getInt("release_id"));
				dtVO.setCurrentBdtId(rs.getInt("current_bdt_id"));
				dtVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
			}
			
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
		}
		return dtVO;
	}
	
	public ArrayList<SRTObject> findObjects(QueryCondition qc) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			Connection conn = tx.open();
			String sql = _FIND_DT_STATEMENT;

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
			while (rs.next()) {
				DTVO dtVO = new DTVO();
				dtVO.setDTID(rs.getInt("DT_ID"));
				dtVO.setDTGUID(rs.getString("guid"));
				dtVO.setDTType(rs.getInt("type"));
				dtVO.setVersionNumber(rs.getString("version_num"));
				dtVO.setPreviousVersionDTID(rs.getInt("previous_version_dt_id"));
				dtVO.setDataTypeTerm(rs.getString("data_type_term"));
				dtVO.setQualifier(rs.getString("qualifier"));
				dtVO.setBasedDTID(rs.getInt("Based_DT_ID"));
				dtVO.setDEN(rs.getString("DEN"));
				dtVO.setContentComponentDEN(rs.getString("Content_Component_DEN"));
				dtVO.setDefinition(rs.getString("Definition"));
				dtVO.setContentComponentDefinition("Content_Component_Definition");
				dtVO.setRevisionDocumentation(rs.getString("Revision_Doc"));
				dtVO.setState(rs.getInt("State"));
				dtVO.setCreatedByUserId(rs.getInt("Created_By"));
				dtVO.setOwnerUserId(rs.getInt("owner_user_id"));
				dtVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				dtVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				dtVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				dtVO.setRevisionNum(rs.getInt("revision_num"));
				dtVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				dtVO.setRevisionAction(rs.getBoolean("revision_action"));
				dtVO.setReleaseId(rs.getInt("release_id"));
				dtVO.setCurrentBdtId(rs.getInt("current_bdt_id"));
				dtVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
				list.add(dtVO);
			}
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

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_DT_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				DTVO dtVO = new DTVO();
				dtVO.setDTID(rs.getInt("DT_ID"));
				dtVO.setDTGUID(rs.getString("guid"));
				dtVO.setDTType(rs.getInt("type"));
				dtVO.setVersionNumber(rs.getString("version_num"));
				dtVO.setPreviousVersionDTID(rs.getInt("previous_version_dt_id"));
				dtVO.setDataTypeTerm(rs.getString("data_type_term"));
				dtVO.setQualifier(rs.getString("qualifier"));
				dtVO.setBasedDTID(rs.getInt("Based_DT_ID"));
				dtVO.setDEN(rs.getString("DEN"));
				dtVO.setContentComponentDEN(rs.getString("Content_Component_DEN"));
				dtVO.setDefinition(rs.getString("Definition"));
				dtVO.setContentComponentDefinition("Content_Component_Definition");
				dtVO.setRevisionDocumentation(rs.getString("Revision_Doc"));
				dtVO.setState(rs.getInt("State"));
				dtVO.setCreatedByUserId(rs.getInt("Created_By"));
				dtVO.setOwnerUserId(rs.getInt("owner_user_id"));
				dtVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				dtVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				dtVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				dtVO.setRevisionNum(rs.getInt("revision_num"));
				dtVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				dtVO.setRevisionAction(rs.getBoolean("revision_action"));
				dtVO.setReleaseId(rs.getInt("release_id"));
				dtVO.setCurrentBdtId(rs.getInt("current_bdt_id"));
				dtVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
				list.add(dtVO);
			}
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
		DTVO dtVO = (DTVO)obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_DT_STATEMENT);

			if( dtVO.getDTGUID()==null ||  dtVO.getDTGUID().length()==0 ||  dtVO.getDTGUID().isEmpty() ||  dtVO.getDTGUID().equals(""))				
				ps.setString(1,"\u00A0");
			else 	
				ps.setString(1, dtVO.getDTGUID());

			ps.setInt(2, dtVO.getDTType());
			if( dtVO.getVersionNumber()==null ||  dtVO.getVersionNumber().length()==0 ||  dtVO.getVersionNumber().isEmpty() ||  dtVO.getVersionNumber().equals(""))				
				ps.setString(3,"\u00A0");
			else 	
				ps.setString(3, dtVO.getVersionNumber());

			ps.setInt(4, dtVO.getPreviousVersionDTID());
			if( dtVO.getDataTypeTerm()==null ||  dtVO.getDataTypeTerm().length()==0 ||  dtVO.getDataTypeTerm().isEmpty() ||  dtVO.getDataTypeTerm().equals(""))				
				ps.setString(5,"\u00A0");
			else 	
				ps.setString(5, dtVO.getDataTypeTerm());

			if( dtVO.getQualifier()==null ||  dtVO.getQualifier().length()==0 ||  dtVO.getQualifier().isEmpty() ||  dtVO.getQualifier().equals(""))				
				ps.setString(6,"\u00A0");
			else 	
				ps.setString(6, dtVO.getQualifier());

			ps.setInt(7, dtVO.getBasedDTID());
			if( dtVO.getDEN()==null ||  dtVO.getDEN().length()==0 ||  dtVO.getDEN().isEmpty() ||  dtVO.getDEN().equals(""))				
				ps.setString(8,"\u00A0");
			else 	
				ps.setString(8, dtVO.getDEN());

			if( dtVO.getContentComponentDEN()==null ||  dtVO.getContentComponentDEN().length()==0 ||  dtVO.getContentComponentDEN().isEmpty() ||  dtVO.getContentComponentDEN().equals(""))				
				ps.setString(9,"\u00A0");
			else 	
				ps.setString(9, dtVO.getContentComponentDEN());

			if( dtVO.getDefinition()==null ||  dtVO.getDefinition().length()==0 ||  dtVO.getDefinition().isEmpty() ||  dtVO.getDefinition().equals(""))				
				ps.setString(10,"\u00A0");
			else 	{
				String s = StringUtils.abbreviate(dtVO.getDefinition(), 4000);
				ps.setString(10, s);
			}
			if( dtVO.getContentComponentDefinition()==null ||  dtVO.getContentComponentDefinition().length()==0 ||  dtVO.getContentComponentDefinition().isEmpty() ||  dtVO.getContentComponentDefinition().equals(""))				
				ps.setString(11,"\u00A0");
			else 	
				ps.setString(11, dtVO.getContentComponentDefinition());

			if( dtVO.getRevisionDocumentation()==null ||  dtVO.getRevisionDocumentation().length()==0 ||  dtVO.getRevisionDocumentation().isEmpty() ||  dtVO.getRevisionDocumentation().equals(""))				
				ps.setString(12,"\u00A0");
			else 	
				ps.setString(12, dtVO.getRevisionDocumentation());

			ps.setInt(13, dtVO.getState());
			ps.setInt(14, dtVO.getCreatedByUserId());
			ps.setInt(15, dtVO.getOwnerUserId());
			ps.setInt(16, dtVO.getLastUpdatedByUserId());
			ps.setInt(17, dtVO.getRevisionNum());
			ps.setInt(18, dtVO.getRevisionTrackingNum());
			if( dtVO.getRevisionAction())				
				ps.setInt(19,1);
			else 	
				ps.setInt(19,0);

			ps.setInt(20, dtVO.getReleaseId());
			ps.setInt(21, dtVO.getCurrentBdtId());
			if( dtVO.getIs_deprecated())				
				ps.setInt(22,1);
			else 	
				ps.setInt(22,0);

			ps.setInt(23, dtVO.getDTID());
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
		DTVO dtVO = (DTVO)obj;

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_DT_STATEMENT);
			ps.setInt(1, dtVO.getDTID());
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

	public ArrayList<SRTObject> findObjects(QueryCondition qc, Connection conn)
			throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			String sql = _FIND_DT_STATEMENT;

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
			while (rs.next()) {
				DTVO dtVO = new DTVO();
				dtVO.setDTID(rs.getInt("DT_ID"));
				dtVO.setDTGUID(rs.getString("guid"));
				dtVO.setDTType(rs.getInt("type"));
				dtVO.setVersionNumber(rs.getString("version_num"));
				dtVO.setPreviousVersionDTID(rs.getInt("previous_version_dt_id"));
				dtVO.setDataTypeTerm(rs.getString("data_type_term"));
				dtVO.setQualifier(rs.getString("qualifier"));
				dtVO.setBasedDTID(rs.getInt("Based_DT_ID"));
				dtVO.setDEN(rs.getString("DEN"));
				dtVO.setContentComponentDEN(rs.getString("Content_Component_DEN"));
				dtVO.setDefinition(rs.getString("Definition"));
				dtVO.setContentComponentDefinition("Content_Component_Definition");
				dtVO.setRevisionDocumentation(rs.getString("Revision_Doc"));
				dtVO.setState(rs.getInt("State"));
				dtVO.setCreatedByUserId(rs.getInt("Created_By"));
				dtVO.setOwnerUserId(rs.getInt("owner_user_id"));
				dtVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				dtVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				dtVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				dtVO.setRevisionNum(rs.getInt("revision_num"));
				dtVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				dtVO.setRevisionAction(rs.getBoolean("revision_action"));
				dtVO.setReleaseId(rs.getInt("release_id"));
				dtVO.setCurrentBdtId(rs.getInt("current_bdt_id"));
				dtVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
				list.add(dtVO);
			}
			
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
		}
		return list;
	}

	@Override
	public ArrayList<SRTObject> findObjects(Connection conn)
			throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		
		try {
			String sql = _FIND_ALL_DT_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				DTVO dtVO = new DTVO();
				dtVO.setDTID(rs.getInt("DT_ID"));
				dtVO.setDTGUID(rs.getString("guid"));
				dtVO.setDTType(rs.getInt("type"));
				dtVO.setVersionNumber(rs.getString("version_num"));
				dtVO.setPreviousVersionDTID(rs.getInt("previous_version_dt_id"));
				dtVO.setDataTypeTerm(rs.getString("data_type_term"));
				dtVO.setQualifier(rs.getString("qualifier"));
				dtVO.setBasedDTID(rs.getInt("Based_DT_ID"));
				dtVO.setDEN(rs.getString("DEN"));
				dtVO.setContentComponentDEN(rs.getString("Content_Component_DEN"));
				dtVO.setDefinition(rs.getString("Definition"));
				dtVO.setContentComponentDefinition("Content_Component_Definition");
				dtVO.setRevisionDocumentation(rs.getString("Revision_Doc"));
				dtVO.setState(rs.getInt("State"));
				dtVO.setCreatedByUserId(rs.getInt("Created_By"));
				dtVO.setOwnerUserId(rs.getInt("owner_user_id"));
				dtVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				dtVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				dtVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				dtVO.setRevisionNum(rs.getInt("revision_num"));
				dtVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				dtVO.setRevisionAction(rs.getBoolean("revision_action"));
				dtVO.setReleaseId(rs.getInt("release_id"));
				dtVO.setCurrentBdtId(rs.getInt("current_bdt_id"));
				dtVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
				list.add(dtVO);
			}
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
		}
		return list;
	}

	@Override
	public int insertObject(SRTObject obj, Connection conn)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return 0;
	}
}