package com.judopay.samples.model

import androidx.annotation.StringRes
import com.judopay.samples.R

enum class DemoFeature(
        @StringRes val title: Int,
        @StringRes val surTitle: Int
) {
    PAYMENT(R.string.feature_title_payment, R.string.feature_surtitle_payment),
    PREAUTH(R.string.feature_title_preauth, R.string.feature_surtitle_preauth),
    CREATE_CARD_TOKEN(R.string.feature_title_create_card_token, R.string.feature_surtitle_create_card_token),
    SAVE_CARD(R.string.feature_title_save_card, R.string.feature_surtitle_save_card),
    CHECK_CARD(R.string.feature_title_check_card, R.string.feature_surtitle_check_card),
    GOOGLE_PAY_PAYMENT(R.string.feature_title_google_pay_payment, R.string.feature_surtitle_google_pay_payment),
    GOOGLE_PAY_PREAUTH(R.string.feature_title_google_pay_preauth, R.string.feature_surtitle_google_pay_preauth),
    PAYMENT_METHODS(R.string.feature_title_payment_methods, R.string.feature_surtitle_payment_methods),
    PREAUTH_PAYMENT_METHODS(R.string.feature_title_preauth_payment_methods, R.string.feature_surtitle_preauth_payment_methods),
}