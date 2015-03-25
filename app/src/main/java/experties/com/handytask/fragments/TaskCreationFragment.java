package experties.com.handytask.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import experties.com.handytask.R;
import experties.com.handytask.helpers.FragmentHelpers;
import experties.com.handytask.models.DataHolder;
import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;

public class TaskCreationFragment extends Fragment implements UploadImageFragment.UploadDialogListener{
    private boolean isMandatoryFilled;

    private EditText edTxtTitle;
    private EditText edTxtComment;

    private Button taskUploadBtn1;
    private Button taskUploadBtn2;
    private Button taskUploadBtn3;
    private Button cancelTaskBtn;
    private Button nextTaskBtn;

    private ImageView imgVwTask1;
    private ImageView imgVwTask2;
    private ImageView imgVwTask3;

    private Spinner sprTaskType;

    private TaskItem item;
    private UploadImageFragment uploadDialog1;
    private UploadImageFragment uploadDialog2;
    private UploadImageFragment uploadDialog3;

    private byte[] selectedImage1;
    private byte[] selectedImage2;
    private byte[] selectedImage3;

    public interface TaskCreationNextStep {
        void onNextStep(int stepId, TaskItem item);
    }

    public TaskCreationFragment() {
        // Required empty public constructor
    }

    public static TaskCreationFragment newInstance(TaskItem item) {
        TaskCreationFragment frag = new TaskCreationFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task_creation, container, false);
        setupView(v);
        if(getArguments() != null) {
            item = (TaskItem) getArguments().getParcelable("item");
        }

        if(item == null) {
            item = new TaskItem();
        }
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupView(View v) {
        final Activity context = getActivity();
        sprTaskType = (Spinner) v.findViewById(R.id.sprTaskType);

        edTxtTitle = (EditText) v.findViewById(R.id.edTxtTitle);
        edTxtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isMandatoryFilled = checkMandatory();
            }
        });
        edTxtComment = (EditText) v.findViewById(R.id.edTxtComment);
        edTxtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isMandatoryFilled = checkMandatory();
            }
        });

        taskUploadBtn1 = (Button) v.findViewById(R.id.taskUploadBtn1);
        taskUploadBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDialog1 = UploadImageFragment.newInstance(R.id.taskUploadBtn1);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                uploadDialog1.setTargetFragment(TaskCreationFragment.this, 1437);
                uploadDialog1.show(fm, "fragment_settings_dialog");
            }
        });
        taskUploadBtn2 = (Button) v.findViewById(R.id.taskUploadBtn2);
        taskUploadBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDialog2 = UploadImageFragment.newInstance(R.id.taskUploadBtn2);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                uploadDialog2.setTargetFragment(TaskCreationFragment.this, 1437);
                uploadDialog2.show(fm, "fragment_settings_dialog");
            }
        });
        taskUploadBtn3 = (Button) v.findViewById(R.id.taskUploadBtn3);
        taskUploadBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDialog3 = UploadImageFragment.newInstance(R.id.taskUploadBtn3);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                uploadDialog3.setTargetFragment(TaskCreationFragment.this, 1437);
                uploadDialog3.show(fm, "fragment_settings_dialog");
            }
        });

        imgVwTask1 = (ImageView) v.findViewById(R.id.imgVwTask1);
        imgVwTask2 = (ImageView) v.findViewById(R.id.imgVwTask2);
        imgVwTask3 = (ImageView) v.findViewById(R.id.imgVwTask3);

        cancelTaskBtn = (Button) v.findViewById(R.id.cancelTaskBtn);
        cancelTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
        nextTaskBtn = (Button) v.findViewById(R.id.nextTaskBtn);
        nextTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMandatoryFilled == true) {
                    String title = edTxtTitle.getText().toString();
                    item.setBriefDescription(title);
                    item.setDetailedDescription(edTxtComment.getText().toString());
                    item.setType(sprTaskType.getSelectedItem().toString());
                    if(selectedImage1 != null) {
                        DataHolder.getInstance().save(title + "-1", selectedImage1);
                        //item.setSelectedImage1(selectedImage1);
                    }
                    if(selectedImage2 != null) {
                        DataHolder.getInstance().save(title + "-2", selectedImage2);
                        //item.setSelectedImage2(selectedImage2);
                    }
                    if(selectedImage3 != null) {
                        DataHolder.getInstance().save(title + "-3", selectedImage3);
                        //item.setSelectedImage3(selectedImage3);
                    }

                    TaskCreationNextStep listner = (TaskCreationNextStep)getActivity();
                    listner.onNextStep(2, item);
                }
            }
        });

        isMandatoryFilled = checkMandatory();
    }

    @Override
    public void onSelectImageDialog(byte[] byteArray, int btnId) {
        switch (btnId) {
            case R.id.taskUploadBtn1:
                if(byteArray != null) {
                    selectedImage1 = byteArray;
                    uploadDialog1.dismiss();
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imgVwTask1.setImageBitmap(FragmentHelpers.getResizedBitmap(bmp, 300, 300));
                }
                break;
            case R.id.taskUploadBtn2:
                if(byteArray != null) {
                    selectedImage2 = byteArray;
                    uploadDialog2.dismiss();
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imgVwTask2.setImageBitmap(FragmentHelpers.getResizedBitmap(bmp, 300, 300));
                }
                break;
            case R.id.taskUploadBtn3:
                if(byteArray != null) {
                    selectedImage3 = byteArray;
                    uploadDialog3.dismiss();
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imgVwTask3.setImageBitmap(FragmentHelpers.getResizedBitmap(bmp, 300, 300));
                }
                break;
        }
    }

    private boolean checkMandatory() {
        String title = edTxtTitle.getText().toString();
        if(title != null && !"".equals(title)) {
            String comment = edTxtComment.getText().toString();
            if(comment != null && !"".equals(comment)) {
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(nextTaskBtn, "alpha", 1.0f);
                fadeAnim.setDuration(500);
                fadeAnim.start();
                return true;
            }
        }

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(nextTaskBtn, "alpha", 0.5f);
        fadeAnim.start();
        return false;
    }
}
