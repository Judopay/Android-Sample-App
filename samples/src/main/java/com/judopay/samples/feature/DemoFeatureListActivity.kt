package com.judopay.samples.feature

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.judopay.JUDO_OPTIONS
import com.judopay.JUDO_RECEIPT
import com.judopay.Judo
import com.judopay.JudoActivity
import com.judopay.PAYMENT_METHODS
import com.judopay.api.model.response.Receipt
import com.judopay.model.Amount
import com.judopay.model.CardNetwork
import com.judopay.model.Currency
import com.judopay.model.PaymentMethod
import com.judopay.model.Reference
import com.judopay.model.Transaction
import com.judopay.samples.BuildConfig
import com.judopay.samples.R
import com.judopay.samples.feature.adapter.DemoFeaturesAdapter
import com.judopay.samples.model.DemoFeature
import com.judopay.samples.settings.SettingsActivity
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_demo_feature_list.coordinatorLayout
import kotlinx.android.synthetic.main.activity_demo_feature_list.recyclerView
import java.util.*


class DemoFeatureListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        setContentView(R.layout.activity_demo_feature_list)
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.demo_feature_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val receipt: Receipt? = data?.getParcelableExtra(JUDO_RECEIPT)
        println(receipt)
    }

    private fun showcaseFeature(feature: DemoFeature) = when (feature) {
        DemoFeature.PAYMENT,
        DemoFeature.PREAUTH,
        DemoFeature.CREATE_CARD_TOKEN,
        DemoFeature.SAVE_CARD,
        DemoFeature.CHECK_CARD,
        DemoFeature.GOOGLE_PAY_PAYMENT,
        DemoFeature.GOOGLE_PAY_PREAUTH -> toast("Unimplemented.")
        DemoFeature.PAYMENT_METHODS,
        DemoFeature.PREAUTH_PAYMENT_METHODS -> presentPaymentMethods()
    }

    private fun presentPaymentMethods() {
        try {
            val intent = Intent(this, JudoActivity::class.java)
            intent.putExtra(JUDO_OPTIONS, judo)
            startActivityForResult(intent, PAYMENT_METHODS)
        } catch (exception: Exception) {
            when (exception) {
                is IllegalArgumentException, is IllegalStateException -> {
                    toast(exception.message ?: "An error occurred, please check your settings.")
                }
                else -> throw exception
            }
        }
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


    private val judo: Judo
        @Throws(IllegalArgumentException::class, IllegalStateException::class)
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val isSandboxed = prefs.getBoolean("is_sandboxed", true)
            val isTokenPayment = prefs.getBoolean("is_token_payment", false)
            val judoId = prefs.getString("judo_id", null)
            val siteId = prefs.getString("site_id", null)
            val token = prefs.getString("token", null)
            val secret = prefs.getString("secret", null)
            val transactionType = prefs.getString("transaction_type", null)
            val amountValue = prefs.getString("amount", null)
            val currency = prefs.getString("currency", null)
            val supportedNetworks = prefs.getStringSet("supported_networks", null)?.map { CardNetwork.valueOf(it) }?.toTypedArray()
            val paymentMethods = prefs.getStringSet("payment_methods", null)?.map { PaymentMethod.valueOf(it) }?.toTypedArray()

            val randomString = UUID.randomUUID().toString()

            val myCurrency = if (!currency.isNullOrEmpty()) {
                Currency.valueOf(currency)
            } else Currency.GBP

            val myTransactionType = if (!transactionType.isNullOrEmpty()) {
                Transaction.valueOf(transactionType)
            } else Transaction.PAYMENT

            val amount = Amount.Builder()
                    .setAmount(amountValue)
                    .setCurrency(myCurrency)
                    .build()

            val reference = Reference.Builder()
                    .setConsumerReference(randomString)
                    .setPaymentReference(randomString)
                    .build()

            return Judo.Builder()
                    .setJudoId(judoId)
                    .setSiteId(siteId)
                    .setApiToken(token)
                    .setApiSecret(secret)
                    .setAmount(amount)
                    .setReference(reference)
                    .setIsSandboxed(isSandboxed)
                .setTransactionType(myTransactionType)
                .setIsTokenPayment(isTokenPayment)
                    .setSupportedCardNetworks(supportedNetworks)
                    .setPaymentMethods(paymentMethods)
                    .build()
        }
}
