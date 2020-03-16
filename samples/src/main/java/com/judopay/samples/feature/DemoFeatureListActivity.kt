package com.judopay.samples.feature

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.crashlytics.android.Crashlytics
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.judopay.*
import com.judopay.api.error.ApiError
import com.judopay.api.model.response.Receipt
import com.judopay.model.*
import com.judopay.model.Currency
import com.judopay.samples.BuildConfig
import com.judopay.samples.R
import com.judopay.samples.feature.adapter.DemoFeaturesAdapter
import com.judopay.samples.model.DemoFeature
import com.judopay.samples.model.isGooglePay
import com.judopay.samples.settings.SettingsActivity
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_demo_feature_list.*
import java.util.*

const val JUDO_PAYMENT_WIDGET_REQUEST_CODE = 1

class DemoFeatureListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        setContentView(R.layout.activity_demo_feature_list)
        setupRecyclerView()

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.demo_feature_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == JUDO_PAYMENT_WIDGET_REQUEST_CODE) {
            when (resultCode) {

                PAYMENT_CANCELLED -> toast("User cancelled the payment.")

                PAYMENT_SUCCESS -> {
                    val receipt = data?.getParcelableExtra<Receipt>(JUDO_RECEIPT)
                    processSuccessfulPayment(receipt)
                }

                PAYMENT_ERROR -> {
                    val error = data?.getParcelableExtra<ApiError>(JUDO_ERROR)
                    processPaymentError(error)
                }
            }
        }
    }

    private fun processSuccessfulPayment(receipt: Receipt?) {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_success_title)
                .setMessage("Receipt id: ${receipt?.receiptId}")
                .setNegativeButton(R.string.dialog_button_ok, null)
                .show()
    }

    private fun processPaymentError(error: ApiError?) {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_error_title)
                .setMessage(error?.message ?: "Unknown error.")
                .setNegativeButton(R.string.dialog_button_ok, null)
                .show()
    }

    private fun showcaseFeature(feature: DemoFeature) {
        if (feature.isGooglePay) {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val judo = when (feature) {
                DemoFeature.PAYMENT -> getJudo(PaymentWidgetType.CARD_PAYMENT)
                DemoFeature.PREAUTH -> getJudo(PaymentWidgetType.PRE_AUTH_CARD_PAYMENT)
                DemoFeature.CREATE_CARD_TOKEN -> getJudo(PaymentWidgetType.CREATE_CARD_TOKEN)
                DemoFeature.SAVE_CARD -> getJudo(PaymentWidgetType.SAVE_CARD)
                DemoFeature.CHECK_CARD -> getJudo(PaymentWidgetType.CHECK_CARD)
                DemoFeature.PAYMENT_METHODS -> getJudo(PaymentWidgetType.PAYMENT_METHODS)
                DemoFeature.PREAUTH_PAYMENT_METHODS -> getJudo(PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS)
                else -> null
            }

            navigateToJudoPaymentWidgetWithConfigurations(judo!!)

        } catch (exception: Exception) {
            when (exception) {
                is IllegalArgumentException, is IllegalStateException -> {
                    val message = exception.message
                            ?: "An error occurred, please check your settings."
                    toast("Error: $message")
                }
                else -> throw exception
            }
        }
    }

    private fun navigateToJudoPaymentWidgetWithConfigurations(judo: Judo) {
        val intent = Intent(this, JudoActivity::class.java)
        intent.putExtra(JUDO_OPTIONS, judo)
        startActivityForResult(intent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
    }

    private fun toast(message: String) = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()

    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.shape_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }

        recyclerView.apply {
            addItemDecoration(dividerItemDecoration)
            adapter = DemoFeaturesAdapter(DemoFeature.values().asList()) {
                showcaseFeature(it)
            }
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun getJudo(widgetType: PaymentWidgetType): Judo {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isSandboxed = prefs.getBoolean("is_sandboxed", true)
        val judoId = prefs.getString("judo_id", null)
        val siteId = prefs.getString("site_id", null)
        val token = prefs.getString("token", null)
        val secret = prefs.getString("secret", null)
        val amountValue = prefs.getString("amount", null)
        val currency = prefs.getString("currency", null)
        val supportedNetworks = prefs.getStringSet("supported_networks", null)?.map { CardNetwork.valueOf(it) }?.toTypedArray()
        val paymentMethods = prefs.getStringSet("payment_methods", null)?.map { PaymentMethod.valueOf(it) }?.toTypedArray()

        val randomString = UUID.randomUUID().toString()

        val myCurrency = if (!currency.isNullOrEmpty()) {
            Currency.valueOf(currency)
        } else Currency.GBP

        val amount = Amount.Builder()
                .setAmount(amountValue)
                .setCurrency(myCurrency)
                .build()

        val reference = Reference.Builder()
                .setConsumerReference("my-unique-ref")
                .setPaymentReference(randomString)
                .build()

        // we want them in a certain order
        val methods = paymentMethods?.toList() ?: emptyList()
        val sortedPaymentMethods = methods.sortedBy { it.ordinal }.toTypedArray()

        return Judo.Builder(widgetType)
                .setJudoId(judoId)
                .setSiteId(siteId)
                .setApiToken(token)
                .setApiSecret(secret)
                .setAmount(amount)
                .setReference(reference)
                .setIsSandboxed(isSandboxed)
                .setSupportedCardNetworks(supportedNetworks)
                .setPaymentMethods(sortedPaymentMethods)
                .build()
    }
}
