package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 9. 30..
 */

public class Location {

    String id, name, pic, director, major, phone, url;
    int capacity, constructionYear;

    public Location(){

    }

    public Location(String data){
        build(data);
    }

    public boolean isEmpty(){
        if(id == null){
            return true;
        }
        return !"".equals(id);
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

            if (keySet.contains("id")) {
                id = (String) temp.get("id");
            }else if(keySet.contains("location_id")){
                id = (String) temp.get("location_id");
            }
            if (keySet.contains("name")) {
                name = (String) temp.get("name");
            }else if(keySet.contains("location_name")){
                name = (String) temp.get("location_name");
            }
            if (keySet.contains("pic")) {
                pic = (String) temp.get("pic");
            }else if(keySet.contains("location_pic")){
                pic = (String) temp.get("location_pic");
            }
            if (keySet.contains("director")) {
                director = (String) temp.get("director");
            }else if(keySet.contains("location_director")){
                director = (String) temp.get("location_director");
            }
            if (keySet.contains("major")) {
                major = (String) temp.get("major");
            }else if(keySet.contains("location_major")){
                major = (String) temp.get("location_major");
            }
            if (keySet.contains("phone")) {
                phone = (String) temp.get("phone");
            }else if(keySet.contains("location_phone")){
                phone = (String) temp.get("location_phone");
            }
            if (keySet.contains("url")) {
                url = (String) temp.get("url");
            }else if(keySet.contains("location_url")){
                url = (String) temp.get("location_url");
            }
            if (keySet.contains("capacity")) {
                capacity = Integer.parseInt((String) temp.get("capacity"));
            }else if(keySet.contains("location_capacity")){
                capacity = Integer.parseInt((String) temp.get("location_capacity"));
            }
            if (keySet.contains("construction_year")) {
                constructionYear = Integer.parseInt((String) temp.get("construction_year"));
            }else if(keySet.contains("location_construction_year")){
                constructionYear = Integer.parseInt((String) temp.get("location_construction_year"));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getConstructionYear() {
        return constructionYear;
    }

    public void setConstructionYear(int constructionYear) {
        this.constructionYear = constructionYear;
    }
}
