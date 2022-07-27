package com.example.tictocteo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.tictocteo.R
import com.example.tictocteo.databinding.CreateRoomDialogBinding
import com.example.tictocteo.databinding.FragmentMenuBinding
import com.example.tictocteo.databinding.PlayDialogBinding
import com.example.tictocteo.isNetworkAvailable
import com.google.firebase.database.*

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val binding: FragmentMenuBinding by viewBinding(FragmentMenuBinding::bind)
    private var mainRef: DatabaseReference? = FirebaseDatabase.getInstance().reference
    private var dialogBinding: CreateRoomDialogBinding? = null
    private var playDialogBinding: PlayDialogBinding? = null
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.exit.setOnClickListener {
            requireActivity().finish()
        }

        binding.createRoom.setOnClickListener {
            showRoomDialog()
        }

        binding.playGame.setOnClickListener {
            playDialogBinding = PlayDialogBinding.inflate(layoutInflater)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(playDialogBinding!!.root)
                .show()

            dialog.setOnDismissListener {
                playDialogBinding = null
            }

            playDialogBinding!!.playBtn.setOnClickListener {
                if (requireContext().isNetworkAvailable()) {
                    val name = playDialogBinding!!.editText.text.toString()
                    mainRef!!.orderByChild("roomName").equalTo(name)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    dialog.dismiss()
                                    navController.navigate(
                                        MenuFragmentDirections.actionMenuFragmentToGameFragment(
                                            name, PlayerType.VISITOR
                                        )
                                    )
                                } else
                                    Toast.makeText(
                                        requireContext(),
                                        "Room not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("LLL", "onCancelled: ${error.message}")
                            }
                        })
                } else
                    Toast.makeText(requireContext(), "no Internet connection", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun showRoomDialog() {
        dialogBinding = CreateRoomDialogBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding!!.root)
            .show()
        var roomName = ""
        dialogBinding!!.createBtn.setOnClickListener {
            if (requireContext().isNetworkAvailable()) {
                roomName = dialogBinding!!.editText.text.toString()
                if (roomName.isEmpty())
                    Toast.makeText(requireContext(), "Enter room name", Toast.LENGTH_SHORT).show()
                else {
                    val values: MutableMap<String, Any> = HashMap()
                    values["roomName"] = roomName

                    mainRef!!.child(roomName).setValue(values).addOnSuccessListener {
                        dialogBinding!!.createBtn.isVisible = false
                        dialogBinding!!.playBtn.isVisible = true

                        Toast.makeText(requireContext(), "$roomName created", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else
                Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT).show()
        }
        dialog.setOnDismissListener {
            dialogBinding = null
        }

        dialogBinding!!.playBtn.setOnClickListener {
            dialog.dismiss()
            navController.navigate(
                MenuFragmentDirections.actionMenuFragmentToGameFragment(
                    roomName, PlayerType.HOST
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainRef = null
    }
}