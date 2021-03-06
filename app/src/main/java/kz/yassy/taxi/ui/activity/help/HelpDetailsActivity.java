package kz.yassy.taxi.ui.activity.help;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;

public class HelpDetailsActivity extends BaseActivity {
    private Button button;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help_only_text;
    }

    @Override
    protected void initView() {
        int id = getIntent().getExtras().getInt("id");
        String[] mainData = findDataById(id);
        TextView mainText = findViewById(R.id.text);
        EditText comment = findViewById(R.id.comment);
        button = findViewById(R.id.submit);
        (findViewById(R.id.back_btn)).setOnClickListener(view -> {
            finish();
        });
        if (id >= 14 && id <= 27 && id != 24) {
            button.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            findViewById(R.id.container).setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.title)).setText(mainData[0]);
        mainText.setText(mainData[1]);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                button.setEnabled(editable.length() != 0);
            }
        });
    }

    private String[] findDataById(int id) {
        if (id == 0) {
            return new String[]{"В машине остались вещи", "Прошло меньше 2 дней. Звоните водителю — обычно они любезно соглашаются всё вернуть. Кнопка звонка есть в истории заказов, она работает 48 часов после поездки. " +
                    "\n\nПрошло больше 2 дней или не выходит договориться с водителем. Опишите забытую вещь, мы будем помогать всё вернуть."};
        } else if (id == 1) {
            return new String[]{"Экстренная ситуация", "Экстренная ситуация. Если вы попали в непредвиденную ситуацию, расскажите, что случилось, и мы с вами свяжемся. " +
                    "\n\nАвария. Если кому-то нужен врач, первым делом вызовите скорую помощь. Убедитесь, что водитель вызвал полицию, если нет — вызовите сами. Постарайтесь дождаться инспектора и проверьте, что он указал вас в протоколе как пострадавшего.Мы страхуем жизнь и здоровье пассажиров и водителей на случай ДТП. Каждому пострадавшему в ДТП пассажиру и водителю полагается компенсация. Чтобы получить компенсацию по страховке заполните форму. Мы расскажем, что делать, и поддержим на каждом этапе."};
        } else if (id == 2) {
            return new String[]{"Поездки не было", "Если заказ отменён, когда водитель уже приехал и ждёт вас, спишется стоимость платной отмены. То же произойдёт, если вы не вышли к машине в течение 10 минут.\n" +
                    "Если же водитель уехал с другим пассажиром или поездки не было, а деньги списались — расскажите нам, будем восстанавливать справедливость."};
        } else if (id == 3) {
            return new String[]{"Заплатили дважды", "Если заказ оформлен по карте и способ оплаты не менялся, деньги спишутся автоматически, отдавать водителю наличные не нужно.\n" +
                    "Если вы по ошибке заплатили наличными и картой, попробуйте связаться с водителем, кнопка звонка в течение суток доступна в истории заказов. Иногда банк может дважды оповестить вас о списании, потому что деньги сначала замораживаются на карте и только потом списываются. Проверить, что лишних списаний не было, вы можете в банковской выписке.\n" +
                    "У вас другой вопрос по оплате — напишите, пожалуйста, мы во всём разберёмся."};
        } else if (id == 4) {
            return new String[]{"Изменилась стоимость", "Сумма может увеличиться, если:\n" +
                    " • Не указать конечный адрес при оформлении заказа или поменять его в пути. В этом случае стоимость рассчитается по прибытии.\n" +
                    " • Добавилось платное ожидание. В каждый заказ включено несколько минут бесплатного ожидания. Когда они заканчиваются, стоимость поездки увеличивается в соответствии с тарифами.\n" +
                    " • В приложении настроены автоматические чаевые водителю.\n" +
                    "Если же произошла какая-то несправедливость с оплатой, расскажите нам — мы сделаем всё, чтобы помочь"};
        } else if (id == 5) {
            return new String[]{"Появился долг", "Чтобы погасить долг, вызовите машину с оплатой по карте. Введите адрес и нажмите Заказать — появится окно оплаты долга. Приложение может попросить ввести CVV-код.\n" +
                    "\n" +
                    "Непонятно, почему появился долг, — напишите нам, будем разбираться вместе."};
        } else if (id == 6) {
            return new String[]{"Другая проблема", "У водителя не оказалось сдачи? Какой-то другой вопрос по оплате? Дайте нам знать — мы обязательно поможем."};
        } else if (id == 7) {
            return new String[]{"Непрофессиональный водитель", "Если водитель повёл себя непрофессионально — поставьте ему справедливую оценку после поездки. Мы разбираемся с каждой оценкой и с каждым отзывом о работе водителей.\n" +
                    "Если вы подверглись опасности или произошло что-то из ряда вон выходящее — пожалуйста, напишите здесь. Мы сделаем всё, чтобы защитить вас и других пассажиров."};
        } else if (id == 8) {
            return new String[]{"Проблема с автомобилем", "Дайте знать, если машина не соответствовала ожиданиям."};
        } else if (id == 9) {
            return new String[]{"Другая проблема", "Не нашли свою проблему — напишите об этом здесь. Если вы увидели, что водитель с фирменными наклейками сервиса нарушает порядок или правила дорожного движения, — пожалуйста, опишите, что произошло. Нам поможет номер машины, время, место и любые детали происшествия."};
        } else if (id == 10) {
            return new String[]{"Карта не срабатывает", "Если инструкция не помогает или у вас другая проблема с картой — пишите, будем разбираться вместе."};
        } else if (id == 11) {
            return new String[]{"Аккаунт не настраивается", "Если что-то пошло не так - пишите нам, мы рядом."};
        } else if (id == 12) {
            return new String[]{"Отчёт о поездке не приходит", "Если вы всё сделали по инструкции, а отчет не пришел, напишите нам."};
        } else if (id == 13) {
            return new String[]{"Что-то ещё не работает", "Если у вас возникли проблемы с настройкой приложения или вы нашли ошибку, напишите об этом ниже."};
        } else if (id == 14) {
            //ToDo visibility off comment and button
            return new String[]{"Работа сервиса", "Яндекс.Такси — это сервис, который позволяет вызвать машину по выгодным тарифам без звонка оператору. Сделать заказ можно на сайте и через приложение для iOS (в том числе в Apple Watch), Android \n" +
                    "\n" +
                    "Преимущества сервиса:\n" +
                    " • Удобный способ вызова машины.\n" +
                    " • Быстрый поиск автомобиля с учётом пожеланий пассажира.\n" +
                    " • Выгодные тарифы вне зависимости от расстояния.\n" +
                    " • Предварительный расчёт стоимости поездки.\n" +
                    " • Оплата наличными или картой.\n" +
                    " • Подробная информация о водителе и автомобиле в приложении.\n" +
                    "\n" +
                    "Заказы выполняют наши партнёры. Для водителей предусмотрено специальное тестирование, а автомобили проходят проверку на соответствие стандартам качества.\n" +
                    "Чтобы подробнее ознакомиться с работой сервиса, изучите другие разделы."};
        } else if (id == 15) {
            //ToDo
            return new String[]{"Добавить или удалить аккаунт", "Вы можете создать несколько аккаунтов и переключаться между ними, например, в путешествиях. В каждом аккаунте можно использовать разные способы оплаты и промокоды, указать уникальные адреса.\n" +
                    "Чтобы создать первый аккаунт, подтвердите свой номер телефона.\n" +
                    "\n" +
                    "Как добавить новый аккаунт (на Android и iOS)\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, нажмите Выбрать другой аккаунт. \n" +
                    " 4.Укажите номер телефона и нажмите Далее — на этот номер придёт смс с кодом подтверждения. \n" +
                    " 5. Введите код и нажмите Готово (или Далее) — аккаунт станет активным.\n" +
                    "\n" +
                    "Как сменить аккаунт (на Android и iOS)\n" +
                    " 1.Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, выберите другой и нажмите Далее (или Продолжить). \n" +
                    "\n" +
                    "Как удалить аккаунт \n" +
                    "Android и iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Нажмите Выйти из профиля → Удалить аккаунт (или Выйти). \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее.\n" +
                    "\n" +
                    "iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона, затем ещё раз.\n" +
                    " 3. Нажмите  рядом с аккаунтом и подтвердите удаление. \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее"};
        } else if (id == 16) {
            //ToDo
            return new String[]{"Добавить или удалить карту", "Оплата картой действует при заказе через приложение для iOS (версия 8.0 и выше) и Android (версия 4.0 и выше).\n" +
                    "\n" +
                    "Как добавить карту\n" +
                    "Вы можете привязать к приложению до 5 банковских карт:\n" +
                    " 1. Откройте меню приложения  → Способы оплаты и нажмите Привязать карту (или Добавить карту). \n" +
                    " 2. Укажите номер карты, срок действия и код CVV. Номер карты можно ввести вручную или отсканировать с помощью камеры смартфона — для этого нажмите значок .\n" +
                    " 3. Ещё раз нажмите Привязать карту (или Добавить карту). \n" +
                    "В зависимости от типа карты, банка-эмитента и других условий проверка карты выполняется одним из следующих способов:\n" +
                    "\n" +
                    " 1. На счёте карты удерживается небольшая сумма. Зарезервированные средства не списываются и будут снова доступны после завершения проверки. \n" +
                    " 2. Приложение предлагает ввести сумму, зарезервированную на карте. Размер суммы можно узнать из смс (если у вас подключено смс-информирование от банка) либо из выписки по счёту на сайте или в приложении банка. \n" +
                    " 3. Придёт запрос на 3-D Secure верификацию карты на сайте банка-эмитента. Способ подтверждения зависит от вашего банка. После проверки вы вернётесь в приложение.\n" +
                    "\n" +
                    "Как удалить карту\n" +
                    " 1. Откройте меню приложения  → Способы оплаты.\n" +
                    " 2. Выберите карту и нажмите Удалить карту."};
        } else if (id == 17) {
            //ToDo
            return new String[]{"Сменить язык в приложении", "iOS\n" +
                    "1. Откройте настройки телефона → Настройки → Язык приложения\n" +
                    "2. Нажмите Язык приложения, выберите нужный, нажмите Изменить.\n" +
                    "\n" +
                    "Android\n" +
                    "1. Откройте настройки телефона → Настройки → Язык приложения\n" +
                    "2. Нажмите Язык приложения, выберите нужный, нажмите Изменить."};
        } else if (id == 18) {
            //ToDo
            return new String[]{"Удалить историю поездок", "Информация по каждому заказу доступна в меню приложения  → История заказов.\n" +
                    "В карточке заказа указаны маршрут, стоимость, дата и время поездки.\n" +
                    "\n" +
                    "Нажмите на карточку, чтобы увидеть больше информации:\n" +
                    " • Адреса посадки и высадки\n" +
                    " • Продолжительность поездки\n" +
                    " • Данные автомобиля\n" +
                    " • Данные водителя\n" +
                    " • Тариф и итоговую стоимость\n" +
                    "\n" +
                    "В истории заказов доступна кнопка Позвонить. Связаться с водителем, выполнившим заказ, можно в течение 24 часов после поездки и до оформления следующего заказа.\n" +
                    "\n" +
                    "Чтобы удалить поездку из истории:\n" +
                    " 1. Зайдите в меню приложения  → История заказов.\n" +
                    " 2. Выберите поездку, которую хотите удалить.\n" +
                    " 3. После информации о поездке нажмите Удалить поездку и выберите Удалить."};
        } else if (id == 19) {
            //ToDo
            return new String[]{"Заказать поездку", " 1. Введите адрес подачи машины. Если нужный адрес указан по умолчанию, нажмите Куда. Чтобы выбрать другой адрес, нажмите Изменить адрес или переместите точку на карте.\n" +
                    "\n" +
                    " 2. Укажите место назначения, тогда для поездки будет рассчитана предварительная стоимость. \n" +
                    "Вы можете добавить до 3 промежуточных точек (нажмите ). Изменить маршрут с промежуточными адресами можно только при оформлении заказа.\n" +
                    "\n" +
                    " 3. Выберите тариф и способ оплаты. \n" +
                    "После оформления заказа вы можете изменить оплату наличными на оплату картой. Если к приложению привязаны несколько карт, можно выбрать любую из них. Для этого в нижней части экрана потяните стрелочку меню с информацией вверх и измените способ оплаты.\n" +
                    "\n" +
                    " 4. Вы можете добавить дополнительную информацию. \n" +
                    "В «Пожеланиях» отметьте важные условия поездки — например, детское кресло или кондиционер. Некоторые пожелания являются платными и могут различаться в зависимости от региона.\n" +
                    "Там же вы можете добавить комментарий для водителя. Например, написать «Ждите меня около проходной», чтобы помочь водителю найти вас. \n" +
                    "\n" +
                    "5. Нажмите Заказать.\n" +
                    "\n" +
                    "После поездки поставьте водителю оценку. От рейтинга зависит, сможет ли он дальше получать заказы в сервисе.\n" +
                    "Чтобы отменить поездку, когда идёт поиск машины, нажмите в нижней части экрана Отменить поездку.\n" +
                    "Если машина уже назначена, потяните стрелочку меню с информацией об авто и водителе вверх — у вас откроется полное меню с деталями поездки, в нём нажмите Отменить поездку.\n" +
                    "Обратите внимание, что отмена заказа после подачи автомобиля будет платной"};
        } else if (id == 20) {
            //ToDo
            return new String[]{"Выбрать способ оплаты", "Оплачивать поездки можно наличными или банковской картой.\n" +
                    "\n" +
                    "Выберите способ оплаты:\n" +
                    " • Перед оформлением заказа. Откройте меню приложения  → Способ оплаты и выберите подходящий способ оплаты.\n" +
                    " • Во время оформления заказа. Нажмите на иконку оплаты, которая появится рядом с адресом отправления после того, как вы укажете конечный адрес.\n" +
                    "\n" +
                    "Если поездка уже началась, вы можете изменить оплату наличными на оплату картой, но не наоборот.\n" +
                    "Если вы выбрали оплату картой, но она заблокирована или на ней не хватает денег, способ оплаты может автоматически переключиться на наличные. Вам и водителю придёт сообщение об этом в приложении."};
        } else if (id == 21) {
            //ToDo
            return new String[]{"Оставить чаевые водителю", "Опция работает только при оплате поездки банковской картой.\n" +
                    "\n" +
                    "Если вам понравилась поездка, вы можете оставить водителю чаевые.\n" +
                    "\n" +
                    "Чтобы указать размер чаевых для всех поездок, откройте меню приложения  → Способ оплаты → Чаевые по умолчанию и выберите размер чаевых.\n" +
                    "\n" +
                    "Изменить размер чаевых можно через меню приложения или в конце поездки после того, как вы поставите водителю оценку.\n" +
                    "Чаевые спишутся автоматически, если в течение 30 минут после поездки вы поставили положительную оценку (или не поставили оценку) и нажали Готово.\n" +
                    "Чаевые не спишутся, если в течение 30 минут после завершения поездки вы поставили отрицательную оценку и нажали Готово.\n" +
                    "Если в течение 30 минут после поездки вы не нажали кнопку Готово, сумма чаевых будет заблокирована на 24 часа. За это время вы можете изменить размер чаевых по этой поездке — через сутки чаевые будут списаны в указанном размере.\n" +
                    "Если вы выбрали способ оплаты Apple Pay или Google Pay, чаевые оставить не получится. Также чаевые не сработают и в поездке с промокодом."};
        } else if (id == 22) {
            //ToDo
            return new String[]{"Получить чек за поездку", "Когда поездка завершилась, вы можете получить:\n" +
                    " • Отчёт\n" +
                    " • Квитанцию об оплате\n" +
                    "\n" +
                    "Чтобы всегда получать отчёты по поездкам, добавьте свою почту в приложении. Вам придёт письмо со ссылкой, по которой нужно перейти и подтвердить подписку на отчёты. Теперь они будут приходить автоматически после каждой поездки.\n" +
                    "Квитанцию об оплате выдаёт водитель по вашей просьбе. Если поездка закончилась и вы не взяли квитанцию, можете запросить её в таксопарке."};
        } else if (id == 23) {
            //ToDo
            return new String[]{"Безопасность", "Безопасность пользователей — главная задача сервиса. Мы постоянно создаём новые инструменты безопасности и следим за тем, чтобы все требования были соблюдены."};
        } else if (id == 24) {
            return new String[]{"Сотрудничество", "Если вы хотите сотрудничать с Ogogo.Такси — например, сделать совместный проект — напишите об этом на taxipartners@ogogo-team.kr. Мы рассматриваем все идеи и предложения, которые вы отправляете.\n" +
                    "\n" +
                    "Стать корпоративным клиентом\n" +
                    "Чтобы стать корпоративным клиентом или узнать подробности, заполните форму. Мы свяжемся с вами в ближайшее время."};
        } else if (id == 25) {
            //ToDO
            return new String[]{"О Приложении", "Вы можете создать несколько аккаунтов и переключаться между ними, например, в путешествиях. В каждом аккаунте можно использовать разные способы оплаты и промокоды, указать уникальные адреса.\n" +
                    "Чтобы создать первый аккаунт, подтвердите свой номер телефона.\n" +
                    "\n" +
                    "Как добавить новый аккаунт (на Android и iOS)\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, нажмите Выбрать другой аккаунт. \n" +
                    " 4.Укажите номер телефона и нажмите Далее — на этот номер придёт смс с кодом подтверждения. \n" +
                    " 5. Введите код и нажмите Готово (или Далее) — аккаунт станет активным.\n" +
                    "\n" +
                    "Как сменить аккаунт (на Android и iOS)\n" +
                    " 1.Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, выберите другой и нажмите Далее (или Продолжить). \n" +
                    "\n" +
                    "Как удалить аккаунт \n" +
                    "Android и iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Нажмите Выйти из профиля → Удалить аккаунт (или Выйти). \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее.\n" +
                    "\n" +
                    "iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона, затем ещё раз.\n" +
                    " 3. Нажмите  рядом с аккаунтом и подтвердите удаление. \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее"};
        } else if (id == 26) {
            //ToDo
            return new String[]{"Пользовательское соглашения", "Вы можете создать несколько аккаунтов и переключаться между ними, например, в путешествиях. В каждом аккаунте можно использовать разные способы оплаты и промокоды, указать уникальные адреса.\n" +
                    "Чтобы создать первый аккаунт, подтвердите свой номер телефона.\n" +
                    "\n" +
                    "Как добавить новый аккаунт (на Android и iOS)\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, нажмите Выбрать другой аккаунт. \n" +
                    " 4.Укажите номер телефона и нажмите Далее — на этот номер придёт смс с кодом подтверждения. \n" +
                    " 5. Введите код и нажмите Готово (или Далее) — аккаунт станет активным.\n" +
                    "\n" +
                    "Как сменить аккаунт (на Android и iOS)\n" +
                    " 1.Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, выберите другой и нажмите Далее (или Продолжить). \n" +
                    "\n" +
                    "Как удалить аккаунт \n" +
                    "Android и iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Нажмите Выйти из профиля → Удалить аккаунт (или Выйти). \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее.\n" +
                    "\n" +
                    "iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона, затем ещё раз.\n" +
                    " 3. Нажмите  рядом с аккаунтом и подтвердите удаление. \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее"};
        } else if (id == 27) {
            //ToDo
            return new String[]{"Политика конфеденциальности", "Вы можете создать несколько аккаунтов и переключаться между ними, например, в путешествиях. В каждом аккаунте можно использовать разные способы оплаты и промокоды, указать уникальные адреса.\n" +
                    "Чтобы создать первый аккаунт, подтвердите свой номер телефона.\n" +
                    "\n" +
                    "Как добавить новый аккаунт (на Android и iOS)\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, нажмите Выбрать другой аккаунт. \n" +
                    " 4.Укажите номер телефона и нажмите Далее — на этот номер придёт смс с кодом подтверждения. \n" +
                    " 5. Введите код и нажмите Готово (или Далее) — аккаунт станет активным.\n" +
                    "\n" +
                    "Как сменить аккаунт (на Android и iOS)\n" +
                    " 1.Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Сдвиньте аккаунт вправо или влево, выберите другой и нажмите Далее (или Продолжить). \n" +
                    "\n" +
                    "Как удалить аккаунт \n" +
                    "Android и iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона.\n" +
                    " 3. Нажмите Выйти из профиля → Удалить аккаунт (или Выйти). \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее.\n" +
                    "\n" +
                    "iOS:\n" +
                    " 1. Откройте меню приложения .\n" +
                    " 2. Нажмите на номер телефона, затем ещё раз.\n" +
                    " 3. Нажмите  рядом с аккаунтом и подтвердите удаление. \n" +
                    " 4. Выберите другой аккаунт и нажмите Далее"};
        }
        return new String[]{"", ""};
    }

}
