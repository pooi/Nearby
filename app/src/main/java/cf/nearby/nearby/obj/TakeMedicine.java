package cf.nearby.nearby.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 6..
 */

public class TakeMedicine extends MainRecord implements Serializable {

    String id, mainRecordId, patientId, patientMedicineId, medicineId, title;
    PatientMedicine patientMedicine;
    Medicine medicine;
//    long registeredDate;


    public TakeMedicine(){
        medicine = new Medicine();
    }

    public TakeMedicine(String data){
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
            if(keySet.contains("main_record_id")){
                mainRecordId = (String) temp.get("main_record_id");
            }
            if(keySet.contains("patient_id")){
                patientId = (String) temp.get("patient_id");
            }
            if(keySet.contains("patient_medicine_id")){
                patientMedicineId = (String) temp.get("patient_medicine_id");
            }
            if(keySet.contains("medicine_id")){
                medicineId = (String) temp.get("medicine_id");
            }
            if(keySet.contains("registered_date")){
                registeredDate = Long.parseLong((String) temp.get("registered_date"));
            }
            if(keySet.contains("title")){
                title = (String) temp.get("title");
            }

            medicine.convert(temp);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<TakeMedicine> getTakeMedicineList(String data){

        ArrayList<TakeMedicine> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                TakeMedicine tm = new TakeMedicine();
                tm.convert(temp);

                list.add(tm);

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

    public String getPatientMedicineId() {
        return patientMedicineId;
    }

    public void setPatientMedicineId(String patientMedicineId) {
        this.patientMedicineId = patientMedicineId;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public PatientMedicine getPatientMedicine() {
        return patientMedicine;
    }

    public void setPatientMedicine(PatientMedicine patientMedicine) {
        this.patientMedicine = patientMedicine;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public long getRegisteredDate() {
//        return registeredDate;
//    }
//
//    public void setRegisteredDate(long registeredDate) {
//        this.registeredDate = registeredDate;
//    }
}
