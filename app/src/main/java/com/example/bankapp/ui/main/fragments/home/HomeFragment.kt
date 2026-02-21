package com.example.bankapp.ui.main.fragments.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.bankapp.R
import com.example.bankapp.databinding.FragmentHomeBinding
import com.example.bankapp.domain.model.HomeUiState
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.model.UploadState
import com.example.bankapp.domain.model.User
import com.example.bankapp.ui.login.LoginActivity
import com.example.bankapp.ui.main.fragments.common.TransactionsAdapter
import com.example.bankapp.ui.main.fragments.home.addtransaction.AddTransactionDialogFragment
import com.example.bankapp.ui.main.fragments.home.addtransaction.TransactionActionListener
import com.example.bankapp.ui.main.fragments.transactions.TransactionsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), TransactionActionListener {

    private val viewModel: HomeViewModel by viewModels()
    private val transactionsViewModel: TransactionsViewModel by viewModels()

    private lateinit var transactionsAdapter: TransactionsAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cameraImageUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) viewModel.uploadProfileImage(uri)
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if (cameraImageUri != null) {
                    val file = File(cameraImageUri!!.path ?: "")
                    if (file.exists() && file.length() > 0) {
                        viewModel.uploadProfileImage(cameraImageUri!!)
                    } else {
                        Toast.makeText(requireContext(), "La cámara no guardó la imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val imageFile = File(requireContext().cacheDir, "profile_temp.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        cameraLauncher.launch(intent)
    }


    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun checkCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        return if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(permission), 1000)
            false
        } else true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupLogoutClick()
        setupProfileClick()
        observeUserData()
        observeUploadState()
        setupRecentTransactions()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupLogoutClick() {
        binding.logoutPopOut.setOnClickListener {
            showLogoutPopout(it)
         }
    }

    private fun showLogoutPopout(anchor: View) {
        val popup = android.widget.PopupMenu(requireContext(), anchor)

        popup.menu.add("Logout")

        popup.setOnMenuItemClickListener {
            logout()
            true
        }

        popup.show()
    }


    private fun setupProfileClick() {
        binding.profilePicture.setOnClickListener {
            showProfileOptions()
        }
    }

    private fun showProfileOptions() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_choose_photo, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())

        view.findViewById<View>(R.id.btnCamera).setOnClickListener {
            if (checkCameraPermission()) openCamera()
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnGallery).setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        HomeUiState.Loading -> {}
                        is HomeUiState.Success -> {
                            displayUserData(state.user)
                            transactionsAdapter.submitList(state.recentTransactions)
                        }
                        is HomeUiState.Error ->
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun observeUploadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadState.collect { state ->
                    when (state) {
                        UploadState.Idle -> {}
                        UploadState.Loading -> binding.profilePicture.alpha = 0.4f
                        UploadState.Success -> {
                            binding.profilePicture.alpha = 1f
                            Toast.makeText(requireContext(), "Foto actualizada!", Toast.LENGTH_SHORT).show()
                        }
                        is UploadState.Error -> {
                            binding.profilePicture.alpha = 1f
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun displayUserData(user: User) {
        binding.textView2.text = user.name
        binding.textView.text = user.profession
        binding.textView3.text = String.format("$%.2f", user.income)
        binding.textView4.text = String.format("$%.2f", user.expenses)

        binding.profilePicture.load(user.profileImageUrl) {
            crossfade(true)
            placeholder(R.drawable.imagepfpmain)
            error(R.drawable.imagepfpmain)
        }
    }

    private fun setupRecentTransactions() {
        transactionsAdapter = TransactionsAdapter(this)
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionsRecyclerView.adapter = transactionsAdapter
    }

    override fun onEditTransaction(transaction: Transaction) {
        val dialog = AddTransactionDialogFragment()
        val bundle = Bundle().apply {
            putParcelable(AddTransactionDialogFragment.KEY_TRANSACTION, transaction)
        }
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, "AddTransactionDialog")
    }

    override fun onDeleteTransactionClicked(view: View, transaction: Transaction) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(Menu.NONE, 1, 0, "Eliminar")

        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == 1) {
                lifecycleScope.launch {
                    transactionsViewModel.deleteTransaction(transaction)
                    Toast.makeText(requireContext(), "Transacción eliminada", Toast.LENGTH_SHORT).show()
                    viewModel.refresh()
                }
                true
            } else false
        }

        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logout() {
        viewModel.logout()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
