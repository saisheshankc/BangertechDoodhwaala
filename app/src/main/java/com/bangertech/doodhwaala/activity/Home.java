package com.bangertech.doodhwaala.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bangertech.doodhwaala.adapter.ViewPagerAdapter;
import com.bangertech.doodhwaala.application.DoodhwaalaApplication;
import com.bangertech.doodhwaala.beans.BeanBrand;
import com.bangertech.doodhwaala.beans.BeanDayPlan;
import com.bangertech.doodhwaala.beans.BeanProduct;
import com.bangertech.doodhwaala.beans.BeanProductType;
import com.bangertech.doodhwaala.customcontrols.CustomViewPager;
import com.bangertech.doodhwaala.customcontrols.SlidingTabLayout;
import com.bangertech.doodhwaala.fragment.MeFragment;
import com.bangertech.doodhwaala.fragment.MilkbarFragment;
import com.bangertech.doodhwaala.fragment.MyMilkFragment;
import com.bangertech.doodhwaala.gcm.GcmIntentService;
import com.bangertech.doodhwaala.general.General;
import com.bangertech.doodhwaala.manager.AsyncResponse;
import com.bangertech.doodhwaala.manager.DialogManager;
import com.bangertech.doodhwaala.manager.MyAsynTaskManager;
import com.bangertech.doodhwaala.R;
import com.bangertech.doodhwaala.manager.PreferenceManager;
import com.bangertech.doodhwaala.utils.AppUrlList;
import com.bangertech.doodhwaala.utils.CGlobal;
import com.bangertech.doodhwaala.utils.CUtils;
import com.bangertech.doodhwaala.utils.ConstantVariables;
import com.helpshift.D;
import com.helpshift.Helpshift;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annutech on 9/22/2015.
 */
public class Home extends AppCompatActivity implements AsyncResponse {
    public static CustomViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout slidingTabLayout;
    private List<BeanProductType> lstBeanProductType=new ArrayList<BeanProductType>();
    int Numboftabs =3;
    private Toolbar app_bar;
    public MenuItem menuItemSearch=null;
    private int MILK_BAR=0,MY_MILK=1,ME=2;
    private MeFragment meFragment;
    private MilkbarFragment milkbarFragment;
    private MyMilkFragment myMilkFragment;

    private MyFragmentPagerAdapter myPagerAdapter;

    private List<BeanProductType> lstBeanProductTypeOnToolBar=null;
    private LinearLayout llFilterMilkBarOnToolbar;
    private TextView txtViewMilkBarOnToolbarTitle;
    private HorizontalScrollView hsFilterMilkBarOnToolbar;
    private int pauseOrResumeIndex=-1;
    private Toolbar mToolbar;
    private General general;
    private String appVersionName;
    private Activity mActivity;
    public static Home newInstance() {

        return new Home();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(PreferenceManager.getInstance().getUserId()!=null) {
            mActivity = Home.this;
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            appVersionName = pInfo.versionName;

            // Toast.makeText(getApplicationContext(),"sai "+version,Toast.LENGTH_LONG).show();
            general = new General();
            lstBeanProductTypeOnToolBar=new ArrayList<BeanProductType>();
            //INSTALLING HELPSHIFT SDK TO USE IN CHAT SUPPORT
            Helpshift.install(getApplication(), ConstantVariables.ACCESS_KEY_HELP_SHIFT, "doodhwala.helpshift.com", ConstantVariables.APP_ID_HELP_SHIFT);
            //END
            setContentView(R.layout.lay_home);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            // setSupportActionBar(mToolbar);
            mToolbar.setTitle("");
            mToolbar.setPadding(0, CUtils.getStatusBarHeight(Home.this), 0, 0);
            //mToolbar.setMinimumHeight(CUtils.getStatusBarHeight(Home.this));
            mToolbar.setVisibility(View.GONE);

            //txtViewMilkBarOnToolbarTitle = (TextView) app_bar.findViewById(R.id.txtViewMilkBarOnToolbarTitle);
            llFilterMilkBarOnToolbar = (LinearLayout)findViewById(R.id.llFilterMilkBarOnToolbar);
            hsFilterMilkBarOnToolbar = (HorizontalScrollView) findViewById(R.id.hsFilterMilkBarOnToolbar);

            // txtViewMilkBarOnToolbarTitle.setText(R.string.app_name);

            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.

            myPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),getResources().getStringArray(R.array.home_tab_array));
            // Assigning ViewPager View and setting the adapter
            pager = (CustomViewPager) findViewById(R.id.pager);
            //pager.setAdapter(adapter);
            pager.setPagingEnabled(true);
            pager.setAdapter(myPagerAdapter);

