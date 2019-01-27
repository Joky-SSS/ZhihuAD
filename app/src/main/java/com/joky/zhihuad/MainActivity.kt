package com.joky.zhihuad

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var dataList: MutableList<ItemData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
        val adapter = ScrollAdapter()
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(adapter.mScrollListener)
    }

    private fun initData() {
        dataList
        for (i in 0..30) {
            if (i % 6 == 0 && i > 0)
                dataList.add(ItemData(1))
            else
                dataList.add(ItemData(0))
        }
    }

    inner class ScrollAdapter : RecyclerView.Adapter<ScrollAdapter.ViewHolder>() {

        lateinit var mScrollListener: RecyclerView.OnScrollListener

        init {
            initListener()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = if (viewType == 0) {
                layoutInflater.inflate(R.layout.item_normal, parent, false)
            } else {
                layoutInflater.inflate(R.layout.item_advers, parent, false)
            }
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        }

        override fun getItemViewType(position: Int): Int {
            return dataList[position].type
        }

        private fun initListener() {
            mScrollListener = object : RecyclerView.OnScrollListener() {
                var radius = 0F
                var top = 0F
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastItemPosition = layoutManager.findLastVisibleItemPosition()
                    for (position in firstItemPosition..lastItemPosition) {
                        if (this@ScrollAdapter.getItemViewType(position) == 1) {
                            val holder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
                            if (holder is ViewHolder) {
                                val rect = Rect()
                                holder.hover?.getGlobalVisibleRect(rect)
                                ensure(recyclerView, holder)
                                holder.hover?.progress = ((rect.centerY() - top) * 100 / radius / 2).toInt()
                            }
                        }
                    }
                }

                private fun ensure(recyclerView: RecyclerView, holder: ViewHolder) {
                    if (radius == 0F) {
                        val rRect = Rect()
                        recyclerView.getGlobalVisibleRect(rRect)
                        radius = (rRect.height() - holder.hover!!.height - dp2px(40F)) / 2
                        top = rRect.centerY() - radius
                    }
                }

                private fun dp2px(dp: Float): Float {
                    return android.util.TypedValue.applyDimension(
                            android.util.TypedValue.COMPLEX_UNIT_SP, dp, Resources.getSystem().displayMetrics)
                }
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var hover: PathHoverView? = itemView.findViewById(R.id.hoverView)
        }
    }

    data class ItemData(var type: Int)

}