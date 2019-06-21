package jp.techacademy.rie.ijichi.taskapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_category.*
import android.widget.ArrayAdapter
import io.realm.Realm
import io.realm.kotlin.createObject
import kotlinx.android.synthetic.main.content_input.*


class CategoryActivity : AppCompatActivity() {

    private var mTask: Task? = null

    private lateinit var mRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        category_register_button.setOnClickListener {

            addCategory()

//            val categoryCreate = category_create_edit.text.toString()
//            categoryList.add(categoryCreate)
//            Log.d("BBB","$categoryList")
//            val adapter = ArrayAdapter(
//                applicationContext, android.R.layout.simple_spinner_item, categoryList)
//            adapter.setDropDownViewResource(
//                android.R.layout.simple_spinner_dropdown_item)
//            adapter.notifyDataSetChanged()

//            mRealm = Realm.getDefaultInstance()
//            mRealm.beginTransaction()
//
//            mTask = Task()
//            mTask!!.category = Category()
//            mRealm.copyToRealmOrUpdate(mTask!!)
//            mRealm.commitTransaction()
//
//            mRealm.close()

            finish()

        }

    }

    private fun addCategory(){
        mRealm = Realm.getDefaultInstance()
        mRealm.beginTransaction()
        val newCategory = Category()
        val categoryCreate = category_create_edit.text.toString()
        newCategory.name = categoryCreate
        val categoryRealmResults = mRealm.where(Category::class.java).findAll()
        val identifier:Int =
            if (categoryRealmResults.max("id") != null){
                categoryRealmResults.max("id")!!.toInt() +1
            }else{
                0
            }
        newCategory.id = identifier
        Log.d("BBB","$identifier")

        mRealm.copyToRealmOrUpdate(newCategory)
        mRealm.commitTransaction()
        mRealm.close()
    }
}

