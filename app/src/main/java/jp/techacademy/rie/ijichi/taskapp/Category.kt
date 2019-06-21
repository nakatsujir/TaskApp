package jp.techacademy.rie.ijichi.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

//internal var categoryList = ArrayList<String>()

open class Category : RealmObject() {
    var category: String = ""
    @PrimaryKey
    var id: Int = 0
}
