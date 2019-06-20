package jp.techacademy.rie.ijichi.taskapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_category.*
import android.widget.ArrayAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*


class CategoryActivity : AppCompatActivity() {

    private var mTask: Task? = null

    private lateinit var mRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        category_register_button.setOnClickListener {
            val categoryCreate = category_create_edit.text.toString()
            categoryList.add(categoryCreate)
            Log.d("BBB","$categoryList")
            val adapter = ArrayAdapter(
                applicationContext, android.R.layout.simple_spinner_item, categoryList)
            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
            adapter.notifyDataSetChanged()

            mRealm = Realm.getDefaultInstance()
            mRealm.beginTransaction()

            mTask = Task()
            mTask!!.category = categoryList
            mRealm.copyToRealmOrUpdate(mTask!!)
            mRealm.commitTransaction()

            mRealm.close()

            finish()

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
}

