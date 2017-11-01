package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 29..
 */

public class MainRecord implements Serializable {

    long registeredDate;

    private String id, patientId, employeeId, locationId;

    String date;
    ArrayList<MainRecord> groupList;

    public MainRecord(){
        groupList = new ArrayList<>();
    }



    public static ArrayList<MainRecord> getMainRecordGroupingList(ArrayList<MainRecord> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.setRegisteredDate(AdditionalFunc.getNoTimeDateMs(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public static ArrayList<MainRecord> getTakeMedicineGroupingList(ArrayList<TakeMedicine> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public static ArrayList<MainRecord> getHaveMealGroupingList(ArrayList<HaveMeal> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public static ArrayList<MainRecord> getPatientRemarkGroupingList(ArrayList<PatientRemark> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public static ArrayList<MainRecord> getVitalSignGroupingList(ArrayList<VitalSign> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public void build(String data){

        try {
            // PHP에서 받아온 JSON 데이터를 JSON오브젝트로 변환
            JSONObject jObject = new JSONObject(data);
            // results라는 key는 JSON배열로 되어있다.
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);
                convert(temp);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void convert(JSONObject temp){


        ArrayList<String> keySet = AdditionalFunc.getKeySet(temp.keys());

        try {

            if(keySet.contains("id")){
                id = (String) temp.get("id");
            }
            if(keySet.contains("location_id")){
                locationId = (String) temp.get("location_id");
            }
            if(keySet.contains("patient_id")){
                patientId = (String) temp.get("patient_id");
            }
            if(keySet.contains("employee_id")){
                employeeId = (String) temp.get("employee_id");
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<MainRecord> getMainRecordList(String data){

        ArrayList<MainRecord> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                MainRecord mr = new MainRecord();
                mr.convert(temp);

                list.add(mr);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }

    public long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(long registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<MainRecord> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<MainRecord> groupList) {
        this.groupList = groupList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
