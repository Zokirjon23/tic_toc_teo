package com.example.tictocteo.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.tictocteo.R
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.databinding.CreateRoomDialogBinding
import com.example.tictocteo.databinding.FragmentMenuBinding
import com.example.tictocteo.databinding.PlayDialogBinding
import com.example.tictocteo.util.isNetworkAvailable
import com.example.tictocteo.presenter.MenuViewModel
import com.example.tictocteo.presenter.impl.MenuViewModelImpl
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val binding: FragmentMenuBinding by viewBinding(FragmentMenuBinding::bind)
    private var dialogBinding: CreateRoomDialogBinding? = null
    private var playDialogBinding: PlayDialogBinding? = null
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private val viewModel: MenuViewModel by viewModels<MenuViewModelImpl>()
    private var createDialog: Dialog? = null
    private var joinDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.navToGameAsVisitor.collectLatest {
                navController.navigate(
                    MenuFragmentDirections.actionMenuFragmentToGameFragment(
                        it, JoinType.VISITOR
                    )
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.dismissJoinDialog.collectLatest {
                if (joinDialog!!.isShowing) joinDialog!!.dismiss()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.dismissCreateDialog.collectLatest {
                if (createDialog!!.isShowing) createDialog!!.dismiss()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.navToGameAsHost.collectLatest {
                navController.navigate(
                    MenuFragmentDirections.actionMenuFragmentToGameFragment(
                        it, JoinType.HOST
                    )
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.showCreateDialog.collectLatest {
                dialogBinding = CreateRoomDialogBinding.inflate(layoutInflater)

                createDialog = AlertDialog.Builder(requireContext())
                    .setView(dialogBinding!!.root)
                    .show()
                dialogBinding!!.createBtn.setOnClickListener {
                    viewModel.dialogCreateClicked(
                        dialogBinding!!.editText.text.toString(), isNetworkAvailable()
                    )

                    dialogBinding!!.playBtn.setOnClickListener {
                        viewModel.dialogOkClicked(
                            dialogBinding!!.editText.text.toString()
                        )
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.showJoinDialog.collectLatest {
                playDialogBinding = PlayDialogBinding.inflate(layoutInflater)

                joinDialog = AlertDialog.Builder(requireContext())
                    .setView(playDialogBinding!!.root)
                    .show()

                joinDialog!!.setOnDismissListener { playDialogBinding = null }

                playDialogBinding!!.playBtn.setOnClickListener {
                    viewModel.dialogPlayClicked(
                        playDialogBinding!!.editText.text.toString(),
                        isNetworkAvailable()
                    )
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.successCreatedRoom.collectLatest {
                Toast.makeText(requireContext(), "Game created", Toast.LENGTH_SHORT).show()
                dialogBinding!!.createBtn.isVisible = false
                dialogBinding!!.playBtn.isVisible = true
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.exitGame.collectLatest {
                requireActivity().finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.exit.setOnClickListener { viewModel.exitClicked() }

        binding.createRoom.setOnClickListener { viewModel.createClicked() }

        binding.playGame.setOnClickListener { viewModel.playClicked() }
    }
}