package org.telegram.irooms.company;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.util.Log;

import org.json.JSONObject;
import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Company;
import org.telegram.irooms.network.IRoomJsonParser;
import org.telegram.irooms.task.RoomsRepository;
import org.telegram.irooms.task.TaskRunner;
import org.telegram.messenger.AndroidUtilities;
import org.rooms.messenger.R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.recyclerview.widget.LinearLayoutManager;
import io.socket.client.Socket;

public class CompanyFragment extends BaseFragment {

    private RecyclerListView listView;
    private TextView btnCreateCompany;
    private CompanyViewAdapter companyViewAdapter;

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
        actionBar.setTitle(LocaleController.getInstance().getRoomsString("teams"));
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

        btnCreateCompany = getCreateCompanyButton(context);
        btnCreateCompany.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("action", "delete");
            args.putBoolean("create_company", true);
            ((LaunchActivity) getParentActivity()).getActionBarLayout().presentFragment(new AddMembersToCompanyActivity(args), true);

        });
        listView = new RecyclerListView(context);
        companyViewAdapter = new CompanyViewAdapter(new CompanyViewAdapter.CompanyDiff(), getParentActivity(), new CompanyViewAdapter.CompanySelecterListener() {
            @Override
            public void onClick(Company company) {
                try {
                    if (company.getId()==0){
                        return;
                    }
                    CompanyInfo companyInfo = new CompanyInfo(company);
                    presentFragment(companyInfo);
                } catch (Exception x) {
                }
            }

            @Override
            public void onSelect(Company company) {
                try {
                    ((LaunchActivity) getParentActivity()).refreshCompany();
                } catch (Exception x) {
                }
            }
        });
        initCompaniesInfo();

        listView.setAdapter(companyViewAdapter);

        listView.setLayoutManager(new LinearLayoutManager(getParentActivity()));

        linearLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 24, 24, 24, 0));
        return fragmentView;
    }

    private void initCompaniesInfo() {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync((Callable<List<Company>>) () -> {
            TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            RoomsRepository repository = RoomsRepository.getInstance(getParentActivity().getApplication(),user.phone);
            return repository.getCurrentUserCompanyList(user.id);
        }, result -> {
            companyViewAdapter.submitList(result);
            if (((LaunchActivity) getParentActivity()).getmSocket().connected()) {
                fillCompanyList(((LaunchActivity) getParentActivity()).getmSocket());
            }
        });
    }

    public void fillCompanyList(Socket socket) {
        IRoomsManager.getInstance().getMyCompanies(getParentActivity(), socket, new IRoomsManager.IRoomsCallback() {
            @Override
            public void onSuccess(String success) {
                try {
                    TaskRunner runner = new TaskRunner();
                    runner.executeAsync((Callable<List<Company>>) () -> {

                        TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
                        RoomsRepository repository = RoomsRepository.getInstance(getParentActivity().getApplication(),user.phone);
                        return repository.getCurrentUserCompanyList(user.id);

                    }, result -> {
                        try {
                            companyViewAdapter.submitList(result);
                            ((LaunchActivity) getParentActivity()).setCompanyList((ArrayList<Company>) result);
                        } catch (Exception x) {
                        }
                    });
                } catch (Exception x) {
                }
            }

            @Override
            public void onError(String error) {
             //   Toast.makeText(getParentActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextView getCreateCompanyButton(Context context) {
        TextView buttonTextView = new TextView(context);
        buttonTextView.setText("Создать новую команду");
        buttonTextView.setPadding(AndroidUtilities.dp(34), 0, AndroidUtilities.dp(34), 0);
        buttonTextView.setGravity(Gravity.CENTER);
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        return buttonTextView;
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


        themeDescriptions.add(new ThemeDescription(btnCreateCompany, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_featuredStickers_buttonText));
        themeDescriptions.add(new ThemeDescription(btnCreateCompany, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_featuredStickers_addButtonPressed));

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        return themeDescriptions;
    }
}
