package kz.yassy.taxi.ui.activity.add_card;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.santalu.maskedittext.MaskEditText;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;

public class AddCardActivityMine extends BaseActivity implements AddCardIView {

    @BindView(R.id.card_number)
    MaskEditText cardNumber;
    @BindView(R.id.card_name)
    EditText cardName;
    @BindView(R.id.card_expire)
    MaskEditText cardExpire;
    @BindView(R.id.card_cvv)
    EditText cardCvv;
    @BindView(R.id.submit)
    Button submit;

    private AddCardPresenter<AddCardActivityMine> presenter = new AddCardPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_cared;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime

//        cardForm.cardRequired(true)
//                .expirationRequired(true)
//                .cvvRequired(true)
//                .postalCodeRequired(false)
//                .mobileNumberRequired(false)
//                .actionLabel(getString(R.string.add_card_details))
//                .setup(this);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {

        if (isEmpty(cardNumber)) {
            Toast.makeText(this, getString(R.string.please_enter_card_number), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(cardExpire)) {
            Toast.makeText(this, getString(R.string.please_enter_card_expiration_details), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(cardCvv)) {
            Toast.makeText(this, getString(R.string.please_enter_card_cvv), Toast.LENGTH_SHORT).show();
            return;
        }

        String cardNumberText = cardNumber.getText().toString();
        int cardMonth = Integer.parseInt(cardExpire.getText().toString().split("/")[0]);
        int cardYear = Integer.parseInt(cardExpire.getText().toString().split("/")[1]);
        String cardCvvText = cardCvv.getText().toString();
        Log.d("CARD", "CardDetails Number: " + cardNumberText + "Month: " + cardMonth + " Year: " + cardYear + " Cvv " + cardCvvText);
        Card card = new Card(cardNumberText, cardMonth, cardYear, cardCvvText);
        addCard(card);
    }

    private void addCard(Card card) {
        showLoading();
        Stripe stripe = new Stripe(this, SharedHelper.getKey(this, "stripe_publishable_key"));
        stripe.createToken(card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        Log.d("CARD:", " " + token.getId());
                        Log.d("CARD:", " " + token.getCard().getLast4());
                        String stripeToken = token.getId();
                        presenter.card(stripeToken);
                    }

                    public void onError(Exception error) {
                        try {
                            hideLoading();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onSuccess(Object card) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.card_added), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
