package com.judopay.samples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.WalletConstants;
import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.PaymentActivity;
import com.judopay.PaymentMethodActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.arch.GooglePaymentUtils;
import com.judopay.model.Currency;
import com.judopay.model.CustomLayout;
import com.judopay.model.GooglePayRequest;
import com.judopay.model.PaymentMethod;
import com.judopay.model.PrimaryAccountDetails;
import com.judopay.model.Receipt;
import com.judopay.samples.response.ReceiptActivity;
import com.judopay.samples.settings.SettingsActivity;
import com.judopay.samples.settings.SettingsPrefs;

import java.util.EnumSet;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.judopay.Judo.JUDO_RECEIPT;
import static com.judopay.Judo.PAYMENT_METHOD;
import static com.judopay.Judo.PAYMENT_REQUEST;
import static com.judopay.Judo.PRE_AUTH_REQUEST;
import static com.judopay.Judo.REGISTER_CARD_REQUEST;
import static com.judopay.Judo.SANDBOX;
import static com.judopay.Judo.TOKEN_PAYMENT_REQUEST;
import static com.judopay.Judo.TOKEN_PRE_AUTH_REQUEST;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String JUDO_ID = "<JUDO_ID>";
    private static final String API_TOKEN = "<API_TOKEN>";
    private static final String API_SECRET = "<API_SECRET>";
    private static final String AMOUNT = "0.10";
    private static final String REFERENCE = UUID.randomUUID().toString();

    private TextView gPayNotSupportedTextView;
    private View gPayButton;
    private Disposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gPayNotSupportedTextView = findViewById(R.id.gPayNotSupportedTextView);
        gPayButton = findViewById(R.id.gPayButton);
        setUpGpay();
    }

    public void performPaymentMethod(View view) {
        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.putExtra(Judo.GPAY_PREAUTH, false);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.of(PaymentMethod.CREATE_PAYMENT, PaymentMethod.GPAY_PAYMENT))
                .build());
        startActivityForResult(intent, PAYMENT_METHOD);
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
                .setPrimaryAccountDetails(getPrimaryAccountDetails())
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PAYMENT_REQUEST:
            case PAYMENT_METHOD:
            case PRE_AUTH_REQUEST:
            case TOKEN_PAYMENT_REQUEST:
            case TOKEN_PRE_AUTH_REQUEST:
                handleResult(requestCode, resultCode, data);
                break;
            case REGISTER_CARD_REQUEST:
                handleRegisterCardResult(resultCode, data);
                break;
            case Judo.GPAY_REQUEST:
                handleGPAYRequest(resultCode, data);
                break;
        }
    }

    private void handleGPAYRequest(int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                PaymentData paymentData = PaymentData.getFromIntent(data);
                if (paymentData != null) {
                    GooglePayRequest googlePayRequest = GooglePaymentUtils.createGooglePayRequest(getJudo(), paymentData);
                    finishGPayRequest(googlePayRequest);
                } else {
                    Log.e(TAG, "LoadPaymentData failed. No payment data found.");
                }
                break;
            case Activity.RESULT_CANCELED:
                // Nothing to do here normally - the user simply cancelled without selecting a
                // payment method.
                break;
            case AutoResolveHelper.RESULT_ERROR:
                Status status = AutoResolveHelper.getStatusFromIntent(data);
                if (status != null) {
                    Log.e(TAG, String.format("LoadPaymentData failed. Error code: %d", status.getStatusCode()));
                } else {
                    Log.e(TAG, "LoadPaymentData failed");
                }
                break;
        }
    }

    private void finishGPayRequest(GooglePayRequest googlePayRequest) {
        final JudoApiService apiService = getJudo().getApiService(this);
        Single<Receipt> apiRequest = apiService.googlePayPayment(googlePayRequest);

        disposable = apiRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(receipt -> {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.payment_successful))
                            .setMessage("Receipt ID: " + receipt.getReceiptId())
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }, error -> {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.transaction_error))
                            .setMessage(getString(R.string.could_not_perform_transaction_check_settings))
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                });

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

    private void handleResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                if (response != null) {
                    if (requestCode == PAYMENT_REQUEST) {
                        Intent intent = new Intent(this, ReceiptActivity.class);
                        intent.putExtra(Judo.JUDO_RECEIPT, response);
                        startActivity(intent);
                        return;
                    }
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

    private void setUpGpay() {
        Judo judo = getJudo();
        PaymentsClient paymentsClient = GooglePaymentUtils.getGooglePayPaymentsClient(this, WalletConstants.ENVIRONMENT_PRODUCTION);

        GooglePaymentUtils.checkIsReadyGooglePay(paymentsClient, result -> {
            if (result) {
                gPayNotSupportedTextView.setVisibility(View.GONE);
                gPayButton.setVisibility(View.VISIBLE);

                PaymentDataRequest paymentData = GooglePaymentUtils.createDefaultPaymentDataRequest(judo);
                final Task<PaymentData> taskDefaultPaymentData = paymentsClient.loadPaymentData(paymentData);
                gPayButton.setOnClickListener(v -> AutoResolveHelper.resolveTask(
                        taskDefaultPaymentData,
                        MainActivity.this,
                        Judo.GPAY_REQUEST
                ));
            } else {
                gPayNotSupportedTextView.setVisibility(View.VISIBLE);
                gPayButton.setVisibility(View.GONE);
            }
        });
    }

    private PrimaryAccountDetails getPrimaryAccountDetails() {
        return new PrimaryAccountDetails.Builder()
                .setName("Judo Pay")
                .setAccountNumber("1234567")
                .setDateOfBirth("2000-12-31")
                .setPostCode("EC2A 4DP")
                .build();
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
        super.onDestroy();
    }
}
