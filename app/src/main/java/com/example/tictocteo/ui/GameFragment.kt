package com.example.tictocteo.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.R
import com.example.tictocteo.databinding.ChooseDialogBinding
import com.example.tictocteo.databinding.FragmentGameBinding
import com.example.tictocteo.presenter.GameViewModel
import com.example.tictocteo.presenter.impl.GameViewModelImpl
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tictocteo.data.model.JoinType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)
    private val args: GameFragmentArgs by navArgs()
    private val arrayList = ArrayList<ImageView>()
    private var chooseDialogBinding: ChooseDialogBinding? = null
    private lateinit var me: String
    private var isClickAble = true
    private var hostImage: Int = 0
    private var visitorImage: Int = 0
    private var chooseDialog: Dialog? = null

    private val viewModel: GameViewModel by viewModels<GameViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.init.collectLatest {
                if (args.type == JoinType.HOST) {
                    me = "X"
                    isClickAble = true
                } else {
                    me = "O"
                    isClickAble = false
                }

                for (i in 0 until binding.gridLayout.childCount) {
                    binding.gridLayout.getChildAt(i).tag = ""
                    arrayList.add(binding.gridLayout.getChildAt(i) as ImageView)
                }
                for (i in 0 until arrayList.size) {
                    arrayList[i].setOnClickListener {
                        if (isClickAble) viewModel.gameButtonClicked(
                            args.roomName,
                            ButtonData(i, me)
                        )
                    }
                }

                viewModel.onGameStarted(args.roomName, arrayList.map { it.tag as String }, me)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.hostImage.collectLatest {
                binding.hostImage.setImageResource(list[it])
                hostImage = it
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.visitorImage.collectLatest {
                binding.visitorImage.setImageResource(list[it])
                visitorImage = it
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loadGameImage.collectLatest { buttonData ->
                isClickAble = buttonData.text != me
                binding.turn.text = if (isClickAble) "Turn: You" else "Turn: Opp.."
                arrayList[buttonData.pos].setImageResource(if ("X" == buttonData.text) list[hostImage] else list[visitorImage])
                arrayList[buttonData.pos].tag = buttonData.text
                arrayList[buttonData.pos].isClickable = false
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.draw.collectLatest {
                binding.draw.text = "Draw: $it"
                if (it != 0)
                    Toast.makeText(requireContext(), "Draw", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.lose.collectLatest {
                binding.lose.text = "Lose: $it"
                if (it != 0)
                    Toast.makeText(requireContext(), "Lose", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.win.collectLatest {
                binding.win.text = "Win: $it"
                if (it != 0)
                    Toast.makeText(requireContext(), "Win", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.restart.collectLatest {
                for (i in 0 until arrayList.size) {
                    arrayList[i].setImageResource(android.R.color.transparent)
                    arrayList[i].isClickable = true
                    binding.gridLayout.getChildAt(i).tag = ""
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isShowChooseDialog.collectLatest {
                if (!it && chooseDialog != null) {
                    chooseDialog!!.dismiss()
                }else if(it){
                    chooseDialogBinding = ChooseDialogBinding.inflate(layoutInflater)
                    chooseDialog = AlertDialog.Builder(requireContext())
                        .setView(chooseDialogBinding!!.root)
                        .setCancelable(false)
                        .show()

                    chooseDialog!!.setOnDismissListener { chooseDialogBinding = null }

                    val views = chooseDialogBinding!!.chooseGrid
                    for (i in 0 until views.childCount) {
                        views.getChildAt(i).setOnClickListener {
                            viewModel.onEmojiChoose(i, args.roomName)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.onCreated(args.type, args.roomName)
        binding.retry.setOnClickListener { viewModel.onRestartClicked(args.roomName) }
    }

    private val list = listOf(
        R.drawable.close,
        R.drawable.circumference,
        R.drawable.boy,
        R.drawable.girl,
        R.drawable.emoji,
        R.drawable.love,
        R.drawable.smile,
        R.drawable.thinking
    )

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy(args.roomName, args.type)
    }
}
