package com.example.finsecureapp.ui.receipt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finsecureapp.databinding.ActivityReceiptBinding

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val amount = intent.getDoubleExtra("amount", 0.0)
        val receiverValue = intent.getStringExtra("receiverValue").orEmpty()
        val transferMethod = intent.getStringExtra("transferMethod").orEmpty()
        val status = intent.getStringExtra("status").orEmpty()
        val createdAt = intent.getStringExtra("createdAt").orEmpty()

        binding.tvStatus.text = "Status: $status"
        binding.tvAmount.text = "Amount: $amount"
        binding.tvReceiver.text = "Receiver: $receiverValue"
        binding.tvMethod.text = "Method: $transferMethod"
        binding.tvDate.text = "Date: $createdAt"

        binding.btnShare.setOnClickListener {
            val receiptText = """
                Transaction receipt
                
                Status: $status
                Amount: $amount
                Receiver: $receiverValue
                Method: $transferMethod
                Date: $createdAt
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, receiptText)
            }

            startActivity(Intent.createChooser(shareIntent, "Share receipt via"))
        }

        binding.btnDone.setOnClickListener {
            finish()
        }
    }
}