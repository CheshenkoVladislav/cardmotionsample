//package com.example.myapplication
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.View
//import androidx.coordinatorlayout.widget.CoordinatorLayout
//import com.google.android.material.appbar.AppBarLayout
//import com.google.android.material.bottomappbar.BottomAppBar
//
//
//class CardRecyclerBehavior(context: Context, attrs: AttributeSet) :
//    CoordinatorLayout.Behavior<View>(context, attrs) {
//
//
//    override fun onStartNestedScroll(
//        coordinatorLayout: CoordinatorLayout,
//        child: View,
//        directTargetChild: View,
//        target: View,
//        axes: Int,
//        type: Int
//    ): Boolean {
////        val b = AppBarLayout.Behavior
//        println("axes scroll = $axes, type = $type")
//        return true
//    }
//
//    override fun onNestedScrollAccepted(
//        coordinatorLayout: CoordinatorLayout,
//        child: View,
//        directTargetChild: View,
//        target: View,
//        axes: Int,
//        type: Int
//    ) {
//        println("On nested scroll accepted $child, $directTargetChild, $target, $axes, $target")
//        super.onNestedScrollAccepted(
//            coordinatorLayout,
//            child,
//            directTargetChild,
//            target,
//            axes,
//            type
//        )
//    }
//
//    override fun onNestedScroll(
//        coordinatorLayout: CoordinatorLayout,
//        child: View,
//        target: View,
//        dxConsumed: Int,
//        dyConsumed: Int,
//        dxUnconsumed: Int,
//        dyUnconsumed: Int,
//        type: Int,
//        consumed: IntArray
//    ) {
//        println("On nestenscroll = $dxConsumed, $dyConsumed, $dxUnconsumed, $dyUnconsumed, $child, $target")
//        super.onNestedScroll(
//            coordinatorLayout,
//            child,
//            target,
//            dxConsumed,
//            dyConsumed,
//            dxUnconsumed,
//            dyUnconsumed,
//            type,
//            consumed
//        )
//    }
//
//    override fun onNestedPreScroll(
//        coordinatorLayout: CoordinatorLayout,
//        child: View,
//        target: View,
//        dx: Int,
//        dy: Int,
//        consumed: IntArray,
//        type: Int
//    ) {
//        println("PRESCROLL = $dx; $dy; $type; $consumed")
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//    }
//}