package cf.nearby.nearby.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import cf.nearby.nearby.obj.Patient;


/**
 * Created by tw on 2017-09-28.
 */

public class AdditionalFunc {

    public static String replaceNewLineString(String s){

        String str = s.replaceAll("\n", "\\\\n");
        return str;

    }

    public static long getMilliseconds(int year, int month, int day){

        long days = 0;

        try {
            String cdate = String.format("%d%02d%02d", year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(cdate);
            days = date.getTime();
            System.out.println(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;

    }

    public static long getMilliseconds(int year, int month, int day, int hour, int min, int sec){

        long days = 0;

        try {
            String cdate = String.format("%d%02d%02d %02d%02d%02d", year, month, day, hour, min, sec);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmmss");
            Date date = sdf.parse(cdate);
            days = date.getTime();
            System.out.println(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;

    }

    public static long getTodayMilliseconds(){
        Calendar now = Calendar.getInstance();
        return getMilliseconds(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DAY_OF_MONTH));
    }

    public static long getTodayMillisecondsWithTime(){
        Calendar now = Calendar.getInstance();
        return getMilliseconds(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DAY_OF_MONTH), now.HOUR, now.MINUTE, now.SECOND);
    }

    public static int getDday(long eTime){

        long cTime = System.currentTimeMillis();
        Date currentDate = new Date(cTime);
        Date finishDate = new Date(eTime);
//        System.out.println(cTime + ", " + eTime);

        DateFormat df = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(df.format(currentDate));
        int finishYear = Integer.parseInt(df.format(finishDate));
        df = new SimpleDateFormat("MM");
        int currentMonth = Integer.parseInt(df.format(currentDate));
        int finishMonth = Integer.parseInt(df.format(finishDate));
        df = new SimpleDateFormat("dd");
        int currentDay = Integer.parseInt(df.format(currentDate));
        int finishDay = Integer.parseInt(df.format(finishDate));

//        System.out.println(currentYear + ", " + currentMonth + ", " + currentDay);
//        System.out.println(finishYear + ", " + finishMonth + ", " + finishDay);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(currentYear, currentMonth, currentDay);
        end.set(finishYear, finishMonth, finishDay);

        Date startDate = start.getTime();
        Date endDate = end.getTime();

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);


        return (int)diffDays;
    }

    public static String getDateString(long time){

        Date currentDate = new Date(time);
        DateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일");
        return df.format(currentDate);

    }

    public static String getTimeString(long time){

        Date currentDate = new Date(time);
        DateFormat df = new SimpleDateFormat("HH시 mm분 ss초");
        return df.format(currentDate);

    }

    public static String getDateTimeSrtString(long time){

        Date currentDate = new Date(time);
        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
        return df.format(currentDate);

    }

    public static long getNoTimeDateMs(long time){

        Date currentDate = new Date(time);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dateStr = df.format(currentDate);
        String year = dateStr.substring(0, 4);
        String month = dateStr.substring(4, 6);
        String sec = dateStr.substring(6);

        return getMilliseconds(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(sec));
    }

    public static String parseDateString(String d, String t){

        String date = "";

        try {
            date = d.substring(0, 4) + "." + d.substring(4, 6) + "." + d.substring(6, 8) + " " + t.substring(0, 2) + ":" + t.substring(2, 4) + ":" + t.substring(4, 6);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return date;

    }

    public static ArrayList<String> stringToArrayList(String str){

        ArrayList<String> list = new ArrayList<>();

        for(String s : str.split(",")){
            if(!"".equals(s)){
                list.add(s);
            }
        }

        return list;
    }

    public static String[] arrayListToStringArray(ArrayList<String> list){
        String[] st = new String[list.size()];
        for(int i=0; i<list.size(); i++){
            st[i] = list.get(i);
        }
        return st;
    }

    public static String arrayListToString(ArrayList<String> list) {

        String str = "";
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i);
            if (i + 1 < list.size()) {
                str += ",";
            }
        }

        return str;

    }

    public static String integerArrayListToString(ArrayList<Integer> list){

        String str = "";
        for(int i=0; i<list.size(); i++){
            str += list.get(i);
            if(i+1<list.size()){
                str += ",";
            }
        }
        return str;
    }

    public static ArrayList<String> getKeySet(Iterator<String> iterator){

        ArrayList<String> list = new ArrayList<>();

        while (iterator.hasNext())
            list.add(iterator.next());

        return list;

    }





}
