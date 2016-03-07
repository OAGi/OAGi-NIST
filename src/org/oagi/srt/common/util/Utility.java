package org.oagi.srt.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Random;
import java.util.UUID;

import org.chanchan.common.persistence.db.ConnectionPoolManager;
import org.chanchan.common.util.ServerProperties;
import org.oagi.srt.common.SRTConstants;
import org.oagi.srt.persistence.dto.DTVO;
import org.oagi.srt.persistence.populate.Types;
import org.oagi.srt.web.startup.SRTInitializer;
import org.oagi.srt.web.startup.SRTInitializerException;

public class Utility {
	public static String generateGUID(){
		return "oagis-id-" + UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String first(String den, boolean upp) {
		den = den.substring(0, den.indexOf(".")).replace("_", " ").replaceAll(" ","").replaceAll("Identifier","ID");
		if(upp==false)
			den = den.substring(0, 1).toLowerCase()+den.substring(1);
		return den;
	}
	
	public static String parseUppercase(String str) {
		int index = 0;
		str = str.replaceAll("ID", "Identifier");
		for(int i = str.length() - 1 ; i > -1 ; i--) {
			if(Character.isUpperCase(str.charAt(i)) && i != 0) {
				index = i;
				break;
			}
		}
		return str.substring(0,index).toLowerCase();
	}
	
	public static String first(String den) {
		den = den.substring(0, den.indexOf(".")).replace("_", " ").replaceAll(" ","").replaceAll("Identifier","ID");
		return den;
	}
	
	public static String second(String den, boolean upp) {
		den = den.substring(den.indexOf(".")+2, den.length());
		den = den.indexOf(".") == -1 ? den.replaceAll("-", "").replaceAll(" ","") : den.substring(0, den.indexOf(".")).replaceAll("-", "").replaceAll(" ","").replaceAll("Identifier","ID");
		if(upp==false)
			den = den.substring(0, 1).toLowerCase()+den.substring(1);
		return den;
	}
	
	public static String third(String den) {
		den = den.substring(den.indexOf(".")+2, den.length());
		den = den.substring(den.indexOf(".")+2, den.length());
		den = den.indexOf(".") == -1 ? den.replaceAll("-", "").replaceAll(" ","").replaceAll("Identifier","ID") : den.substring(0, den.indexOf(".")).replaceAll("-", "").replaceAll(" ","").replaceAll("Identifier","ID");
		den = den.substring(0, 1).toLowerCase()+den.substring(1);
		return den;
	}

	public static String DenToName(String den) {
			den = den.substring(0, den.indexOf(". Type")).replaceAll(" ", "")+"Type";
		return den;
	}
	
	public static String createDENFormat(String str) {
		String pre = str.substring(0, str.indexOf("Type"));
		return pre + ". Type";
	}
	
	public static String denToTypeName(String den) {
		String part1 = den.substring(0, den.indexOf("_"));
		String part2 = den.substring(den.indexOf("_"), den.indexOf("."));
		return part1.replaceAll(" ", "") + "Type" + part2;
	}
	
	public static String denToUnqualified(String den) {
		if(den.contains("_")){
			String part1 = den.substring(0, den.indexOf("_"));
			String part2 = den.substring(den.indexOf("."), den.length());
			return part1 + part2;
		}
		else
			return den;
	}
	
	public static String typeToDen(String type) {
		String den;
		if(type.contains("_")){
			String part1 = type.substring(0, type.indexOf("Type"));
			String part2 = type.substring(type.indexOf("_"), type.length());
			den = spaceSeparator(part1) + part2 + ". Type";
		}
		else
			den = spaceSeparator(type.substring(0, type.indexOf("Type")))+". Type";
		return den;
	}
	
	public static String typeToContent(String type) {
		String den;
		if(type.contains("_")){
			String part1 = type.substring(0, type.indexOf("Type"));
			String part2 = type.substring(type.indexOf("_"), type.length());
			den = spaceSeparator(part1) + part2 + ". Content";
		}
		else
			den = spaceSeparator(type.substring(0, type.indexOf("Type")))+". Content";
		return den;
	}
	
	public static String getLastToken(String str) {
		StringBuffer sb = new StringBuffer();
		
		if(str.contains("_")) {
			int underscore = str.indexOf("_");
			str = str.substring(0, underscore)+str.substring(underscore+1, underscore+2).toUpperCase()+str.substring(underscore+2, str.length());
		}
		
		for(int i = 0; i < str.length(); i++) {
			if(Character.isUpperCase(str.charAt(i)) && i != 0) {
				if(Character.isUpperCase(str.charAt(i - 1)))
					if (i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1)) && (str.charAt(i) != 'D' && str.charAt(i-1) != 'I'))
						sb.append(" " + str.charAt(i));
					else
						sb.append(str.charAt(i));
				else 
					sb.append(" " + str.charAt(i));
			} else if(Character.isLowerCase(str.charAt(i)) && i == 0) {
				sb.append(String.valueOf(str.charAt(i)).toUpperCase());
			} else {
				sb.append(str.charAt(i));
			}
		}

