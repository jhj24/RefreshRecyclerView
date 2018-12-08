package com.jhj.refreshrecyclerview.refresh

class RefreshResult<T>(
    val data: List<T>,
    val result: Int,
    val msg: String
)