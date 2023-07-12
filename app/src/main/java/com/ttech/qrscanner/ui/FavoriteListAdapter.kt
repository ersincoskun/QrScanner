package com.ttech.qrscanner.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ttech.qrscanner.R
import com.ttech.qrscanner.data.QrCodeResultData
import com.ttech.qrscanner.databinding.ItemQrResultListBinding
import com.ttech.qrscanner.utils.onSingleClickListener
import com.ttech.qrscanner.utils.remove

class FavoriteListAdapter(private val context: Context, private val rootItemClick: (Long?) -> Unit) :
    ListAdapter<QrCodeResultData, FavoriteListAdapter.FavoriteListViewHolder>(FavoriteListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        return FavoriteListViewHolder(
            ItemQrResultListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) =
        with(holder.binding) {
            val item = getItem(position)
            tvQrResultData.text = item.result
            tvQrResultDate.text = item.date
            ivAddFavorite.remove()
            when {
                item.isUrl -> {
                    val context = tvQrResultType.context
                    tvQrResultType.text = context.getString(R.string.result_fragment_url_type_text)
                    ivQrResultLogo.setImageResource(R.drawable.link_icon)
                }

                item.isQr -> {
                    ivQrResultLogo.setImageResource(R.drawable.qr_scan_icon)
                    tvQrResultType.text = context.getString(R.string.result_fragment_qr_type_text)
                }

                else -> {
                    ivQrResultLogo.setImageResource(R.drawable.barcode_icon)
                    tvQrResultType.text = context.getString(R.string.result_fragment_barcode_type_text)
                }
            }
            llItemQrResultRoot.onSingleClickListener {
                rootItemClick(item.primaryId)
            }
        }

    class FavoriteListViewHolder(val binding: ItemQrResultListBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FavoriteListDiffUtil : DiffUtil.ItemCallback<QrCodeResultData>() {
        override fun areItemsTheSame(oldItem: QrCodeResultData, newItem: QrCodeResultData): Boolean {
            return oldItem.primaryId == newItem.primaryId
        }

        override fun areContentsTheSame(oldItem: QrCodeResultData, newItem: QrCodeResultData): Boolean {
            return oldItem == newItem
        }
    }
}