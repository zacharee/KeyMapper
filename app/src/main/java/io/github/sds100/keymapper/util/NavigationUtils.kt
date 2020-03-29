package io.github.sds100.keymapper.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

/**
 * Created by sds100 on 25/03/2020.
 */

fun <T> NavBackStackEntry.observeLiveData(lifecycleOwner: LifecycleOwner, key: String, observe: (t: T) -> Unit) {
    savedStateHandle.getLiveData<T>(key).observe(lifecycleOwner) {
        observe(it)
    }
}

fun <T> NavBackStackEntry.setLiveData(key: String, value: T) {
    savedStateHandle.set(key, value)
}

fun <T> NavBackStackEntry.removeLiveData(key: String) {
    savedStateHandle.remove<T>(key)
}

fun <T> NavController.observeCurrentDestinationLiveData(lifecycleOwner: LifecycleOwner, key: String, observe: (t: T) -> Unit) {
    currentDestination?.id?.let {
        getBackStackEntry(it).observeLiveData(lifecycleOwner, key, observe)
    }
}

fun <T> NavController.setCurrentDestinationLiveData(key: String, value: T) {
    currentDestination?.id?.let {
        getBackStackEntry(it).setLiveData(key, value)
    }
}

fun <T> NavController.removeCurrentDestinationLiveData(key: String) {
    currentDestination?.id?.let {
        getBackStackEntry(it).removeLiveData<T>(key)
    }
}