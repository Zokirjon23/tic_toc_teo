package com.example.tictocteo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.tictocteo.ButtonData
import com.example.tictocteo.R
import com.example.tictocteo.checkWin
import com.example.tictocteo.databinding.ChooseDialogBinding
import com.example.tictocteo.databinding.FragmentGameBinding
import com.example.tictocteo.isDraw
import com.google.firebase.database.*

class GameFragment : Fragment(R.layout.fragment_game), ChildEventListener {

    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)
    private val args: GameFragmentArgs by navArgs()
    private var mainRef : DatabaseReference? = FirebaseDatabase.getInstance().reference
    private val arrayList = ArrayList<ImageView>()
    private var chooseDialogBinding: ChooseDialogBinding? = null
    private var you = ""
    private var isClickAble = true
    private var hostImage: Int = 0
    private var visitorImage: Int = 0
    private var winCount = 0
    private var drawCount = 0
    private var loseCount = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.type == PlayerType.HOST) {
            you = "X"
            isClickAble = true
            chooseDialog()
        } else {
            you = "O"
            isClickAble = false

            val hostListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                        hostImage = snapshot.getValue(Int::class.java)!!
                    binding.hostImage.setImageResource(hostImage)
                }

                override fun onCancelled(error: DatabaseError) {}
            }

            val visitorListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                        visitorImage = snapshot.getValue(Int::class.java)!!
                    binding.visitorImage.setImageResource(visitorImage)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            mainRef!!.child("${args.roomName}/emoji/visitor")
                .addValueEventListener(visitorListener)

            mainRef!!.child("${args.roomName}/emoji/host")
                .addValueEventListener(hostListener)
        }

        for (i in 0 until binding.gridLayout.childCount) {
            binding.gridLayout.getChildAt(i).tag = ""
            arrayList.add(binding.gridLayout.getChildAt(i) as ImageView)
        }

        binding.retry.setOnClickListener {
            mainRef!!.child("${args.roomName}/game").setValue(null)
        }

        for (i in 0 until arrayList.size) {
            arrayList[i].setOnClickListener {
                if (isClickAble)
                    mainRef!!.child("${args.roomName}/game/${System.currentTimeMillis()}")
                        .setValue(ButtonData(i, you))
            }
        }

        mainRef!!.child("${args.roomName}/game").addChildEventListener(this)
    }

    private fun chooseDialog() {
        chooseDialogBinding = ChooseDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(chooseDialogBinding!!.root)
            .setCancelable(false)
            .show()

        dialog.setOnDismissListener { chooseDialogBinding = null }

        val views = chooseDialogBinding!!.chooseGrid
        for (i in 0 until views.childCount) {
            views.getChildAt(i).setOnClickListener {
                when (i) {
                    0 -> {
                        hostImage = R.drawable.close
                        visitorImage = R.drawable.circumference
                    }
                    1 -> {
                        hostImage = R.drawable.circumference
                        visitorImage = R.drawable.close
                    }
                    2 -> {
                        hostImage = R.drawable.boy
                        visitorImage = R.drawable.girl
                    }
                    3 -> {
                        hostImage = R.drawable.girl
                        visitorImage = R.drawable.boy
                    }
                    4 -> {
                        hostImage = R.drawable.emoji
                        visitorImage = R.drawable.love
                    }
                    5 -> {
                        hostImage = R.drawable.love
                        visitorImage = R.drawable.emoji
                    }
                    6 -> {
                        hostImage = R.drawable.smile
                        visitorImage = R.drawable.thinking
                    }
                    7 -> {
                        hostImage = R.drawable.thinking
                        visitorImage = R.drawable.smile
                    }
                }
                binding.hostImage.setImageResource(hostImage)
                binding.visitorImage.setImageResource(visitorImage)
                val values: MutableMap<String, Any> = HashMap()
                values["host"] = hostImage
                values["visitor"] = visitorImage
                dialog.dismiss()
                mainRef!!.child("${args.roomName}/emoji").setValue(values)
            }
        }
    }


    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val buttonData = snapshot.getValue(ButtonData::class.java)!!
        isClickAble = buttonData.text != you
        binding.turn.text = if (isClickAble) "Turn: You" else "Turn: Opp.."

        arrayList[buttonData.pos].setImageResource(if ("X" == buttonData.text) hostImage else visitorImage)
        arrayList[buttonData.pos].tag = buttonData.text
        arrayList[buttonData.pos].isClickable = false

        val winner = arrayList.map { it.tag.toString() }.checkWin(buttonData.pos)
        if (winner != null) {
            Toast.makeText(requireContext(), if (winner == you) "You win" else "Opponent win", Toast.LENGTH_SHORT).show()
            if (winner == you) {
                winCount++
                binding.win.text = "Win: $winCount"
            } else {
                loseCount++
                binding.lose.text = "Lose: $loseCount"
            }
        } else if (arrayList.isDraw()) {
            drawCount++
            binding.draw.text = "Draw: $drawCount"
            Toast.makeText(requireContext(), "Draw", Toast.LENGTH_SHORT).show()
            for (i in 0 until arrayList.size)
                arrayList[i].isClickable = false
        }
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
    override fun onChildRemoved(snapshot: DataSnapshot) {
        for (i in 0 until arrayList.size) {
            arrayList[i].setImageResource(android.R.color.transparent)
            arrayList[i].isClickable = true
            binding.gridLayout.getChildAt(i).tag = ""
        }
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

    override fun onCancelled(error: DatabaseError) {
        Log.d("LLL", "onCancelled: ${error.message}")
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (args.type == PlayerType.HOST)
            mainRef!!.child(args.roomName).setValue(null)
        mainRef = null
    }

    val String.getResource : Int
        get() = resources.getIdentifier(this, "drawable", requireContext().packageName)
}
