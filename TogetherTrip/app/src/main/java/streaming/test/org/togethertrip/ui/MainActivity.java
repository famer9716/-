package streaming.test.org.togethertrip.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import streaming.test.org.togethertrip.R;
import streaming.test.org.togethertrip.application.ApplicationController;
import streaming.test.org.togethertrip.datas.login.LoginDatas;
import streaming.test.org.togethertrip.datas.login.LoginEchoResult;
import streaming.test.org.togethertrip.datas.login.LoginResult;
import streaming.test.org.togethertrip.network.NetworkService;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivityLog";
    Activity activity = this;
    static Context context;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    @BindViews({R.id.button_home, R.id.button_spot, R.id.button_course, R.id.button_alarm, R.id.button_mypage})
    List<Button> tabButtonList;
//    @BindView(R.id.tv_main) TextView tv_main;
//    @BindView(R.id.btn_map) Button btn_map;
//    @BindView(R.id.btn_search) Button btn_search;
//    @BindView(R.id.real_searchBtn) Button real_searchBtn;
//    @BindView(R.id.home_searchBtn) Button home_searchBtn;
//    @BindView(R.id.edit_search) EditText edit_search;

    AlarmFragment alarm;
    CourseFragment course;
    HomeFragment home;
    MypageFragment mypage;
    SpotFragment spot;

    String receivedEmail, receivedProfileImg, receivedUserNickName, token;

    LoginEchoResult loginEchoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //키보드 생성시 화면 밀림현상 없애기
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        context = this;

        //로그인시 데이터 받기
        Intent receivedIntent = getIntent();
        receivedEmail = receivedIntent.getStringExtra("email");
        receivedProfileImg = receivedIntent.getStringExtra("profileImg");
        receivedUserNickName = receivedIntent.getStringExtra("userNickName");
