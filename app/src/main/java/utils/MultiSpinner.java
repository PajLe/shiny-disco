package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.example.shinydisco.R;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List<String> items;
    private List<Boolean> selected;
    private String defaultText = "Unknown";
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context) {
        super(context);
        items = Arrays.asList(getResources().getStringArray(R.array.music));
        selected = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            selected.add(false);
        }
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        items = Arrays.asList(getResources().getStringArray(R.array.music));
        selected = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            selected.add(false);
        }
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        items = Arrays.asList(getResources().getStringArray(R.array.music));
        selected = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            selected.add(false);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected.set(which, isChecked);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someSelected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected.get(i)) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                someSelected = true;
            }
        }
        String spinnerText;
        if (someSelected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { spinnerText });
        setAdapter(adapter);
        if (listener != null)
            listener.onItemsSelected(selected);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), convertToPrimitiveBoolean(selected), this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(List<Boolean> selected);
    }

    private boolean[] convertToPrimitiveBoolean(List<Boolean> bools) {
        boolean[] b = new boolean[bools.size()];
        for (int i = 0; i < bools.size(); i++) {
            b[i] = bools.get(i);
        }
        return b;
    }
}