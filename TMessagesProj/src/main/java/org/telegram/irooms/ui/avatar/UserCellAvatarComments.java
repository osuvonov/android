/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.irooms.ui.avatar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class UserCellAvatarComments extends FrameLayout {

    private BackupImageView avatarImageView;


    private AvatarDrawable avatarDrawable;
    private Object currentObject;

    private CharSequence currentName;
    private int currentId;

    private boolean selfAsSavedMessages;

    private TLRPC.FileLocation lastAvatar;

    private int currentAccount = UserConfig.selectedAccount;

    private boolean needDivider;

    public UserCellAvatarComments(Context context, AttributeSet attrs) {
        super(context, attrs);
        avatarDrawable = new AvatarDrawable();

        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(21));
        int padding = 0;
//        addView(avatarImageView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 0, 2,0, 0));
        addView(avatarImageView, LayoutHelper.createFrame(43, 43, Gravity.LEFT | Gravity.BOTTOM, LocaleController.isRTL ? 0 : 2 + padding, 6, LocaleController.isRTL ? 7 + padding : 0, 0));

        setFocusable(true);
    }

    public void setData(Object object, CharSequence name, CharSequence status, int resId) {
        setData(object, null, name, status, resId, false);
    }

    public void setData(Object object, CharSequence name, CharSequence status, int resId, boolean divider) {
        setData(object, null, name, status, resId, divider);
    }

    public void setData(Object object, TLRPC.EncryptedChat ec, CharSequence name, CharSequence status, int resId, boolean divider) {
        if (object == null && name == null && status == null) {
            currentName = null;
            currentObject = null;
            avatarImageView.setImageDrawable(null);
            return;
        }
        currentName = name;
        currentObject = object;
        needDivider = divider;
        setWillNotDraw(!needDivider);
        update(0);
    }

    public Object getCurrentObject() {
        return currentObject;
    }

    public void setCurrentId(int id) {
        currentId = id;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(43) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void update(int mask) {
        TLRPC.FileLocation photo = null;
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        if (currentObject instanceof TLRPC.User) {
            currentUser = (TLRPC.User) currentObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }

        } else if (currentObject instanceof TLRPC.Chat) {
            currentChat = (TLRPC.Chat) currentObject;
            if (currentChat.photo != null) {
                photo = currentChat.photo.photo_small;
            }
        }

        if (mask != 0) {
            boolean continueUpdate = false;
            if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                if (lastAvatar != null && photo == null || lastAvatar == null && photo != null || lastAvatar != null && photo != null && (lastAvatar.volume_id != photo.volume_id || lastAvatar.local_id != photo.local_id)) {
                    continueUpdate = true;
                }
            }
            if (currentUser != null && !continueUpdate && (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {

            }

            if (!continueUpdate) {
                return;
            }
        }

        if (currentObject instanceof String) {
            String str = (String) currentObject;
            switch (str) {
                case "contacts":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_CONTACTS);
                    break;
                case "non_contacts":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_NON_CONTACTS);
                    break;
                case "groups":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_GROUPS);
                    break;
                case "channels":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_CHANNELS);
                    break;
                case "bots":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_BOTS);
                    break;
                case "muted":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_MUTED);
                    break;
                case "read":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_READ);
                    break;
                case "archived":
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_FILTER_ARCHIVED);
                    break;
            }
            avatarImageView.setImage(null, "50_50", avatarDrawable);
        } else {
            if (currentUser != null) {
                if (selfAsSavedMessages && UserObject.isUserSelf(currentUser)) {

                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_SAVED);
                    avatarImageView.setImage(null, "50_50", avatarDrawable, currentUser);
                    return;
                }
                avatarDrawable.setInfo(currentUser);

            } else if (currentChat != null) {
                avatarDrawable.setInfo(currentChat);
            } else if (currentName != null) {
                avatarDrawable.setInfo(currentId, currentName.toString(), null);
            } else {
                avatarDrawable.setInfo(currentId, "#", null);
            }
        }

        lastAvatar = photo;
        if (currentUser != null) {
            avatarImageView.setImage(ImageLocation.getForUser(currentUser, ImageLocation.TYPE_SMALL), "50_50", avatarDrawable, currentUser);
        } else if (currentChat != null) {
            avatarImageView.setImage(ImageLocation.getForChat(currentChat, ImageLocation.TYPE_SMALL), "50_50", avatarDrawable, currentChat);
        } else {
            avatarImageView.setImageDrawable(avatarDrawable);
        }
    }

    public void setSelfAsSavedMessages(boolean value) {
        selfAsSavedMessages = value;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0 : AndroidUtilities.dp(30), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(30) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