		String result = sb.toString();
		if(result.endsWith(" Code Type"))
			result = result.substring(0, result.indexOf((" Code Type"))).replaceAll(" ", "").concat(" Code Type");
		return result.substring(result.lastIndexOf(" ")+1);
	}
	
	public static String getRepresentationTerm(String str){
		String lastToken = getLastToken(str);
		String representationTerm = null;
		for(int i = 0 ; i < Types.allowed_Representation_Term_List.length; i++){
			if(lastToken.equalsIgnoreCase(Types.allowed_Representation_Term_List[i])){
				representationTerm = lastToken;
				break;
			}
			
		}
		if(representationTerm == null)
			representationTerm = "Text";
		return representationTerm;
	}
	public static String firstToUpperCase(String str) {
		String prefix = str.substring(0, 1);
		String suffix = str.substring(1);
		return prefix.toUpperCase() + suffix;
	}
	
	public static int getRandomID(int c) {
		Random r = new Random();
		return c + r.nextInt(Integer.MAX_VALUE - c);
	}
	
	public static String spaceSeparator(String str) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < str.length(); i++) {
			if(Character.isUpperCase(str.charAt(i)) && i != 0) {
				if(Character.isUpperCase(str.charAt(i - 1)))
					if (i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1)) && (str.charAt(i) != 'D' && str.charAt(i-1) != 'I'))
						sb.append(" " + str.charAt(i));
					else
						sb.append(str.charAt(i));
				else 
					sb.append(" " + str.charAt(i));
			} else if(Character.isLowerCase(str.charAt(i)) && i == 0) {
				sb.append(String.valueOf(str.charAt(i)).toUpperCase());
			} else {
				sb.append(str.charAt(i));
			}
		}
		String result = sb.toString();
		if(result.endsWith(" Code Type"))
			result = result.substring(0, result.indexOf((" Code Type"))).replaceAll(" ", "").concat(" Code Type");
		return result;
	}
	
	public static String spaceSeparatorBeforeStr(String str, String beforeStr) {
		int pos = str.indexOf(beforeStr);
		if(pos!=-1){
			str=str.substring(0,pos);
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < str.length(); i++) {
			if(Character.isUpperCase(str.charAt(i)) && i != 0) {
				if(Character.isUpperCase(str.charAt(i - 1)))
					if (i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1)) && (str.charAt(i) != 'D' && str.charAt(i-1) != 'I'))
						sb.append(" " + str.charAt(i));
					else
						sb.append(str.charAt(i));
				else 
					sb.append(" " + str.charAt(i));
			} else if(Character.isLowerCase(str.charAt(i)) && i == 0) {
				sb.append(String.valueOf(str.charAt(i)).toUpperCase());
			} else {
				sb.append(str.charAt(i));
			}
		}
		String result = sb.toString();
		//if(result.endsWith(" Code Type"))
		//	result = result.substring(0, result.indexOf((" Code Type"))).concat(" Code Type");
		result = result.trim();
		return result;
	}
	
	public static String denWithoutUUID(String den){
		
		int uuidCheck = den.indexOf("_");
		int qualifiedCheck = den.indexOf("_ ");//Qualified should not have UUID den
		
		if(qualifiedCheck == -1){//if not qualified
			if(uuidCheck != -1){// if it has "_"
				String part1 = den.substring(0, uuidCheck);
				return part1+". Type";
			}
		}
		
		return den;
	}
	
	public static String firstDenWithoutUUID(String den){
		
		String denXUUID = denWithoutUUID(den);
		denXUUID = denXUUID.replaceAll("_", "");
		denXUUID = denXUUID.substring(0, denXUUID.indexOf(". Type"));
		return denXUUID;
		
	}
	
	public static String denWithQualifier(String qualifier, String baseDen){
		String denWithQualifier = "";
		baseDen = Utility.denWithoutUUID(baseDen);
		denWithQualifier =qualifier +"_ "+baseDen;
		
		return denWithQualifier;
	}
	
	public static String toQualifiedCodeContentDen(String den){
		
		String QCCDen = "";
		int posCodeContent = den.indexOf("Code Content");
		String part1 = den.substring(0, posCodeContent);
		String part2 = den.substring(posCodeContent+12);
		
		return part1+"_ "+part2;
	}
	
	public static String qualifier(String type, DTVO baseDT){
		
		String qualifier = "";
		String baseDen = baseDT.getDEN();
				
		if(baseDT.getDataTypeTerm().equals("Text")){
			qualifier= Utility.spaceSeparatorBeforeStr(type, "Type");
		}
		else if(baseDen.equals("Code Content. Type")){
			qualifier = Utility.spaceSeparatorBeforeStr(type, "CodeContentType");
		}
		else if(baseDen.endsWith("Code Content. Type")){
			qualifier= Utility.spaceSeparatorBeforeStr(type, "CodeType");
		}
		else {
			String p1 = Utility.firstDenWithoutUUID(baseDen);
			p1 = Utility.toCamelCase(p1);
			int pos = type.indexOf("Type");
			if(pos!=-1){
				type=type.substring(0,pos);
			}
			qualifier = Utility.spaceSeparatorBeforeStr(type, p1);
		}
//		String p1 = Utility.spaceSeparatorWithoutStr(type, "Type");
//		//System.out.print(p1);
//		String p2 = Utility.firstDenWithoutUUID(baseDen);
//		//System.out.print("\t"+p2);
//		
//		int pos = p1.indexOf(p2);
//		
//		if(p2.endsWith("Code Content")){
//			pos = p1.indexOf("Code");
//		}
//		
//		if(pos == -1){
//			qualifier= p1;
//		}
//		else {
//			qualifier= p1.substring(0, pos);
//		}
		qualifier = qualifier.trim();
		//System.out.println("\t\t"+qualifier);
		return qualifier;
	}
	
	public static void dbSetup() throws Exception {
		ServerProperties props = ServerProperties.getInstance();
		String _propFile = SRTConstants.SRT_PROPERTIES_FILE_NAME;
		try {
			InputStream is = SRTInitializer.class.getResourceAsStream(_propFile);
			if (is == null) {
				throw new SRTInitializerException(_propFile + " not found!");
			}
			try {
				props.load(is, true);
			} catch (IOException e) {
				throw new SRTInitializerException(_propFile + " cannot be read...");
			}
		} catch (Exception e) {
			System.out.println("[SRTInitializer] Fail to Getting "
					+ SRTConstants.SRT_PROPERTIES_FILE_NAME + " URL : "
					+ e.toString());
		}
		try {
			ConnectionPoolManager cpm = ConnectionPoolManager.getInstance();
			String poolName = cpm.getDefaultPoolName();
			System.out.println("DefaultPoolName:" + poolName);
			Connection dbConnection = cpm.getConnection(poolName);
			dbConnection.close();
			System.out.println("DB Connection Pool initialized...");
			cpm.release();
		} catch (Exception e) {
			System.out.println("[SRTInitializer] Fail to Creating Connection Pool : "
					+ e.toString());
			e.printStackTrace();
			throw new SRTInitializerException("[SRTInitializer] Fail to Creating Connection Pool : "
					+ e.toString());
		}
	
	}
	
	public static String toCamelCase(final String init) {
	    if (init==null)
	        return null;

	    final StringBuilder ret = new StringBuilder(init.length());

	    for (String word : init.split(" ")) {
	    	if(word.startsWith("_"))
	    		word = word.substring(0, 1);
	    	if(word.startsWith(" "))
	    		word = word.substring(0, 1);
	    	if(word.equalsIgnoreCase("identifier"))
	        	ret.append("ID");      	
	        if (!word.isEmpty()) {
	            ret.append(word.substring(0, 1).toUpperCase());
	            ret.append(word.substring(1));
	        }
	    }
	    return ret.toString();
	}
	
	public static String toLowerCamelCase(final String init) {
	    if (init==null)
	        return null;

	    final StringBuilder ret = new StringBuilder(init.length());
	    
	    int cnt = 0;
	    
	    for (String word : init.split(" ")) {
	    	if(word.startsWith("_"))
	    		word = word.substring(0, 1);
	    	if(word.startsWith(" "))
	    		word = word.substring(0, 1);
	    	if(word.equalsIgnoreCase("identifier"))
	        	ret.append("ID");      	
	    	else {
		        if (!word.isEmpty() && cnt != 0 ) {
		            ret.append(word.substring(0, 1).toUpperCase());
		            ret.append(word.substring(1).toLowerCase());
		        }
		    	
		        else if (!word.isEmpty() && cnt == 0 ) {
		            ret.append(word.substring(0, 1).toLowerCase());
		            ret.append(word.substring(1).toLowerCase());
		        }
	    	}
	    	cnt++;
	    }
	    return ret.toString();
	}
	
	public static String format(int a) {
		String s = String.format("%02d", a);
		return s;
	}
	
	public static void main(String args[]) {
		String str = "Amount_0723C8. Type";
		System.out.println(denToTypeName(str));
	}
}
