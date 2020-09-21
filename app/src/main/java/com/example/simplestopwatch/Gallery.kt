package com.example.simplestopwatch

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_infomation.*
import kotlinx.android.synthetic.main.activity_last_view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.text.DecimalFormat

class Gallery : AppCompatActivity() {

    val Gallery = 0
    var storage = Firebase.storage
    var path : String = ""
    private lateinit var bitmap : Bitmap

    // storage찹조변수
    var storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var gallery_cost = ""

        gallery_cost_Edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(gallery_cost)){
                    gallery_cost = makeCommaNumber(Integer.parseInt(s.toString().replace("," , "")))
                    gallery_cost_Edit.setText(gallery_cost)
                    gallery_cost_Edit.setSelection(gallery_cost.length) //커서를 오른쪽 끝으로 보냄
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        //갤러리 버튼
        gallery_album.setOnClickListener { gallery() }

        //확인 버튼
        check.setOnClickListener {

            // Create a storage reference from our app
            val storageRef = storage.reference

            gallery_image.isDrawingCacheEnabled = true
            gallery_image.buildDrawingCache()
            //사진을 bitmap으로 받아옴
            val bitmap = (gallery_image.drawable as BitmapDrawable).bitmap
            //받아온 bitmap사진을 바이트로 바꿈
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            //사진이 있는 경로에 들어가서 파일을 받아옴
            var file = Uri.fromFile(File(path))
            //파이어베이스 storage에 경로를 설정
            val imageRef = storageRef.child("image/${file.lastPathSegment}")
            //File을 취하고 UploadTask를 반환
            var uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(this@Gallery , "성공" , Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                Toast.makeText(this@Gallery , "실패" , Toast.LENGTH_SHORT).show()
            }

        }

    }//onCreate


    //세자리 콤마찍기
    fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }


    //갤러리 접근
    fun gallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        //intent.putExtra("crop" , true)

        startActivityForResult(Intent.createChooser(intent, "Load Picture") , Gallery)
        //startActivityForResult(intent , OPEN_GALLERY)
    }


    //갤러리 접근 후 사진 뿌리기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if(requestCode == Gallery){
                if(resultCode == RESULT_OK){
                    var dataUri = data?.data
                    path = data?.data.toString()
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver , dataUri)
                        gallery_image.setImageBitmap(bitmap)


                    }catch (e:Exception){
                        Toast.makeText(this , "$e",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this , "실패" , Toast.LENGTH_SHORT).show()
                }
            }
        
    }

}