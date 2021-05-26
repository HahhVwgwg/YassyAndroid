package kz.yassy.taxi.ui.activity.help;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.HelpLocal;

public class HelpChildActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_help_child;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        RecyclerView recyclerView = findViewById(R.id.help_options);
        int type = 100;
        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getInt("type");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new HelpAdapter(getHelpOptions(type)));
    }

    private List<HelpLocal> getHelpOptions(int type) {
        List<HelpLocal> locals = new ArrayList<>();
        String title = "";
        if (type == 100) {
            title = "Что-то с деньгами";
            locals.add(new HelpLocal("Поездки не было", 2));
            locals.add(new HelpLocal("Заплатили дважды", 3));
            locals.add(new HelpLocal("Изменилась стоимость", 4));
            locals.add(new HelpLocal("Появился долг", 5));
            locals.add(new HelpLocal("Другая проблема", 6));
        } else if (type == 101) {
            title = "Хочу пожаловаться";
            locals.add(new HelpLocal("Непрофессиональный водитель", 7));
            locals.add(new HelpLocal("Проблема с автомобилем", 8));
            locals.add(new HelpLocal("Другая проблема", 9));
        } else if (type == 102) {
            title = "Всё сломалось";
            locals.add(new HelpLocal("Карта не срабатывает", 10));
            locals.add(new HelpLocal("Аккаунт не настраивается", 11));
            locals.add(new HelpLocal("Отчёт о поездке не приходит", 12));
            locals.add(new HelpLocal("Что-то ещё не работает", 13));
        } else if (type == 103) {
            title = "Частые вопросы";
            locals.add(new HelpLocal("Работа сервиса", 14));
            locals.add(new HelpLocal("Настройки приложения", 104));
            locals.add(new HelpLocal("Заказ и оплата поездки", 105));
            locals.add(new HelpLocal("Безопасность", 23));
            locals.add(new HelpLocal("Сотрудничество", 24));
        } else if (type == 104) {
            title = "Настройки приложения";
            locals.add(new HelpLocal("Добавить или удалить аккаунт", 15));
            locals.add(new HelpLocal("Добавить или удалить карту", 16));
            locals.add(new HelpLocal("Включить пуш-уведомления", 105));
            locals.add(new HelpLocal("Сменить язык в приложении", 17));
            locals.add(new HelpLocal("Удалить историю поездок", 18));
        } else if (type == 105) {
            title = "Заказ и оплата поездки";
            locals.add(new HelpLocal("Заказать поездку", 19));
            locals.add(new HelpLocal("Выбрать способ оплаты", 20));
            locals.add(new HelpLocal("Оставить чаевые водителю", 21));
            locals.add(new HelpLocal("Получить чек за поездку", 22));
        }
        ((TextView) findViewById(R.id.title)).setText(title);
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