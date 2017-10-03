package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 3..
 */

public class PatientMedicine {

    String id, title;
    long startDate, finishDate, registeredDate;
    ArrayList<PatientMedicineDetail> details;

    public PatientMedicine(){
        details = new ArrayList<>();
    }
    public PatientMedicine(String data){
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
            if(keySet.contains("title")){
                title = (String) temp.get("title");
            }
            if(keySet.contains("start_date")){
                startDate = Long.parseLong((String) temp.get("start_date"));
            }
            if(keySet.contains("finish_date")){
                finishDate = Long.parseLong((String) temp.get("finish_date"));
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void buildDetails(String data){

        details.clear();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                PatientMedicineDetail detail = new PatientMedicineDetail();
                detail.convert(temp);

                details.add(detail);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<PatientMedicine> getPatientMedicineList(String data){

        ArrayList<PatientMedicine> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                PatientMedicine pm = new PatientMedicine();
                pm.convert(temp);

                list.add(pm);

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(long finishDate) {
        this.finishDate = finishDate;
    }

    public String getPeriodString(){
        return AdditionalFunc.getDateString((long)startDate) + " ~ " + AdditionalFunc.getDateString((long)finishDate);
    }

    public long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(long registeredDate) {
        this.registeredDate = registeredDate;
    }

    public ArrayList<PatientMedicineDetail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<PatientMedicineDetail> details) {
        this.details = details;
    }
}
