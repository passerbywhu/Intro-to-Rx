package com.passerbywhu.introtorx;

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
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainActivity extends RxAppCompatActivity {
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
        closeBtn2 = item2.findViewById(R.id.del);
        closeBtn3 = item3.findViewById(R.id.del);
        close1ClickStream = RxView.clicks(closeBtn1).startWith(Observable.just(mVoid));
        refreshClickStream = RxView.clicks(refresh);
        requestStream = refreshClickStream.startWith(Observable.just(mVoid));
//        refreshClickStream.flatMap(new Func1() {
//            @Override
//            public Object call(Object o) {
//                return RetrofitUtils.getAPIService().getJingxuanCategory();
//            }
//        }).startWith(RetrofitUtils.getAPIService().getJingxuanCategory());
//        responseStream = RetrofitUtils.getAPIService().getJingxuanCategory();
        responseStream = requestStream.flatMap(new Func1<Void, Observable<KaResponse<List<JingXuanEntity>>>>() {
            @Override
            public Observable<KaResponse<List<JingXuanEntity>>> call(Void aVoid) {
                return RetrofitUtils.getAPIService().getJingxuanCategory();
            }
        });
//        suggestion1Stream = responseStream.map(new Func1<KaResponse<List<JingXuanEntity>>, JingXuanEntity>() {
//            @Override
//            public JingXuanEntity call(KaResponse<List<JingXuanEntity>> listKaResponse) {
//                return listKaResponse.info.get((int) Math.floor(Math.random() * listKaResponse.info.size()));
//            }
//        }).mergeWith(refreshClickStream.map(new Func1() {
//            @Override
//            public Object call(Object o) {
//                return null;
//            }
//        })).startWith(Observable.just(null));

        suggestion1Stream = Observable.combineLatest(close1ClickStream, responseStream, new Func2<Void, KaResponse<List<JingXuanEntity>>, JingXuanEntity>() {
            @Override
            public JingXuanEntity call(Void aVoid, KaResponse<List<JingXuanEntity>> listKaResponse) {
                return listKaResponse.info.get((int) Math.floor(Math.random() * listKaResponse.info.size()));
            }
        }).mergeWith(refreshClickStream.map(new Func1<Void, JingXuanEntity>() {
            @Override
            public JingXuanEntity call(Void aVoid) {
                return null;
            }
        })).startWith(Observable.just(null));
//        suggestion2Stream = responseStream.map(new Func1<KaResponse<List<JingXuanEntity>>, JingXuanEntity>() {
//            @Override
//            public JingXuanEntity call(KaResponse<List<JingXuanEntity>> listKaResponse) {
//                return listKaResponse.info.get((int) Math.floor(Math.random() * listKaResponse.info.size()));
//            }
//        });
//        suggestion3Stream = responseStream.map(new Func1<KaResponse<List<JingXuanEntity>>, JingXuanEntity>() {
//            @Override
//            public JingXuanEntity call(KaResponse<List<JingXuanEntity>> listKaResponse) {
//                return listKaResponse.info.get((int) Math.floor(Math.random() * listKaResponse.info.size()));
//            }
//        });
        suggestion1Stream.subscribe(new Action1<JingXuanEntity>() {
            @Override
            public void call(JingXuanEntity suggestion) {
                if (suggestion == null) {
                    Log.i("IntroToRx", "suggestion1 null");
                } else {
                    //show the first suggestion element and render the data;
                    Log.i("IntroToRx", "suggestion1 " + suggestion.getTopicName() + " " + suggestion.getDescription());
                }
            }
        });

        suggestion1Stream.subscribe(new Subscriber<JingXuanEntity>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.i("IntroToRx", e.toString());
            }

            @Override
            public void onNext(JingXuanEntity jingXuanEntity) {
                if (jingXuanEntity == null) {
                    Log.i("IntroToRx", "suggest null");
                } else {
                    Log.i("IntroToRx", "suggest " + jingXuanEntity.getTopicName() + " " + jingXuanEntity.getDescription());
                }
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
