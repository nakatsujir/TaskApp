package jp.techacademy.rie.ijichi.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Category : RealmObject() {
    var name: String = ""
    @PrimaryKey
    var id: Int = 0
}
