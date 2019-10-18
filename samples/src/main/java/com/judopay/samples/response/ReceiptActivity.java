package com.judopay.samples.response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.judopay.Judo;
import com.judopay.model.Receipt;
import com.judopay.samples.R;
import com.judopay.samples.databinding.ActivityReceiptBinding;

public class ReceiptActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityReceiptBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt);
        Receipt receipt = getIntent().getParcelableExtra(Judo.JUDO_RECEIPT);
        binding.setReceipt(receipt);
    }

    public void openHomeScreen(View view) {
        finish();
    }

    public void openCardDetailsScreen(View view) {
        Receipt receipt = getIntent().getParcelableExtra(Judo.JUDO_RECEIPT);
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);
        startActivity(intent);
    }
}
