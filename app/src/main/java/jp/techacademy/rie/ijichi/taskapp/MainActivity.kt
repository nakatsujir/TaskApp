package jp.techacademy.rie.ijichi.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_input.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

const val EXTRA_TASK = "jp.techacademy.rie.ijichi.taskapp.TASK"

internal var categoryList = ArrayList<String>()

class MainActivity : AppCompatActivity() {

    private lateinit var mTaskAdapter: TaskAdapter

    private lateinit var mRealm: Realm

    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(t: Realm) {
            reloadListView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        //spinner
        categoryList.add("カテゴリーを選択")
        categoryList.add("仕事")
        categoryList.add("旅行")
        categoryList.add("遊び")
        categoryList.add("運動")
        val adapter = ArrayAdapter(
            applicationContext, android.R.layout.simple_spinner_item, categoryList
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        main_category_spinner.adapter = adapter

        main_category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val spinnerSelectItem = spinnerParent.selectedItem as String

                category_search_edit.text = spinnerSelectItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)


        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        listView1.setOnItemClickListener { parent, view, position, id ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, view, position, id ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            // ダイアログを表示する
            AlertDialog.Builder(this).apply {
                setTitle("削除")
                setMessage(task.title + "を削除しますか")
                setPositiveButton("Ok") { _, _ ->
                    val result = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                    mRealm.beginTransaction()
                    result.deleteAllFromRealm()
                    mRealm.commitTransaction()

                    val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                    val resultPendingIntent = PendingIntent.getBroadcast(
                        this@MainActivity,
                        task.id,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(resultPendingIntent)

                    reloadListView()
                }
                setNegativeButton("CANCEL", null)

                val dialog = create()
                dialog.show()
            }
            true
        }

        category_search_image.setOnClickListener {
            categorySearch()
        }

        reloadListView()

    }

    private fun categorySearch() {
        val category = category_search_edit.text.toString()
        val query = mRealm.where(Task::class.java).equalTo("category", category).findAll()
        Log.d("AAA", "$query")
        mTaskAdapter.taskList = mRealm.copyFromRealm(query)
        listView1.adapter = mTaskAdapter

    }

    private fun reloadListView() {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }
}

open class Task : RealmObject(), Serializable {
    var title: String = ""
    var contents: String = ""
    //    var category:String = ""
    var category: ArrayList<String> = categoryList
    var date: Date = Date()

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}
