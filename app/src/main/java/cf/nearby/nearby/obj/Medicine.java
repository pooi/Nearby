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

public class Medicine implements Serializable {

    String id, type, code, name, company, standard, unit;

    public Medicine(){

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

            if(keySet.contains("medicine_id")){
                id = (String) temp.get("medicine_id");
            }
            if(keySet.contains("medicine_type")){
                type = (String) temp.get("medicine_type");
            }
            if(keySet.contains("medicine_code")){
                code = (String) temp.get("medicine_code");
            }
            if(keySet.contains("medicine_name")){
                name = (String) temp.get("medicine_name");
            }
            if(keySet.contains("medicine_company")){
                company = (String) temp.get("medicine_company");
            }
            if(keySet.contains("medicine_standard")){
                standard = (String) temp.get("medicine_standard");
            }
            if(keySet.contains("medicine_unit")){
                unit = (String) temp.get("medicine_unit");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void convertSrt(JSONObject temp){


        ArrayList<String> keySet = AdditionalFunc.getKeySet(temp.keys());

        try {

            if(keySet.contains("id")){
                id = (String) temp.get("id");
            }
            if(keySet.contains("type")){
                type = (String) temp.get("type");
            }
            if(keySet.contains("code")){
                code = (String) temp.get("code");
            }
            if(keySet.contains("name")){
                name = (String) temp.get("name");
            }
            if(keySet.contains("company")){
                company = (String) temp.get("company");
            }
            if(keySet.contains("standard")){
                standard = (String) temp.get("standard");
            }
            if(keySet.contains("unit")){
                unit = (String) temp.get("unit");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<Medicine> getMedicineList(String data){

        ArrayList<Medicine> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                Medicine medicine = new Medicine();
                medicine.convertSrt(temp);

                list.add(medicine);

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getNameSrt(){

        String srtName = name;
        if(name.length() > 10)
            srtName = name.substring(0, 8) + "...";
        return srtName;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
