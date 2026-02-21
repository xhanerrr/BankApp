package com.example.bankapp.ui.main.fragments.transactions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.bankapp.R
import com.example.bankapp.databinding.FragmentTransactionsBinding
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.model.ProfileUiState
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.UserRepository
import com.example.bankapp.ui.main.fragments.home.addtransaction.TransactionActionListener
import com.example.bankapp.ui.main.fragments.common.TransactionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions), TransactionActionListener {

    private var _binding : FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels()

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var authRepository: AuthRepository

    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        observeTransactions()
        setupListeners()
        observeProfileAvatars()
        observeUserProfileImage()
    }

    private fun observeUserProfileImage() {
        val uid = authRepository.getCurrentUserUid() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userRepository.getUserProfile(uid).collect { result ->
                    result.onSuccess { user ->
                        val url = user.profileImageUrl
                        binding.imgCenter.apply {
                            clipToOutline = true
                            load(url) {
                                crossfade(true)
                                placeholder(R.drawable.defaultpng)
                                error(R.drawable.defaultpng)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeProfileAvatars() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileUiState.Loading -> {}
                        is ProfileUiState.Success -> loadAvatars(state.urls)
                        is ProfileUiState.Error -> {
                            val errorMessage = "Error al cargar perfiles: ${state.message}"
                            Log.e("PROFILES_LOAD", errorMessage)
                            Toast.makeText(context, "Error al cargar perfiles: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun loadAvatars(urls: List<String>) {
        if (urls.size != 5) return

        val imageViewsToUpdate = listOf<ImageView>(
            binding.imgTop,
            binding.imgRight,
            binding.imgLeft,
            binding.imgBottomLeft,
            binding.imgBottomRight
        )

        imageViewsToUpdate.zip(urls).forEach { (imageView, url) ->
            imageView.apply {
                clipToOutline = true
                load(url) {
                    crossfade(true)
                    error(R.drawable.defaultpng)
                    placeholder(R.drawable.defaultpng)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        transactionsAdapter = TransactionsAdapter(this)

        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionsAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupListeners() {
        binding.textSeeAll.setOnClickListener {
            val intent = Intent(activity, TransactionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFilters() {
        updateButtonSelection(viewModel.currentFilter.value)

        binding.btnAll.setOnClickListener {
            viewModel.setFilter(FilterType.ALL)
            updateButtonSelection(FilterType.ALL)
        }
        binding.btnIncome.setOnClickListener {
            viewModel.setFilter(FilterType.INCOME)
            updateButtonSelection(FilterType.INCOME)
        }
        binding.btnExpense.setOnClickListener {
            viewModel.setFilter(FilterType.EXPENSE)
            updateButtonSelection(FilterType.EXPENSE)
        }
    }

    private fun updateButtonSelection(selectedType: FilterType) {
        val allButtons = listOf(binding.btnAll, binding.btnIncome, binding.btnExpense)

        allButtons.forEach { button ->
            val isSelected = when (button.id) {
                R.id.btnAll -> selectedType == FilterType.ALL
                R.id.btnIncome -> selectedType == FilterType.INCOME
                R.id.btnExpense -> selectedType == FilterType.EXPENSE
                else -> false
            }

            if (isSelected) {
                button.setBackgroundResource(R.drawable.oval_button_selected)
                button.setTextColor(resources.getColor(R.color.white, null))
            } else {
                button.setBackgroundResource(R.drawable.oval_button)
                button.setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.latestFilteredTransaction.collect { transactions ->
                    transactionsAdapter.submitList(transactions)

                    if (transactions.isEmpty()) {
                        binding.transactionsRecyclerView.visibility = View.GONE
                    } else {
                        binding.transactionsRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onEditTransaction(transaction: Transaction) {}

    override fun onDeleteTransactionClicked(
        view: View,
        transaction: Transaction
    ) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
