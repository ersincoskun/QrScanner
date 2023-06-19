package com.ttech.qrscanner.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ttech.qrscanner.R
import com.ttech.qrscanner.core.base.BaseFragment
import com.ttech.qrscanner.data.QrCodeResultData
import com.ttech.qrscanner.databinding.FragmentHistoryBinding
import com.ttech.qrscanner.utils.showErrorSnackBar
import com.ttech.qrscanner.viewModel.QrCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    private val viewModel: QrCodeViewModel by viewModels()
    private val historyList: MutableList<QrCodeResultData> = mutableListOf()
    private lateinit var historyListAdapter: HistoryListAdapter

    override fun assignObjects() {
        super.assignObjects()
        historyListAdapter = HistoryListAdapter(requireContext(), { itemId ->
            itemId?.let { safeItemId ->
                val navDirection = HistoryFragmentDirections.actionHistoryFragmentToResultFragment(safeItemId)
                navigate(navDirections = navDirection)
            } ?: kotlin.run {
                showErrorSnackBar(binding.rvHistory, context)
            }
        }) { favoriteItemId, isFavorite ->
            favoriteItemId?.let {safeFavoriteItemId->
                viewModel.updateIsFavorite(safeFavoriteItemId, !isFavorite)
            }
        }
    }

    override fun subLivData() {
        super.subLivData()
        viewModel.qrCodeResultDataList.observe(viewLifecycleOwner) {
            it?.let {
                historyList.clear()
                historyList.addAll(it)
                historyListAdapter.submitList(emptyList())
                historyListAdapter.submitList(it)
            }
        }
    }

    override fun prepareUI() {
        super.prepareUI()
        binding.rvHistory.adapter = historyListAdapter
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        setSwipeToDelete()
        viewModel.getAllQrCodeResultData()
    }

    private fun setSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                historyList[position].primaryId?.let {
                    viewModel.deleteQrCodeResultDataById(it)
                }
                historyList.removeAt(position)
                historyListAdapter.submitList(emptyList())
                historyListAdapter.submitList(historyList)
                historyListAdapter.notifyDataSetChanged()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_icon)
                val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
                val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0
                val background = ColorDrawable()
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCancelled = dX == 0f && !isCurrentlyActive

                if (isCancelled) {
                    clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.rvHistory)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
    }

}