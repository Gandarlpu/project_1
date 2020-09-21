package com.example.simplestopwatch

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_infomation.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.concurrent.timer


// https://jaejong.tistory.com/59 스톱워치 참고 사이트

class MainActivity : AppCompatActivity() {

            var time = 0
            var isRunning = true
            var timerTask: Timer? = null
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                var work_time = ""
                var salary = ""
                var work_start = ""

                var sec_salary = intent.getDoubleExtra("salary" , 0.0)

                val docRef = db.collection(auth.currentUser?.uid.toString())
                    .document(auth.currentUser?.uid.toString())

                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        salary = documentSnapshot.get("salary").toString()
                        //sec_salary = documentSnapshot.get("sec_salary").toString()
                        work_start = documentSnapshot.get("start_time").toString()
                        work_time = documentSnapshot.get("work_time").toString()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this , "실패" , Toast.LENGTH_SHORT).show()
                    }



        //시작
        start.setOnClickListener {

            if(start.getVisibility() == View.VISIBLE) {
                start.setVisibility(View.INVISIBLE)

                val lion_View = findViewById<ImageView>(R.id.lion)
                Glide.with(this).load(R.raw.lion).into(lion_View)

                //isRunning = !isRunning
                if (isRunning) {
                    println("초당급여 : " + sec_salary)
                    timeToText(sec_salary , work_time.toInt())
                } else {
                    Log.d("msg", "실패")
                }
            }

        }//start_Button


    } // onCreate

    //로그아웃
    fun logout(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener{
/*                val activity_login = Login()

                activity_login.login()*/
                val intent = Intent(this@MainActivity , Login::class.java)
                startActivity(intent)
            }
    }


    //메뉴 띄우기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.login_out, menu)

        return true
    }

    //메뉴 아이템 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_log_out -> {
                logout()
                true
            }
            /*R.id.dark_mode -> {
https://enfanthoon.tistory.com/50 텍스트뷰 동적추가
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun timeToText(salary : Double , work_time : Int){
        var temp : Int = 0
        var result : Double = 0.0
        var sec_salary = 0.0

        timerTask = timer(period = 1000) {
            time++
            var hour = (time / 1440) % 24 // 1분
            var min = (time / 60) % 60 //1초
            var sec = time % 60 // 0.01 초

/*            //val hour = (time / 144000) % 24 // 1시간
            val min = (time / 6000) % 60 // 1분
            val sec = (time / 100) % 60 //1초
            val milli = time % 100 // 0.01 초*/

            result += salary
            sec_salary = Math.round(result*100)/100.0

            runOnUiThread {

                salary_View.text = NumberFormat.getInstance().format(sec_salary)

                //시간 종료
                if (sec == work_time.toInt()){
                    //Toast.makeText(this@MainActivity , "근무 시간 종료" , Toast.LENGTH_LONG).show()

                    hour=0
                    min=0
                    sec=0

                    val intent = Intent(this@MainActivity , LastView::class.java)
                    intent.putExtra("Last_salary" , result)
                    startActivity(intent)

                    timerTask?.cancel()
                    finish()
                }//if

                if (hour < 10) {
                    hourView.text = "0$hour"
                } else {
                    hourView.text = "$hour"
                }

                if (min < 10) {
                    minView.text = "0$min"
                } else {
                    minView.text = "$min"

                }

                if (sec < 10){
                    secView.text = "0$sec"
                }else {
                    secView.text = "$sec"
                }

                //$ 를 붙여주면 변하는 값을 계속 덮어준다
                //ex) $를 붙여주면 기존에 1이라는 값이 잇을때 값이 2로변하면 2로 바꿔준다.


            }
        }
    }



}// class

