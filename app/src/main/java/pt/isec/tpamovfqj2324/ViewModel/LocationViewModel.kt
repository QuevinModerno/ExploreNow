package pt.isec.tpamovfqj2324.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import pt.isec.tpamovfqj2324.utils.Location
import pt.isec.tpamovfqj2324.utils.getImageId
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationViewModel() : ViewModel(){
    val local = mutableStateOf<List<Location>>(emptyList())
    private val _listaLocalizacoes = mutableStateListOf<Location>()
    init{
        getRates()
    }

    private fun getRates(){
        viewModelScope.launch {
            _listaLocalizacoes.addAll(fetchLocals())
            local.value = _listaLocalizacoes.toList()
        }

    }

    fun setCliqueNoBotao(localizacao: Location, foiClicado: Boolean) {
        val index = _listaLocalizacoes.indexOf(localizacao)
        if (index != -1) {
            _listaLocalizacoes[index] = localizacao.copy(canVote = foiClicado)
            local.value = _listaLocalizacoes
        }
    }
}
suspend fun fetchLocals(): List<Location> {
    return suspendCoroutine { continuation ->
        val db = Firebase.firestore
        val locals = mutableListOf<Location>()
        db.collection("location").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    try {
                        // Acessar os campos do documento
                        val name = document.getString("Name")
                        val description = document.getString("Description")
                        val user = document.getString("Useradded")
                        val n =  document.getLong("Approvals")?.toInt() ?: 0
                        val imageId = document.getString("image")?.let { getImageId(it) }

                        // Criar um objeto Location com os dados do documento
                        val local = imageId?.let {
                            Location( name.orEmpty(),description.orEmpty(),user.orEmpty(),n, true,
                                it
                            )
                        }

                        // Adicionar à lista geral de localizações
                        if (local != null) {
                            locals.add(local)
                        }
                    } catch (e: Exception) {
                        // Lidar com erros ao acessar os campos do documento
                        Log.e("Firebase", "Error fetching document fields: $e")
                    }
                }
                continuation.resume(locals)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting documents: ", e)
                continuation.resume(locals)
            }
    }
}