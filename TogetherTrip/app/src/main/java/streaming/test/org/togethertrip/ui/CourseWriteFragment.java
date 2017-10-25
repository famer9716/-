package streaming.test.org.togethertrip.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.melnykov.fab.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import streaming.test.org.togethertrip.R;


/**
 * Created by taehyung on 2017-09-06.
 */

public class CourseWriteFragment extends Fragment {
    private static final String TAG = "CourseWriteFragment";
    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    Context context;
    Activity activity;
    ImageView imageView;
    Intent intent;
    EditText courseTitle;

    //CourseWrite 액티비티의 버튼
    Button okBtn;
    FloatingActionButton nextfab;

    String title;
//    Uri mUri;
    String imgUrl;
    Uri uri;
    DataSetListner mListner;


    String choice_category = "";
    Spinner spinner_category;
    CourseWriteFragment.SpinnerAdapter adspin1;
    final static String[] arrayLocation1 = {"카테고리1", "카테고리2", "카테고리3", "카테고리4"};


    public CourseWriteFragment() {}

    public CourseWriteFragment(Activity activity){
        this.context = activity;
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_course_write, container, false);

        imageView =(ImageView)view.findViewById(R.id.elbum);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        courseTitle = (EditText) view.findViewById(R.id.courseTitle);

        try{
            courseTitle.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //입력하기 전에
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        입력되는 텍스트에 변화가 있을때
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //입력이 끝났을 때
                        title = courseTitle.getText().toString();
                        ((DataSetListner)activity).FirstFragmentTitleSet(courseTitle.getText().toString());
                    }
                });
        }catch(Exception e){

        }

//        okBtn = (Button) activity.findViewById(R.id.okbtn);
//        nextfab = (FloatingActionButton) activity.findViewById(R.id.nextfab);
//
//        okBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    mListner.FirstFragmentDataSet(mUri, choice_category, title);
//                }catch(Exception e){
//
//                }
//
//            }
//        });
//
//        nextfab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //타이틀 입력 받기
//                courseTitle.addTextChangedListener(new TextWatcher() {
//
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                        //입력하기 전에
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
////                        입력되는 텍스트에 변화가 있을때
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        //입력이 끝났을 때
//                        title = courseTitle.getText().toString();
//                    }
//                });
//
//            }
//        });


        spinner_category = (Spinner) view.findViewById(R.id.categoryspinner);

        adspin1 = new CourseWriteFragment.SpinnerAdapter(activity, arrayLocation1, android.R.layout.simple_spinner_dropdown_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adspin1);
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adspin1.getItem(position).equals("카테고리1")){
                    choice_category = "카테고리1";
                    ((DataSetListner)activity).FirstFragmentCategorySet("카테고리1");
                }else if(adspin1.getItem(position).equals("카테고리2")){
                    choice_category = "카테고리2";
                    ((DataSetListner)activity).FirstFragmentCategorySet("카테고리2");
                }else if(adspin1.getItem(position).equals("카테고리3")){
                    choice_category = "카테고리3";
                    ((DataSetListner)activity).FirstFragmentCategorySet("카테고리3");
                }else if(adspin1.getItem(position).equals("카테고리4")){
                    choice_category = "카테고리4";
                    ((DataSetListner)activity).FirstFragmentCategorySet("카테고리4");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // 줄때
//        Fragment f = new Fragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("Obj", course1);   // Object 넘기기
//        f.setArguments(bundle);
/*
TODO 메롱
 */
        Toast.makeText(context, "김태형은 핵바보다", Toast.LENGTH_SHORT).show();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void pickImage(){
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //getActivity().startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }
    public void getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        this.imgUrl = imgPath;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    getImageNameToUri(data.getData());

                    uri = data.getData();
                    Glide.with(context).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (imgUrl == "") {
                    ((DataSetListner)activity).FirstFragmentImageSet(null);
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inSampleSize = 2; //얼마나 줄일지 설정하는 옵션 4--> 1/4로 줄이겠다

                    InputStream in = null; // here, you need to get your context.
                    try {
                        in = context.getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                        /*inputstream 형태로 받은 이미지로 부터 비트맵을 만들어 바이트 단위로 압축
                        그이우 스트림 배열에 담아서 전송합니다.
                         */

                    Bitmap bitmap = BitmapFactory.decodeStream(in, null, options); // InputStream 으로부터 Bitmap 을 만들어 준다.
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                    // 압축 옵션( JPEG, PNG ) , 품질 설정 ( 0 - 100까지의 int형 ), 압축된 바이트 배열을 담을 스트림
                    RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpg"), baos.toByteArray());

                    File photo = new File(imgUrl); // 가져온 파일의 이름을 알아내려고 사용합니다


                    // MultipartBody.Part 실제 파일의 이름을 보내기 위해 사용!!
                    ((DataSetListner)activity).FirstFragmentImageSet(MultipartBody.Part.createFormData("image", photo.getName(), photoBody));

                    Glide.with(context).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);

                }
            }
        }
    }
    public class SpinnerAdapter extends ArrayAdapter<String> {
        Context context;
        String[] items = new String[] {};

        public SpinnerAdapter(final Context context,
                              final String[] objects, final int textViewResourceId) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }

        /**
         * 스피너 클릭시 보여지는 View의 정의
         */
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setTextColor(Color.parseColor("#1E3790"));
            tv.setTextSize(12);
            tv.setHeight(50);
            return convertView;
        }

        /**
         * 기본 스피너 View 정의
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_item, parent, false);
            }

            TextView tv = (TextView) convertView
                    .findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setTextColor(Color.parseColor("#1E3790"));
            tv.setTextSize(12);
            return convertView;
        }
    }



    /*
    * TODO 프래그먼트 <-> 액티비티간 통신
    * 액티비티 내의 ok버튼을 CourseWriteFragment, CourseWriteFragment2에서 인지하고
    * Fragment들에 있는 데이터들을 합쳐 서버에 통신해야함 어려워 못하겠어 엉엉
    */


    public interface DataSetListner{
        void FirstFragmentImageSet(MultipartBody.Part image);
        void FirstFragmentCategorySet( String category);
        void FirstFragmentTitleSet(String title);
    }
    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            // 사용될 activity 에 context 정보 가져오는 부분
            this.activity = (Activity)context;
        }
    }



//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if(context instanceof DataSetListner){
//            mListner = (DataSetListner) context;
//        }else{
//            throw new RuntimeException(context.toString() + "must be implement DataSetListner");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListner = null;
//    }
}

