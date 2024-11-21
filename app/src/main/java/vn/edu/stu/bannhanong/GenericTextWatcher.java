package vn.edu.stu.bannhanong;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class GenericTextWatcher implements TextWatcher {
    private EditText currentView;
    private EditText nextView;

    public GenericTextWatcher(EditText currentView, EditText nextView) {
        this.currentView = currentView;
        this.nextView = nextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 1 && nextView != null) {
            nextView.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }
}

