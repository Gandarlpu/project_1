package com.example.simplestopwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_infomation.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.text.DecimalFormat

open class Information : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var Edit_salary : String = ""
    var Edit_work_time : String = ""
    var Edit_start_time : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infomation)

        val docRef = db.collection(auth.currentUser?.uid.toString())
            .document(auth.currentUser?.uid.toString())

        var pointNumStr = ""
        salary.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(pointNumStr)){
                    pointNumStr = makeCommaNumber(Integer.parseInt(s.toString().replace("," , "")))
                    salary.setText(pointNumStr)
                    salary.setSelection(pointNumStr.length) //커서를 오른쪽 끝으로 보냄
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        //EditText.getText().toString()
        next.setOnClickListener {
            Edit_salary = salary.text.toString()
            Edit_work_time = work_time.text.toString()
            Edit_start_time = start_time.text.toString()

            Edit_salary = Edit_salary.replace("," , "")

            //초당급여 계산
            val sec_salary: Double =
                real_salary(Edit_salary.toDouble() , Edit_work_time.toDouble())

            //공백처리
            if(Edit_salary != null && Edit_salary.equals("")){
                Toast.makeText(this@Information , "월급 입력" , Toast.LENGTH_SHORT).show()
            }else if(TextUtils.isEmpty(Edit_start_time)){
                Toast.makeText(this@Information , "출근시간 입력" , Toast.LENGTH_SHORT).show()
            }else if(TextUtils.isEmpty(Edit_work_time)){
                Toast.makeText(this@Information , "근무시간 입력" , Toast.LENGTH_SHORT).show()
            }else {

            //DB입력
            input_db(sec_salary)
                
            }
        }
    }//onCreate


    //메뉴 띄우기
    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.gallery, menu)

        return true
    }

    //메뉴 아이템 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.gallery -> {
                val intent = Intent(this@Information , Gallery::class.java)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    //DB입력 함수
    fun input_db(sec_salary : Double){
        
        //DB입력
        val user_info = hashMapOf(
            "salary" to Edit_salary,
            "start_time" to Edit_start_time,
            "work_time" to Edit_work_time
        )

        // Add a new document with a generated ID
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            db.collection(user.uid)
                .document(auth.currentUser?.uid.toString())
                .set(user_info)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                }


            //화면전환
            val intent = Intent(this@Information, MainActivity::class.java)
            intent.putExtra("sec_salary" , sec_salary)
            startActivity(intent)

            finish()
        }
    }//input_db

    //세자리 콤마찍기
    fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }

    //초급계산
    open fun real_salary(salary : Double , work_time : Double) : Double {
        // 월급 -> 주급 -> 일급 -> 시급 -> 분급 -> 초급
        // 8시간 주5일

        val week = salary.div(5)
        val day = week.div(work_time)
        val salary_hour = day.div(60)
        val salary_sec = salary_hour.div(60)

        return salary_sec

    }// fun real_salary

}
