package cf.nearby.nearby.util;

import android.content.Context;

import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.Employee;
import cf.nearby.nearby.obj.Patient;

/**
 * Created by tw on 2017. 10. 17..
 */

public class LogManager {

    public final String TYPE_WEIGHT = "weight";

    Context context;

    Patient patient;
    String type, msg;

    public LogManager(Context context){
        this.context = context;
    }

    public LogManager buildWeightMsg(Patient patient, String weight){

        this.type = TYPE_WEIGHT;
        this.patient = patient;
        this.msg = String.format(context.getString(R.string.log_add_weight), patient.getName(), weight);


        return this;
    }

    public void record(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "addLog");
        map.put("location_id", StartActivity.employee.getLocation().getId());
        map.put("employee_id", StartActivity.employee.getId());
        if(patient != null)
            map.put("patient_id", patient.getId());
        map.put("type", type);
        map.put("msg", msg);

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {

                System.out.println(data);

            }
        }.start();

    }


}
