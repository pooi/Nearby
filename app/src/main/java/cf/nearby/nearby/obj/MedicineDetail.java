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

public class MedicineDetail implements Serializable {

    String code, ingredient, classification, url;
    String name, imgUrl;

    public MedicineDetail(String code, String name, String imgUrl){
        this.code = code;
        this.name = name;
        this.imgUrl = imgUrl;
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

            if(keySet.contains("code")){
                code = (String) temp.get("code");
            }
            if(keySet.contains("ingredient")){
                ingredient = (String) temp.get("ingredient");
            }
            if(keySet.contains("classification")){
                classification = (String) temp.get("classification");
            }
            if(keySet.contains("url")){
                url = (String) temp.get("url");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getMedicineDetailInfo(MedicineDetail md, String data){

        md.build(data);

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
