package org.telegram.irooms.company;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Company;
import org.telegram.messenger.AndroidUtilities;
import org.rooms.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import io.socket.client.Socket;

public class CompanyInfo extends BaseFragment {

    private Company mCompany;
    private CompanyMemberAdapter memberAdapter;

    public CompanyInfo(Company company) {
        this.mCompany = company;
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
                builder.setTitle("Вы уверены, что хотите удалить этого участника?");
                TextView info = new TextView(getParentActivity());
                info.setPadding(54, 0, 0, 0);
                String fullName = user.first_name != null ? user.first_name : "" + user.last_name != null ? user.last_name : "";

                info.setText(fullName);
                builder.setView(info);
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    removeMemberFromCompany(position, user.id);
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
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
        Socket socket = ((LaunchActivity) getParentActivity()).getmSocket();
        IRoomsManager.getInstance().deleteMembersFromCompany(getParentActivity(), socket, mCompany.getId(), members, new IRoomsManager.IRoomsCallback() {
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
