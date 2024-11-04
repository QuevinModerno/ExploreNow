package pt.isec.tpamovfqj2324.ui.activity.Add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ui.activity.ContributeActivity
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AddInterestPlacesActivity : ComponentActivity() {
    private var db= Firebase.firestore
    var selectedCategory=""
    var selectedLocation=""
    var latitude =0.0
    var longitude =0.0

    private var shouldShowCamera: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            } else {

            }
        }

    private fun obtainOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()
            ?.let { File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return mediaDir ?: filesDir
    }

    private fun takePhoto(
        filenameFormat: String,
        imageCapture: ImageCapture,
        outputDirectory: File,
        executor: Executor,
        onImageCapture: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(filenameFormat, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCapture(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            })
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tpamovfqj2324Theme {
                AddInterestPlaces()
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddInterestPlaces() {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var imageUri by remember{ mutableStateOf<Uri?>(null) }
        val bitmap = remember{ mutableStateOf<Bitmap?>(null) }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
                uri : Uri? ->
            imageUri = uri
        }
        var categoryOptions by remember { mutableStateOf(emptyList<String>()) }
        var locationOptions by remember { mutableStateOf(emptyList<String>()) }

        suspend fun loadNameOptions(collectionName: String): List<String> {
            return try {
                val result = db.collection(collectionName).get().await()
                result.documents.mapNotNull { it.getString("Name") }
            } catch (e: Exception) {
                emptyList()
            }
        }
        LaunchedEffect(Unit) {
            categoryOptions = loadNameOptions("category")

            locationOptions = loadNameOptions("location")
        }
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.title)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(stringResource(R.string.description)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        imageUri?.let{
                            if(Build.VERSION.SDK_INT<28){
                                bitmap.value = MediaStore.Images
                                    .Media.getBitmap(context.contentResolver, it)
                            }
                            else{
                                val source = ImageDecoder.createSource(context.contentResolver, it)
                                bitmap.value = ImageDecoder.decodeBitmap(source)
                            }

                            bitmap.value?.let{btm->
                                Image(
                                    bitmap = btm.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(55.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(8.dp)
                                )
                            }
                        }
                        Button(onClick = { launcher.launch("image/*")}) {
                            Text(text = stringResource(R.string.pick_image))

                        }

                        dropDownMenuCategory()


                        dropDownMenuLocation()

                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(
                                        this@AddInterestPlacesActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    getCurrentLocation()
                                } else {
                                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text(text= stringResource(R.string.get_location))
                        }


                        Button(onClick = {
                            savenewPlace(title, description, imageUri, selectedCategory, selectedLocation)
                            val intent = Intent(this@AddInterestPlacesActivity, ContributeActivity::class.java)
                            startActivity(intent)
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                            Text(stringResource(R.string.submit))
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Button(onClick = {
                            val intent = Intent(this@AddInterestPlacesActivity, ContributeActivity::class.java)
                            startActivity(intent)
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                            Text(stringResource(R.string.back))
                        }
                    }
            }
        }
    }


    private fun savenewPlace(titleText: String, phraseText: String, imageUri: Uri?, category:String, location:String) {
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("images/${imageUri?.lastPathSegment}")
        imageUri?.let {
            imagesRef.putFile(it) .addOnCompleteListener { task->
                if(task.isSuccessful){
                    imagesRef.downloadUrl.addOnCompleteListener {
                    }
                }else{
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        val useradded= FirebaseAuth.getInstance().currentUser!!.email
        val placeMap= hashMapOf(
            "Name" to titleText,
            "Description" to phraseText,
            "Useradded" to useradded,
            "image" to imageUri,
            "location" to location,
            "category" to category,
            "latitude" to latitude,
            "longitude" to longitude
        )
        db.collection("Place").document(titleText).set(placeMap)
            .addOnSuccessListener { Toast.makeText(this,
                getString(R.string.success), Toast.LENGTH_SHORT).show()}
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun dropDownMenuCategory() {
        var expanded by remember { mutableStateOf(false) }
        var categoryOptions by remember { mutableStateOf(emptyList<String>()) }
        var selectedItem by remember {
            mutableStateOf("")
        }
        var textFiledSize by remember {
            mutableStateOf(Size.Zero)
        }
        LaunchedEffect(Unit) {
            categoryOptions = getAllCategoryNames()
        }
        val icon = if (expanded) {
            Icons.Default.ArrowDownward
        } else {
            Icons.Default.ArrowUpward
        }

        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = selectedItem,
                onValueChange = { selectedItem = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFiledSize = coordinates.size.toSize()
                    },
                label = { Text(text = stringResource(R.string.select_category)) },
                trailingIcon = { Icon(icon, "", Modifier.clickable { expanded = !expanded }) })

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded=false },modifier=Modifier.width(with(
                LocalDensity.current){textFiledSize.width.toDp()})) {
                categoryOptions.forEach{category->
                    DropdownMenuItem(text = {Text(text=category)}, onClick = {
                        selectedItem=category
                        selectedCategory=category
                        expanded=false
                    })

                }
            }

        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun dropDownMenuLocation(){
        var expanded by remember { mutableStateOf(false) }
        var LocalOptions by remember { mutableStateOf(emptyList<String>()) }
        var selectedItem by remember {
            mutableStateOf("")
        }
        var textFiledSize by remember {
            mutableStateOf(Size.Zero)
        }
        LaunchedEffect(Unit) {
            LocalOptions = getAllLocalNames()
        }
        val icon = if (expanded) {
            Icons.Default.ArrowDownward
        } else {
            Icons.Default.ArrowUpward
        }

        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = selectedItem,
                onValueChange = { selectedItem = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFiledSize = coordinates.size.toSize()
                    },
                label = { Text(text = stringResource(R.string.select_local)) },
                trailingIcon = { Icon(icon, "", Modifier.clickable { expanded = !expanded }) })

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded=false },modifier=Modifier.width(with(
                LocalDensity.current){textFiledSize.width.toDp()})) {
                LocalOptions.forEach{Local->
                    DropdownMenuItem(text = {Text(text=Local)}, onClick = {
                        selectedItem=Local
                        selectedLocation=Local
                        expanded=false
                    })
                }
            }

        }
    }

    private suspend fun getAllLocalNames(): List<String> {
        return try {
            val result = db.collection("location").get().await()
            result.documents.mapNotNull { it.getString("Name") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllCategoryNames(): List<String> {
        return try {
            val result = db.collection("category").get().await()
            result.documents.mapNotNull { it.getString("Name") }
        } catch (e: Exception) {
            emptyList()
        }
    }
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it

                    latitude = it.latitude
                    longitude = it.longitude
                    Toast.makeText(this,
                        getString(R.string.latitude)+"$latitude"+ getString(R.string.longitude)+ "$longitude", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { e ->
               Toast.makeText(this,
                   getString(R.string.erro_ao_obter_a_localiza_o), Toast.LENGTH_SHORT).show()
            }
    }

}
