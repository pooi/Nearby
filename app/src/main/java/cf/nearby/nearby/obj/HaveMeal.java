package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 6..
 */

public class HaveMeal implements Serializable {

    String id, mainRecordId, patientId, type, description;
    long registeredDate;


    public HaveMeal(){

    }

    public HaveMeal(String data){

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
            if(keySet.contains("main_record_id")){
                mainRecordId = (String) temp.get("main_record_id");
            }
            if(keySet.contains("patient_id")){
                patientId = (String) temp.get("patient_id");
            }
            if(keySet.contains("type")){
                type = (String) temp.get("type");
            }
            if(keySet.contains("description")){
                description = (String) temp.get("description");
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean isEmpty(){
        if(type == null || "".equals(type)){
            return true;
        }else{
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainRecordId() {
        return mainRecordId;
    }

    public void setMainRecordId(String mainRecordId) {
        this.mainRecordId = mainRecordId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(long registeredDate) {
        this.registeredDate = registeredDate;
    }
}
