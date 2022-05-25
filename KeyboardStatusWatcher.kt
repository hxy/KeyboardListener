package com.yidian.local.chat.utils

import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yidian.common.utils.DensityUtil

/**
 * Author: hy
 * Date: 5/13/22
 * Description:
 */
class KeyboardStatusWatcher(
        private val activity: Activity,
        private val lifecycleOwner: LifecycleOwner,
        private val listener: (isKeyboardShowed: Boolean, keyboardHeight: Int) -> Unit
) : PopupWindow(activity), ViewTreeObserver.OnGlobalLayoutListener {

    private val rootView by lazy { activity.window.decorView.rootView }

    /**
     * 原始的可见区域高度
     */
    private var originalVisibleHeight = 0

    /**
     * 软键盘是否显示
     */
    var isKeyboardShowed = false
        private set

    /**
     * 最近一次弹出的软键盘高度
     */
    var keyboardHeight = 0
        private set

    /**
     * PopupWindow 布局
     */
    private val popupView by lazy {
        FrameLayout(activity).also {
            it.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            //监听布局大小变化
            it.viewTreeObserver.addOnGlobalLayoutListener(this)
        }
    }

    init {
        //初始化 PopupWindow
        contentView = popupView
        //软键盘弹出时，PopupWindow 要调整大小
        softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED
        //宽度设为0，避免遮挡界面
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(0))
        rootView.post { showAtLocation(rootView, Gravity.NO_GRAVITY, 0, 0) }

        //activity 销毁时或者 Fragment onDestroyView 时必须关闭 popupWindow ，避免内存泄漏
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                dismiss()
            }
        })
    }

    /**
     * 监听布局大小变化
     */
    override fun onGlobalLayout() {
        val rect = Rect()
        //获取当前可见区域
        popupView.getWindowVisibleDisplayFrame(rect)
        if(originalVisibleHeight == 0){
            originalVisibleHeight = rect.height()
            return
        }
        val heightDiff = originalVisibleHeight - rect.height()
        //当窗口高度变化值超过屏幕的 1/3 时，视为软键盘弹出
        if(heightDiff>DensityUtil.getScreenHeight() / 3){
            isKeyboardShowed = true
            keyboardHeight = heightDiff
            listener.invoke(isKeyboardShowed, keyboardHeight)
        }else if(isKeyboardShowed){
            //软键盘隐藏时键盘高度为0
            isKeyboardShowed = false
            keyboardHeight = 0
            listener.invoke(isKeyboardShowed, keyboardHeight)
        }
    }
}