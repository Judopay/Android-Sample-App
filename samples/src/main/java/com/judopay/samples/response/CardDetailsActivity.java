package com.judopay.samples.response;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.judopay.Judo;
import com.judopay.model.Receipt;
import com.judopay.samples.R;
import com.judopay.samples.databinding.ActivityCardDetailsBinding;

public class CardDetailsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCardDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_card_details);
        Receipt receipt = getIntent().getParcelableExtra(Judo.JUDO_RECEIPT);
        binding.setCardDetails(receipt != null ? receipt.getCardDetails() : null);
    }
}
