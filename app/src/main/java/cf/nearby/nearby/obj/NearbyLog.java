package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 11. 19..
 */

public class NearbyLog {

    String id, locationId, employeeId, patientId, type, msg;
    Long registeredDate;

    public NearbyLog(){

    }

    public NearbyLog(String data){
        build(data);
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
            if(keySet.contains("type")){
                type = (String) temp.get("type");
            }
            if(keySet.contains("msg")){
                msg = (String) temp.get("msg");
            }
            if(keySet.contains("location_id")){
                locationId = (String) temp.get("location_id");
            }
            if(keySet.contains("employee_id")){
                employeeId = (String) temp.get("employee_id");
            }
            if(keySet.contains("patient_id")){
                patientId = (String) temp.get("patient_id");
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<NearbyLog> getLogList(String data){

        ArrayList<NearbyLog> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                NearbyLog log = new NearbyLog();
                log.convert(temp);

                list.add(log);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Long registeredDate) {
        this.registeredDate = registeredDate;
    }
}