//        token = receivedIntent.getStringExtra("token");

        Intent intent = getIntent();

        int currentPosition = intent.getIntExtra("position",0);

        //Fragment 생성(검색시 필요한 닉네임을 생성자에 담아 전송)
        course = new CourseFragment(this, receivedUserNickName);
        home = new HomeFragment(this, receivedUserNickName);
        mypage = new MypageFragment(this, receivedEmail, receivedUserNickName, token);
        spot = new SpotFragment(this, receivedUserNickName);
        alarm = new AlarmFragment(this);

        //Fragment 추가
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        fragmentList.add(0, home);
        fragmentList.add(1, spot);
        fragmentList.add(2, course);
        fragmentList.add(3, alarm);
        fragmentList.add(4, mypage);

        //메인 viewPager 연결
        mViewPager = (ViewPager) findViewById(R.id.container);
        Log.d(TAG, "onCreate: mViewPager: " + mViewPager);
        Log.d(TAG, "onCreate: mSectionPagerAdapter: " + mSectionsPagerAdapter);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //페이지가 선택되었을 때
            @Override
            public void onPageSelected(int position) {
                tabSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //홈에서 로그인 유무 체크
        mypage.checkLogin();
        //마이페이지의 이메일이 null일 때 SharedPreferences에 담겨있는 로그인 정보를 받아와서 로그인
        //한 것으로 가정, 자동로그인이 안되어있을 시 빈 값을 가짐
        if(mypage.email==null) {
            SharedPreferences loginInfo = getSharedPreferences("loginSetting", 0);

            LoginDatas loginDatas = new LoginDatas();
            loginDatas.email = loginInfo.getString("email", "");
            loginDatas.password = loginInfo.getString("password", "");
            requestAutoSignin(loginDatas);
        }
    }

    //페이지 선택 확인을 위한 어댑터
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return fragmentList.get(position);
            } catch (Exception e) {
                return new HomeFragment();
            }
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    //아래 탭에서 홈 클릭시
    @OnClick(R.id.button_home)
    public void homeTab(View view) {
        mViewPager.setCurrentItem(0);
        tabSelect(0);


    }

    //아래 탭에서 관광지 선택시
    @OnClick(R.id.button_spot)
    public void spotTab(View view) {
        mViewPager.setCurrentItem(1);
        tabSelect(1);

    }

    //아래 탭에서 코스 선택시
    @OnClick(R.id.button_course)
    public void courseTab(View view) {
        mViewPager.setCurrentItem(2);
        tabSelect(2);

    }

    //아래 탭에서 알람 선택시
    @OnClick(R.id.button_alarm)
    public void alarmTab(View view) {
        mViewPager.setCurrentItem(3);
        tabSelect(3);

    }

    //아래 탭에서 마이페이지 선택시
    @OnClick(R.id.button_mypage)
    public void mypageTab(View view) {
        mViewPager.setCurrentItem(4);
        tabSelect(4);

    }

    /**
     * 페이지 이동에 따른 탭 버튼들의 이미지들에 대한 처리
     * @param page
     */
    public void tabSelect(int page){
        mViewPager.setCurrentItem(page);
        for(Button btn : tabButtonList){
            btn.setSelected(false);
            btn.setTextColor(Color.GRAY);

        }
        tabButtonList.get(page).setSelected(true);
        switch(page){
            case 0: //home
                tabButtonList.get(0).setBackgroundResource(R.drawable.navi_home_on);
                tabButtonList.get(1).setBackgroundResource(R.drawable.navi_trip_off);
                tabButtonList.get(2).setBackgroundResource(R.drawable.navi_course_off);
                tabButtonList.get(3).setBackgroundResource(R.drawable.navi_alrarm_off);
                tabButtonList.get(4).setBackgroundResource(R.drawable.navi_mypage_off);
                break;
            case 1: //spot
                tabButtonList.get(0).setBackgroundResource(R.drawable.navi_home_off);
                tabButtonList.get(1).setBackgroundResource(R.drawable.navi_trip_on);
                tabButtonList.get(2).setBackgroundResource(R.drawable.navi_course_off);
                tabButtonList.get(3).setBackgroundResource(R.drawable.navi_alrarm_off);
                tabButtonList.get(4).setBackgroundResource(R.drawable.navi_mypage_off);
                break;
            case 2: //course
                tabButtonList.get(0).setBackgroundResource(R.drawable.navi_home_off);
                tabButtonList.get(1).setBackgroundResource(R.drawable.navi_trip_off);
                tabButtonList.get(2).setBackgroundResource(R.drawable.navi_course_on);
                tabButtonList.get(3).setBackgroundResource(R.drawable.navi_alrarm_off);
                tabButtonList.get(4).setBackgroundResource(R.drawable.navi_mypage_off);
                break;
            case 3: //alarm
                tabButtonList.get(0).setBackgroundResource(R.drawable.navi_home_off);
                tabButtonList.get(1).setBackgroundResource(R.drawable.navi_trip_off);
                tabButtonList.get(2).setBackgroundResource(R.drawable.navi_course_off);
                tabButtonList.get(3).setBackgroundResource(R.drawable.navi_alrarm_on);
                tabButtonList.get(4).setBackgroundResource(R.drawable.navi_mypage_off);
                break;
            case 4: //mypage
                tabButtonList.get(0).setBackgroundResource(R.drawable.navi_home_off);
                tabButtonList.get(1).setBackgroundResource(R.drawable.navi_trip_off);
                tabButtonList.get(2).setBackgroundResource(R.drawable.navi_course_off);
                tabButtonList.get(3).setBackgroundResource(R.drawable.navi_alrarm_off);
                tabButtonList.get(4).setBackgroundResource(R.drawable.navi_mypage_on);
                break;

        }

    }

    public static Context getContext(){
        return context;
    }

    //로그인 네트워크
    public void requestAutoSignin(LoginDatas loginDatas){
        NetworkService networkService = ApplicationController.getInstance().getNetworkService();

        Call<LoginResult> requestLogin = networkService.requestSignin(loginDatas);
        requestLogin.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "reponse.body: " + response.body().message);
                    if (response.body().message.equals("ok")) { // 로그인 성공
                        loginEchoResult = response.body().result;
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("email", loginEchoResult.email);
                        intent.putExtra("password", loginEchoResult.password);
                        intent.putExtra("profileImg", loginEchoResult.img);
                        intent.putExtra("userNickName", loginEchoResult.userid);
                        intent.putExtra("token", loginEchoResult.token);

                        //기존에 쌓여있던 task를 모두 삭제 -> 액티비티가 중첩되어 쌓이는 것을 방지
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //TOP에 있는 액티비티 제거 -> 액티비티가 중첩되어 쌓이는 것을 방지
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//                        intent.putExtra("token", loginEchoResult.token);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "아이디 혹은 암호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
