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
 * Created by tw on 2017. 10. 3..
 */

public class PatientWeight implements Serializable {

    String id;
    double weight;
    long registeredDate;

    public PatientWeight(){
    }
    public PatientWeight(String data){
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
            if(keySet.contains("weight")){
                weight = Double.parseDouble((String) temp.get("weight"));
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<PatientWeight> getPatientWeightList(String data){

        ArrayList<PatientWeight> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                PatientWeight pw = new PatientWeight();
                pw.convert(temp);

                list.add(pw);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }

    public static float getListMinValue(ArrayList<PatientWeight> list){
        double min = 10000000;
        for(PatientWeight weight : list){
            if(weight.getWeight() < min){
                min = weight.getWeight();
            }
        }
        return (float)min;
    }

    public static float getListMaxValue(ArrayList<PatientWeight> list){
        double max = -10000000;
        for(PatientWeight weight : list){
            if(weight.getWeight() > max){
                max = weight.getWeight();
            }
        }
        return (float)max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(long registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getRegisteredDateStringSrt(){
        Date currentDate = new Date(registeredDate);
        DateFormat df = new SimpleDateFormat("MM.dd");
        String str = df.format(currentDate);
//        str.replace("/", "\n");
        return str;
    }
}
