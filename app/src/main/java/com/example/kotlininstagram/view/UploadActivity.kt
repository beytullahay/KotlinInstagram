package com.example.kotlininstagram.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlininstagram.databinding.ActivityFeedBinding
import com.example.kotlininstagram.databinding.ActivityMainBinding
import com.example.kotlininstagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding

    //
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionnLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    fun upload (view: View) {
        Toast.makeText(this, "Butona tıklandı", Toast.LENGTH_LONG).show()
        // benzersin string veriyor.
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if (selectedPicture !=null ){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                // storagaye yüklendikten sonra urlini alıp firestore atmak için
            val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString() // eklediğimiz görselin url'ini aldık

                    // map olarak firestore'e kayıt ediyoruz.
                    if (auth.currentUser != null) {
                        val postMap = hashMapOf<String, Any>()
                        postMap.put("downloadUrl", downloadUrl)
                        postMap.put("userEmail", auth.currentUser!!.email!!)
                        postMap.put("comment", binding.commentText.text.toString())
                        postMap.put("date", Timestamp.now())

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this@UploadActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }


            }.addOnFailureListener{
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun selectImage (view: View) {
        // izin verilmediyse galeriye erişmeye.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeri izni gerekiyor.", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", ){
                    // izin iste
                    permissionnLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {
                // izin iste
                permissionnLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            val intentToGalery = Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGalery)
        }
    }

    // galeri işlemleri
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if(result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture =  intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

        permissionnLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result ->
            if (result){
                // izin verildi
                val intentToGalery = Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }else{
                // izin verilmedi
                Toast.makeText(this@UploadActivity, "İzin gerekiyor.", Toast.LENGTH_LONG).show()
            }
        }

    }
}