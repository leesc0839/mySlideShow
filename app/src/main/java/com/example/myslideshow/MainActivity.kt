package com.example.myslideshow

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.myslideshow.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private var imageUriList: MutableList<Uri> = mutableListOf<Uri>()
    private val imageList: List<AppCompatImageView> by lazy {
        mutableListOf<AppCompatImageView>().apply {
            add(binding.imageView1)
            add(binding.imageView2)
            add(binding.imageView3)
            add(binding.imageView4)
            add(binding.imageView5)
            add(binding.imageView6)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCreateProjectButton()
        initPlayButton()
        initActivityResultLauncher()
    }

    private fun startToMakeProjects() { // 갤러리에서 사진 선택 시작
        // 안드로이드 내장 activity로 intent 전달
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) .apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*" // * 을 통해서 모든 이미지 타입을 가져올 수 있음
        }
        activityResultLauncher.launch(intent) // request 코드가 필요없음.
    }

    private fun initActivityResultLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            val selectedImageUri: Uri? = result.data?.data // 여기서 data 는 intent
            selectedImageUri?.let {
                if (imageUriList.size == 6) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("사진은 최대 6장입니다.")
                        .setMessage("다른 사진을 추가하시겠습니까? 확인을 누르면 처음에 설정한 사진이 지워집니다.")
                        .setPositiveButton("확인") { _, _ ->
                            imageUriList[0] = selectedImageUri
                            imageUriList.forEachIndexed { index, uri ->
                                imageList[index].isVisible = true
                                imageList[index].setImageURI(uri)
                            }
                        }.setNegativeButton("취소") { _, _ -> }
                        .create().show()
                } else{
                    imageUriList.add(0, selectedImageUri)
                    imageUriList.forEachIndexed { index, uri ->
                        imageList[index].isVisible = true
                        imageList[index].setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun initCreateProjectButton() {
        binding.run {
            createProjectButton.setOnClickListener {
                when {
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED -> {
                        startToMakeProjects()
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        //교육용 팝업 띄운 후 권한 팝업을 띄워야함.
                        showPermissionContextPopUp()
                    }
                    else -> {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            1000
                        ) // 이 요청은 콜백 메서드로 받아온다.
                    }

                }
            }
        }
    }

    private fun initPlayButton() {
        binding.run {
            playButton.setOnClickListener {
                val intent = Intent(this@MainActivity, SlideshowActivity::class.java)
                imageUriList.forEachIndexed { index, uri ->
                    intent.putExtra("imageView$index", uri.toString()) // string 으로 변환해서 보내 주어야함.
                }
                intent.putExtra("imgUriListSize",imageUriList.size)
                startActivity(intent)
            }
        }
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한 요청")
            .setMessage("사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000 // 이 요청은 콜백 메서드로 받아온다.
                )
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startToMakeProjects()
                } else
                    Snackbar.make(
                        binding.createProjectButton,
                        "권한을 거부하셨습니다.",
                        Snackbar.LENGTH_SHORT
                    ).show()
            }
            else -> {

            }
        }
    }




}