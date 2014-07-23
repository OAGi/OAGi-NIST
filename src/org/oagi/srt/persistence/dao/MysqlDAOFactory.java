package org.oagi.srt.persistence.dao;

/**
 * @author Yunsu Lee
 * @version 1.0
 */
public class MysqlDAOFactory extends DAOFactory {

	private String _prefix = "org.oagi.srt.persistence.dao.mysql.";
	private String _suffix = "MysqlDAO";

	public MysqlDAOFactory() {
	}

	public SRTDAO getDAO(String name) throws SRTDAOException {
		Object dao = null;
		String className = _prefix + name + _suffix;
		try {
			dao = Class.forName(className).newInstance();
		} catch (ClassNotFoundException e) {
			throw new SRTDAOException(SRTDAOException.DAO_CLASS_NOT_FOUND);
		} catch (InstantiationException e) {
			throw new SRTDAOException(SRTDAOException.DAO_CLASS_INSTANTIATION_FAILED);
		} catch (IllegalAccessException e) {
			throw new SRTDAOException(SRTDAOException.DAO_CLASS_ILLEGAL_ACCESS);
		}
		return (SRTDAO) dao;
	}
}