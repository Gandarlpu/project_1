package com.example.simplestopwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.timer

// https://jaejong.tistory.com/59 스톱워치 참고 사이트

class MainActivity : AppCompatActivity() {

        var time = 0
        var isRunning = true
        var timerTask: Timer? = null
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        var work_time = ""
        var salary = ""
        var work_start = ""
        var sec_salary : Double = 0.0

    var count1 = 0
    var count2 = 0
    var count3 = 0
    var count4 = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            sec_salary = intent.getDoubleExtra("sec_salary" , 0.0)

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


            //컨텐츠 추가
            content_add.setOnClickListener {
                Toast.makeText(this@MainActivity , "준비 중입니다." , Toast.LENGTH_SHORT).show()
            }

            //다시시작
            reload.setVisibility(View.INVISIBLE)
            reload.setOnClickListener {

                val intent = Intent(this@MainActivity, Information::class.java)
                startActivity(intent)

            }

            //시작
            start.setOnClickListener {

                if(start.getVisibility() == View.VISIBLE) {
                    start.setVisibility(View.INVISIBLE)

                    if (isRunning) {
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

    fun timeToText(sec_salary : Double , work_time : Int){
        var result : Double = 0.0

        timerTask = timer(period = 1000) {
            time++
            var hour = (time / 1440) % 24 // 1시간
            var min = (time / 60) % 60 //1분
            var sec = time % 60 // 1 초

/*            //val hour = (time / 144000) % 24 // 1시간
            val min = (time / 6000) % 60 // 1분
            val sec = (time / 100) % 60 //1초
            val milli = time % 100 // 0.01 초*/

            //초당 급여
            result += sec_salary
            
            //컨텐츠 계산
            var americano = result.div(6000)
            var soup = result.div(7000)
            var movie = result.div(8000)
            var walk = min * 3.5//분당 3.5kcal

            //컨텐츠 계산-2
            var seoul_apartment = 1000000000
            //seoul_apartment/reslt/24 = 몇일 일하면 구할수잇는지



            runOnUiThread {

                //content_View
                if(americano >= 1){
                    val americano_View = makeCommaNumber(americano.toInt())
                    first_content.text = "$americano_View"
                }
                if(soup >= 1){
                    val soup_View = makeCommaNumber(soup.toInt())
                    second_content.text = "${soup.toInt()}"
                }
                if(movie >= 1){
                    val movie_View = makeCommaNumber(movie.toInt())
                    third_content.text = "${movie.toInt()}"
                }


                forth_content.text = "$walk"


                //시간 종료
                if (hour == work_time){
                    if(reload.getVisibility() == View.INVISIBLE)
                        reload.setVisibility(View.VISIBLE)

                    hour=0
                    min=0
                    sec=0

                    timerTask?.cancel()

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

    //세자리 콤마찍기
    fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }

}// class

