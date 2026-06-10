package com.example.merchio.fragments;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.merchio.R;
import com.example.merchio.models.Category;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetFragment extends DialogFragment {

    public static final int SORT_DEFAULT    = 0;
    public static final int SORT_POPULAR    = 1;
    public static final int SORT_PRICE_ASC  = 2;
    public static final int SORT_PRICE_DESC = 3;
    public static final int SORT_NAME_AZ    = 4;

    private List<Category> categories  = new ArrayList<>();
    private String  selectedCategory   = null;
    private int     selectedSort       = SORT_DEFAULT;

    public interface FilterListener {
        void onFilterApplied(String category, int sort);
    }

    private FilterListener filterListener;

    public static FilterBottomSheetFragment newInstance(
            List<Category> categories,
            String selectedCategory,
            int selectedSort
    ) {
        FilterBottomSheetFragment f = new FilterBottomSheetFragment();
        f.categories       = categories;
        f.selectedCategory = selectedCategory;
        f.selectedSort     = selectedSort;
        return f;
    }

    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }

    private TextView chipSortDefault, chipSortPopular, chipSortPriceAsc,
            chipSortPriceDesc, chipSortAz;
    private FlexboxLayout flexboxCategories;
    private Button btnApply;
    private TextView btnReset;
    private TextView currentSortChip = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.BottomDialogStyle);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipSortDefault   = view.findViewById(R.id.chip_sort_default);
        chipSortPopular   = view.findViewById(R.id.chip_sort_popular);
        chipSortPriceAsc  = view.findViewById(R.id.chip_sort_price_asc);
        chipSortPriceDesc = view.findViewById(R.id.chip_sort_price_desc);
        chipSortAz        = view.findViewById(R.id.chip_sort_az);
        flexboxCategories = view.findViewById(R.id.flexbox_categories);
        btnApply          = view.findViewById(R.id.btn_apply_filter);
        btnReset          = view.findViewById(R.id.btn_reset);

        styleAllSortChips();
        setActiveSortChip(sortChipFor(selectedSort));

        chipSortDefault.setOnClickListener(v -> setActiveSortChip(chipSortDefault));
        chipSortPopular.setOnClickListener(v -> setActiveSortChip(chipSortPopular));
        chipSortPriceAsc.setOnClickListener(v -> setActiveSortChip(chipSortPriceAsc));
        chipSortPriceDesc.setOnClickListener(v -> setActiveSortChip(chipSortPriceDesc));
        chipSortAz.setOnClickListener(v -> setActiveSortChip(chipSortAz));

        buildCategoryChips();

        btnApply.setOnClickListener(v -> applyAndDismiss());
        btnReset.setOnClickListener(v -> resetAll());
    }

    private void styleAllSortChips() {
        for (TextView chip : new TextView[]{chipSortDefault, chipSortPopular,
                chipSortPriceAsc, chipSortPriceDesc, chipSortAz}) {
            applySortChipStyle(chip, false);
        }
    }

    private void setActiveSortChip(TextView chip) {
        if (currentSortChip != null) applySortChipStyle(currentSortChip, false);
        currentSortChip = chip;
        applySortChipStyle(chip, true);
    }

    private void applySortChipStyle(TextView chip, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(50f);
        bg.setColor(selected ? 0xFF8B6DFF : 0xFFF4F1FF);
        chip.setTextColor(selected ? 0xFFFFFFFF : 0xFF555555);
        chip.setBackground(bg);
    }

    private TextView sortChipFor(int sort) {
        switch (sort) {
            case SORT_POPULAR:    return chipSortPopular;
            case SORT_PRICE_ASC:  return chipSortPriceAsc;
            case SORT_PRICE_DESC: return chipSortPriceDesc;
            case SORT_NAME_AZ:    return chipSortAz;
            default:              return chipSortDefault;
        }
    }

    private int sortValueFor(TextView chip) {
        if (chip == chipSortPopular)   return SORT_POPULAR;
        if (chip == chipSortPriceAsc)  return SORT_PRICE_ASC;
        if (chip == chipSortPriceDesc) return SORT_PRICE_DESC;
        if (chip == chipSortAz)        return SORT_NAME_AZ;
        return SORT_DEFAULT;
    }

    private void buildCategoryChips() {
        flexboxCategories.removeAllViews();
        addCategoryChip("Semua", null);
        for (Category cat : categories) addCategoryChip(cat.getName(), cat.getName());
    }

    private void addCategoryChip(String label, String value) {
        TextView chip = new TextView(requireContext());
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 10);
        chip.setLayoutParams(params);
        chip.setText(label);
        chip.setTextSize(13f);
        chip.setPadding(36, 16, 36, 16);
        chip.setTag(value);

        boolean isSelected = (value == null && selectedCategory == null)
                || (value != null && value.equals(selectedCategory));
        applyCategoryChipStyle(chip, isSelected);

        chip.setOnClickListener(v -> {
            selectedCategory = (String) chip.getTag();
            for (int i = 0; i < flexboxCategories.getChildCount(); i++) {
                View child = flexboxCategories.getChildAt(i);
                if (child instanceof TextView) {
                    String tag = (String) child.getTag();
                    boolean sel = (tag == null && selectedCategory == null)
                            || (tag != null && tag.equals(selectedCategory));
                    applyCategoryChipStyle((TextView) child, sel);
                }
            }
        });
        flexboxCategories.addView(chip);
    }

    private void applyCategoryChipStyle(TextView chip, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(50f);
        bg.setColor(selected ? 0xFF8B6DFF : 0xFFF4F1FF);
        chip.setTextColor(selected ? 0xFFFFFFFF : 0xFF555555);
        chip.setBackground(bg);
    }

    private void applyAndDismiss() {
        if (filterListener != null)
            filterListener.onFilterApplied(selectedCategory, sortValueFor(currentSortChip));
        dismiss();
    }

    private void resetAll() {
        selectedCategory = null;
        selectedSort     = SORT_DEFAULT;
        setActiveSortChip(chipSortDefault);
        for (int i = 0; i < flexboxCategories.getChildCount(); i++) {
            View child = flexboxCategories.getChildAt(i);
            if (child instanceof TextView) {
                String tag = (String) child.getTag();
                applyCategoryChipStyle((TextView) child, tag == null);
            }
        }
    }
}