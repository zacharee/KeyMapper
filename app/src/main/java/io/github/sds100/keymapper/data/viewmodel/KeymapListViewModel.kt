package io.github.sds100.keymapper.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadilq.liveevent.LiveEvent
import io.github.sds100.keymapper.data.model.KeymapListItemModel
import io.github.sds100.keymapper.data.repository.DeviceInfoRepository
import io.github.sds100.keymapper.data.usecase.KeymapListUseCase
import io.github.sds100.keymapper.util.*
import io.github.sds100.keymapper.util.delegate.IModelState
import io.github.sds100.keymapper.util.result.Failure

class KeymapListViewModel internal constructor(
    private val keymapRepository: KeymapListUseCase,
    private val deviceInfoRepository: DeviceInfoRepository
) : ViewModel(), IModelState<List<KeymapListItemModel>> {

    private val _model: MutableLiveData<DataState<List<KeymapListItemModel>>> =
        MutableLiveData(Loading())

    override val model = _model
    override val viewState = MutableLiveData<ViewState>(ViewLoading())

    val selectionProvider: ISelectionProvider = SelectionProvider()

    private val _eventStream = LiveEvent<Event>().apply {
        addSource(keymapRepository.keymapList) {
            postValue(BuildKeymapListModels(it))
        }
    }

    val eventStream: LiveData<Event> = _eventStream

    fun duplicate(vararg id: Long) = keymapRepository.duplicateKeymap(*id)
    fun delete(vararg id: Long) = keymapRepository.deleteKeymap(*id)
    fun enableSelectedKeymaps() = keymapRepository.enableKeymapById(*selectionProvider.selectedIds)
    fun disableSelectedKeymaps() = keymapRepository.disableKeymapById(*selectionProvider.selectedIds)
    fun enableAll() = keymapRepository.enableAll()
    fun disableAll() = keymapRepository.disableAll()

    fun rebuildModels() {
        if (keymapRepository.keymapList.value == null) return

        if (keymapRepository.keymapList.value?.isEmpty() == true) {
            _model.value = Empty()
            return
        }

        _model.value = Loading()

        _eventStream.value =
            BuildKeymapListModels(keymapRepository.keymapList.value ?: emptyList())
    }

    fun setModelList(list: List<KeymapListItemModel>) {
        selectionProvider.updateIds(list.map { it.id }.toLongArray())

        _model.value = when {
            list.isEmpty() -> Empty()
            else -> Data(list)
        }
    }

    fun requestBackupSelectedKeymaps() = run { _eventStream.value = RequestBackupSelectedKeymaps() }

    fun fixError(failure: Failure) {
        _eventStream.value = FixFailure(failure)
    }

    suspend fun getDeviceInfoList() = deviceInfoRepository.getAll()

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val keymapListUseCase: KeymapListUseCase,
        private val deviceInfoRepository: DeviceInfoRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return KeymapListViewModel(keymapListUseCase, deviceInfoRepository) as T
        }
    }
}