package com.example.finsecureapp.ui.transfer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.TransactionRepository
import com.example.finsecureapp.databinding.ActivityTransferBinding
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.TransactionViewModel
import com.example.finsecureapp.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.launch

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(
                TransactionRepository(),
                TokenManager(applicationContext)
            )
        )[TransactionViewModel::class.java]

        binding.btnSend.setOnClickListener {
            val receiverValue = binding.etReceiver.text.toString().trim()
            val amount = binding.etAmount.text.toString().trim().toDoubleOrNull()

            val transferMethod = if (binding.rbByPhone.isChecked) {
                "PHONE"
            } else {
                "ACCOUNT"
            }

            if (receiverValue.isEmpty() || amount == null) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.transfer(transferMethod, receiverValue, amount)
        }

        observeTransfer()
    }

    private fun observeTransfer() {
        lifecycleScope.launch {
            viewModel.transferState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val tx = state.data.transaction

                        val intent = android.content.Intent(
                            this@TransferActivity,
                            com.example.finsecureapp.ui.receipt.ReceiptActivity::class.java
                        ).apply {
                            putExtra("amount", tx.amount)
                            putExtra("receiverValue", tx.receiverValue)
                            putExtra("transferMethod", tx.transferMethod)
                            putExtra("status", tx.status)
                            putExtra("createdAt", tx.createdAt)
                        }

                        startActivity(intent)
                        finish()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@TransferActivity,
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}