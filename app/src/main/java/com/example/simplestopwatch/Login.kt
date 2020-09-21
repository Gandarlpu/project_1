package com.example.simplestopwatch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

//https://heropy.blog/2019/12/29/firebase-auth-with-sapper/ firebaseUI테마

val RC_SIGN_IN = 1000
class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //로그인 안됨
        if(FirebaseAuth.getInstance().currentUser == null){
            login()
        }else{
            //로그인 되잇을 때
            val intent = Intent(this@Login , Information::class.java)
            startActivity(intent)
        }
    }

    fun login(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
/*            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build() */
            )



        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                //.setLogo(R.drawable.Email_theme_background) //로고 그림 넣기
                //.setTheme() //스타일 바꾸기
                .setTosAndPrivacyPolicyUrls(
                    "https://policies.google.com/privacy?hl=ko",
                "https://policies.google.com/?hl=ko"
                )
                .build(),
            RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // 로그인 성공
                val user = FirebaseAuth.getInstance().currentUser

                val intent = Intent(this@Login , Information::class.java)
                startActivity(intent)

            } else {
                // 로그인 실패
                finish()
            }
        }
    }
}