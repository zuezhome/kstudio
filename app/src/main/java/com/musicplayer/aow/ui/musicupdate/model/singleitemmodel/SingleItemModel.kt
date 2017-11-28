package com.musicplayer.aow.ui.musicupdate.model.singleitemmodel

/**
 * Created by Arca on 11/28/2017.
 */
class SingleItemModel {

    var name: String? = null
    var url: String? = null
    var description: String? = null

    constructor() {}

    constructor(name: String, url: String) {
        this.name = name
        this.url = url
    }
}