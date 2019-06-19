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
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey

import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.util.*

const val EXTRA_TASK = "jp.techacademy.rie.ijichi.taskapp.TASK"

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

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)


        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        listView1.setOnItemClickListener { parent, view, position, id ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            //Log.d("AAA","$task")
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
            //Log.d("AAA","$intent")
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
                setNegativeButton("CANCEL",null)

                val dialog = create()
                dialog.show()
            }
            true
        }

//        // アプリ起動時に表示テスト用のタスクを作成する
//        addTaskForTest()

        reloadListView()

    }

//    private fun addTaskForTest() {
//        val task = Task().apply {
//            title = "作業"
//            contents = "プログラムを書いてPUSHする"
//            date = Date()
//            id = 0
//        }
//        mRealm.beginTransaction()
//        mRealm.copyToRealmOrUpdate(task)
//        mRealm.commitTransaction()
//
//    }

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }
}

open class Task : RealmObject(), Serializable {
    var title: String = ""
    var contents: String = ""
    var category:String = ""
    var date: Date = Date()

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}
