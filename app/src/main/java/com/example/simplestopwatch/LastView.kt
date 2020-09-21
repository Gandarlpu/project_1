package com.example.simplestopwatch

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.graphics.createBitmap
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_last_view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.cos

class LastView : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_view)

        var doc_tax = ""
        var total_tax = 0.0
        var result_income = 0.0

        var last_salary = intent.getDoubleExtra("Last_salary" , 0.0)
        last_salary = Math.round(last_salary*100)/100.0
        result_salary_View.setText(last_salary.toString())

       val docRef = db.collection(auth.currentUser?.uid.toString())
            .document(auth.currentUser?.uid.toString())

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->

                doc_tax = documentSnapshot.get("salary").toString()
                total_tax = tax_cal(doc_tax.toDouble().div(21.6)) //평균 월 근무일수 = 21.6일
                tax_view.setText(NumberFormat.getInstance().format(total_tax))

                //일급 - 하루세금
                result_income = last_salary - total_tax
                result_income = Math.round(result_income*100)/100.0
                val result_income_String = makeCommaNumber(result_income.toInt())

                total_income_view.setText(result_income_String)
                //gallery_cost_view.setText(result_income_String)

            }
            .addOnFailureListener {
                Toast.makeText(this@LastView , "불러오기 실패" , Toast.LENGTH_SHORT).show()
            }

        //재시작
        reload.setOnClickListener {
            val intent = Intent(this@LastView , MainActivity::class.java)
            startActivity(intent)

            finish()
        }

        //사진, 가격
        var cost = intent.getStringExtra("cost")
        //firebase_strage_download()

    }//onCreate
 /*   fun firebase_strage_download(){

        var file = Uri.fromFile(File("content://com.android.providers.media.documents/document/"))

        var storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("image/${file.lastPathSegment}")
        println("파일경로 : " + imageRef)

        val gsReference = storage.getReferenceFromUrl("gs://timmer-f798f.appspot.com/image/document")

        val ONE_MEGABYTE : Long = 300 * 250
        imageRef.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener {
                Glide.with(this)
                    .load(storageRef)
                    .into(show_image)
            }
            .addOnFailureListener {
                Toast.makeText(this , "실패 " , Toast.LENGTH_SHORT).show()
            }

    }//firebase_storage_download*/

    fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }

    //초기화
    fun reset(){
        val intent = Intent(this@LastView , Information::class.java)
        startActivity(intent)
    }

    //로그아웃
    fun logout(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener{

                val intent = Intent(this@LastView , Login::class.java)
                startActivity(intent)
            }
    }

    //메뉴 띄우기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.reset, menu)

        return true
    }

    //메뉴 아이템 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout -> {
                logout()
                true
            }
            R.id.reset -> {
                reset()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //세금계산
    fun tax_cal(salary: Double) : Double{

        val tax_salary : Double = salary
        var tax : Double = 0.0
        var total_tax : Double = 0.0
        var local_income_tax = 0.0

        //4대 보험
        val National_pension = tax_salary * 0.045
        val health = tax_salary * 0.0323
        val long_Nursing_Insurance = health * 0.0851
        val Employment_Insurance = tax_salary * 0.008

        //소득세
        //1인기준
        if(tax_salary >= 2000000 && tax_salary <= 3000000){
            tax = tax_salary * 0.01
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 3000000 && tax_salary <= 4000000){
            tax = tax_salary * 0.03
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 4000000 && tax_salary <= 5000000){
            tax = tax_salary * 0.05
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 5000000 && tax_salary <= 6000000){
            tax = tax_salary * 0.07
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 6000000 && tax_salary <= 7000000){
            tax = tax_salary * 0.09
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 7000000 && tax_salary <= 8000000){
            tax = tax_salary * 0.11
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 8000000 && tax_salary <= 9000000){
            tax = tax_salary * 0.13
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 9000000 && tax_salary <= 10000000){
            tax = tax_salary * 0.14
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 10000000 && tax_salary <= 11000000){
            tax = tax_salary * 0.16
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 11000000 && tax_salary <= 12000000){
            tax = tax_salary * 0.26
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 12000000 && tax_salary <= 13000000){
            tax = tax_salary * 0.30
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 13000000 && tax_salary <= 14000000){
            tax = tax_salary * 0.32
            local_income_tax = tax * 0.01
        }else if(tax_salary >= 14000000 && tax_salary <= 15000000){
            tax = tax_salary * 0.34
            local_income_tax = tax * 0.01
        }



        //주민세



        total_tax = tax + National_pension + health +
                long_Nursing_Insurance + Employment_Insurance + local_income_tax

        println("최종 세금 : " + total_tax)

        return total_tax

    }

}