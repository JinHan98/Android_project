package com.example.androidproject_maps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 10
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseauth: FirebaseAuth
    private lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener
    private lateinit var menuArr : ArrayList<MenuFood>
    private lateinit var shopKey : String
    private lateinit var shopName : String
    private lateinit var shopRating : String
    private lateinit var bank : String
    private lateinit var bankID : String
    private lateinit var address : String
    private lateinit var accountHolder : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mFirebaseauth = FirebaseAuth.getInstance()
        menuArr = intent.getSerializableExtra("MenuArr") as ArrayList<MenuFood>
        shopKey = intent.extras.getString("ShopKey")
        shopName = intent.extras.getString("ShopName")
        shopRating = intent.extras.getString("ShopRating")
        bank = intent.extras.getString("Bank")
        bankID = intent.extras.getString("BankID")
        address = intent.extras.getString("Address")
        accountHolder = intent.extras.getString("AccountHolder")
        initFBAuthState()


    }
    override fun onStart() {//oncreate 다음에 호출  액티비티가 사용자에게 보여지기 직전에 호출됨
        super.onStart()
        mFirebaseauth.addAuthStateListener(mAuthStateListener)
    }
    override fun onStop() {//onstart와 짝을이룸  사용자에게 액티비티가 보여지지 않을 때 호출됨.
        // 보통 객체의 null 체크후 값이 있을 경우 자원을 해제 할때 사용한다.
        super.onStop()
        if(mAuthStateListener != null){
            mFirebaseauth.removeAuthStateListener { mAuthStateListener }
        }
    }
    private fun initFBAuthState(){
        mAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("95521268619-gs5tfh7onci46u8e3spj00v5usqtv7bl.apps.googleusercontent.com")//jason 파일의 client id값
                    .requestEmail()
                    .build()
                googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)


                googleloginBt.setOnClickListener{
                    //1.구글 로그인 버튼 클릭하면 이사람이 구글 사용자니 물어본다
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)//rc_Sign은 result 코드
                    OKDialog()

                }
            }
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                // 2. 구글 사용자 맞다고 하면 이쪽으로
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)//3.파이어베이스로 넘긴다
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                // ...
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {//4.파이어베이스에서 이 사람의 정보 받아서
        //파이어베이스 서버 authentication으로 값이 넘어감
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseauth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                } else {
                    Toast.makeText(this@LoginActivity,"오류가 발생하였습니다.",Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun OKDialog(){
        var builder = AlertDialog.Builder(this@LoginActivity)
        builder.setMessage("구글 인증 성공!!")
        builder.setPositiveButton(
            "확인"
        ){dialog, which ->
            //logindetailActivity로 넘기기
            var intent = Intent(this@LoginActivity, LogindetailActivity::class.java)
            intent.putExtra("MenuArr", menuArr)
            intent.putExtra("ShopKey", shopKey)
            intent.putExtra("ShopName",shopName)
            intent.putExtra("ShopRating",shopRating.toFloat())
            intent.putExtra("Address", address)
            intent.putExtra("AccountHolder", accountHolder)
            intent.putExtra("Bank", bank)
            intent.putExtra("BankID", bankID)
            finish()
            startActivity(intent)
        }
        builder.show()
    }
}
