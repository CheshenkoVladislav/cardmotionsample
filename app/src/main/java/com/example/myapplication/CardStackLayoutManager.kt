package com.example.myapplication

import androidx.recyclerview.widget.RecyclerView

class CardStackLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.let {
            detachAndScrapAttachedViews(recycler)
            recycler.setViewCacheSize(0)
            val firstView = recycler.getViewForPosition(0)
            addView(firstView)
            measureChildWithMargins(firstView, 0, 0)
            val firstChildWidth = firstView.measuredWidth
            val firstChildHeight = firstView.measuredHeight
            layoutDecorated(firstView, 0, 0, firstChildWidth, firstChildHeight)
            val diffBetweenRootAndChildHeight = height - firstChildHeight
            val bias = diffBetweenRootAndChildHeight/(itemCount - 1)
            for (i in 1 until itemCount) {
                val nextView = recycler.getViewForPosition(i)
                addView(nextView)
                measureChildWithMargins(nextView, 0, 0)
                val nextChildWidth = nextView.measuredWidth
                val nextChildHeight = nextView.measuredHeight
                val nextBias = bias * i
                layoutDecorated(nextView, 0, nextBias, nextChildWidth, nextChildHeight + nextBias)
            }
        }
    }
}