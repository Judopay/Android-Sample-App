package com.judopay.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.VCOPaymentRequest;
import com.judopay.model.VCOWallet;
import com.judopay.samples.settings.SettingsActivity;
import com.judopay.samples.settings.SettingsPrefs;
import com.visa.checkout.CheckoutButton;
import com.visa.checkout.Environment;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaPaymentSummary;

import java.math.BigDecimal;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

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
    private static final String VCO_KEY = "<VCO_KEY>";
    private static final String AMOUNT = "0.10";
    private static final String REFERENCE = UUID.randomUUID().toString();

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVisaCheckoutButton();
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    private void initVisaCheckoutButton() {
        Profile profile = new Profile.ProfileBuilder(VCO_KEY, Environment.SANDBOX)
                .setDisplayName("Judo sample")
                .setMerchantId("123")
                .setDataLevel(Profile.DataLevel.FULL)
                .setEnableTokenization(true)
                .setCountryCode(Profile.Country.GB)
                .build();

        PurchaseInfo purchaseInfo = new PurchaseInfo.PurchaseInfoBuilder(new BigDecimal(AMOUNT), PurchaseInfo.Currency.GBP)
                .build();

        CheckoutButton checkoutButton = findViewById(R.id.visaCheckoutButton);
        checkoutButton.init(this, profile, purchaseInfo, new VisaCheckoutSdk.VisaCheckoutResultListener() {
            @Override
            public void onButtonClick(LaunchReadyHandler launchReadyHandler) {
                launchReadyHandler.launch();
            }

            @Override
            public void onResult(VisaPaymentSummary visaPaymentSummary) {
                onVCOResult(visaPaymentSummary);
            }
        });
    }

    private void onVCOResult(VisaPaymentSummary visaPaymentSummary) {
        switch (visaPaymentSummary.getStatusName()) {
            case VisaPaymentSummary.PAYMENT_SUCCESS:
                VCOPaymentRequest vcoPaymentRequest = new VCOPaymentRequest.Builder()
                        .setJudoId(JUDO_ID)
                        .setAmount(AMOUNT)
                        .setCurrency(Currency.GBP)
                        .setConsumerReference(UUID.randomUUID().toString())
                        .setVCOWallet(new VCOWallet.Builder()
                                .setCallId(visaPaymentSummary.getCallId())
                                .setEncryptedKey(visaPaymentSummary.getEncKey())
                                .setEncryptedPaymentData(visaPaymentSummary.getEncPaymentData())
                                .build())
                        .build();

                disposables.clear();
                disposables.add(getJudo().getApiService(this)
                        .vcoPayment(vcoPaymentRequest)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                receipt -> showDialog(R.string.payment_successful, "Receipt ID: " + receipt.getReceiptId()),
                                error -> showDialog(R.string.transaction_error, getString(R.string.could_not_perform_transaction_check_settings))
                        ));
                break;
            case VisaPaymentSummary.PAYMENT_CANCEL:
                break;
            default:
                showDialog(R.string.transaction_error, "Something went wrong with Visa Checkout");
                break;
        }
    }

    private void showDialog(@StringRes int title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void performPayment(View view) {
        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
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
        switch (item.getItemId()) {
            case R.id.settings_menu_item:
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
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);
                saveReceipt(receipt);
                showTokenPaymentDialog(receipt);
                break;
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
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.payment_successful))
                        .setMessage("Receipt ID: " + response.getReceiptId())
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                break;

            case Judo.RESULT_ERROR:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.transaction_error))
                        .setMessage(getString(R.string.could_not_perform_transaction_check_settings))
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                break;
        }
    }
}
