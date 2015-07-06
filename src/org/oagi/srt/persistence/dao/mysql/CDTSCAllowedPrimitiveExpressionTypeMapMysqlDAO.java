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
import org.oagi.srt.persistence.dto.CDTSCAllowedPrimitiveExpressionTypeMapVO;

/**
 *
 * @author Nasif Sikder
 * @version 1.0
 *
 */
public class CDTSCAllowedPrimitiveExpressionTypeMapMysqlDAO extends SRTDAO {
	private final String _tableName = "cdt_sc_allowed_primitive_expression_type_map";

	private final String _FIND_ALL_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT
	= "SELECT CDT_SC_Allowed_Primitive_Expression_Type_Map_ID, CDT_SC_Allowed_Primitive, XSD_BuiltIn_Type_ID "
			+ "FROM " + _tableName;
	
	private final String _FIND_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT
	= "SELECT CDT_SC_Allowed_Primitive_Expression_Type_Map_ID, CDT_SC_Allowed_Primitive, XSD_BuiltIn_Type_ID "
			+ "FROM " + _tableName;
	
	private final String _INSERT_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT
	= "INSERT INTO " + _tableName + " (CDT_SC_Allowed_Primitive, XSD_BuiltIn_Type_ID) VALUES (?, ?)";
	
	private final String _UPDATE_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT
	= "UPDATE " + _tableName + " SET CDT_SC_Allowed_Primitive = ?, XSD_BuiltIn_Type_ID = ? "
			+ "WHERE CDT_SC_Allowed_Primitive_Expression_Type_Map_ID = ?";
	
	private final String _DELETE_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT
	= "DELETE FROM " + _tableName + " WHERE CDT_SC_Allowed_Primitive_Expression_Type_Map_ID = ?";


	@Override
	public int findMaxId() throws SRTDAOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ArrayList<SRTObject> findObjects(QueryCondition qc)
			throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT;
			
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
				CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
						new CDTSCAllowedPrimitiveExpressionTypeMapVO();
				cdt_sc_allowed_primitive_expression_type_mapVO.setCTSCAllowedPrimitiveExpressionTypeMapID(rs.getInt("CDT_SC_Allowed_Primitive_Expression_Type_Map_ID"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setCDTSCAllowedPrimitive(rs.getInt("CDT_SC_Allowed_Primitive"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setXSDBuiltInTypeID(rs.getInt("XSD_BuiltIn_Type_ID"));
				list.add(cdt_sc_allowed_primitive_expression_type_mapVO);
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
	
	public ArrayList<SRTObject> findObjects(QueryCondition qc, Connection conn)
			throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = _FIND_ALL_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT;
			
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
				CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
						new CDTSCAllowedPrimitiveExpressionTypeMapVO();
				cdt_sc_allowed_primitive_expression_type_mapVO.setCTSCAllowedPrimitiveExpressionTypeMapID(rs.getInt("CDT_SC_Allowed_Primitive_Expression_Type_Map_ID"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setCDTSCAllowedPrimitive(rs.getInt("CDT_SC_Allowed_Primitive"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setXSDBuiltInTypeID(rs.getInt("XSD_BuiltIn_Type_ID"));
				list.add(cdt_sc_allowed_primitive_expression_type_mapVO);
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
	
	public int insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
				(CDTSCAllowedPrimitiveExpressionTypeMapVO)obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT);
			ps.setInt(1, cdt_sc_allowed_primitive_expression_type_mapVO.getCDTSCAllowedPrimitive());
			ps.setInt(2, cdt_sc_allowed_primitive_expression_type_mapVO.getXSDBuiltInTypeID());

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
		CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
		new CDTSCAllowedPrimitiveExpressionTypeMapVO();
		try {
			Connection conn = tx.open();
			String sql = _FIND_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT;

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
				cdt_sc_allowed_primitive_expression_type_mapVO.setCTSCAllowedPrimitiveExpressionTypeMapID(rs.getInt("CDT_SC_Allowed_Primitive_Expression_Type_Map_ID"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setCDTSCAllowedPrimitive(rs.getInt("CDT_SC_Allowed_Primitive"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setXSDBuiltInTypeID(rs.getInt("XSD_BuiltIn_Type_ID"));
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
		return cdt_sc_allowed_primitive_expression_type_mapVO;
	}
	
	public SRTObject findObject(QueryCondition qc, Connection conn) throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
		new CDTSCAllowedPrimitiveExpressionTypeMapVO();
		try {
			String sql = _FIND_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT;

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
				cdt_sc_allowed_primitive_expression_type_mapVO.setCTSCAllowedPrimitiveExpressionTypeMapID(rs.getInt("CDT_SC_Allowed_Primitive_Expression_Type_Map_ID"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setCDTSCAllowedPrimitive(rs.getInt("CDT_SC_Allowed_Primitive"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setXSDBuiltInTypeID(rs.getInt("XSD_BuiltIn_Type_ID"));
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
		return cdt_sc_allowed_primitive_expression_type_mapVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
						new CDTSCAllowedPrimitiveExpressionTypeMapVO();
				cdt_sc_allowed_primitive_expression_type_mapVO.setCTSCAllowedPrimitiveExpressionTypeMapID(rs.getInt("CDT_SC_Allowed_Primitive_Expression_Type_Map_ID"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setCDTSCAllowedPrimitive(rs.getInt("CDT_SC_Allowed_Primitive"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setXSDBuiltInTypeID(rs.getInt("XSD_BuiltIn_Type_ID"));
				list.add(cdt_sc_allowed_primitive_expression_type_mapVO);
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
	
	public ArrayList<SRTObject> findObjects(Connection conn) throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = _FIND_ALL_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
						new CDTSCAllowedPrimitiveExpressionTypeMapVO();
				cdt_sc_allowed_primitive_expression_type_mapVO.setCTSCAllowedPrimitiveExpressionTypeMapID(rs.getInt("CDT_SC_Allowed_Primitive_Expression_Type_Map_ID"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setCDTSCAllowedPrimitive(rs.getInt("CDT_SC_Allowed_Primitive"));
				cdt_sc_allowed_primitive_expression_type_mapVO.setXSDBuiltInTypeID(rs.getInt("XSD_BuiltIn_Type_ID"));
				list.add(cdt_sc_allowed_primitive_expression_type_mapVO);
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

	public boolean updateObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
				(CDTSCAllowedPrimitiveExpressionTypeMapVO)obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT);

			ps.setInt(1, cdt_sc_allowed_primitive_expression_type_mapVO.getCDTSCAllowedPrimitive());
			ps.setInt(2, cdt_sc_allowed_primitive_expression_type_mapVO.getXSDBuiltInTypeID());
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
		CDTSCAllowedPrimitiveExpressionTypeMapVO cdt_sc_allowed_primitive_expression_type_mapVO =
				(CDTSCAllowedPrimitiveExpressionTypeMapVO)obj;
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_CDT_SC_ALLOWED_PRIMITIVE_EXPRESSION_TYPE_MAP_STATEMENT);
			ps.setInt(1, cdt_sc_allowed_primitive_expression_type_mapVO.getCTSCAllowedPrimitiveExpressionTypeMapID());
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
	public int insertObject(SRTObject obj, Connection conn)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
