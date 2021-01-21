package org.telegram.irooms.company;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.util.Log;

import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Company;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.rooms.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import io.socket.client.Socket;

public class CompanyInfo extends BaseFragment {

    private Company mCompany;
    private CompanyMemberAdapter memberAdapter;

    public CompanyInfo(Company company) {
        this.mCompany = company;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        return true;
    }

    @Override
    public boolean onBackPressed() {
        try {
            ((LaunchActivity) getParentActivity()).refreshCompany();
        } catch (Exception x) {
        }
        return super.onBackPressed();
    }

    @Override
    public View createView(Context context) {

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(mCompany.getName());
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) fragmentView;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener((v, event) -> true);

        TextView companiesTitle = new TextView(context);
        companiesTitle.setText("Members");
        //        companiesTitle.setPadding(AndroidUtilities.dp(14), 0, AndroidUtilities.dp(14), 0);
        companiesTitle.setGravity(Gravity.CENTER);
        companiesTitle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        companiesTitle.setBackgroundColor(context.getResources().getColor(R.color.lighter_gray));
        linearLayout.addView(companiesTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 0, 24, 0, 0));

        RecyclerListView listView = new RecyclerListView(context);
        ArrayList<TLRPC.User> userList = new ArrayList<>();
        for (long i : mCompany.getMembers()) {
            TLRPC.User user = getMessagesController().getUser((int) i);
            if (user != null) {
                userList.add(user);
            }
        }
        memberAdapter = new CompanyMemberAdapter(new CompanyMemberAdapter.UserDiff(), getParentActivity().getApplicationContext(), mCompany, userList, new CompanyMemberAdapter.OnRemoveUserListener() {
            @Override
            public void removeUserFromCompany(int position, TLRPC.User user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Are you sure you want to delete this user?");
                TextView info = new TextView(getParentActivity());
                info.setPadding(34, 0, 0, 0);
                info.setText(user.first_name + " " + user.last_name);
                builder.setView(info);
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    removeMemberFromCompany(position, user.id);
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog showDialog = builder.create();
                showDialog(showDialog);
            }
        });

        listView.setAdapter(memberAdapter);

        listView.setLayoutManager(new LinearLayoutManager(getParentActivity()));

        linearLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 24, 24, 24, 0));

        return fragmentView;

    }

    private void removeMemberFromCompany(int position, int id) {
        ArrayList<Integer> members = new ArrayList<>(1);
        members.add(id);
        Socket socket =((LaunchActivity)getParentActivity()).getmSocket();
        IRoomsManager.getInstance().deleteMembersFromCompany(getParentActivity(),socket, mCompany.getId(), members, new IRoomsManager.IRoomsCallback() {
            @Override
            public void onSuccess(String success) {
                memberAdapter.removeItem(position);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getParentActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private EditTextBoldCursor getEditText(Context context) {

        EditTextBoldCursor editText = new EditTextBoldCursor(context);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setBackground(Theme.createEditTextDrawable(context, false));
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setPadding(0, 0, 0, 0);
        editText.setSingleLine(true);
        editText.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setHint("Company name");
        editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setCursorSize(AndroidUtilities.dp(20));
        editText.setCursorWidth(1.5f);
        return editText;
    }

    private TextView getCreateCompanyButton(Context context) {
        TextView buttonTextView = new TextView(context);
        buttonTextView.setText("Create a new company");
        buttonTextView.setPadding(AndroidUtilities.dp(34), 0, AndroidUtilities.dp(34), 0);
        buttonTextView.setGravity(Gravity.CENTER);
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        return buttonTextView;
    }

    private void confirmAndDelete(final CallLogRow row) {
        if (getParentActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getParentActivity())
                .setTitle(LocaleController.getString("AppName", R.string.AppName).replace("Telegram","Rooms"))
                .setMessage(LocaleController.getString("ConfirmDeleteCallLog", R.string.ConfirmDeleteCallLog))
                .setPositiveButton(LocaleController.getString("Delete", R.string.Delete), (dialog, which) -> {
                    ArrayList<Integer> ids = new ArrayList<>();
                    for (TLRPC.Message msg : row.calls) {
                        ids.add(msg.id);
                    }
                    MessagesController.getInstance(currentAccount).deleteMessages(ids, null, null, 0, 0, false, false);
                })
                .setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null)
                .show()
                .setCanceledOnTouchOutside(true);
    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions,
                                                   int[] grantResults) {
        if (requestCode == 101 || requestCode == 102) {
            boolean allGranted = true;
            for (int a = 0; a < grantResults.length; a++) {
                if (grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (grantResults.length > 0 && allGranted) {
            } else {
                VoIPHelper.permissionDenied(getParentActivity(), null, requestCode);
            }
        }
    }

    private static class CallLogRow {
        public TLRPC.User user;
        public List<TLRPC.Message> calls;
        public int type;
        public boolean video;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();


        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        return themeDescriptions;
    }
}
