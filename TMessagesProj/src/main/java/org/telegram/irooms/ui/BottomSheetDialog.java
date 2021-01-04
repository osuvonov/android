//package org.telegram.irooms.ui;
//
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//import com.google.android.material.datepicker.MaterialDatePicker;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.telegram.irooms.database.Task;
//import org.telegram.irooms.network.Backend;
//import org.telegram.irooms.network.VolleyCallback;
//import org.telegram.irooms.task.TaskUtil;
//import org.telegram.irooms.task.TaskViewModel;
//import org.rooms.messenger.R;
//import org.telegram.messenger.databinding.AddTaskBottomSheetBinding;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//
//import androidx.annotation.Nullable;
//
//public class BottomSheetDialog extends BottomSheetDialogFragment {
//
//    private AddTaskBottomSheetBinding bottomSheet;
//
//    private String deadline = "";
//
//    private int selectedDeadLineView = -1;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable
//            ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
//
//        deadline = TaskUtil.getMaxDate();
//
//        bottomSheet = AddTaskBottomSheetBinding.inflate(getLayoutInflater());
//
//        bottomSheet.btnTaskDeadlineToday.setOnClickListener(view -> {
//            boolean todaySelected = selectedDeadLineView == 1;
//            if (todaySelected) {
//                 deadline = TaskUtil.getMaxDate();
//                bottomSheet.btnTaskDeadlineToday.setTextColor(getResources().getColor(R.color.disabled_text_color));
//            } else {
//                bottomSheet.btnTaskDeadlineToday.setTextColor(getResources().getColor(R.color.holo_blue_bright));
//                selectedDeadLineView = 1;
//                deadline = TaskUtil.getEndOfTheDay();
//            }
//            bottomSheet.btnTaskCalendar.setTextColor(getResources().getColor(R.color.disabled_text_color));
//            bottomSheet.btnTaskDeadlineTomorrow.setTextColor(getResources().getColor(R.color.disabled_text_color));
//        });
//
//        bottomSheet.btnTaskDeadlineTomorrow.setOnClickListener(view -> {
//            boolean tomorrowSelected = selectedDeadLineView == 2;
//            if (tomorrowSelected) {
//                 deadline = TaskUtil.getMaxDate();
//                bottomSheet.btnTaskDeadlineTomorrow.setTextColor(getResources().getColor(R.color.disabled_text_color));
//            } else {
//                bottomSheet.btnTaskDeadlineTomorrow.setTextColor(getResources().getColor(R.color.holo_blue_bright));
//                selectedDeadLineView = 2;
//                deadline = TaskUtil.getEndOfTomorrow();
//            }
//            bottomSheet.btnTaskDeadlineToday.setTextColor(getResources().getColor(R.color.disabled_text_color));
//            bottomSheet.btnTaskCalendar.setTextColor(getResources().getColor(R.color.disabled_text_color));
//        });
//        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
//
//        // now define the properties of the
//        // materialDateBuilder that is title text as SELECT A DATE
//        materialDateBuilder.setTitleText("SELECT A DATE");
//
//        // now create the instance of the material date
//        // picker
//        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
//        materialDatePicker.addOnPositiveButtonClickListener((object) -> {
//            selectedDeadLineView = 3;
//            bottomSheet.btnTaskDeadlineToday.setTextColor(getResources().getColor(R.color.disabled_text_color));
//            bottomSheet.btnTaskDeadlineTomorrow.setTextColor(getResources().getColor(R.color.disabled_text_color));
//            bottomSheet.btnTaskCalendar.setText(materialDatePicker.getHeaderText());
//            bottomSheet.btnTaskCalendar.setTextColor(getResources().getColor(R.color.holo_blue_bright));
//        });
//
//        bottomSheet.btnTaskCalendar.setOnClickListener(object -> {
//            materialDatePicker.show(getChildFragmentManager(), "");
//        });
//
//        bottomSheet.btnTaskSave.setOnClickListener(v1 -> {
//            //todo saveTask
//            String description = bottomSheet.etTaskDescription.getText().toString();
//            String status = bottomSheet.etTaskStatus.getText().toString();
//            long companyId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("company_id", 0);
//            Backend.getInstance().addTask(getActivity(), companyId, description, status, deadline, new VolleyCallback() {
//                @Override
//                public void onSuccess(String response) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response.toString());
//                        String success = jsonObject.getString("success");
//                        long taskId = jsonObject.getLong("task_id");
//                        String error = jsonObject.opt("error") == null ? "" : jsonObject.getString("error");
//                        Task task = new Task(taskId, companyId );
//                        TaskViewModel taskViewModel = new TaskViewModel(getActivity().getApplication());
//                        taskViewModel.insert(task);
//                        taskViewModel.getAllTasks();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onError(String error) {
//
//                }
//            });
//            dismiss();
//        });
//        // enable disable save btn
//        bottomSheet.etTaskDescription.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try {
//                    boolean saveBtnEnable = editable.toString().length() > 0;
//
//                    bottomSheet.btnTaskSave.setEnabled(saveBtnEnable);
//
//                } catch (Exception x) {
//                }
//            }
//        });
//
//
//        return bottomSheet.getRoot();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        bottomSheet = null;
//    }
//
//}
