package com.ttech.qrscanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttech.qrscanner.data.QrCodeResultData
import com.ttech.qrscanner.storage.dao.QrCodeResultDao
import com.ttech.qrscanner.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val qrCodeResultDao: QrCodeResultDao
) : ViewModel() {

    private val _qrCodeResultData = MutableLiveData<QrCodeResultData?>()
    val qrCodeResultData: LiveData<QrCodeResultData?>
        get() = _qrCodeResultData

    private val _qrCodeResultDataList = MutableLiveData<List<QrCodeResultData>?>()
    val qrCodeResultDataList: LiveData<List<QrCodeResultData>?>
        get() = _qrCodeResultDataList

    private val _insertedItemId = SingleLiveEvent<Long?>()
    val insertedItemId: LiveData<Long?>
        get() = _insertedItemId

    fun addQrCodeResultData(qrCodeResultData: QrCodeResultData) {
        viewModelScope.launch {
            _insertedItemId.value = qrCodeResultDao.insertQrCodeResultData(qrCodeResultData)
        }
    }

    fun getQrCodeResultDataById(id: Long) {
        viewModelScope.launch {
            _qrCodeResultData.value = qrCodeResultDao.getQrCodeResultDataById(id)
        }
    }

    fun getAllQrCodeResultData() {
        viewModelScope.launch {
            _qrCodeResultDataList.value = qrCodeResultDao.getAllQrCodeResultData()
        }
    }

    fun deleteQrCodeResultDataById(id: Long) {
        viewModelScope.launch {
            qrCodeResultDao.deleteQrCodeResultDataById(id)
        }
    }

    fun deleteAllQrCodeResultData() {
        viewModelScope.launch {
            qrCodeResultDao.deleteAllQrCodeResultData()
        }
    }

    fun updateIsFavorite(itemId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            qrCodeResultDao.updateIsFavorite(itemId, isFavorite)
        }
    }

}