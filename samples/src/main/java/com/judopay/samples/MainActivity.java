package com.judopay.samples;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.model.Currency;
import com.judopay.model.CustomLayout;
import com.judopay.model.Receipt;
import com.judopay.samples.settings.SettingsActivity;
import com.judopay.samples.settings.SettingsPrefs;

import java.util.UUID;

import static com.judopay.Judo.JUDO_RECEIPT;
import static com.judopay.Judo.PAYMENT_REQUEST;
import static com.judopay.Judo.PRE_AUTH_REQUEST;
import static com.judopay.Judo.REGISTER_CARD_REQUEST;
import static com.judopay.Judo.SANDBOX;
import static com.judopay.Judo.TOKEN_PAYMENT_REQUEST;
import static com.judopay.Judo.TOKEN_PRE_AUTH_REQUEST;

public class MainActivity extends BaseActivity {
    private static final String JUDO_ID = "<JUDO_ID>";
    private static final String API_TOKEN = "<API_TOKEN>";
    private static final String API_SECRET = "<API_SECRET>";
    private static final String AMOUNT = "0.10";
    private static final String REFERENCE = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void performPayment(View view) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
        startActivityForResult(intent, PAYMENT_REQUEST);
    }

    public void performPaymentWithCustomLayout(View view) {
        // based on https://github.com/Judopay/Android-Sample-App/wiki/Custom-layouts
        CustomLayout customLayout = new CustomLayout.Builder()
                .cardNumberInput(R.id.card_number_input)
                .expiryDateInput(R.id.expiry_date_input)
                .securityCodeInput(R.id.security_code_input)
                .startDateInput(R.id.start_date_input)
                .issueNumberInput(R.id.issue_number_input)
                .postcodeInput(R.id.post_code_input)
                .countrySpinner(R.id.country_spinner)
                .submitButton(R.id.pay_button)
                .build(R.layout.custom_layout);
        Judo judo = getJudo().newBuilder()
                .setCustomLayout(customLayout)
                .setCardNumber("4976000000003436")
                .setExpiryMonth("12")
                .setExpiryYear("20")
                .build();
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, judo);
        startActivityForResult(intent, PAYMENT_REQUEST);
    }

    public void performPreAuth(View view) {
        Intent intent = new Intent(this, PreAuthActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
        startActivityForResult(intent, PRE_AUTH_REQUEST);
    }

    public void performRegisterCard(View view) {
        Intent intent = new Intent(this, RegisterCardActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
        startActivityForResult(intent, REGISTER_CARD_REQUEST);
    }

    public void performTokenPreAuth(View view) {
        Receipt receipt = getLastReceipt();
        if (receipt != null) {
            Intent intent = new Intent(this, PreAuthActivity.class);
            intent.putExtra(Judo.JUDO_OPTIONS, getJudo().newBuilder()
                    .setConsumerReference(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .build());

            startActivityForResult(intent, TOKEN_PRE_AUTH_REQUEST);
        } else {
            Toast.makeText(this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    public void performTokenPayment(View view) {
        Receipt receipt = getLastReceipt();
        if (receipt != null) {
            Intent intent = new Intent(this, PaymentActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, getJudo().newBuilder()
                    .setConsumerReference(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .build());

            startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
        } else {
            Toast.makeText(this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    private Judo getJudo() {
        SettingsPrefs settingsPrefs = new SettingsPrefs(this);

        return new Judo.Builder()
                .setJudoId(JUDO_ID)
                .setApiToken(API_TOKEN)
                .setApiSecret(API_SECRET)
                .setEnvironment(SANDBOX)
                .setAmount(AMOUNT)
                .setCurrency(getCurrency())
                .setConsumerReference(REFERENCE)
                .setAvsEnabled(settingsPrefs.isAvsEnabled())
                .setMaestroEnabled(settingsPrefs.isMaestroEnabled())
                .setAmexEnabled(settingsPrefs.isAmexEnabled())
                .build();
    }

    private String getCurrency() {
        return getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(CURRENCY_KEY, Currency.GBP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_menu_item) {
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PAYMENT_REQUEST:
            case PRE_AUTH_REQUEST:
            case TOKEN_PAYMENT_REQUEST:
            case TOKEN_PRE_AUTH_REQUEST:
                handleResult(resultCode, data);
                break;
            case REGISTER_CARD_REQUEST:
                handleRegisterCardResult(resultCode, data);
                break;
        }
    }

    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void handleRegisterCardResult(int resultCode, Intent data) {
        if (resultCode == Judo.RESULT_SUCCESS) {
                Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);
                saveReceipt(receipt);
                showTokenPaymentDialog(receipt);
        }
    }

    private void showTokenPaymentDialog(final Receipt receipt) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.make_token_payment))
                .setMessage(getString(R.string.registered_card_can_perform_token_payments))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> startTokenPayment(receipt))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void startTokenPayment(Receipt receipt) {
        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().newBuilder()
                .setConsumerReference(receipt.getConsumer().getYourConsumerReference())
                .setCardToken(receipt.getCardDetails())
                .build());

        startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
    }

    private void handleResult(int resultCode, Intent data) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                if (response != null) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.payment_successful))
                        .setMessage("Receipt ID: " + response.getReceiptId())
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                }
                break;
            case Judo.RESULT_ERROR:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.transaction_error))
                        .setMessage(getString(R.string.could_not_perform_transaction_check_settings))
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                break;
            case Judo.RESULT_DECLINED:
                Receipt declinedResponse = data.getParcelableExtra(JUDO_RECEIPT);
                if (declinedResponse != null) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.payment_declined))
                            .setMessage("Receipt ID: " + declinedResponse.getReceiptId())
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }
                break;
        }
    }
}
