package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 11..
 */

public class VitalSign extends MainRecord implements Serializable {

    String id, patientId;
    Double bpMax, bpMin, pulse, temperature;
//    long registeredDate;

    public VitalSign(){

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
            if(keySet.contains("patient_id")){
                patientId = (String) temp.get("patient_id");
            }
            if(keySet.contains("blood_pressure_max")){
                String bp = (String) temp.get("blood_pressure_max");
                bpMax = "".equals(bp) ? 0 : Double.parseDouble((String) temp.get("blood_pressure_max"));
            }
            if(keySet.contains("blood_pressure_min")){
                String bp = (String) temp.get("blood_pressure_min");
                bpMin = "".equals(bp) ? 0 : Double.parseDouble((String) temp.get("blood_pressure_min"));
            }
            if(keySet.contains("pulse")){
                String pul = (String) temp.get("pulse");
                pulse = "".equals(pul) ? 0 : Double.parseDouble((String) temp.get("pulse"));
            }
            if(keySet.contains("temperature")){
                String tem = (String) temp.get("temperature");
                temperature = "".equals(tem) ? 0 : Double.parseDouble((String) temp.get("temperature"));
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<VitalSign> getVitalSignList(String data){

        ArrayList<VitalSign> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                VitalSign vs = new VitalSign();
                vs.convert(temp);

                list.add(vs);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }

    public boolean isEmpty(){
        return bpMax == null && bpMin == null && pulse == null && temperature == null;
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

    public Double getBpMax() {
        return bpMax;
    }

    public void setBpMax(Double bpMax) {
        this.bpMax = bpMax;
    }

    public Double getBpMin() {
        return bpMin;
    }

    public void setBpMin(Double bpMin) {
        this.bpMin = bpMin;
    }

    public Double getPulse() {
        return pulse;
    }

    public void setPulse(Double pulse) {
        this.pulse = pulse;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getRegisteredDateStringSrt(){
        Date currentDate = new Date(registeredDate);
        DateFormat df = new SimpleDateFormat("MM.dd");
        String str = df.format(currentDate);
//        str.replace("/", "\n");
        return str;
    }
//    public long getRegisteredDate() {
//        return registeredDate;
//    }
//
//    public void setRegisteredDate(long registeredDate) {
//        this.registeredDate = registeredDate;
//    }
}
