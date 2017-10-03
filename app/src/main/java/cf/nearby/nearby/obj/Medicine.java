package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 3..
 */

public class Medicine {

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
