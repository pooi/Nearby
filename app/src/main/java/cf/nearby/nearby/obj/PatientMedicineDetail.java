package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 3..
 */

public class PatientMedicineDetail implements Serializable {

    String id, description, time;
    double sd, ndd, tdd;
    Medicine medicine;

    public PatientMedicineDetail(){
        medicine = new Medicine();
    }
    public PatientMedicineDetail(String data){
        this();
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
            if(keySet.contains("description")){
                description = (String) temp.get("description");
            }
            if(keySet.contains("sd")){
                sd = Double.parseDouble((String) temp.get("sd"));
            }
            if(keySet.contains("ndd")){
                ndd = Double.parseDouble((String) temp.get("ndd"));
            }
            if(keySet.contains("tdd")){
                tdd = Double.parseDouble((String) temp.get("tdd"));
            }
            if(keySet.contains("time")){
                time = (String) temp.get("time");
            }


            medicine.convert(temp);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSd() {
        return sd;
    }

    public void setSd(double sd) {
        this.sd = sd;
    }

    public double getNdd() {
        return ndd;
    }

    public void setNdd(double ndd) {
        this.ndd = ndd;
    }

    public double getTdd() {
        return tdd;
    }

    public void setTdd(double tdd) {
        this.tdd = tdd;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
}
