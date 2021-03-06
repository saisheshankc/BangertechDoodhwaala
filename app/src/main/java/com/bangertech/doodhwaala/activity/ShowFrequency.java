package com.bangertech.doodhwaala.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bangertech.doodhwaala.manager.AsyncResponse;
import com.bangertech.doodhwaala.manager.DialogManager;
import com.bangertech.doodhwaala.manager.MyAsynTaskManager;
import com.bangertech.doodhwaala.R;
import com.bangertech.doodhwaala.utils.AppUrlList;
import com.bangertech.doodhwaala.utils.CUtils;
import com.bangertech.doodhwaala.utils.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annutech on 10/12/2015.
 */
public class ShowFrequency  extends AppCompatActivity implements AsyncResponse {


    private RadioGroup radioGroupFrequency;
    private List<BeanFrequency> bucketFrequency=new ArrayList<BeanFrequency>();
    private LinearLayout llquantity;
    String previousValue="";
    String freqId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency);
        llquantity = (LinearLayout) findViewById(R.id.llquantity);
        radioGroupFrequency=(RadioGroup)findViewById(R.id.radioGroupFrequency);
        previousValue=getIntent().getStringExtra(ConstantVariables.SELECTED_USER_PLAN_KEY);
        if((!TextUtils.isEmpty(previousValue))) {

            try {
                JSONObject obj = new JSONObject(previousValue);
                freqId=obj.getString("frequency_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //CUtils.printLog("SELECTED_DETAIL",getIntent().getStringExtra("SELECTED_PRODUCT"), ConstantVariables.LOG_TYPE.ERROR);
        /*llquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size=radioGroupFrequency.getChildCount();
                int selectedIndex=-1;
                for(int i=0;i<size;i++)
                    selectedIndex=i;
                try {
                    JSONObject obj = new JSONObject(previousValue);
                    obj.put("frequency_id", bucketFrequency.get(selectedIndex).getFrequencyId());
                    obj.put("frequency_name", bucketFrequency.get(selectedIndex).getFrequencyName());
                    obj.put("frequency_days", bucketFrequency.get(selectedIndex).getNumberOfDays());

                    startActivity(new Intent(ShowFrequency.this, ShowQuantity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(ConstantVariables.SELECTED_USER_PLAN_KEY, obj.toString()));
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();
                } catch (Exception e) {

                }
            }
        });*/
        fetchFrequencyFromServer();

    }
    private void fetchFrequencyFromServer() {

        bucketFrequency.clear();
        MyAsynTaskManager myAsyncTask=new MyAsynTaskManager();
        myAsyncTask.delegate=this;
        myAsyncTask.setupParamsAndUrl("getFrequencies", ShowFrequency.this, AppUrlList.ACTION_URL,
                new String[]{"module", "action"},
                new String[]{"user", "listFrequencies"});
        myAsyncTask.execute();

    }


    @Override
    public void backgroundProcessFinish(String from, String output) {
        if(from.equalsIgnoreCase("getFrequencies"))
        {
            if (output != null) {
                parseFrequency(output);
            } else {
                DialogManager.showDialog(ShowFrequency.this, "Server Error Occurred! Try Again!");
            }

            //CUtils.printLog("FREQUENCY",output, ConstantVariables.LOG_TYPE.ERROR);
        }

    }
    private void parseFrequency(String addressList)
    {
       try {
            JSONObject jsonObject = new JSONObject(addressList);
            if(jsonObject.getBoolean("result"))
            {

                JSONArray array=jsonObject.getJSONArray("frequencies");
                BeanFrequency beanFrequencies;
                if(array.length()>0) {
                    JSONObject obj;
                    for(int i=0;i<array.length();i++) {
                        obj=array.getJSONObject(i);
                        if(obj!=null) {
                            beanFrequencies=new BeanFrequency();
                            beanFrequencies.setFrequencyId(obj.getString("frequency_id"));
                            beanFrequencies.setFrequencyName(obj.getString("frequency_name"));
                            beanFrequencies.setNumberOfDays(obj.getString("no_of_days"));
                            bucketFrequency.add(beanFrequencies);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
        if(bucketFrequency.size()>0)
            showFrequencyRadioButton();

    }

    private void showFrequencyRadioButton() {
        try {
            radioGroupFrequency.removeAllViews();
        }
        catch(Exception e)
        {

        }
        float density = getResources().getDisplayMetrics().density;
        RadioGroup.LayoutParams rprms;
        rprms= new RadioGroup.LayoutParams((int)(216*density),
                (int)(50*density));
        /*RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)radioGroupFrequency.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);*/
        int size = bucketFrequency.size();
        for (int index=0;index<size;index++) {

            RadioButton radioButton = new RadioButton(this);
            radioButton.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            radioButton.setPadding(10, 10, 10, 10);
            radioButton.setText(bucketFrequency.get(index).getFrequencyName());
            radioButton.setTag(index);

            radioGroupFrequency.addView(radioButton,index,rprms);
            if(!freqId.equals("")) {
                if (bucketFrequency.get(index).getFrequencyId().equals(freqId)) {
                    radioButton.setChecked(true);
                }
            }
        }

        //radioGroupFrequency.setLayoutParams(layoutParams);

    }

    class BeanFrequency
    {
        private String frequency_id;
        private String frequency_name="0";
        private String number_of_days="0";
        public String getFrequencyId() {
            return frequency_id;
        }

        public void setFrequencyId(String frequency_id) {
            this.frequency_id = frequency_id;
        }



        public String getFrequencyName() {
            return frequency_name;
        }

        public void setFrequencyName(String frequency_name) {
            this.frequency_name = frequency_name;
        }



        public String getNumberOfDays() {
            return number_of_days;
        }

        public void setNumberOfDays(String number_of_days) {
            this.number_of_days = number_of_days;
        }




    }
    public void gotoContinueFrequency(View view)
    {
        int size=radioGroupFrequency.getChildCount();
        int selectedIndex=-1;
        for(int i=0;i<size;i++)
            if(((RadioButton)radioGroupFrequency.getChildAt(i)).isChecked())
                selectedIndex=i;
        if(selectedIndex>-1) {
            try {
                JSONObject obj = new JSONObject(previousValue);
                obj.put("frequency_id", bucketFrequency.get(selectedIndex).getFrequencyId());
                obj.put("frequency_name", bucketFrequency.get(selectedIndex).getFrequencyName());
                obj.put("frequency_days", bucketFrequency.get(selectedIndex).getNumberOfDays());

                startActivity(new Intent(ShowFrequency.this, ShowDuration.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(ConstantVariables.SELECTED_USER_PLAN_KEY, obj.toString()));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } catch (Exception e) {

            }
        }
        else
            DialogManager.showDialog(ShowFrequency.this, "Please select frequency");
          //CUtils.showUserMessage(this,"Please select frequency");

    }

    @Override
    public void onBackPressed() {
        int size=radioGroupFrequency.getChildCount();
        int selectedIndex=-1;
        for(int i=0;i<size;i++)
            /*if(((RadioButton)radioGroupFrequency.getChildAt(i)).isChecked()) {
                selectedIndex = i;
                if (selectedIndex > -1) {
                    try {
                        JSONObject obj = new JSONObject(previousValue);
                        obj.put("frequency_id", bucketFrequency.get(selectedIndex).getFrequencyId());
                        obj.put("frequency_name", bucketFrequency.get(selectedIndex).getFrequencyName());
                        obj.put("frequency_days", bucketFrequency.get(selectedIndex).getNumberOfDays());

                        startActivity(new Intent(ShowFrequency.this, ShowQuantity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(ConstantVariables.SELECTED_USER_PLAN_KEY, obj.toString()));
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                        finish();
                    } catch (Exception e) {

                    }
                }
            } else*/ {
                selectedIndex=i;
                try {
                    JSONObject obj = new JSONObject(previousValue);
                    obj.put("frequency_id", "");
                    obj.put("frequency_name", bucketFrequency.get(selectedIndex).getFrequencyName());
                    obj.put("frequency_days", bucketFrequency.get(selectedIndex).getNumberOfDays());
                    startActivity(new Intent(ShowFrequency.this, ShowQuantity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(ConstantVariables.SELECTED_USER_PLAN_KEY, obj.toString()));
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();
                } catch (Exception e) {

                }
            }
        super.onBackPressed();
    }

}
