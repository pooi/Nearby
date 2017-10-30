package cf.nearby.nearby.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017. 10. 29..
 */

public class MainRecord implements Serializable {

    long registeredDate;

    String date;
    ArrayList<MainRecord> groupList;

    public MainRecord(){
        groupList = new ArrayList<>();
    }


    public static ArrayList<MainRecord> getTakeMedicineGroupingList(ArrayList<TakeMedicine> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public static ArrayList<MainRecord> getHaveMealGroupingList(ArrayList<HaveMeal> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public static ArrayList<MainRecord> getPatientRemarkGroupingList(ArrayList<PatientRemark> records){

        ArrayList<MainRecord> list = new ArrayList<>();

        HashMap<String, MainRecord> indexing = new HashMap<>();

        for(int i=0; i<records.size(); i++){
            MainRecord record = records.get(i);
            String hash = Integer.toString(AdditionalFunc.getDday(record.getRegisteredDate()));

            if(indexing.keySet().contains(hash)){
                MainRecord groupRecord = indexing.get(hash);
                groupRecord.getGroupList().add(record);
            }else{
                MainRecord mainRecord = new MainRecord();
                mainRecord.setDate(AdditionalFunc.getDateString(record.getRegisteredDate()));
                mainRecord.getGroupList().add(record);
                indexing.put(hash, mainRecord);
            }

        }

        Object[] keys = indexing.keySet().toArray();
        Arrays.sort(keys);

        for(Object key : keys){
            list.add(indexing.get(key));
        }

        return list;

    }

    public long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(long registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<MainRecord> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<MainRecord> groupList) {
        this.groupList = groupList;
    }
}
