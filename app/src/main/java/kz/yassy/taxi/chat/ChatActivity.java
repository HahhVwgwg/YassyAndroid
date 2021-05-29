package kz.yassy.taxi.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.APIClient;

import static kz.yassy.taxi.MvpApplication.DATUM;

public class ChatActivity extends BaseActivity {

    public static String chatPath = null;
    public static String sender = "app";

    @BindView(R.id.back_btn)
    View back;
    @BindView(R.id.chat_lv)
    ListView chatLv;
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.send)
    ImageView send;
    @BindView(R.id.chat_controls_layout)
    LinearLayout chatControlsLayout;

    private ChatMessageAdapter mAdapter;
    private DatabaseReference myRef;
    private CompositeDisposable mCompositeDisposable;
    private int notificationId = 1200;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    public static void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(400);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mCompositeDisposable = new CompositeDisposable();
        Log.e("inafalkf", "sadfaf");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            chatPath = extras.getString("request_id", "yassy-taxi-default-rtdb");
            initChatView(chatPath);
        }
    }

    private void initChatView(String chatPath) {
        System.out.println("RRR chatPath = " + chatPath);
        if (chatPath == null) return;

        message.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String myText = message.getText().toString().trim();
                if (myText.length() > 0) sendMessage(myText);
                handled = true;
            }
            return handled;
        });
        Log.e("Chapting", ">>>>>>>>>>>>>>>>>>>");
        mAdapter = new ChatMessageAdapter(baseActivity(), new ArrayList<>());
        chatLv.setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(chatPath)/*.child(chatPath).child("chat")*/;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0)
                    message.setText("Здраствуйте,");
                Log.e("Chapting", snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                assert chat != null;
                if (chat.getSender() != null && chat.getRead() != null) {
                    if (!chat.getSender().equals(sender) && chat.getRead() == 0) {
                        chat.setRead(1);
                        dataSnapshot.getRef().setValue(chat);
                    }
                    mAdapter.add(chat);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @OnClick({R.id.send, R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send:
                String myText = message.getText().toString();
                if (myText.length() > 0) sendMessage(myText);
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void sendMessage(String messageStr) {
        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setTimestamp(new Date().getTime());
        chat.setType("text");
        chat.setText(messageStr);
        chat.setRead(0);
        chat.setDriverId(1212);
        chat.setUserId(12121);
        myRef.push().setValue(chat);
        message.setText("");
        try {
            mCompositeDisposable.add(APIClient
                    .getAPIClient()
                    .postChatItem("app", String.valueOf(DATUM.getCurrentProviderId()), messageStr)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> System.out.println("RRR o = " + o)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
//        MvpApplication.isChatScreenOpen = false;
    }

    @Override
    public void onResume() {
        super.onResume();
//        MvpApplication.isChatScreenOpen = true;
        NotificationManager mManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.cancelAll();
        cancelNotification(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MvpApplication.isChatScreenOpen = false;
    }
}
