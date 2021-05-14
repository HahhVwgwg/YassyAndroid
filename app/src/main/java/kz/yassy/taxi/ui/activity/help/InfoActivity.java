package kz.yassy.taxi.ui.activity.help;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.HelpLocal;

public class InfoActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_info;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        RecyclerView recyclerView = findViewById(R.id.help_options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new HelpAdapter(getHelpOptions(0)));
    }

    private List<HelpLocal> getHelpOptions(int type) {
        List<HelpLocal> locals = new ArrayList<>();
        locals.add(new HelpLocal("О Приложении", 22));
        locals.add(new HelpLocal("Пользовательское соглашения", 23));
        locals.add(new HelpLocal("Политика конфеденциальности", 24));
        return locals;
    }

    @OnClick({R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }

    }
}