/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package barajas.meli.test.ui.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import barajas.meli.test.data.MainActivityRepository
import barajas.meli.test.ui.mainactivity.MainActivityUiState.Error
import barajas.meli.test.ui.mainactivity.MainActivityUiState.Loading
import barajas.meli.test.ui.mainactivity.MainActivityUiState.Success
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val mainActivityRepository: MainActivityRepository
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = mainActivityRepository
        .mainActivitys.map<List<String>, MainActivityUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addMainActivity(name: String) {
        viewModelScope.launch {
            mainActivityRepository.add(name)
        }
    }
}

sealed interface MainActivityUiState {
    object Loading : MainActivityUiState
    data class Error(val throwable: Throwable) : MainActivityUiState
    data class Success(val data: List<String>) : MainActivityUiState
}
