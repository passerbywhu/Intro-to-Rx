package com.passerbywhu.introtorx;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity {
    private static final String TAG = "IntroToRx";
    private View item1;
    private View item2;
    private View item3;
    private Button refresh;
    private Observable refreshClickStream;
    private Observable responseStream;
    private Observable requestStream;
    private Observable suggestion1Stream;
    private Observable suggestion2Stream;
    private Observable suggestion3Stream;
    private View closeBtn1;
    private TextView desc1;
    private ImageView icon1;
    private View closeBtn2;
    private View closeBtn3;
    private Observable close1ClickStream;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh = (Button) findViewById(R.id.refresh);
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);

        Void mVoid = null;
        closeBtn1 = item1.findViewById(R.id.del);
        icon1 = (ImageView) item1.findViewById(R.id.icon);
        desc1 = (TextView) item1.findViewById(R.id.desc);
        closeBtn2 = item2.findViewById(R.id.del);
        closeBtn3 = item3.findViewById(R.id.del);
        close1ClickStream = RxView.clicks(closeBtn1).startWith(Observable.just(mVoid));
        refreshClickStream = RxView.clicks(refresh);
        requestStream = refreshClickStream.startWith(Observable.just(mVoid));
        responseStream = requestStream.observeOn(Schedulers.io()).flatMap(new Func1<Void, Observable<KaResponse<List<JingXuanEntity>>>>() {
            @Override
            public Observable<KaResponse<List<JingXuanEntity>>> call(Void aVoid) {
                return RetrofitUtils.getAPIService().getJingxuanCategory();
            }
        }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Func1<KaResponse<List<JingXuanEntity>>, Observable<List<JingXuanEntity>>>() {
            @Override
            public Observable<List<JingXuanEntity>> call(KaResponse<List<JingXuanEntity>> listKaResponse) {
                return Observable.just(listKaResponse.info);
            }
        });

        suggestion1Stream = Observable.combineLatest(close1ClickStream, responseStream, new Func2<Void, List<JingXuanEntity>, JingXuanEntity>() {
            @Override
            public JingXuanEntity call(Void aVoid, List<JingXuanEntity> list) {
                return list.get((int) Math.floor(Math.random() * list.size()));
            }
        }).mergeWith(refreshClickStream.map(new Func1<Void, JingXuanEntity>() {
            @Override
            public JingXuanEntity call(Void aVoid) {
                return null;
            }
        })).startWith(Observable.just(null));
        suggestion1Stream.subscribe(new Action1<JingXuanEntity>() {
            @Override
            public void call(JingXuanEntity jingXuanEntity) {
                if (jingXuanEntity == null) {
                    Log.i(TAG, "suggestion1 null");
                } else {
                    ImageLoader.getInstance().loadImage(jingXuanEntity.getIconUrl(), icon1);
                    desc1.setText(jingXuanEntity.getTopicName());
                    Log.i(TAG, "suggestion1 " + jingXuanEntity.getTopicName() + " " + jingXuanEntity.getDescription());
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    void renderItem(View view) {
        ImageView icon;
        TextView desc;
        TextView del;
        if (view.getTag() == null) {
            icon = (ImageView) view.findViewById(R.id.icon);
            desc = (TextView) view.findViewById(R.id.desc);
            del = (TextView) view.findViewById(R.id.del);
            Map map = new HashMap();
            map.put(R.id.icon, icon);
            map.put(R.id.desc, desc);
            map.put(R.id.del, del);
            view.setTag(map);
        } else {
            Map map = (Map) view.getTag();
            icon = (ImageView) map.get(R.id.icon);
            desc = (TextView) map.get(R.id.desc);
            del = (TextView) map.get(R.id.del);
        }
    }

    private void onDelClicked() {

    }
}
