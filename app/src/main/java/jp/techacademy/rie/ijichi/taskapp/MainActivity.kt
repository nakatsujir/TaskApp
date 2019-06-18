package jp.techacademy.rie.ijichi.taskapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mTaskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        listView1.setOnItemClickListener { parent, view, position, id ->
            // 入力・編集する画面に遷移させる
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, view, position, id ->
            // タスクを削除する
            true
        }

        reloadListView()

    }

    private fun reloadListView() {
        // 後でTaskクラスに変更する
        val taskList = mutableListOf("aaa", "bbb", "ccc")

        mTaskAdapter.taskList = taskList
        listView1.adapter = mTaskAdapter
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

open class Task : RealmObject(), Serializable {
    var title: String = ""
    var contents: String = ""
    var date: Date = Date()

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}
