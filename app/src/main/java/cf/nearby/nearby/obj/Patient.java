package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by ewon on 2017-10-01.
 */

public class Patient {
    String id, loginId, email, fn, ln, role, license, gender, address, zip, phone, pic, major, description;
    double startDate, dob, registeredDate, height, basicLivingAllowance;
    Location location;

    public Patient(){

        location = new Location();

    }

    public Patient (String data){
        this();
        build(data);
    }

    public boolean isEmpty(){
        if(id == null){
            return true;
        }
        return "".equals(id);
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
            }
            if (keySet.contains("login_id")) {
                loginId = (String) temp.get("login_id");
            }
            if (keySet.contains("email")) {
                email = (String) temp.get("email");
            }
            if (keySet.contains("first_name")) {
                fn = (String) temp.get("first_name");
            }
            if (keySet.contains("last_name")) {
                ln = (String) temp.get("last_name");
            }
            if (keySet.contains("role")) {
                role = (String) temp.get("role");
            }
            if (keySet.contains("license")) {
                license = (String) temp.get("license");
            }
            if (keySet.contains("gender")) {
                gender = (String) temp.get("gender");
            }
            if (keySet.contains("address")) {
                address = (String) temp.get("address");
            }
            if (keySet.contains("zip")) {
                zip = (String) temp.get("zip");
            }
            if (keySet.contains("phone")) {
                phone = (String) temp.get("phone");
            }
            if (keySet.contains("pic")) {
                pic = (String) temp.get("pic");
            }
            if (keySet.contains("major")) {
                major = (String) temp.get("major");
            }
            if (keySet.contains("description")) {
                description = (String) temp.get("description");
            }
            if (keySet.contains("start_date")) {
                startDate = Double.parseDouble((String) temp.get("start_date"));
            }
            if (keySet.contains("date_of_birth")) {
                dob = Double.parseDouble((String) temp.get("date_of_birth"));
            }
            if (keySet.contains("registered_date")) {
                registeredDate = Double.parseDouble((String) temp.get("registered_date"));
            }

            if (keySet.contains("height")) {
                height = Double.parseDouble((String) temp.get("height"));
            }
            if (keySet.contains("basic_living_allowance")) {
                basicLivingAllowance = Double.parseDouble((String) temp.get("basic_living_allowance"));
            }

            location.convert(temp);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
