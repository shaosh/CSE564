package com.ProjMgmtSys.model.Data;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.hibernate.Query;
import org.hibernate.Session;
import org.xml.sax.SAXException;

import com.ProjMgmtSys.model.Gro.Gro;
import com.ProjMgmtSys.model.Object.ObjectManager;
import com.ProjMgmtSys.model.User.NameId;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;


public class DataManager {
	private static Session session;
	
	private static void createSession(){
		session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
	}
	
	public static String createData(String Value, int fieldId, int objId, int rowId, int depId, int groId){
		Data data = new Data();
		data.setValue(Value);
		data.setFieldId(fieldId);
		data.setObjId(objId);
		data.setRowId(rowId);
		data.setDepId(depId);
		data.setGroId(groId);
		
		createSession();
		session.save(data);
		session.getTransaction().commit();
		session.close();
		return  "" + data.getDataId();
	}
	
	//Used for create Proj Object
	public static String createData(JSONArray jArray, int objId, int depId, int groId) throws SAXException, JAXBException{
		List<Data> dataList = new ArrayList<Data>();
		int rowNO = ObjectManager.queryRowNO(objId);
		for(Object json : jArray){
			JSONObject jObj = (JSONObject)json;
			createSession();
			Data data = new Data();
			data.setDepId(depId);
			data.setFieldId(jObj.getInt("id"));
			data.setGroId(groId);
			data.setObjId(objId);
			data.setValue("" + jObj.get("val"));
			data.setRowId(rowNO);
			data.setDataId(objId + "_" + depId + "_" + groId + "_" + rowNO + "_" + jObj.getInt("id"));		
			session.save(data);
			session.getTransaction().commit();
			session.close();
		}
		Iterator<Data> iter = dataList.iterator();
		
		int projId = rowNO;
		ObjectManager.updateRowNO(objId, rowNO + 1);
		return "" + projId;
	}
	
	//Used for create Detail Object
	public static String createData(JSONArray jArray, int objId, int depId, int groId, int projId) throws SAXException, JAXBException{
		List<Data> dataList = new ArrayList<Data>();
		int rowNO = ObjectManager.queryRowNO(objId);
		for(Object json : jArray){
			JSONObject jObj = (JSONObject)json;
			createSession();
			Data data = new Data();
			data.setDepId(depId);
			data.setFieldId(jObj.getInt("id"));
			data.setGroId(groId);
			data.setObjId(objId);
			data.setValue("" + jObj.get("val"));
			data.setRowId(projId);
			data.setDataId(objId + "_" + depId + "_" + groId + "_" + rowNO + "_" + jObj.getInt("id"));		
			session.save(data);
			session.getTransaction().commit();
			session.close();
		}
		Iterator<Data> iter = dataList.iterator();
		
		ObjectManager.updateRowNO(objId, rowNO + 1);
		return rowNO + "";
	}
	
	@SuppressWarnings("unchecked")
	public static Object queryData(int dataId){
		createSession();
		String hql = "from Data as data where data.dataId=:dataId";
		Query query = session.createQuery(hql);
		query.setInteger("dataId", dataId);
		List <Data>list = query.list();
		Data data = null;
		java.util.Iterator<Data> iter = list.iterator();
		while (iter.hasNext()) {
			data = iter.next();
		}					
		session.getTransaction().commit();
		session.close();
		return data;	
	}

	public static Data queryData(int depId, int groId, int objId, int rowId, int fieldId){
		createSession();
		String hql = "from Data where depId=:depId and objId=:objId and groId=:groId and rowId=:rowId and fieldId=:fieldId";
		Query query = session.createQuery(hql);
		query.setInteger("depId", depId);
		query.setInteger("groId", groId);
		query.setInteger("objId", objId);
		query.setInteger("rowId", rowId);
		query.setInteger("fieldId", fieldId);
		List <Data>list = query.list();
		Data data = null;
		java.util.Iterator<Data> iter = list.iterator();
		while (iter.hasNext()) {
			data = iter.next();
		}					
		session.getTransaction().commit();
		session.close();
		return data;	
	}
	
	public static void updateValue(int dataId, String value){
		createSession();
		String hql1 = "update Data as data set data.value=:value where dataId=:dataId";
		Query query1 = session.createQuery(hql1);
		query1.setInteger("dataId", dataId);
		query1.setString("value", value);
		query1.executeUpdate(); 
		session.getTransaction().commit();
		session.close();
	}
	
	public static void updateValue(int depId, int groId, int objId, int rowId, int fieldId, String value){
		createSession();
		String hql = "update Data set value=:value where depId=:depId and groId=:groId and objId=:objId and rowId=:rowId and fieldId=:fieldId";
		Query query = session.createQuery(hql);
		query.setInteger("depId", depId);
		query.setInteger("groId", groId);
		query.setInteger("objId", objId);
		query.setInteger("fieldId", fieldId);
		query.setInteger("rowId", rowId);
		query.setString("value", value);
		query.executeUpdate(); 
		session.getTransaction().commit();
		session.close();
	}
	

	public static JSONArray getProjList(int depId, int groId, int objId){
		JSONArray array = new JSONArray();
		createSession();
		String hql;
		hql = "from Data where depId=:depId and groId=:groId and objId=:objId";
		Query query = session.createQuery(hql);
		query.setInteger("depId", depId);
		query.setInteger("groId", groId);
		query.setInteger("objId", objId);
		List <Data>list = query.list();
		Iterator<Data> iter = list.iterator();
		Data data = null;
		List<BasicProj> projList = new ArrayList();
		while(iter.hasNext()){
			data = iter.next();
			int target = -1;
			for(int i = 0; i < projList.size(); i++){
				if(projList.get(i).getId() == data.getRowId()){
					target = i;
					break;
				}
			}
			BasicProj proj;
			if(target > -1){
				proj = projList.get(target);
			}
			else{
				proj = new BasicProj();
				proj.setId(data.getRowId());
				projList.add(proj);
			}
			int fieldId = data.getFieldId();
			String val = data.getValue();
			if(fieldId == 1)
				proj.setName(val);
			else if(fieldId == 2)
				proj.setDescription(val);
			else if(fieldId == 3)
				proj.setStartDate(val);
			else if(fieldId == 4)
				proj.setStatus(ProjStatus.PROJ_STATUS[Integer.parseInt(val)]);
		}

		Iterator<BasicProj> iterProj = projList.iterator();
		while (iterProj.hasNext()) {
			array.add(iterProj.next());
		}					
		session.getTransaction().commit();
		session.close();
		return array;
	}
}