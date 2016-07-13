package com.smartisanos.sidebar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import com.smartisanos.sidebar.R;
import com.smartisanos.sidebar.SidebarController;
import com.smartisanos.sidebar.util.LOG;
import com.smartisanos.sidebar.util.Utils;
import com.smartisanos.sidebar.util.anim.AnimUtils;

public class ContentView extends RelativeLayout {
    private static final LOG log = LOG.getInstance(ContentView.class);

    public enum ContentType{
        NONE,
        PHOTO,
        FILE,
        CLIPBOARD,
        ADDTOSIDEBAR
    }

    private ViewStub mViewStubAddToSidebar;

    private RecentPhotoViewGroup mRecentPhotoViewGroup;
    private RecentFileViewGroup mRecentFileViewGroup;
    private ClipboardViewGroup mClipboardViewGroup;

    private Context mViewContext;

    private ContentType mCurType = ContentType.NONE;

    // add content related
    private View mAddContainner;

    public ContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ContentType getCurrentContent(){
        return mCurType;
    }

    public void setCurrent(ContentType ct){
        mCurType = ct;
    }

    public void show(ContentType ct, boolean anim) {
        if (mCurType != ContentType.NONE) {
            return;
        }
        setVisibility(View.VISIBLE);
        SidebarController.getInstance(mContext).addContentView();
        mCurType = ct;
        this.animate().alpha(1.0f).setDuration(ANIMATION_DURA).start();
        switch (ct) {
        case PHOTO:
            mRecentPhotoViewGroup.show(anim);
            break;
        case FILE:
            mRecentFileViewGroup.show(anim);
            break;
        case CLIPBOARD:
            mClipboardViewGroup.show(anim);
            break;
        case ADDTOSIDEBAR:
            if(mAddContainner == null){
                initAddToSidebar();
            }
            if(anim){
                mAddContainner.startAnimation(AnimUtils.getEnterAnimationForContainer(mAddContainner));
            }else{
                mAddContainner.setVisibility(View.VISIBLE);
            }
            break;
        default:
            break;
        }
    }

    private static final int ANIMATION_DURA = 314;

    public void dismiss(ContentType ct, boolean anim) {
        if (mCurType != ct) {
            return;
        }
        mCurType = ContentType.NONE;
        if(anim){
            this.animate().alpha(0.0f).setDuration(ANIMATION_DURA).start();
        }else{
            this.setAlpha(0.0f);
        }
        switch (ct) {
        case PHOTO:
            mRecentPhotoViewGroup.dismiss(anim);
            break;
        case FILE:
            mRecentFileViewGroup.dismiss(anim);
            break;
        case CLIPBOARD:
            mClipboardViewGroup.dismiss(anim);
            break;
        case ADDTOSIDEBAR:
            if(mAddContainner == null){
                initAddToSidebar();
            }
            if (anim) {
                mAddContainner.startAnimation(AnimUtils.getExitAnimationForContainer(mAddContainner));
            } else {
                mAddContainner.setVisibility(View.INVISIBLE);
            }
            break;
        case NONE:
            break;
        default:
            break;
        }
    }

    private void initAddToSidebar(){
        if(mAddContainner == null){
            mViewStubAddToSidebar.inflate();
            mAddContainner = findViewById(R.id.addtosidebar_container);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewStubAddToSidebar = (ViewStub)findViewById(R.id.viewstub_addtosidebar);

        mRecentPhotoViewGroup = (RecentPhotoViewGroup)findViewById(R.id.recent_photo_view_group);
        mRecentPhotoViewGroup.setContentView(this);
        mRecentFileViewGroup = (RecentFileViewGroup)findViewById(R.id.recent_file_view_group);
        mRecentFileViewGroup.setContentView(this);
        mClipboardViewGroup = (ClipboardViewGroup)findViewById(R.id.clipboard_view_group);
        mClipboardViewGroup.setContentView(this);
    }

    @Override
    protected void onChildVisibilityChanged(View child, int oldVisibility,
            int newVisibility) {
        super.onChildVisibilityChanged(child, oldVisibility, newVisibility);
        if(newVisibility != View.VISIBLE){
            int count = getChildCount();
            for(int i = 0;i < count; ++ i){
                if(getChildAt(i).getVisibility() == View.VISIBLE){
                    // do nothing
                    return ;
                }
            }
            setVisibility(View.INVISIBLE);
            SidebarController.getInstance(mContext).resumeTopView();
            SidebarController.getInstance(mContext).removeContentView();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Utils.resumeSidebar(mContext);
            return true;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.KEYCODE_BACK:
            boolean isUp = event.getAction() == KeyEvent.ACTION_UP;
            if (isUp && getCurrentContent() != ContentType.NONE) {
                Utils.resumeSidebar(mContext);
            }
            break;
        default:
            break;
        }
        return super.dispatchKeyEvent(event);
    }
}