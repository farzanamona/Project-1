package com.example.project_1.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.example.project_1.R
import com.example.project_1.data.UserDataBase
import com.example.project_1.model.User
import kotlinx.android.synthetic.main.activity_home.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private var context: Context? =null
    private var image_view: ImageView? =null
    private var etFullName: TextView? =null
    private var etEmail: TextView? =null
    private var etPhone: TextView? =null
    private var etAddress: TextView? =null
    private var toolbar: Toolbar? = null
    var btnupdate:AppCompatButton?=null
    private var drawerLayout: DrawerLayout? = null
    var db: UserDataBase? = null
    private var user: User? = null

    var currentPath : String?=null
    val TAKE_PICTURE=3
    private var imageview: ImageView? = null
    private val GALLERY = 1
    private val PERMISSION_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        user = intent.getSerializableExtra("User") as? User
        etFullName = findViewById(R.id.etName)as TextView
        etEmail = findViewById(R.id.etuEmail) as TextView
        etPhone = findViewById(R.id.etuPhone) as TextView
        etAddress = findViewById(R.id.etuAddress) as TextView
        btnupdate = findViewById(R.id.btnUpdateProfile)as AppCompatButton
        imageview = findViewById<View>(R.id.image_view) as ImageView
        val imageplus = findViewById<ImageView>(R.id.img_plus)
        user = intent.getSerializableExtra("User") as User
        if (user != null) {
            etFullName!!.setText(user!!.name)
            etEmail!!.setText(user!!.email)
            etPhone!!.setText(user!!.phone)
            etAddress!!.setText(user!!.address)

        }
        imageplus!!.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    showPictureDialog()
                }
            }
            else{
                //system os is < marshmallow
                showPictureDialog()
            }
        }

        initit()
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    showPictureDialog()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun initit(){
        db = Room.databaseBuilder<UserDataBase>(applicationContext, UserDataBase::class.java, "userdb"
        ).allowMainThreadQueries().build()
        btnupdate!!.setOnClickListener {
            val name = etFullName!!.getText().toString()
            val email: String = etEmail!!.getText().toString()
            val address: String = etAddress!!.getText().toString()
            val phn: String = etPhone!!.getText().toString()
            val user = User()
            user.name =name
            user.email = email
            user.phone = phn
            user.address = address
            db!!.userDao!!.update(user)
            Toast.makeText(this, "update", Toast.LENGTH_SHORT).show()
            etFullName!!.setText(name)
            etEmail!!.setText(email)
            etPhone!!.setText(phn)
            etAddress!!.setText(address)

        }

        context=this
        toolbar = findViewById(R.id.toolbar)as Toolbar
        drawerLayout = findViewById(R.id.drawerLayout)as DrawerLayout
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                setTitle(R.string.dashboard)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                setTitle(R.string.dashboard)
            }
        }


        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener{
            when (it.itemId){
                R.id.nav_settings -> {
//                    startActivity(Intent(context,Activity::class.java))
//                    drawerLayout!!.closeDrawer(GravityCompat.START)
                }

                R.id.nav_logout -> {
                   finish()
                }

            }
            // Close the drawer
            true
        }



    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }
    fun choosePhotoFromGallary(){
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    private fun takePhotoFromCamera(){

        dishpetOnCameraIntent()
    }

    fun dishpetOnCameraIntent(){
        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager)!= null){
            var photofile: File?= null
            try{
                photofile = createImage()

            }catch (e: IOException){
                e.printStackTrace()
            }
            if (photofile!= null){
                var photoUri = FileProvider.getUriForFile(this,"com.example.fileproviderwithcamera",photofile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent,TAKE_PICTURE)
            }
        }
    }
    fun createImage(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
        val imageName = timestamp
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile("image",".jpg",storageDir)
        currentPath = image.absolutePath
        return image

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageview!!.setImageBitmap(bitmap)


                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        if (requestCode==TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            try{
                val file = File(currentPath)
                Log.e("currentPath",""+currentPath)
                val uri = Uri.fromFile(file)
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                imageview!!.setImageBitmap(bitmap)
                saveImage(bitmap)
                Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    companion object {
        private val IMAGE_DIRECTORY = "/demonut"
    }

}