            // Assigning the Sliding Tab Layout View
            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
            slidingTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width


            // Setting the ViewPager For the SlidingTabsLayout
            slidingTabLayout.setViewPager(pager);


            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    hideShowSearchOption(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            checkAppVersion();
        } else {
            Intent navigation = new Intent(this, LoginActivity.class);
            navigation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(navigation);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addFilterProductTypeOnToolBar(BeanProductType beanProductType)
    {
       lstBeanProductTypeOnToolBar.add(beanProductType);
        Toast.makeText(this, String.valueOf(lstBeanProductTypeOnToolBar.size()),Toast.LENGTH_SHORT).show();
        showFilterProductTypeOnToolBar();

    }
    public void gotoTagsAndProductsScreen(BeanProductType beanProductType)
    {
        Bundle  bundle=new Bundle();
        bundle.putString("tag_id", beanProductType.getTagId());
        bundle.putString("tag_name", beanProductType.getTagName());
        bundle.putString("tag_type", beanProductType.getTagType());
        Intent intent=new Intent(this, FilteresAppliedByTagsAndProducts.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

    }

    private void showFilterProductTypeOnToolBar()
    {

        int itemInBucket=lstBeanProductTypeOnToolBar.size();
        if((itemInBucket)==0)
           hsFilterMilkBarOnToolbar.setVisibility(View.GONE);
        if((itemInBucket)==1)
            hsFilterMilkBarOnToolbar.setVisibility(View.VISIBLE);

        if(itemInBucket>0) {
            int childCount=llFilterMilkBarOnToolbar.getChildCount();
            if(childCount>0)
            llFilterMilkBarOnToolbar.removeAllViews();

           /* int size=lstBeanProductTypeOnToolBar.size();*/
            int size=itemInBucket;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(int i =0;i<size;i++)
            {

               View view=inflater.inflate(R.layout.item_filter_toolbar_milk_bar, null);//txtViewFilterToobar
                TextView txtFilter=(TextView)view.findViewById(R.id.txtViewFilterToobar);
                txtFilter.setText(lstBeanProductTypeOnToolBar.get(i).getTagName());

                view.setTag(String.valueOf(i));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFilterProductTypeOnToolBar(Integer.parseInt(v.getTag().toString()));
                    }
                });
                llFilterMilkBarOnToolbar.addView(view,i);

              /*  TextView txtFilter=(TextView)inflater.inflate(R.layout.item_filter_toolbar_milk_bar, null);
                txtFilter.setText(lstBeanProductTypeOnToolBar.get(i).getTagName());

                txtFilter.setTag(String.valueOf(i));
                txtFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFilterProductTypeOnToolBar(Integer.parseInt(v.getTag().toString()));
                    }
                });
                llFilterMilkBarOnToolbar.addView(txtFilter,i);*/
            }

        }
       /* else
            hsFilterMilkBarOnToolbar.setVisibility(View.GONE);*/
    }

    private void removeFilterProductTypeOnToolBar(int position)
    {
        if(lstBeanProductTypeOnToolBar.size()>0) {
            BeanProductType beanProductType = lstBeanProductTypeOnToolBar.get(position);
            milkbarFragment.addFilterProductTypeAgainRemoveFromToolBar(beanProductType);
            lstBeanProductTypeOnToolBar.remove(position);
            showFilterProductTypeOnToolBar();
        }

        //addFilterProductTypeAgainRemoveFromToolBar
    }

    /**
     * This function is used to hide and show search option on action bar
     * @param position
     */
    private void hideShowSearchOption(int position)
    {
       if(position==MILK_BAR) {

       }
        if(position==MY_MILK) {
            MyMilkFragment.textViewDate.setText(MyMilkFragment.date_string);
            MyMilkFragment.textViewDayHeading.setVisibility(View.VISIBLE);
        }
        if(position==ME) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PreferenceManager.getInstance().getFlag()) {
           /* final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchMostSellingProducts();
                }
            }, 1000);*/
            fetchMostSellingProducts();
            PreferenceManager.getInstance().setFlag(false);
        }
        Home.this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //PreferenceManager.getInstance().setFlag(false);
        Home.this.unregisterReceiver(mMessageReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        menuItemSearch = menu.findItem(R.id.action_search);
        /*LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_menu_item_save, null);
       menuItemSearch.setActionView(view);*/
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //startActivity(new Intent(ShowAddress.this, AddUserAddress.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void backgroundProcessFinish(String from, final String output) {
        if(from.equalsIgnoreCase("checkAppVersion")){
            Log.i("AppVersionResult", output);
            if(output!=null) {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (jsonObject.getBoolean("result")) {
                        Log.i("AppVersionResult", "No need to update");
                        if (general.isNetworkAvailable(Home.this)) {
                            fetchMostSellingProducts();
                        } else {
                            DialogManager.showDialog(Home.this, "Please Check your internet connection.");
                        }
                    } else {
                        if(jsonObject.getInt("forceUpgrade") == 1) {
                            showDialogForceUpdate(mActivity, "Please upgrade to the latest version of Doodhwala to continue");
                        }
                        if(jsonObject.getInt("recommendUpgrade") == 1) {
                            showDialogRecommendedUpdate(mActivity, "A new version of Doodhwala is available please press OK to update?");
                        }

                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else {
                DialogManager.showDialog(mActivity, "Server Error Occurred! Try Again!");
            }
        }
        /*if(from.equalsIgnoreCase("fetchProductType")) {
            if (general.isNetworkAvailable(Home.this)) {
                if(!parseAndFormateProductTypeList(output))
                    lstBeanProductType.clear();
                fetchMostSellingProducts();
            } else {
                DialogManager.showDialog(Home.this, "Please Check your internet connection.");
            }

        }*/
        if(from.equalsIgnoreCase("mostSellingProducts"))
        {
            if (general.isNetworkAvailable(Home.this)) {
                if(output!=null)
                milkbarFragment.reDrawFragment(output);
                //fetchDayPlanFromServer("", 0);
            } else {
                DialogManager.showDialog(Home.this, "Please Check your internet connection.");
            }

        }
        if (from.equalsIgnoreCase("fetchDayPlan")) {
            if (general.isNetworkAvailable(Home.this)) {
                myMilkFragment.reDrawFragment(output);
            } else {
                DialogManager.showDialog(Home.this, "Please Check your internet connection.");
            }
        }

        if (from.equalsIgnoreCase("updatePauseOrResumePlan"))
        {
            if (general.isNetworkAvailable(Home.this)) {
                if(output!=null) {
                    try {

                        if ((new JSONObject(output)).getBoolean("result")) {
                            myMilkFragment.updatePauseOrResumePlanInList(pauseOrResumeIndex);

                        }
                    } catch (Exception e) {

                    }
                } else {
                    DialogManager.showDialog(Home.this, "Server Error Occurred! Try Again!");
                }
                pauseOrResumeIndex=-1;
            } else {
                DialogManager.showDialog(Home.this, "Please Check your internet connection.");
            }
            meFragment.reDrawFragment();
            meFragment.reProductType();
        }


    }


    public  class MyFragmentPagerAdapter extends FragmentPagerAdapter {

       List<Fragment> fragments;
        String[] _titles;

        public MyFragmentPagerAdapter(FragmentManager fragmentManager, String[] titles) {
            super(fragmentManager);
            _titles=titles;
            fragments=new ArrayList<Fragment>();
            Home.this.milkbarFragment=MilkbarFragment.newInstance();
            Home.this.myMilkFragment=MyMilkFragment.newInstance();
            Home.this.meFragment=MeFragment.newInstance();
            fragments.add( Home.this.milkbarFragment);
            fragments.add(Home.this.myMilkFragment);
            fragments.add(Home.this.meFragment);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }
      @Override
        public CharSequence getPageTitle(int position) {
            return _titles[ position];
        }

    }

    /*public void fetchProductType()
    {
        MyAsynTaskManager myAsyncTask=new MyAsynTaskManager();
        myAsyncTask.delegate=this;
        myAsyncTask.setupParamsAndUrl("fetchProductType", Home.this, AppUrlList.ACTION_URL,
                new String[]{"module", "action"},
                new String[]{"products", "fetchProductType"});
        myAsyncTask.execute();

    }*/
    private void fetchMostSellingProducts()
    {
        MyAsynTaskManager myAsyncTask=new MyAsynTaskManager();
        myAsyncTask.delegate=this;
        myAsyncTask.setupParamsAndUrl("mostSellingProducts", Home.this, AppUrlList.ACTION_URL,
                new String[]{"module", "action"},
                new String[]{"products", "mostSellingProducts"});
        myAsyncTask.execute();
    }
    public void fetchDayPlanFromServer(String strDate,int moveIndex)
    {
        MyAsynTaskManager myAsyncTask = new MyAsynTaskManager();
        myAsyncTask.delegate = this;
        myAsyncTask.setupParamsAndUrl("fetchDayPlan", Home.this, AppUrlList.ACTION_URL,
                new String[]{"module", "action","user_id","move","move_date"},
                new String[]{"plans", "fetchDayPlan", PreferenceManager.getInstance().getUserId(),String.valueOf(moveIndex),strDate});
        myAsyncTask.execute();
    }

    public void checkAppVersion() {
        MyAsynTaskManager myAsyncTask=new MyAsynTaskManager();
        myAsyncTask.delegate=this;
        myAsyncTask.setupParamsAndUrl("checkAppVersion", mActivity, AppUrlList.ACTION_URL,
                new String[]{"module", "action", "appVersion"},
                new String[]{"user", "checkAppVersion", appVersionName});
        myAsyncTask.execute();
    }

    public void fetchPreviousOrNextDayMyPlan(String strDate,int moveIndex)
    {
       /* String _date=CUtils.getFormatedDateForMyPlan(strDate,moveIndex);
        CUtils.printLog("MY_DATE",_date, ConstantVariables.LOG_TYPE.ERROR);
        if(TextUtils.isEmpty(_date))
            fetchDayPlanFromServer("",ConstantVariables.MY_PLAN_TOMORROW);
        else
            fetchDayPlanFromServer(_date,moveIndex);*/

        if(TextUtils.isEmpty(strDate)) {
            if (general.isNetworkAvailable(Home.this)) {
                fetchDayPlanFromServer("", ConstantVariables.MY_PLAN_TOMORROW);
            } else {
                DialogManager.showDialog(Home.this, "Please Check your internet connection.");
            }

        }
        else {
            if (general.isNetworkAvailable(Home.this)) {
                fetchDayPlanFromServer(strDate, moveIndex);
            } else {
                DialogManager.showDialog(Home.this, "Please Check your internet connection.");
            }
        }
    }

    public void updatePauseOrResumePlanOnServer(BeanDayPlan beanDayPlan,int prIndex)
    {
        pauseOrResumeIndex=prIndex;
        String action=beanDayPlan.isPaused()?"resumeUserPlan2":"pauseUserPlan3";
        MyAsynTaskManager myAsyncTask = new MyAsynTaskManager();
        myAsyncTask.delegate = this;
        myAsyncTask.setupParamsAndUrl("updatePauseOrResumePlan", Home.this, AppUrlList.ACTION_URL,
                new String[]{"module", "action","date_id"},
                new String[]{"plans", action, beanDayPlan.getDateId()});
        myAsyncTask.execute();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            //do other stuff here
            fetchMostSellingProducts();
        }
    };

    public void showDialogRecommendedUpdate(final Activity parentActivity, String message)
    {


//    	AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
//    	builder.setMessage(message)
//    		.setPositiveButton("Close", dialogClickListener)
//    	    .show();

        //customFont = Typeface.createFromAsset(parentActivity.getAssets(), "fonts/myriad.ttf");
        final Dialog dialog = new Dialog(parentActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_app_update_recommended_dialog);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialog.dismiss();
                }
                return true;
            }
        });

        TextView msgText=(TextView)dialog.findViewById(R.id.body);
        msgText.setText(message);
        Button btn_ok=(Button)dialog.findViewById(R.id.btn_ok);
        Button btn_cancel=(Button)dialog.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = parentActivity.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    parentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    parentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                parentActivity.finish();
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (general.isNetworkAvailable(Home.this)) {
                    fetchMostSellingProducts();
                } else {
                    DialogManager.showDialog(Home.this, "Please Check your internet connection.");
                }
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    public void showDialogForceUpdate(Activity parentActivity, String message)
    {

        //customFont = Typeface.createFromAsset(parentActivity.getAssets(), "fonts/myriad.ttf");
        final Dialog dialog = new Dialog(parentActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_app_update_force_dialog);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialog.dismiss();
                }
                return true;
            }
        });

        TextView msgText=(TextView)dialog.findViewById(R.id.body);
        msgText.setText(message);
        Button btn_ok=(Button)dialog.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private boolean parseAndFormateProductTypeList(String productTypeList)
    {
        boolean isSuccess=true;
        if(productTypeList!=null) {
            try {
                JSONObject jsonObject = new JSONObject(productTypeList);
                //if(jsonObject.getString("result").equalsIgnoreCase("true"))
                if (jsonObject.getBoolean("result")) {

                    lstBeanProductType.clear();
                    JSONArray array = jsonObject.getJSONArray("product_types");
                    if (array.length() > 0) {
                        BeanProductType beanProductType;
                        JSONObject obj;
                        for (int i = 0; i < array.length(); i++) {

                            obj = array.getJSONObject(i);
                            if (obj != null) {

                                beanProductType = new BeanProductType();
                                beanProductType.setTagId(obj.getString("tag_id"));
                                beanProductType.setTagName(obj.getString("tag_name"));
                                beanProductType.setTagType(obj.getString("tag_type"));
                                lstBeanProductType.add(beanProductType);

                            }
                        }

                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                isSuccess = false;
            }
        }

        return isSuccess;
    }
}
